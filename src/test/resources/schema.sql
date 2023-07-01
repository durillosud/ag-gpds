create schema if not exists ag_cap_gpds;

drop table if exists ag_cap_gpds.atividade_projeto_cliente;
drop table if exists ag_cap_gpds.atividades;
drop table if exists ag_cap_gpds.clientes;
drop table if exists ag_cap_gpds.projetos;
drop table if exists ag_cap_gpds.time;
drop table if exists ag_cap_gpds.status;

create table if not exists ag_cap_gpds.clientes
(
    id   bigserial,
    nome varchar not null,
    constraint clientes_pk
        primary key (id)
);

comment on table ag_cap_gpds.clientes is 'Contém dados de clientes';

-- alter table ag_cap_gpds.clientes
--     owner to gpds;

create table if not exists ag_cap_gpds.status
(
    id        bigserial,
    descricao varchar not null,
    constraint status_pk
        primary key (id)
);

comment on table ag_cap_gpds.status is 'Contém status de projetos e atividades de projetos';

-- alter table ag_cap_gpds.status
--     owner to gpds;

create table if not exists ag_cap_gpds.atividades
(
    id          bigserial,
    descricao   varchar not null,
    status      bigint,
    percentagem numeric(3, 1),
    constraint atividades_pk
        primary key (id),
    constraint atividades_status_id_fk
        foreign key (status) references ag_cap_gpds.status
);

comment on table ag_cap_gpds.atividades is 'Contém atividades de projetos';

-- alter table ag_cap_gpds.atividades
--     owner to gpds;

create table if not exists ag_cap_gpds.time
(
    nome varchar not null,
    id   bigserial,
    constraint time_pk
        primary key (id)
);

comment on table ag_cap_gpds.time is 'Contém rol de times de desenvolvimento';

-- alter table ag_cap_gpds.time
--     owner to gpds;

create table if not exists ag_cap_gpds.projetos
(
    id        bigserial,
    descricao varchar not null,
    status    bigint,
    time      bigint  not null,
    constraint projetos_pk
        primary key (id),
    constraint projetos_status_id_fk
        foreign key (status) references ag_cap_gpds.status,
    constraint projetos_time_id_fk
        foreign key (time) references ag_cap_gpds.time
);

comment on table ag_cap_gpds.projetos is 'Contém projetos dos clientes';

-- alter table ag_cap_gpds.projetos
--     owner to gpds;

create table if not exists ag_cap_gpds.atividade_projeto_cliente
(
    cliente   bigint not null,
    projeto   bigint not null,
    atividade bigint not null,
    id        bigserial,
    constraint atividade_projeto_cliente_pk
        primary key (id),
    constraint atividade_projeto_cliente_clientes_id_fk
        foreign key (cliente) references ag_cap_gpds.clientes,
    constraint atividade_projeto_cliente_projetos_id_fk
        foreign key (projeto) references ag_cap_gpds.projetos,
    constraint atividade_projeto_cliente_atividades_id_fk
        foreign key (atividade) references ag_cap_gpds.atividades
);

comment on table ag_cap_gpds.atividade_projeto_cliente is 'Contém a associação de atividades de projetos de clientes';

-- alter table ag_cap_gpds.atividade_projeto_cliente
--     owner to gpds;

insert into ag_cap_gpds.status (id, descricao)
values (default, 'Em análise'),
       (default, 'Em implementação'),
       (default, 'Em implantação'),
       (default, 'Em suporte'),
       (default, 'Finalizado');

insert into ag_cap_gpds.time (nome, id)
values ('Time 1', default),
       ('Time 2', default),
       ('Time 3', default);
--============================================================
--
-- create schema if not exists ag_cap_gpds;
--
--
-- create table if not exists ag_cap_gpds.status
-- (
--     id        bigserial  not null
--     constraint status_pk
--     primary key,
--     descricao varchar not null
-- );
--
-- alter table ag_cap_gpds.status
--     owner to gpds;
--
-- comment on table ag_cap_gpds.status is 'Contém status de projetos e atividades de projetos';
--
-- create table if not exists ag_cap_gpds.clientes
-- (
--     id   bigserial  not null
--         constraint clientes_pk
--             primary key,
--     nome varchar not null
-- );
--
-- comment on table ag_cap_gpds.clientes is 'Contém dados de clientes';
--
-- alter table ag_cap_gpds.clientes
--     owner to gpds;
--
-- create table if not exists ag_cap_gpds.projetos
-- (
--     id        bigserial  not null
--         constraint projetos_pk
--             primary key,
--     descricao varchar not null,
--     cliente   bigint
--         constraint projetos_clientes_id_fk
--             references ag_cap_gpds.clientes,
--     status    bigint
--         constraint projetos_status_id_fk
--             references ag_cap_gpds.status
-- );
--
-- comment on table ag_cap_gpds.projetos is 'Contém projetos dos clientes';
--
-- alter table ag_cap_gpds.projetos
--     owner to gpds;
--
-- create table if not exists ag_cap_gpds.atividades
-- (
--     id          bigserial  not null
--         constraint atividades_pk
--             primary key,
--     descricao   varchar not null,
--     projeto     bigint
--         constraint atividades_projetos_id_fk
--             references ag_cap_gpds.projetos,
--     status      bigint
--         constraint atividades_status_id_fk
--             references ag_cap_gpds.status,
--     percentagem numeric(3, 1)
-- );
--
-- comment on table ag_cap_gpds.atividades is 'Contém atividades de projetos';
--
-- alter table ag_cap_gpds.atividades
--     owner to gpds;
--
-- insert into ag_cap_gpds.status (id, descricao)
-- values (default, 'Em análise'),
--        (default, 'Em implementação'),
--        (default, 'Em implantação'),
--        (default, 'Em suporte');
