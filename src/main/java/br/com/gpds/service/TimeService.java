package br.com.gpds.service;

import br.com.gpds.domain.TimeEntity;
import br.com.gpds.repository.TimeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TimeService {

    private final TimeRepository timeRepository;

    public TimeService(TimeRepository timeRepository) {
        this.timeRepository = timeRepository;
    }

    public List<TimeEntity> findTeamsByNameContaining(String name) {
        return timeRepository.findAllByNomeContainingIgnoreCase(name);
    }
}
