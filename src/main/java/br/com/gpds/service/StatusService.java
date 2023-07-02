package br.com.gpds.service;

import br.com.gpds.domain.StatusEntity;
import br.com.gpds.repository.StatusRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StatusService {

    private final StatusRepository statusRepository;

    public StatusService(StatusRepository statusRepository) {
        this.statusRepository = statusRepository;
    }

    public List<StatusEntity> findAll() {
        return statusRepository.findAll();
    }
}
