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