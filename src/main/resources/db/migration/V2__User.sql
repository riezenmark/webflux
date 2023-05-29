create table usr
(
    id       bigserial primary key,
    username varchar(64),
    password varchar(128),
    role     varchar(64)
);
