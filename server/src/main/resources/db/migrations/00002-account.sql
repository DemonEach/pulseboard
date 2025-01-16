--liquibase formatted sql

--changeset bbfce:1
--comment: create tables for users

CREATE TABLE IF NOT EXISTS account
(
    id SERIAL PRIMARY KEY,
    username TEXT,
    password TEXT,
    email TEXT,
    name TEXT,
    phone_number TEXT,
    organization TEXT,
    enabled TEXT,
    creation_time TIMESTAMPTZ,
    update_time TIMESTAMPTZ
);
