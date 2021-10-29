--liquibase formatted sql

--changeset silentrobi:002

alter table users add column created_at timestamp;
--rollback alter table users drop column created_at;

alter table users add column updated_at timestamp;
--rollback alter table users drop column updated_at;