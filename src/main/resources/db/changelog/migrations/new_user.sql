--liquibase formatted sql

--changeset rinat-s:add user2
insert into users (name) values ('user2');
