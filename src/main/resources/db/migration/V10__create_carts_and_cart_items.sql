create table carts
(
    id           binary(16) default (uuid_to_bin(uuid())) not null
        primary key,
    date_created date       default (curdate())           not null
);

