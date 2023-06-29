create schema if not exists ag_cap_gpds;

create table if not exists ag_cap_gpds.clientes
(
    id   bigint  not null
    constraint clientes_pk
    primary key,
    nome varchar not null
);

comment on table ag_cap_gpds.clientes is 'Contém dados de clientes';

alter table ag_cap_gpds.clientes
    owner to gpds;

create table if not exists ag_cap_gpds.projetos
(
    id        bigint  not null
    constraint projetos_pk
    primary key,
    descricao varchar not null,
    cliente   integer
    constraint projetos_clientes_id_fk
    references ag_cap_gpds.clientes
);

comment on table ag_cap_gpds.projetos is 'Contém projetos dos clientes';

alter table ag_cap_gpds.projetos
    owner to gpds;

create table if not exists ag_cap_gpds.atividades
(
    id        bigint  not null
    constraint atividades_pk
    primary key,
    descricao varchar not null,
    projeto   integer
    constraint atividades_projetos_id_fk
    references ag_cap_gpds.projetos
);

comment on table ag_cap_gpds.atividades is 'Contém atividades de projetos';

alter table ag_cap_gpds.atividades
    owner to gpds;
