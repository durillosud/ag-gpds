package br.com.gpds.repository;

import br.com.gpds.domain.ClientesEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface ClientesRepository extends JpaRepository<ClientesEntity, Long> {
    Page<ClientesEntity> findClientesEntitiesByNomeContainingIgnoreCaseOrderByNome(
        @Param("nome") String nome, Pageable pageable
    );
}
