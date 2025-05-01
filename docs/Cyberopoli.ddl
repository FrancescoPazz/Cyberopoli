-- *********************************************
-- * Cyberopoli Database DDL (PostgreSQL)      
-- *********************************************

DROP TABLE IF EXISTS users           CASCADE;
DROP TABLE IF EXISTS lobbies         CASCADE;
DROP TABLE IF EXISTS lobby_members   CASCADE;
DROP TABLE IF EXISTS games           CASCADE;
DROP TABLE IF EXISTS game_players    CASCADE;
DROP TABLE IF EXISTS game_events     CASCADE;

-- Tabella utenti
CREATE TABLE users (
    id            UUID     PRIMARY KEY DEFAULT auth.uid(),
    name          TEXT,
    surname       TEXT,
    username      TEXT             NOT NULL UNIQUE,
    email         TEXT             UNIQUE,
    is_guest      BOOLEAN          NOT NULL DEFAULT FALSE,
    avatar_url    TEXT,
    level         INTEGER         NOT NULL DEFAULT 1,
    total_score   INTEGER          NOT NULL DEFAULT 0,
    total_games   INTEGER          NOT NULL DEFAULT 0,
    total_wins    INTEGER          NOT NULL DEFAULT 0,
    total_medals  INTEGER          NOT NULL DEFAULT 0,
    created_at    TIMESTAMP        NOT NULL DEFAULT NOW()
);

-- Tabella lobby
CREATE TABLE lobbies (
    id         UUID   PRIMARY KEY DEFAULT uuid_generate_v4(),
    host_id    UUID          NOT NULL,
    status     TEXT          NOT NULL,
    created_at TIMESTAMP     NOT NULL DEFAULT NOW(),
    FOREIGN KEY (host_id) REFERENCES users(id) ON DELETE RESTRICT
);

-- Associativa tra utenti e lobby
CREATE TABLE lobby_members (
    lobby_id   UUID          NOT NULL,
    user_id    UUID          NOT NULL,
    ready      BOOLEAN       NOT NULL DEFAULT FALSE,
    joined_at  TIMESTAMP     NOT NULL DEFAULT NOW(),
    PRIMARY KEY (lobby_id, user_id),
    FOREIGN KEY (lobby_id) REFERENCES lobbies(id)   ON DELETE CASCADE,
    FOREIGN KEY (user_id)  REFERENCES users(id)     ON DELETE CASCADE
);

-- Tabella partite (games)
CREATE TABLE games (
    lobby_id  UUID          NOT NULL,
    id        UUID          NOT NULL DEFAULT uuid_generate_v4(),
    turn      INTEGER       NOT NULL,
    PRIMARY KEY (lobby_id, id),
    FOREIGN KEY (lobby_id) REFERENCES lobbies(id) ON DELETE CASCADE
);

-- Giocatori in ciascuna partita
CREATE TABLE game_players (
    lobby_id  UUID          NOT NULL,
    game_id  UUID           NOT NULL,
    user_id   UUID          NOT NULL,
    score     INTEGER       NOT NULL DEFAULT 0,
    PRIMARY KEY (lobby_id, game_id, user_id),
    FOREIGN KEY (lobby_id, game_id)
      REFERENCES games(lobby_id, id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id)     ON DELETE CASCADE
);

-- Eventi generati durante le partite
CREATE TABLE game_events (
    lobby_id    UUID          NOT NULL,
    game_id    UUID          NOT NULL,
    user_id     UUID          NOT NULL,
    event_type  TEXT          NOT NULL,
    value       INTEGER,
    created_at  TIMESTAMP     NOT NULL DEFAULT NOW(),
    PRIMARY KEY (lobby_id, game_id, user_id, event_type, created_at),
    FOREIGN KEY (lobby_id, game_id)
      REFERENCES games(lobby_id, id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id)     ON DELETE CASCADE
);