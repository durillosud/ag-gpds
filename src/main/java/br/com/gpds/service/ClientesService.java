package br.com.gpds.service;

import br.com.gpds.domain.ClientesEntity;
import br.com.gpds.domain.query.filter.ClientesFilter;
import br.com.gpds.domain.request.CustomerRequest;
import br.com.gpds.domain.response.CustomerResponse;
import br.com.gpds.repository.ClientesRepository;
import br.com.gpds.web.rest.errors.BadRequestAlertException;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.ErrorResponseException;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class ClientesService {

    private final ClientesRepository clientesRepository;

    public ClientesService(ClientesRepository clientesRepository) {
        this.clientesRepository = clientesRepository;
    }

    public Page<ClientesEntity> getAllClientesByFilters(ClientesFilter filter, Pageable pageable) {
        if (Objects.nonNull(filter.name()) && filter.name().isEmpty()) {
            return clientesRepository.findAll(pageable);
        }
        return clientesRepository.findClientesEntitiesByNomeContainingIgnoreCaseOrderByNome(
            filter.name(), pageable
        );
    }

    public ClientesEntity saveCustomer(CustomerRequest request) {
        return clientesRepository.saveAndFlush(new ClientesEntity(request.id(), request.name()));
    }

    public ClientesEntity updateCustomer(CustomerRequest request) throws RuntimeException {
        if (Objects.isNull(request.id()) || Objects.equals(0L, request.id()))
            throw new RuntimeException(
                "O identificador do cliente não pode ser nulo ou zero"
            );

        return clientesRepository.saveAndFlush(new ClientesEntity(request.id(), request.name()));
    }

    public CustomerResponse deleteCustomer(Long id) throws RuntimeException {
        var entity = clientesRepository.findById(id);
        if (!entity.isEmpty()) {
            var responseAtomicReference = new AtomicReference<CustomerResponse>();
            entity.ifPresent(e -> responseAtomicReference.set(
                    new CustomerResponse(
                        "Os dados do cliente foram deletados!",
                        e
                    )
                )
            );
            clientesRepository.deleteById(id);
            return responseAtomicReference.get();
        }

        return new CustomerResponse("O cliente não existe", null);
    }
}
