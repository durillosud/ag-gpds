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
    cliente   bigint
        constraint projetos_clientes_id_fk
            references ag_cap_gpds.clientes,
    status    bigint
        constraint projetos_status_id_fk
            references ag_cap_gpds.status
);

comment on table ag_cap_gpds.projetos is 'Contém projetos dos clientes';

alter table ag_cap_gpds.projetos
    owner to gpds;

create table if not exists ag_cap_gpds.atividades
(
    id          bigint  not null
        constraint atividades_pk
            primary key,
    descricao   varchar not null,
    projeto     bigint
        constraint atividades_projetos_id_fk
            references ag_cap_gpds.projetos,
    status      bigint
        constraint atividades_status_id_fk
            references ag_cap_gpds.status,
    percentagem numeric(3, 1)
);

comment on table ag_cap_gpds.atividades is 'Contém atividades de projetos';

alter table ag_cap_gpds.atividades
    owner to gpds;

create table if not exists ag_cap_gpds.status
(
    id        bigint  not null
        constraint status_pk
            primary key,
    descricao varchar not null
);

alter table ag_cap_gpds.status
    owner to gpds;

insert into ag_cap_gpds.status (id, descricao)
values (default, 'Em análise'),
       (default, 'Em implementação'),
       (default, 'Em implantação'),
       (default, 'Em suporte');
