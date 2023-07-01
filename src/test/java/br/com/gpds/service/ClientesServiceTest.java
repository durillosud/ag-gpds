package br.com.gpds.service;

import br.com.gpds.domain.ClientesEntity;
import br.com.gpds.domain.query.filter.ClientesFilter;
import br.com.gpds.domain.request.CustomerRequest;
import br.com.gpds.repository.ClientesRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class ClientesServiceTest {

    @Autowired
    private ClientesService clientesService;

    @Autowired
    private ClientesRepository clientesRepository;

    @BeforeEach
    public void setUp() {
        var cliente1 = new ClientesEntity();
        cliente1.setNome("John");
        cliente1.setId(1L);

        var cliente2 = new ClientesEntity();
        cliente2.setNome("Mary");
        cliente2.setId(2L);

        var cliente3 = new ClientesEntity();
        cliente3.setNome("Peter");
        cliente3.setId(3L);

        clientesRepository.saveAll(Arrays.asList(cliente1, cliente2, cliente3));
    }

    @AfterEach
    public void tearDown() {
        clientesRepository.deleteAll();
    }


    @Test
    @DisplayName("Should throw an exception when the id is null or zero")
    void updateCustomerWhenIdIsNullOrZeroThenThrowException() {
        CustomerRequest request = new CustomerRequest(null, "John Doe");

        assertThrows(RuntimeException.class, () -> {
            clientesService.updateCustomer(request);
        });
    }

    @Test
    @DisplayName("Should update the customer when the id is not null or zero")
    void updateCustomerWhenIdIsNotNullOrZero() {        // Create a valid customer request
        CustomerRequest customerRequest = new CustomerRequest(1L, "John Doe");

        // Call the updateCustomer method
        ClientesEntity updatedCustomer = clientesService.updateCustomer(customerRequest);

        // Retrieve the customer from the database
        ClientesEntity retrievedCustomer = clientesRepository.findById(updatedCustomer.getId()).orElse(null);

        // Assert that the retrieved customer is not null
        assertThat(retrievedCustomer).isNotNull();

        // Assert that the retrieved customer has the updated name
        assertEquals(customerRequest.name(), retrievedCustomer.getNome());
    }

    @Test
    @DisplayName("Should save the customer when the request is valid")
    void saveCustomerWhenRequestIsValid() {        // Create a valid customer request
        CustomerRequest request = new CustomerRequest(4L, "Alice");

        // Save the customer using the service
        ClientesEntity savedCustomer = clientesService.saveCustomer(request);

        // Retrieve the customer from the repository
        ClientesEntity retrievedCustomer = clientesRepository.findById(savedCustomer.getId()).orElse(null);

        // Assert that the retrieved customer is not null
        assertThat(retrievedCustomer).isNotNull();

        // Assert that the retrieved customer has the same id and name as the saved customer
        assertEquals(savedCustomer.getId(), retrievedCustomer.getId());
        assertEquals(savedCustomer.getNome(), retrievedCustomer.getNome());
    }

    @Test
    @DisplayName("Should save the customer and return the saved customer")
    void saveCustomerAndReturnSavedCustomer() {
        String name = "Alice";

        ClientesEntity savedCustomer = clientesService.saveCustomer(new CustomerRequest(null, name));

        assertThat(savedCustomer.getId()).isNotNull();
        assertEquals(name, savedCustomer.getNome());
    }

    @Test
    @DisplayName("Should return paginated clients when pageable is applied")
    void getAllClientesByFiltersWhenPageableIsApplied() {
        var filter = new ClientesFilter(null, "John");
        var pageable = PageRequest.of(0, 10);

        var result = clientesService.getAllClientesByFilters(filter, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("John", result.getContent().get(0).getNome());
    }

    @Test
    @DisplayName("Should return clients matching the filter")
    void getAllClientesByFiltersWhenFilterIsApplied() {
        var filter = new ClientesFilter(null, "John");
        var pageable = PageRequest.of(0, 10);

        var result = clientesService.getAllClientesByFilters(filter, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("John", result.getContent().get(0).getNome());
    }

    @Test
    @DisplayName("Should return clients ordered by name when filter is applied")
    void getAllClientesByFiltersWhenFilterIsAppliedAndOrderedByName() {
        var filter = new ClientesFilter(null, "John");
        var pageable = PageRequest.of(0, 10);

        var result = clientesService.getAllClientesByFilters(filter, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getNome()).isEqualTo("John");
    }

    @Test
    @DisplayName("Should return all clients when the filter is empty")
    void getAllClientesByFiltersWhenFilterIsEmpty() {
        var filter = new ClientesFilter(null, "");
        var pageable = PageRequest.of(0, 10);

        var result = clientesService.getAllClientesByFilters(filter, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getContent()).extracting(ClientesEntity::getNome)
            .containsExactlyInAnyOrder("John", "Mary", "Peter");
    }
}
