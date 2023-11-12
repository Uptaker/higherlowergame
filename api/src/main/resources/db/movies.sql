--liquibase formatted sql

--changeset higherlower:movies
create table if not exists movies(
    id                   uuid primary key default uuid_generate_v4(),
    original_title       varchar(86) not null,
    overview             varchar(1000) not null,
    popularity           numeric(10,6) not null,
    release_date         date  not null,
    revenue              bigint  not null,
    runtime              numeric(5,1) not null,
    tagline              varchar(252) null,
    title                varchar(86) not null,
    vote_average         numeric(4,1) not null,
    vote_count           integer  not null
);