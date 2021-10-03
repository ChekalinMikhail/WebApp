package org.example.app.repository;

import lombok.RequiredArgsConstructor;
import org.example.app.domain.Card;
import org.example.jdbc.JdbcTemplate;
import org.example.jdbc.RowMapper;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class CardRepository {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Card> cardRowMapper = resultSet -> new Card(
            resultSet.getLong("id"),
            resultSet.getString("number"),
            resultSet.getLong("balance")
    );

    public Optional<Card> receiveMoney(long receiverCardId, long transferAmount) {
        // language=PostgreSQL
        return jdbcTemplate.queryOne(
                """
                        UPDATE cards SET balance = balance + ? WHERE id = ? AND active = TRUE RETURNING id, number, balance
                        """,
                cardRowMapper,
                transferAmount,
                receiverCardId
        );
    }

    public Optional<Card> sendMoney(long senderCardId, long transferAmount) {
        // language=PostgreSQL
        return jdbcTemplate.queryOne(
                """
                        UPDATE cards SET balance = balance - ? WHERE id = ? AND active = TRUE RETURNING id, number, balance
                        """,
                cardRowMapper,
                transferAmount,
                senderCardId
        );
    }

    public void registerTransaction(long senderCardId, long receiverCardId, long transferAmount) {
        // language=PostgreSQL
        jdbcTemplate.update(
                """
                        INSERT INTO transactions("senderCardId", "receiverCardId", "transferAmount") VALUES (?, ? , ?)
                        """,
                senderCardId,
                receiverCardId,
                transferAmount
                );
    }

    public List<Card> getAllByOwnerId(long ownerId) {
        // language=PostgreSQL
        return jdbcTemplate.queryAll(
                "SELECT id, number, balance FROM cards WHERE \"ownerId\" = ? AND active = TRUE",
                cardRowMapper,
                ownerId
        );
    }

    public Optional<Card> getCardByCardId(long cardId) {
        // language=PostgreSQL
        return jdbcTemplate.queryOne(
                "SELECT id, number, balance FROM cards WHERE id = ? AND active = TRUE",
                cardRowMapper,
                cardId
        );
    }

    public Optional<Card> orderCard(long ownerId, String cardNumber) {
        // language=PostgreSQL
        return jdbcTemplate.queryOne(
                "INSERT INTO cards(\"ownerId\", number) VALUES (?, ?) RETURNING id, number, balance",
                cardRowMapper,
                ownerId,
                cardNumber
        );
    }

    public Optional<Card> blockById(long cardId) {
        // language=PostgreSQL
        return jdbcTemplate.queryOne(
                """
                        UPDATE cards SET active = ? WHERE id = ? RETURNING id, number, balance
                        """,
                cardRowMapper,
                false,
                cardId
        );
    }
}
