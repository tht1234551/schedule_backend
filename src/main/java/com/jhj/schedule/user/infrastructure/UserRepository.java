package com.jhj.schedule.user.infrastructure;

import com.jhj.schedule.user.domain.Authority;
import com.jhj.schedule.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record;
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

    public void updatePassword(User user) {
        LocalDateTime now = LocalDateTime.now();

        dsl.update(USERS)
                .set(USERS.PASSWORD, user.getPassword())
                .set(USERS.UPDATED_AT, now)
                .where(USERS.ID.eq(user.getId()))
                .execute();
    }

    private static User toDomain(Record r) {
        return User.builder()
                .id(r.get(USERS.ID))
                .name(r.get(USERS.NAME))
                .email(r.get(USERS.EMAIL))
                .password(r.get(USERS.PASSWORD))
                .authority(Authority.valueOf(r.get(USERS.AUTHORITY)))
                .avatar(r.get(USERS.AVATAR))
                .createdAt(r.get(USERS.CREATED_AT))
                .updatedAt(r.get(USERS.UPDATED_AT))
                .build();
    }
}