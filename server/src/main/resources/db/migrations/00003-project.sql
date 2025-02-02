--liquibase formatted sql

--changeset demoneach:1
--comment: create tables for projects
CREATE EXTENSION IF NOT EXISTS "uuid-ossp"; -- for UUID autogen

CREATE TABLE IF NOT EXISTS project
(
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    owner INT REFERENCES account(id) NOT NULL,
    name TEXT,
    code VARCHAR(10),
    description TEXT,
    creation_time TIMESTAMPTZ,
    update_time TIMESTAMPTZ
);

CREATE TABLE IF NOT EXISTS project_permission
(
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4() ,
    project_id UUID REFERENCES project(id) NOT NULL,
    account_id INT REFERENCES account(id) NOT NULL,
    permission TEXT,
    creation_time TIMESTAMPTZ,
    update_time TIMESTAMPTZ
);