package com.jhj.schedule.user;

import com.jhj.schedule.jooq.tables.records.UsersRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.jhj.schedule.jooq.Tables.USERS;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final DSLContext dsl;

    public boolean existsByEmail(String email) {
        return dsl.fetchExists(USERS, USERS.EMAIL.eq(email));
    }

    public Optional<User> findByEmail(String email) {
        return dsl.selectFrom(USERS)
                .where(USERS.EMAIL.eq(email))
                .fetchOptional()
                .map(UserRepository::toDomain);
    }

    public Optional<User> findById(Long id) {
        return dsl.selectFrom(USERS)
                .where(USERS.ID.eq(id))
                .fetchOptional()
                .map(UserRepository::toDomain);
    }

    public User insert(User user) {
        LocalDateTime now = LocalDateTime.now();

        Long id = dsl.insertInto(USERS)
                .set(USERS.NAME, user.getName())
                .set(USERS.EMAIL, user.getEmail())
                .set(USERS.PASSWORD, user.getPassword())
                .set(USERS.AVATAR, user.getAvatar())
                .set(USERS.AUTHORITY, user.getAuthority().name())
                .set(USERS.CREATED_AT, now)
                .set(USERS.UPDATED_AT, now)
                .returningResult(USERS.ID)
                .fetchOne()
                .value1();

        user.setId(id);
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        return user;
    }

    public User update(User user) {
        LocalDateTime now = LocalDateTime.now();

        dsl.update(USERS)
                .set(USERS.NAME, user.getName())
                .set(USERS.EMAIL, user.getEmail())
                .set(USERS.PASSWORD, user.getPassword())
                .set(USERS.AVATAR, user.getAvatar())
                .set(USERS.AUTHORITY, user.getAuthority().name())
                .set(USERS.UPDATED_AT, now)
                .where(USERS.ID.eq(user.getId()))
                .execute();
        user.setUpdatedAt(now);

        return user;
    }

    private static User toDomain(UsersRecord record) {
        return User.builder()
                .id(record.getId())
                .name(record.getName())
                .email(record.getEmail())
                .password(record.getPassword())
                .authority(Authority.valueOf(record.getAuthority()))
                .avatar(record.getAvatar())
                .updatedAt(record.getUpdatedAt())
                .createdAt(record.getCreatedAt())
                .build();
    }
}