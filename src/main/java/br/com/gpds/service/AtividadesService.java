package br.com.gpds.service;

import br.com.gpds.domain.AtividadesEntity;
import br.com.gpds.domain.ClientesEntity;
import br.com.gpds.domain.ProjetosEntity;
import br.com.gpds.domain.StatusEntity;
import br.com.gpds.domain.common.DomainConstants;
import br.com.gpds.domain.request.ActivityCreateRequest;
import br.com.gpds.domain.request.ActivityUpdateRequest;
import br.com.gpds.domain.response.ActivityResponse;
import br.com.gpds.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class AtividadesService {

    private final AtividadesRepository atividadesRepository;
    private final StatusRepository statusRepository;
    private final ClientesRepository clientesRepository;
    private final AtividadeProjetoClienteRepository atividadeProjetoClienteRepository;
    private final ProjetosRepository projetosRepository;

    public AtividadesService(
        AtividadesRepository atividadesRepository,
        StatusRepository statusRepository,
        ClientesRepository clientesRepository,
        AtividadeProjetoClienteRepository atividadeProjetoClienteRepository, ProjetosRepository projetosRepository) {
        this.atividadesRepository = atividadesRepository;
        this.statusRepository = statusRepository;
        this.clientesRepository = clientesRepository;
        this.atividadeProjetoClienteRepository = atividadeProjetoClienteRepository;
        this.projetosRepository = projetosRepository;
    }

    public Page<ActivityResponse> getActivitiesByStatusIdAndCustomerIdPaginated(
        Long statusId, Optional<Long> customerId, Pageable pageable
    ) {
        var activityResponseList = new LinkedList<ActivityResponse>();

        var customer = new AtomicReference<>(new ClientesEntity());
        if (customerId.isPresent() && clientesRepository.existsById(customerId.get())) {
            customer.set(
                clientesRepository.findById(customerId.get()).orElseGet(ClientesEntity::new)
            );
        }

        var activitiesPage = customerId.isEmpty()
            ? atividadesRepository.findAllByStatus_IdOrderByDescricao(statusId, pageable)
            : atividadesRepository.findAllByStatus_IdAndOptCustomer_IdOrderByDescricao(statusId, customerId, pageable);

        activitiesPage.getContent().forEach(atividadesEntity -> {
                var project = new ProjetosEntity();

                if (Objects.nonNull(customer.get().getId())) {
                    project = atividadeProjetoClienteRepository
                        .findProjectByCustomerIdAndActivityId(customer.get().getId(), atividadesEntity.getId())
                        .orElseGet(ProjetosEntity::new);
                }

                activityResponseList.add(
                    new ActivityResponse(atividadesEntity, project, customer.get())
                );
            }
        );

        return new PageImpl<>(activityResponseList, pageable, activitiesPage.getTotalElements());
    }

    public String getStatusDescriptionById(Long statusId) throws RuntimeException {
        if (!statusRepository.existsById(statusId))
            throw new RuntimeException("O status da atividade vindo na requisição, não existe");

        return statusRepository.findStatusDescriptionByStatusId(statusId);
    }

    public ActivityResponse save(ActivityCreateRequest activityCreateRequest) throws RuntimeException {
        var status = statusRepository.findById(DomainConstants.STARTED_STATUS_ID);

        var customer = clientesRepository.findById(activityCreateRequest.customerId());
        if (customer.isEmpty())
            throw new RuntimeException("O Product Owner não existe");

        var project = projetosRepository.findById(activityCreateRequest.projectId());
        if (project.isEmpty())
            throw new RuntimeException("O projeto não existe");

        var projectIsOwnedByCustomer = atividadeProjetoClienteRepository
            .existsByClienteAndProjeto(customer.get(), project.get());

        if (!projectIsOwnedByCustomer)
            throw new RuntimeException("O projeto não pertence ao Product Owner da requisição");

        var activity = atividadesRepository.saveAndFlush(
            new AtividadesEntity(
                activityCreateRequest.description(), activityCreateRequest.percentage(), status.orElseGet(StatusEntity::new)
            )
        );

        return new ActivityResponse(activity, project.get(), customer.get());
    }

    public ActivityResponse update(ActivityUpdateRequest activityUpdateRequest) {
        var activity = atividadesRepository
            .findById(activityUpdateRequest.id()).orElseGet(AtividadesEntity::new);

        var status = statusRepository.findById(
            Optional.ofNullable(activityUpdateRequest.statusId()).orElse(activity.getStatus().getId())
        );

        var project = projetosRepository.findById(
            Optional.ofNullable(activityUpdateRequest.projectId()).orElse(0L)
        );
        if (project.isEmpty())
            throw new RuntimeException("O projeto não existe");

        var isProjectActivity = atividadeProjetoClienteRepository
            .existsByProjetoAndAtividade(project.get(), activity);

        if (!isProjectActivity)
            throw new RuntimeException("A atividade não é uma atividade do projeto da requisição");

        var customer = clientesRepository.findById(
            Optional.ofNullable(activityUpdateRequest.customerId()).orElse(0L)
        );
        if (customer.isEmpty())
            throw new RuntimeException("O Product Owner não existe");

        var projectIsOwnedByCustomer = atividadeProjetoClienteRepository
            .existsByClienteAndProjeto(customer.get(), project.get());

        if (!projectIsOwnedByCustomer)
            throw new RuntimeException("O projeto não pertence ao Product Owner da requisição");

        activity.setDescricao(
            Optional.ofNullable(activityUpdateRequest.description()).orElse(activity.getDescricao())
        );
        activity.setPercentagem(
            Optional.ofNullable(activityUpdateRequest.percentage()).orElse(activity.getPercentagem())
        );
        activity.setStatus(status.orElse(activity.getStatus()));

        activity = atividadesRepository.saveAndFlush(activity);

        return new ActivityResponse(activity, project.get(), customer.get());
    }

    public ActivityResponse delete(Long id) {
        var activity = atividadesRepository.findById(id);
        if (activity.isPresent()) {
            var assoc = atividadeProjetoClienteRepository.findByAtividade_Id(activity.get().getId());

            var response = new ActivityResponse(activity.get(), new ProjetosEntity(), new ClientesEntity());
            atividadeProjetoClienteRepository.deleteAll(assoc);
            atividadesRepository.delete(activity.get());

            return response;
        }
        return new ActivityResponse(new AtividadesEntity(), new ProjetosEntity(), new ClientesEntity());
    }
}
