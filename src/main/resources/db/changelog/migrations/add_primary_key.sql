--liquibase formatted sql

--changeset rinat-s:add primary
ALTER TABLE game add primary key (id);