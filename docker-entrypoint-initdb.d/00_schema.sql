CREATE TABLE users
(
    id       BIGSERIAL PRIMARY KEY,
    username TEXT        NOT NULL UNIQUE,
    password TEXT        NOT NULL,
    active   BOOLEAN     NOT NULL DEFAULT TRUE,
    created  timestamptz NOT NULL DEFAULT current_timestamp
);

CREATE TABLE tokens
(
    token    TEXT PRIMARY KEY,
    "userId" BIGINT      NOT NULL REFERENCES users,
    created  timestamptz NOT NULL DEFAULT current_timestamp
);

CREATE TABLE roles
(
    "userId" BIGINT NOT NULL REFERENCES users,
    role     BIGINT NOT NULL DEFAULT 2 CHECK ( role < 3 AND role > 0)
);

CREATE TABLE cards
(
    id        BIGSERIAL PRIMARY KEY,
    "ownerId" BIGINT  NOT NULL REFERENCES users,
    number    TEXT    NOT NULL,
    balance   BIGINT  NOT NULL DEFAULT 0,
    active    BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE transactions
(
    id             BIGSERIAL PRIMARY KEY,
    senderCardId   BIGINT NOT NULL REFERENCES cards,
    receiverCardId BIGINT NOT NULL REFERENCES cards,
    transferAmount BIGINT NOT NULL
);

CREATE TABLE password_recovery_keys
(
    "userId" BIGINT      NOT NULL REFERENCES users,
    key      TEXT        NOT NULL,
    created  timestamptz NOT NULL DEFAULT current_timestamp
);
