package br.com.gpds.repository;

import br.com.gpds.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Optional;

public interface AtividadeProjetoClienteRepository extends JpaRepository<AtividadeProjetoClienteEntity, Long> {
    Collection<AtividadeProjetoClienteEntity> findByCliente_Id(Long clientId);
    Collection<AtividadeProjetoClienteEntity> findByProjeto_Id(Long projectId);
    Collection<AtividadeProjetoClienteEntity> findByAtividade_Id(Long activityId);

    Boolean existsByClienteAndProjeto(ClientesEntity clientes, ProjetosEntity projetos);
    Boolean existsByProjetoAndAtividade(ProjetosEntity projetos, AtividadesEntity atividades);

    @Query("""
        select distinct assoc.projeto
        from AtividadeProjetoClienteEntity assoc
        join assoc.cliente customer on customer = :customer
        """)
    Collection<ProjetosEntity> findProjectsOwnedByCustomer(@Param("customer") ClientesEntity customer);

    @Query("""
        select distinct assoc.atividade
        from AtividadeProjetoClienteEntity assoc
        join assoc.cliente customer on customer = :customer
        """)
    Collection<AtividadesEntity> findActivitiesOwnedByCustomer(@Param("customer") ClientesEntity customer);

    @Query("""
        select distinct assoc.atividade
        from AtividadeProjetoClienteEntity assoc
        join assoc.cliente customer
        join assoc.projeto project
        where customer = :customer
        and project = :project
        and project.status <> :status
        """)
    Collection<AtividadesEntity> findActivitiesByProjectAndCustomerWithProjectStatusNotEquals(
        @Param("customer") ClientesEntity customer,
        @Param("project") ProjetosEntity project,
        @Param("status") StatusEntity status
    );

    @Query("""
        select distinct assoc.projeto
        from AtividadeProjetoClienteEntity assoc
        join assoc.cliente customer
        join assoc.atividade activity
        where customer.id = :customerId
        and activity.id = :activityId
        """)
    Optional<ProjetosEntity> findProjectByCustomerIdAndActivityId(
        @Param("customerId") Long customerId, @Param("activityId") Long activityId
    );

    @Query("""
        select distinct assoc.projeto
        from AtividadeProjetoClienteEntity assoc
        join assoc.projeto project
        join assoc.cliente customer
        where project.id = :projectId
        and project.status <> :status
        and customer = :customer
        """)
    Optional<ProjetosEntity> findProjectByProjectIdWithProjectStatusNotEqualsAndProjectCustomer(
        @Param("projectId") Long projectId,
        @Param("customer") ClientesEntity customer,
        @Param("status") StatusEntity status
    );

    @Query("""
        select distinct assoc.projeto
        from AtividadeProjetoClienteEntity assoc
        join assoc.projeto project
        join assoc.cliente customer
        where project.id = :projectId
        and project.status = :status
        and customer = :customer
        """)
    Optional<ProjetosEntity> findProjectByProjectIdWithProjectStatusEqualsAndProjectCustomer(
        @Param("projectId") Long projectId,
        @Param("customer") ClientesEntity customer,
        @Param("status") StatusEntity status
    );
}
