package br.com.gpds.service;

import br.com.gpds.domain.ClientesEntity;
import br.com.gpds.domain.query.filter.ClientesFilter;
import br.com.gpds.domain.request.CustomerRequest;
import br.com.gpds.domain.response.CustomerResponse;
import br.com.gpds.repository.AtividadeProjetoClienteRepository;
import br.com.gpds.repository.AtividadesRepository;
import br.com.gpds.repository.ClientesRepository;
import br.com.gpds.repository.ProjetosRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientesServiceTest {

    @Mock
    private ClientesRepository clientesRepository;
    @Mock
    private ProjetosRepository projetosRepository;
    @Mock
    private AtividadesRepository atividadesRepository;
    @Mock
    private AtividadeProjetoClienteRepository atividadeProjetoClienteRepository;

    @InjectMocks
    private ClientesService clientesService;


    @Test
    @DisplayName("Should return a response indicating the customer does not exist when the customer does not exist")
    void deleteCustomerWhenCustomerDoesNotExist() {
        var customerId = 1L;
        when(clientesRepository.findById(customerId)).thenReturn(Optional.empty());

        var response = clientesService.delete(customerId);

        assertEquals("O cliente não existe", response.message());
        assertNull(response.entity());
        verify(clientesRepository, times(1)).findById(customerId);
        verify(clientesRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Should delete the customer when the customer exists")
    void deleteCustomerWhenCustomerExists() {
        var customerId = 1L;
        // Create a mock CustomerEntity
        ClientesEntity customerEntity = new ClientesEntity(customerId, "John Doe");

        // Create a mock CustomerResponse
        var expectedResponse = new CustomerResponse(
            "Os dados do cliente foram deletados!",
            customerEntity
        );

        // Mock the findById method of the clientesRepository to return the customerEntity
        when(clientesRepository.findById(customerId)).thenReturn(Optional.of(customerEntity));

        // Call the delete method of the clientesService
        var actualResponse = clientesService.delete(customerId);

        // Verify that the findById method was called once with the customerId
        verify(clientesRepository, times(1)).findById(customerId);

        // Verify that the deleteById method was called once with the customerId
        verify(clientesRepository, times(1)).deleteById(customerId);

        // Assert that the actualResponse is equal to the expectedResponse
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    @DisplayName("Should throw an exception when the id is null or zero")
    void updateCustomerWhenIdIsNullOrZeroThenThrowException() {
        var request = new CustomerRequest(null, "John Doe");

        assertThrows(RuntimeException.class, () -> clientesService.update(request));
    }

    @Test
    @DisplayName("Should update the customer when the id is not null or zero")
    void updateCustomerWhenIdIsNotNullOrZero() {
        var id = 1L;
        var name = "John Doe";
        var request = new CustomerRequest(id, name);
        var expectedEntity = new ClientesEntity(id, name);
        when(clientesRepository.saveAndFlush(any(ClientesEntity.class))).thenReturn(expectedEntity);

        var result = clientesService.update(request);

        assertEquals(expectedEntity, result);
        verify(clientesRepository, times(1)).saveAndFlush(any(ClientesEntity.class));
    }

    @Test
    @DisplayName("Should throw an exception when the name is null or empty")
    void saveCustomerWhenNameIsNullOrEmptyThenThrowException() {
        var request1 = new CustomerRequest(1L, "");
        assertThrows(RuntimeException.class, () -> clientesService.save(request1));

        var request2 = new CustomerRequest(2L, null);
        assertThrows(RuntimeException.class, () -> clientesService.save(request2));

        verify(clientesRepository, never()).saveAndFlush(any(ClientesEntity.class));
    }

    @Test
    @DisplayName("Should save the customer when the name is not null or empty")
    void saveCustomerWhenNameIsNotNullOrEmpty() {
        var request = new CustomerRequest(1L, "John Doe");
        var expectedEntity = new ClientesEntity(1L, "John Doe");
        when(clientesRepository.saveAndFlush(any(ClientesEntity.class))).thenReturn(expectedEntity);

        var actualEntity = clientesService.save(request);

        assertEquals(expectedEntity, actualEntity);
        verify(clientesRepository, times(1)).saveAndFlush(any(ClientesEntity.class));
    }

    @Test
    @DisplayName("Should throw an exception when the request is null")
    void saveCustomerWhenRequestIsNullThenThrowException() {
        assertThrows(NullPointerException.class, () -> {
            clientesService.save(null);
        });
    }

    @Test
    @DisplayName("Should throw an exception when the customer name is null or empty")
    void saveCustomerWhenNameIsNullThenThrowException() {
        var request = new CustomerRequest(1L, null);
        assertThrows(RuntimeException.class, () -> clientesService.save(request));
    }

    @Test
    @DisplayName("Should save the customer when the request is valid")
    void saveCustomerWhenRequestIsValid() {
        var request = new CustomerRequest(1L, "John Doe");
        var expectedEntity = new ClientesEntity(1L, "John Doe");
        when(clientesRepository.saveAndFlush(any(ClientesEntity.class))).thenReturn(expectedEntity);

        var actualEntity = clientesService.save(request);

        assertEquals(expectedEntity, actualEntity);
        verify(clientesRepository, times(1)).saveAndFlush(any(ClientesEntity.class));
    }

    @Test
    @DisplayName("Should return all customers when filter name is null or empty")
    void getCustomersFilteredAndSortedAndPaginatedWhenFilterNameIsEmptyOrNull() {
        // Create a mock filter and pageable objects
        var filter = new ClientesFilter(null, "");
        var pageable = PageRequest.of(0, 10);

        // Create a list of customers
        var customers = new ArrayList<ClientesEntity>();
        customers.add(new ClientesEntity(1L, "John Doe"));
        customers.add(new ClientesEntity(2L, "Jane Smith"));

        // Mock the repository method to return the list of customers
        when(clientesRepository.findAll(pageable)).thenReturn(new PageImpl<>(customers));

        // Call the method under test
        var result = clientesService.getCustomersFilteredAndSortedAndPaginated(filter, pageable);

        // Verify that the repository method was called
        verify(clientesRepository, times(1)).findAll(pageable);

        // Verify that the result matches the expected list of customers
        assertEquals(customers, result.getContent());
    }

    @Test
    @DisplayName("Should return customers filtered by name, sorted and paginated when filter name is not null or empty")
    void getCustomersFilteredAndSortedAndPaginatedWhenFilterNameIsNotEmptyOrNull() {
        var filter = new ClientesFilter(1L, "John");
        var pageable = PageRequest.of(0, 10);
        var expectedCustomers = new ArrayList<ClientesEntity>();
        expectedCustomers.add(new ClientesEntity(1L, "John Doe"));
        expectedCustomers.add(new ClientesEntity(2L, "John Smith"));
        var expectedPage = new PageImpl<>(expectedCustomers, pageable, expectedCustomers.size());

        when(clientesRepository.findClientesEntitiesByNomeContainingIgnoreCaseOrderByNome(filter.name(), pageable))
            .thenReturn(expectedPage);

        var result = clientesService.getCustomersFilteredAndSortedAndPaginated(filter, pageable);

        assertEquals(expectedPage, result);
        verify(clientesRepository, times(1)).findClientesEntitiesByNomeContainingIgnoreCaseOrderByNome(filter.name(),
            pageable);
    }
}
