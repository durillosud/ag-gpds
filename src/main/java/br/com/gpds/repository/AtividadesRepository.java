package br.com.gpds.repository;

import br.com.gpds.domain.AtividadeProjetoClienteEntity;
import br.com.gpds.domain.AtividadesEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Optional;

public interface AtividadesRepository extends JpaRepository<AtividadesEntity, Long> {

    @Query("""
        select activity
        from AtividadeProjetoClienteEntity assoc
        join AtividadesEntity activity
        where assoc in (:assocList)
        """)
    Collection<AtividadesEntity> findAllByAssocList(
        @Param("assocList") Collection<AtividadeProjetoClienteEntity> assocList
    );
    Page<AtividadesEntity> findAllByStatus_IdOrderByDescricao(Long statusId, Pageable pageable);
    @Query("""
        select distinct activity
        from AtividadeProjetoClienteEntity assoc
        join assoc.atividade activity
        join assoc.cliente customer
        where (:customerId is null or customer.id = :customerId)
        and activity.status.id = :statusId
        """)
    Page<AtividadesEntity> findAllByStatus_IdAndOptCustomer_IdOrderByDescricao(
        @Param("statusId") Long statusId,
        @Param("customerId") Optional<Long> customerId,
        Pageable pageable
    );
}
