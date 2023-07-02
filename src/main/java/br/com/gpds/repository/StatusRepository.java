package br.com.gpds.repository;

import br.com.gpds.domain.StatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StatusRepository extends JpaRepository<StatusEntity, Long> {

    @Query("""
        select status.descricao
        from StatusEntity status
        where status.id = :statusId
        """)
    String findStatusDescriptionByStatusId(@Param("statusId") Long statusId);
}
