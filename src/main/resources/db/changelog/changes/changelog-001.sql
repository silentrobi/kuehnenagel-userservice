--liquibase formatted sql

--changeset silentrobi:001

create extension if not exists "uuid-ossp";
--rollback drop extension if exists "uuid-ossp";
create table users (
                        id  uuid  not null DEFAULT uuid_generate_v1(),
                        name varchar(255) not null,
                        email varchar(255) not null,
                        age int,
                        phone_number varchar(255)
);

--rollback drop table if exists users;

alter table users add constraint PK_users primary key (id);