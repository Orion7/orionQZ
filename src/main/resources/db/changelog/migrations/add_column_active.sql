--liquibase formatted sql

--changeset rinat-s:add column active
ALTER TABLE question add column active boolean default false;