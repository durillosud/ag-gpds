package br.com.gpds.repository;

import br.com.gpds.domain.TimeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TimeRepository extends JpaRepository<TimeEntity, Long> {
    List<TimeEntity> findAllByNomeContainingIgnoreCase(String nome);
}
