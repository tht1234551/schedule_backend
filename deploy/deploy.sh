#!/bin/bash
#
# Blue/Green 무중단 배포 스크립트
#
# 사용법: bash deploy.sh <전송된 tar.gz 경로>
#   예) bash ~/deploy/deploy.sh ~/schedule-0.0.1.jar.tar.gz
#
# 동작:
#   1) nginx service-url.inc 에서 현재 active 포트 확인 → 반대 포트를 TARGET 으로 결정
#   2) tar.gz 를 TARGET 디렉터리에 풀고 jar 확보
#   3) TARGET 포트의 잔여 프로세스 정리
#   4) 신버전 기동 (nohup, prod 프로파일, TARGET 포트)
#   5) /actuator/health 가 UP 될 때까지 폴링 (타임아웃 시 롤백)
#   6) nginx upstream 을 TARGET 으로 전환 후 reload
#   7) 이전 active 포트(구버전) 종료
#
set -euo pipefail

# ===== 설정 =====
BLUE=8081
GREEN=8082
PROFILE=prod
APP_DIR="${HOME}/app"
INC_FILE="/etc/nginx/conf.d/service-url.inc"
HEALTH_PATH="/actuator/health"
HEALTH_TIMEOUT=60          # health 최대 대기(초)
HEALTH_INTERVAL=2          # health 폴링 간격(초)
STOP_WAIT=10               # 구버전 graceful 종료 대기(초)
JAVA_BIN="${JAVA_BIN:-java}"   # 필요 시 절대경로로 override (예: JAVA_BIN=/usr/lib/jvm/.../bin/java)

# ===== 인자 검증 =====
TARBALL="${1:-}"
if [[ -z "${TARBALL}" || ! -f "${TARBALL}" ]]; then
    echo "[ERROR] tar.gz 경로 인자가 필요합니다. 사용법: bash deploy.sh <tar.gz 경로>" >&2
    exit 1
fi

log() { echo "[$(date '+%Y-%m-%d %H:%M:%S')] $*"; }

# ===== 1) active 포트 판별 → TARGET 결정 =====
CURRENT_PORT=""
if [[ -f "${INC_FILE}" ]]; then
    CURRENT_PORT="$(grep -oE '127\.0\.0\.1:[0-9]+' "${INC_FILE}" | grep -oE '[0-9]+$' || true)"
fi

if [[ "${CURRENT_PORT}" == "${BLUE}" ]]; then
    TARGET_PORT="${GREEN}"
else
    # green 이거나, 값이 없거나(최초 배포) → blue 로 기동
    TARGET_PORT="${BLUE}"
fi
log "현재 active 포트: '${CURRENT_PORT:-없음}' → 배포 대상(TARGET) 포트: ${TARGET_PORT}"

TARGET_DIR="${APP_DIR}/${TARGET_PORT}"
PID_FILE="${TARGET_DIR}/app.pid"
LOG_FILE="${TARGET_DIR}/app.log"

# ===== 2) 압축 해제 =====
log "압축 해제: ${TARBALL} → ${TARGET_DIR}"
rm -rf "${TARGET_DIR}"
mkdir -p "${TARGET_DIR}"
tar -xzf "${TARBALL}" -C "${TARGET_DIR}"

JAR_FILE="$(find "${TARGET_DIR}" -maxdepth 1 -name '*.jar' | head -n 1)"
if [[ -z "${JAR_FILE}" ]]; then
    echo "[ERROR] 압축 안에서 jar 파일을 찾지 못했습니다." >&2
    exit 1
fi
log "배포 jar: ${JAR_FILE}"

# ===== 3) TARGET 포트 잔여 프로세스 정리 =====
if [[ -f "${PID_FILE}" ]] && kill -0 "$(cat "${PID_FILE}")" 2>/dev/null; then
    OLD_TARGET_PID="$(cat "${PID_FILE}")"
    log "TARGET 포트의 잔여 프로세스(${OLD_TARGET_PID}) 종료"
    kill "${OLD_TARGET_PID}" 2>/dev/null || true
    sleep "${STOP_WAIT}"
    kill -9 "${OLD_TARGET_PID}" 2>/dev/null || true
fi

# ===== 4) 신버전 기동 =====
log "신버전 기동 (포트 ${TARGET_PORT}, 프로파일 ${PROFILE})"
nohup "${JAVA_BIN}" -jar "${JAR_FILE}" \
    --spring.profiles.active="${PROFILE}" \
    --server.port="${TARGET_PORT}" \
    > "${LOG_FILE}" 2>&1 &
NEW_PID=$!
echo "${NEW_PID}" > "${PID_FILE}"
log "기동 PID: ${NEW_PID} (로그: ${LOG_FILE})"

# ===== 5) 헬스 게이트 =====
log "헬스체크 시작: http://127.0.0.1:${TARGET_PORT}${HEALTH_PATH} (최대 ${HEALTH_TIMEOUT}초)"
HEALTHY=0
ELAPSED=0
while (( ELAPSED < HEALTH_TIMEOUT )); do
    # 프로세스가 죽었으면 즉시 실패 처리
    if ! kill -0 "${NEW_PID}" 2>/dev/null; then
        log "[ERROR] 기동 프로세스가 종료되었습니다. (로그 확인 필요)"
        break
    fi
    if curl -sf "http://127.0.0.1:${TARGET_PORT}${HEALTH_PATH}" 2>/dev/null | grep -q '"status":"UP"'; then
        HEALTHY=1
        break
    fi
    sleep "${HEALTH_INTERVAL}"
    ELAPSED=$(( ELAPSED + HEALTH_INTERVAL ))
done

# ===== 6) 실패 시 롤백 =====
if (( HEALTHY == 0 )); then
    log "[ERROR] 헬스체크 실패 → 신버전(${NEW_PID}) 종료, 배포 중단 (구버전 트래픽 유지)"
    kill "${NEW_PID}" 2>/dev/null || true
    sleep "${STOP_WAIT}"
    kill -9 "${NEW_PID}" 2>/dev/null || true
    echo "----- 기동 로그 마지막 50줄 -----"
    tail -n 50 "${LOG_FILE}" 2>/dev/null || true
    exit 1
fi
log "헬스체크 통과 (UP)"

# ===== 7) nginx 전환 =====
log "nginx upstream 전환: ${CURRENT_PORT:-없음} → ${TARGET_PORT}"
echo "set \$service_url http://127.0.0.1:${TARGET_PORT};" | sudo tee "${INC_FILE}" > /dev/null
sudo nginx -t
sudo nginx -s reload
log "nginx reload 완료"

# ===== 8) 구버전 종료 =====
if [[ -n "${CURRENT_PORT}" && "${CURRENT_PORT}" != "${TARGET_PORT}" ]]; then
    OLD_PID_FILE="${APP_DIR}/${CURRENT_PORT}/app.pid"
    if [[ -f "${OLD_PID_FILE}" ]] && kill -0 "$(cat "${OLD_PID_FILE}")" 2>/dev/null; then
        OLD_PID="$(cat "${OLD_PID_FILE}")"
        log "구버전(포트 ${CURRENT_PORT}, PID ${OLD_PID}) 종료"
        kill "${OLD_PID}" 2>/dev/null || true
        sleep "${STOP_WAIT}"
        kill -9 "${OLD_PID}" 2>/dev/null || true
    fi
fi

log "배포 완료 ✅ (active 포트: ${TARGET_PORT})"
