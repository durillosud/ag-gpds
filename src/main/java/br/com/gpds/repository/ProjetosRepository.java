package br.com.gpds.repository;

import br.com.gpds.domain.AtividadeProjetoClienteEntity;
import br.com.gpds.domain.ProjetosEntity;
import br.com.gpds.domain.StatusEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface ProjetosRepository extends JpaRepository<ProjetosEntity, Long> {

    Page<ProjetosEntity> findDistinctByAtividadeProjetoClientesIn(
        @Param("atividadeProjetoClientes") Collection<AtividadeProjetoClienteEntity> atividadeProjetoClientes, Pageable page
    );

    @Query("""
        select project
        from AtividadeProjetoClienteEntity assoc
        join assoc.projeto project
        where assoc in (:assocList)
        order by project.descricao
        """)
    Page<ProjetosEntity> findAllByAssocList(
        @Param("assocList") Collection<AtividadeProjetoClienteEntity> assocList, Pageable page
    );

    Page<ProjetosEntity> findAllByStatusNotInOrderByDescricao(List<StatusEntity> statuses, Pageable pageable);
}
