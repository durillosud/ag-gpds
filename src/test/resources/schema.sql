-- create schema if not exists ag_cap_gpds;
--
-- drop table if exists ag_cap_gpds.atividade_projeto_cliente;
-- drop table if exists ag_cap_gpds.atividades;
-- drop table if exists ag_cap_gpds.clientes;
-- drop table if exists ag_cap_gpds.projetos;
-- drop table if exists ag_cap_gpds.time;
-- drop table if exists ag_cap_gpds.status;
--
-- create table if not exists ag_cap_gpds.clientes
-- (
--     id   bigint  identity not null primary key,
--     nome varchar not null
-- );
--
-- comment on table ag_cap_gpds.clientes is 'Contém dados de clientes';
--
-- -- alter table ag_cap_gpds.clientes
-- --     owner to gpds;
--
-- create table if not exists ag_cap_gpds.status
-- (
--     id        bigint identity not null primary key,
--     descricao varchar not null
-- );
--
-- comment on table ag_cap_gpds.status is 'Contém status de projetos e atividades de projetos';
--
-- -- alter table ag_cap_gpds.status
-- --     owner to gpds;
--
-- create table if not exists ag_cap_gpds.atividades
-- (
--     id          bigint  identity not null primary key,
--     descricao   varchar not null,
--     status      bigint,
--     percentagem numeric(3, 1),
--     constraint atividades_status_id_fk
--         foreign key (status) references ag_cap_gpds.status
-- );
--
-- comment on table ag_cap_gpds.atividades is 'Contém atividades de projetos';
--
-- -- alter table ag_cap_gpds.atividades
-- --     owner to gpds;
--
-- create table if not exists ag_cap_gpds.time
-- (
--     nome varchar not null  identity not null primary key,
--     id   bigint
-- );
--
-- comment on table ag_cap_gpds.time is 'Contém rol de times de desenvolvimento';
--
-- -- alter table ag_cap_gpds.time
-- --     owner to gpds;
--
-- create table if not exists ag_cap_gpds.projetos
-- (
--     id        bigint  identity not null primary key,
--     descricao varchar not null,
--     status    bigint,
--     time      bigint  not null,
--     constraint projetos_status_id_fk
--         foreign key (status) references ag_cap_gpds.status,
--     constraint projetos_time_id_fk
--         foreign key (time) references ag_cap_gpds.time
-- );
--
-- comment on table ag_cap_gpds.projetos is 'Contém projetos dos clientes';
--
-- -- alter table ag_cap_gpds.projetos
-- --     owner to gpds;
--
-- create table if not exists ag_cap_gpds.atividade_projeto_cliente
-- (
--     cliente   bigint not null,
--     projeto   bigint not null,
--     atividade bigint not null,
--     id        bigint  identity not null primary key,
--     constraint atividade_projeto_cliente_clientes_id_fk
--         foreign key (cliente) references ag_cap_gpds.clientes,
--     constraint atividade_projeto_cliente_projetos_id_fk
--         foreign key (projeto) references ag_cap_gpds.projetos,
--     constraint atividade_projeto_cliente_atividades_id_fk
--         foreign key (atividade) references ag_cap_gpds.atividades
-- );
--
-- comment on table ag_cap_gpds.atividade_projeto_cliente is 'Contém a associação de atividades de projetos de clientes';
--
-- -- alter table ag_cap_gpds.atividade_projeto_cliente
-- --     owner to gpds;
--
-- insert into ag_cap_gpds.status (descricao)
-- values ('Em análise'),
--        ('Em implementação'),
--        ('Em implantação'),
--        ('Em suporte'),
--        ('Finalizado');
--
-- insert into ag_cap_gpds.time (nome)
-- values ('Time 1'),
--        ('Time 2'),
--        ('Time 3');
-- --============================================================
-- alter table if exists AG_CAP_GPDS.ATIVIDADE_PROJETO_CLIENTE set referential_integrity false;
-- alter table if exists AG_CAP_GPDS.ATIVIDADES set referential_integrity false;
-- alter table if exists AG_CAP_GPDS.CLIENTES set referential_integrity false;
-- alter table if exists AG_CAP_GPDS.PROJETOS set referential_integrity false;
-- alter table if exists AG_CAP_GPDS.TIME set referential_integrity false;
-- alter table if exists AG_CAP_GPDS.STATUS set referential_integrity false;
--
-- drop table if exists AG_CAP_GPDS.ATIVIDADE_PROJETO_CLIENTE;
-- drop table if exists AG_CAP_GPDS.ATIVIDADES;
-- drop table if exists AG_CAP_GPDS.CLIENTES;
-- drop table if exists AG_CAP_GPDS.PROJETOS;
-- drop table if exists AG_CAP_GPDS.TIME;
-- drop table if exists AG_CAP_GPDS.STATUS;
--
-- drop schema if exists AG_CAP_GPDS;

