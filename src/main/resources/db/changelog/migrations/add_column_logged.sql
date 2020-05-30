--liquibase formatted sql

--changeset rinat-s:add column logged
ALTER TABLE users add column is_logged boolean default false;