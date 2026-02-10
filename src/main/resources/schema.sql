drop table if exists refresh_tokens cascade;
drop table if exists users cascade;
drop table if exists participated cascade;
drop table if exists game_rooms cascade;
drop table if exists played cascade;
drop table if exists games cascade;

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

create table if not exists games(
    id BIGINT primary key,
    started_at TIMESTAMP not null,
    ended_at TIMESTAMP not null
);

create table if not exists played(
    id BIGINT primary key,
    game_id BIGINT references games(id) on delete cascade,
    user_id BIGINT references users(id) on delete set null,
    score INT,
    game_result VARCHAR(255) not null
);

create table if not exists game_rooms(
    id BIGINT primary key,
    status varchar(255) not null,
    version BIGINT default 0,
    created_at TIMESTAMP not null,
    updated_at TIMESTAMP not null,
    host_id BIGINT references users(id) on delete cascade
);

create table if not exists participated(
    id BIGINT primary key,
    game_room_id BIGINT references game_rooms(id) on delete cascade ,
    created_at TIMESTAMP not null,
    updated_at TIMESTAMP not null,
    user_id BIGINT references users(id) on delete cascade
);