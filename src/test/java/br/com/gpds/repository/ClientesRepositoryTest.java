package br.com.gpds.repository;

import br.com.gpds.domain.ClientesEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientesRepositoryTest {

    @Mock
    private ClientesRepository clientesRepository;

    @Test
    @DisplayName("Should return an empty page when no ClientesEntity name contains the search term")
    void findClientesEntitiesByNomeNotContainingSearchTerm() {
        var searchTerm = "John";
        var pageRequest = PageRequest.of(0, 10);
        List<ClientesEntity> emptyList = Collections.emptyList();
        var emptyPage = new PageImpl<>(emptyList, pageRequest, 0);

        when(clientesRepository.findClientesEntitiesByNomeContainingIgnoreCaseOrderByNome(searchTerm, pageRequest))
                .thenReturn(emptyPage);

        Page<ClientesEntity> result = clientesRepository.findClientesEntitiesByNomeContainingIgnoreCaseOrderByNome(searchTerm, pageRequest);

        assertThat(result).isEmpty();
        verify(clientesRepository, times(1)).findClientesEntitiesByNomeContainingIgnoreCaseOrderByNome(searchTerm, pageRequest);
    }

    @Test
    @DisplayName("Should return a page of ClientesEntity when the name contains the search term, ignoring case and ordered by name")
    void findClientesEntitiesByNomeContainingIgnoreCaseOrderByNome() {
        var searchTerm = "John";
        var pageRequest = PageRequest.of(0, 10);
        var clientesList = Collections.singletonList(
                new ClientesEntity(Long.valueOf(1L), "John Doe")
        );
        var expectedPage = new PageImpl<>(clientesList, pageRequest, 1);

        when(clientesRepository.findClientesEntitiesByNomeContainingIgnoreCaseOrderByNome(searchTerm, pageRequest))
                .thenReturn(expectedPage);

        Page<ClientesEntity> resultPage = clientesRepository.findClientesEntitiesByNomeContainingIgnoreCaseOrderByNome(searchTerm, pageRequest);

        assertThat(resultPage).isEqualTo(expectedPage);
        verify(clientesRepository, times(1)).findClientesEntitiesByNomeContainingIgnoreCaseOrderByNome(searchTerm, pageRequest);
    }

}
