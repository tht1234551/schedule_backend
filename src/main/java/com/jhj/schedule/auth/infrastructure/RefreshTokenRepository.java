package com.jhj.schedule.auth.infrastructure;

import com.jhj.schedule.auth.domain.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.jhj.schedule.jooq.Sequences.REFRESH_TOKENS_SEQ;
import static com.jhj.schedule.jooq.Tables.REFRESH_TOKENS;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepository {

    private final DSLContext dsl;

    public Optional<RefreshToken> findByToken(String token) {
        return dsl.selectFrom(REFRESH_TOKENS)
                .where(REFRESH_TOKENS.TOKEN.eq(token))
                .fetchOptional()
                .map(this::toDomain);
    }

    public void deleteByUserId(Long userId) {
        dsl.deleteFrom(REFRESH_TOKENS)
                .where(REFRESH_TOKENS.USER_ID.eq(userId))
                .execute();
    }

    public void deleteByToken(String token) {
        dsl.deleteFrom(REFRESH_TOKENS)
                .where(REFRESH_TOKENS.TOKEN.eq(token))
                .execute();
    }

    public RefreshToken insert(RefreshToken refreshToken) {
        LocalDateTime now = LocalDateTime.now();

        Long id = dsl.nextval(REFRESH_TOKENS_SEQ);
        dsl.insertInto(REFRESH_TOKENS)
                .set(REFRESH_TOKENS.ID, id)
                .set(REFRESH_TOKENS.USER_ID, refreshToken.getUserId())
                .set(REFRESH_TOKENS.TOKEN, refreshToken.getToken())
                .set(REFRESH_TOKENS.EXPIRY_DATE, refreshToken.getExpiryDate())
                .set(REFRESH_TOKENS.CREATED_AT, now)
                .set(REFRESH_TOKENS.UPDATED_AT, now)
                .execute();

        refreshToken.setId(id);
        refreshToken.setCreatedAt(now);
        refreshToken.setUpdatedAt(now);

        return refreshToken;
    }

    private RefreshToken toDomain(Record r) {
        return RefreshToken.builder()
                .id(r.get(REFRESH_TOKENS.ID))
                .userId(r.get(REFRESH_TOKENS.USER_ID))
                .token(r.get(REFRESH_TOKENS.TOKEN))
                .expiryDate(r.get(REFRESH_TOKENS.EXPIRY_DATE))
                .createdAt(r.get(REFRESH_TOKENS.CREATED_AT))
                .updatedAt(r.get(REFRESH_TOKENS.UPDATED_AT))
                .build();
    }
}