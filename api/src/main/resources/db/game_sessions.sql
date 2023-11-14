--liquibase formatted sql

--changeset higherlower:game_sessions
create table if not exists game_sessions(
    id                   uuid primary key,
    userId               uuid not null,
    score                integer default 0,
    finishedAt           timestamptz,
    category             text,
    createdAt            timestamptz not null default current_timestamp
);

--changeset higherlower:game_rounds
create table game_rounds(
    id                   uuid primary key default uuid_generate_v4(),
    gameSessionId        uuid not null references game_sessions(id) on delete cascade,
    currentMovieId       uuid not null references movies(id) on delete cascade,
    nextMovieId          uuid not null references movies(id) on delete cascade,
    state                text not null default 'PENDING',
    createdAt            timestamptz not null default current_timestamp
);

--changeset higherlower:game_sessions-drop-score
alter table game_sessions drop score;

--changeset higherlower:game_sessions-hard-difficulty
alter table game_sessions add column hard boolean not null default false;
