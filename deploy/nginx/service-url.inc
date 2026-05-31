# 현재 트래픽이 향하는 백엔드 포트. deploy.sh 가 배포 시 이 한 줄을 갱신한다.
# 최초 배포 전 초기값(blue). 배포 후에는 deploy.sh 가 active 포트로 덮어쓴다.
set $service_url http://127.0.0.1:8081;
