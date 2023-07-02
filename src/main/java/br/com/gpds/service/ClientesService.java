package br.com.gpds.service;

import br.com.gpds.domain.ClientesEntity;
import br.com.gpds.domain.query.filter.ClientesFilter;
import br.com.gpds.domain.request.CustomerRequest;
import br.com.gpds.domain.response.CustomerResponse;
import br.com.gpds.repository.AtividadeProjetoClienteRepository;
import br.com.gpds.repository.AtividadesRepository;
import br.com.gpds.repository.ClientesRepository;
import br.com.gpds.repository.ProjetosRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class ClientesService {

    private final ClientesRepository clientesRepository;
    private final ProjetosRepository projetosRepository;
    private final AtividadesRepository atividadesRepository;
    private final AtividadeProjetoClienteRepository atividadeProjetoClienteRepository;

    public ClientesService(
        ClientesRepository clientesRepository,
        ProjetosRepository projetosRepository,
        AtividadesRepository atividadesRepository,
        AtividadeProjetoClienteRepository atividadeProjetoClienteRepository
    ) {
        this.clientesRepository = clientesRepository;
        this.projetosRepository = projetosRepository;
        this.atividadesRepository = atividadesRepository;
        this.atividadeProjetoClienteRepository = atividadeProjetoClienteRepository;
    }

    public Page<ClientesEntity> getCustomersFilteredAndSortedAndPaginated(ClientesFilter filter, Pageable pageable) {
        if (Objects.nonNull(filter.name()) && filter.name().isEmpty()) {
            return clientesRepository.findAll(pageable);
        }
        return clientesRepository.findClientesEntitiesByNomeContainingIgnoreCaseOrderByNome(
            filter.name(), pageable
        );
    }

    public ClientesEntity save(CustomerRequest request) throws RuntimeException {
        if (Objects.isNull(request.name()) || request.name().isEmpty())
            throw new RuntimeException("O nome do Product Owner não pode ser nulo ou vazio");
        return clientesRepository.saveAndFlush(new ClientesEntity(request.name()));
    }

    public ClientesEntity update(CustomerRequest request) throws RuntimeException {
        if (Objects.isNull(request.id()) || Objects.equals(0L, request.id()))
            throw new RuntimeException(
                "O identificador do cliente não pode ser nulo ou zero"
            );

        return clientesRepository.saveAndFlush(new ClientesEntity(request.id(), request.name()));
    }

    public CustomerResponse delete(Long id) throws RuntimeException {
        var customer = clientesRepository.findById(id);
        if (!customer.isEmpty()) {
            var assoc = atividadeProjetoClienteRepository.findByCliente_Id(id);
            var activities = atividadeProjetoClienteRepository.findActivitiesOwnedByCustomer(customer.get());
            var projects = atividadeProjetoClienteRepository.findProjectsOwnedByCustomer(customer.get());

            var responseAtomicReference = new AtomicReference<CustomerResponse>();
            customer.ifPresent(e -> responseAtomicReference.set(
                    new CustomerResponse(
                        "Os dados do cliente foram deletados!",
                        e
                    )
                )
            );
            atividadeProjetoClienteRepository.deleteAll(assoc);
            atividadesRepository.deleteAll(activities);
            projetosRepository.deleteAll(projects);

            clientesRepository.deleteById(id);
            return responseAtomicReference.get();
        }

        return new CustomerResponse("O cliente não existe", null);
    }
}
