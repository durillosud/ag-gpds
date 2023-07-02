package br.com.gpds.service;

import br.com.gpds.domain.StatusEntity;
import br.com.gpds.repository.StatusRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatusServiceTest {

    @Mock
    private StatusRepository statusRepository;

    @InjectMocks
    private StatusService statusService;

    @Test
    @DisplayName("Should return all status entities from the repository")
    void findAllStatusEntities() {
        var expectedStatusEntities = new ArrayList<StatusEntity>();
        expectedStatusEntities.add(new StatusEntity(1L, "Status 1"));
        expectedStatusEntities.add(new StatusEntity(2L, "Status 2"));
        expectedStatusEntities.add(new StatusEntity(3L, "Status 3"));

        when(statusRepository.findAll()).thenReturn(expectedStatusEntities);

        var actualStatusEntities = statusService.findAll();

        assertEquals(expectedStatusEntities.size(), actualStatusEntities.size());
        assertEquals(expectedStatusEntities.get(0), actualStatusEntities.get(0));
        assertEquals(expectedStatusEntities.get(1), actualStatusEntities.get(1));
        assertEquals(expectedStatusEntities.get(2), actualStatusEntities.get(2));

        verify(statusRepository, times(1)).findAll();
    }

}
