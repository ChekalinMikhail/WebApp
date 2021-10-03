package org.example.app.repository;

import lombok.RequiredArgsConstructor;
import org.example.app.domain.*;
import org.example.jdbc.JdbcTemplate;
import org.example.jdbc.RowMapper;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class UserRepository {
    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<User> rowMapper = resultSet -> new User(
            resultSet.getLong("id"),
            resultSet.getString("username")
    );

    private final RowMapper<UserWithPassword> rowMapperWithPassword = resultSet -> new UserWithPassword(
            resultSet.getLong("id"),
            resultSet.getString("username"),
            resultSet.getString("password")
    );

    private final RowMapper<UserRole> rowMapperUserRole = resultSet -> new UserRole(
            resultSet.getLong("userId"),
            resultSet.getLong("role")
    );

    private final RowMapper<PasswordRecoveryKey> rowMapperPasswordRecoveryKey = resultSet -> new PasswordRecoveryKey(
            resultSet.getString("userId"),
            resultSet.getString("key")
    );

    private final RowMapper<Token> rowMapperToken = resultSet -> new Token(
            resultSet.getString("token"),
            resultSet.getTimestamp("created")
    );

    public Optional<User> getByUsername(String username) {
        // language=PostgreSQL
        return jdbcTemplate.queryOne("SELECT id, username FROM users WHERE username = ?", rowMapper, username);
    }

    public Optional<UserWithPassword> getByUsernameWithPassword(String username) {
        // language=PostgreSQL
        return jdbcTemplate.queryOne("SELECT id, username, password FROM users WHERE username = ?", rowMapperWithPassword, username);
    }

    /**
     * saves user to db
     *
     * @param id       - user id, if 0 - insert, if not 0 - update
     * @param username -
     * @param hash -
     */
    public Optional<User> userRegister(long id, String username, String hash) {
        // language=PostgreSQL
        return id == 0 ? jdbcTemplate.queryOne(
                """
                        INSERT INTO users(username, password) VALUES (?, ?) RETURNING id, username
                        """,
                rowMapper,
                username, hash
        ) : jdbcTemplate.queryOne(
                """
                        UPDATE users SET username = ?, password = ? WHERE id = ? RETURNING id, username
                        """,
                rowMapper,
                username, hash, id
        );
    }

    public void setRole(long userId) {
        // language=PostgreSQL
        jdbcTemplate.update(
                """
                        INSERT INTO roles("userId") VALUES (?)
                        """,
                userId
        );
    }

    public List<UserRole> getRoles(long userId) {
        // language=PostgreSQL
        return jdbcTemplate.queryAll(
                """
                        SELECT "userId", role FROM roles WHERE "userId" = ?
                        """,
                rowMapperUserRole,
                userId
        );
    }

    public Optional<Token> findToken(String token) {
        // language=PostgreSQL
        return jdbcTemplate.queryOne(
                """
                        SELECT token, created FROM tokens WHERE token = ?
                        """,
                rowMapperToken,
                token
        );
    }

    public Optional<User> findUserByToken(String token) {
        // language=PostgreSQL
        return jdbcTemplate.queryOne(
                """
                        SELECT u.id, u.username, t.token, t.created FROM tokens t
                        JOIN users u ON t."userId" = u.id
                        WHERE t.token = ?
                        """,
                rowMapper,
                token
        );
    }

    public Optional<User> findUserByLogin(String login) {
        // language=PostgreSQL
        return jdbcTemplate.queryOne(
                """
                        SELECT u.id, u.username, t.token, t.created FROM tokens t
                        JOIN users u ON t."userId" = u.id
                        WHERE u.username = ?
                        """,
                rowMapper,
                login
        );
    }

    public void saveToken(long userId, String token) {
        // language=PostgreSQL
        jdbcTemplate.update(
                """
                        INSERT INTO tokens(token, "userId") VALUES (?, ?)
                        """,
                token,
                userId
        );
    }

    public void saveNewToken(long userId, String token) {
        // language=PostgreSQL
        jdbcTemplate.update(
                """
                        DELETE FROM tokens WHERE "userId" = ?
                        """,
                userId
        );
        // language=PostgreSQL
        jdbcTemplate.update(
                """
                        INSERT INTO tokens(token, "userId") VALUES (?, ?)
                        """,
                token,
                userId
        );
    }

    public void updateTokenLifeTime(String token) {
        // language=PostgreSQL
        jdbcTemplate.update(
                """
                        UPDATE tokens SET created = current_timestamp WHERE token = ?
                        """,
                token
        );
    }

    public void createKeyForPasswordRecovery(long userId, String key) {
        // language=PostgreSQL
        jdbcTemplate.update(
                """
                        DELETE FROM password_recovery_keys WHERE "userId" = ?
                        """,
                userId
        );
        // language=PostgreSQL
        jdbcTemplate.queryOne(
                """
                        INSERT INTO password_recovery_keys("userId", key) VALUES (?, ?) RETURNING "userId", key
                        """,
                rowMapperPasswordRecoveryKey,
                userId,
                key
        );
    }

    public Optional<PasswordRecoveryKey> checkPasswordRecoveryKey(long userId) {
        // language=PostgreSQL
        return jdbcTemplate.queryOne(
                """
                        SELECT "userId", key FROM password_recovery_keys WHERE "userId" = ?
                        """,
                rowMapperPasswordRecoveryKey,
                userId
        );
    }

    public void deleteUsedKeyForPasswordRecovery(long userId) {
        // language=PostgreSQL
        jdbcTemplate.update(
                """
                        DELETE FROM password_recovery_keys WHERE "userId" = ?
                        """,
                userId
        );
    }

}
