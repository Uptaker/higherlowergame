--liquibase formatted sql

--changeset higherlower:uuid-extension
create extension if not exists "uuid-ossp";