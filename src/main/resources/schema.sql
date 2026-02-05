drop table if exists refresh_tokens cascade;
drop table if exists users cascade;


create table if not exists users(
    id BIGINT auto_increment primary key,
    login_id varchar(50) not null unique,
    password varchar(255) not null,
    nickname varchar(50) not null unique,
    created_at TIMESTAMP not null,
    updated_at TIMESTAMP not null,
    role varchar(255) not null
);

create index users_login_id_index on users(login_id);
create index users_nickname_id_index on users(nickname);

create table if not exists refresh_tokens(
    id BINARY(32) primary key,
    valid_until datetime not null,
    user_id BIGINT not null,
    foreign key (user_id) references users(id) on delete cascade
);
