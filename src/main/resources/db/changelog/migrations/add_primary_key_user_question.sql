--liquibase formatted sql

--changeset rinat-s:add primary to user and questions
ALTER TABLE users add primary key (id);
ALTER TABLE question add primary key (id);

CREATE TABLE answer (id serial,
answer varchar(1024),
question_id integer NOT NULL,
user_id integer NOT NULL,
date timestamp,
processed boolean);