-- create schema AG_CAP_GPDS;
create table CLIENTES
(
    ID   BIGINT,-- auto_increment,
    NOME CHARACTER VARYING not null,
    constraint CLIENTES_PK
        primary key (ID)
);

comment on table CLIENTES is 'Contém dados de clientes';

create table STATUS
(
    ID        BIGINT,-- auto_increment,
    DESCRICAO CHARACTER VARYING not null,
    constraint STATUS_PK
        primary key (ID)
);

comment on table STATUS is 'Contém status de projetos e atividades de projetos';

create table ATIVIDADES
(
    ID          BIGINT,-- auto_increment,
    DESCRICAO   CHARACTER VARYING not null,
    STATUS      BIGINT,
    PERCENTAGEM NUMERIC(3, 1),
    constraint ATIVIDADES_PK
        primary key (ID),
    constraint ATIVIDADES_STATUS_ID_FK
        foreign key (STATUS) references STATUS
);

comment on table ATIVIDADES is 'Contém atividades de projetos';

create table TIME
(
    NOME CHARACTER VARYING not null,
    ID   BIGINT,-- auto_increment,
    constraint TIME_PK
        primary key (ID)
);

comment on table TIME is 'Contém rol de times de desenvolvimento';

create table PROJETOS
(
    ID        BIGINT,-- auto_increment,
    DESCRICAO CHARACTER VARYING not null,
    STATUS    BIGINT,
    TIME      BIGINT            not null,
    constraint PROJETOS_PK
        primary key (ID),
    constraint PROJETOS_STATUS_ID_FK
        foreign key (STATUS) references STATUS,
    constraint PROJETOS_TIME_ID_FK
        foreign key (TIME) references TIME
);

comment on table PROJETOS is 'Contém projetos dos clientes';

create table ATIVIDADE_PROJETO_CLIENTE
(
    CLIENTE   BIGINT not null,
    PROJETO   BIGINT not null,
    ATIVIDADE BIGINT not null,
    ID        BIGINT,-- auto_increment,
    constraint ATIVIDADE_PROJETO_CLIENTE_PK
        primary key (ID),
    constraint ATIVIDADE_PROJETO_CLIENTE_ATIVIDADES_ID_FK
        foreign key (ATIVIDADE) references ATIVIDADES,
    constraint ATIVIDADE_PROJETO_CLIENTE_CLIENTES_ID_FK
        foreign key (CLIENTE) references CLIENTES,
    constraint ATIVIDADE_PROJETO_CLIENTE_PROJETOS_ID_FK
        foreign key (PROJETO) references PROJETOS
);

comment on table ATIVIDADE_PROJETO_CLIENTE is 'Contém a associação de atividades de projetos de clientes';
--
-- alter table if exists AG_CAP_GPDS.ATIVIDADE_PROJETO_CLIENTE set referential_integrity true;
-- alter table if exists AG_CAP_GPDS.ATIVIDADES set referential_integrity true;
-- alter table if exists AG_CAP_GPDS.CLIENTES set referential_integrity true;
-- alter table if exists AG_CAP_GPDS.PROJETOS set referential_integrity true;
-- alter table if exists AG_CAP_GPDS.TIME set referential_integrity true;
-- alter table if exists AG_CAP_GPDS.STATUS set referential_integrity true;

