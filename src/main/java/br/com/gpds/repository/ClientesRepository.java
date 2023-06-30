package br.com.gpds.repository;

import br.com.gpds.domain.ClientesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientesRepository extends JpaRepository<ClientesEntity, Long> {
}
