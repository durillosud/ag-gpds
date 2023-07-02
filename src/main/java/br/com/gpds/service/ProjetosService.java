package br.com.gpds.service;

import br.com.gpds.domain.*;
import br.com.gpds.domain.common.DomainConstants;
import br.com.gpds.domain.request.ProjectCreateRequest;
import br.com.gpds.domain.request.ProjectListRequest;
import br.com.gpds.domain.request.ProjectUpdateRequest;
import br.com.gpds.domain.response.ProjectResponse;
import br.com.gpds.repository.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class ProjetosService {

    public static final long FINISHED_STATUS = 6L;
    private final ProjetosRepository projetosRepository;
    private final ClientesRepository clientesRepository;
    private final AtividadesRepository atividadesRepository;
    private final AtividadeProjetoClienteRepository atividadeProjetoClienteRepository;
    private final StatusRepository statusRepository;
    private final TimeRepository timeRepository;

    public ProjetosService(
        ProjetosRepository projetosRepository,
        ClientesRepository clientesRepository,
        AtividadesRepository atividadesRepository,
        AtividadeProjetoClienteRepository atividadeProjetoClienteRepository,
        StatusRepository statusRepository,
        TimeRepository timeRepository
    ) {
        this.projetosRepository = projetosRepository;
        this.clientesRepository = clientesRepository;
        this.atividadesRepository = atividadesRepository;
        this.atividadeProjetoClienteRepository = atividadeProjetoClienteRepository;
        this.statusRepository = statusRepository;
        this.timeRepository = timeRepository;
    }

    //@Todo: lista de projetos por status
    public Page<ProjectResponse> getAllProjetosByProjectRequest(
        ProjectListRequest projectListRequest, @JsonProperty("ProjectsPage") Pageable pageable
    ) throws RuntimeException {
        if (Objects.isNull(projectListRequest.customerId()) || Objects.equals(0L, projectListRequest.customerId())) {
            throw new RuntimeException(
                String.format(
                    "O identificador do Product Owner não pode ser nulo ou zero, '%s=%s'",
                    "customerId", projectListRequest.customerId()
                )
            );
        }

        var activities = atividadeProjetoClienteRepository.findByCliente_Id(projectListRequest.customerId());

        if (activities.isEmpty())
            return Page.empty();


        var customer = clientesRepository.findById(projectListRequest.customerId());
        var projects = projetosRepository.findAllByAssocList(
            activities, pageable
        );

        var projectResponseList = new LinkedList<ProjectResponse>();
        projects.getContent().forEach(projetosEntity ->
            projectResponseList
                .add(new ProjectResponse(
                        projetosEntity,
                        customer.orElseGet(ClientesEntity::new),
                        activities.stream()
                            .filter(assoc -> Objects.equals(assoc.getProjeto(), projetosEntity))
                            .map(AtividadeProjetoClienteEntity::getAtividade)
                            .toList()
                    )

                )
        );

        return new PageImpl<>(projectResponseList, pageable, projects.getTotalElements());
    }

    @Transactional
    public ProjectResponse save(ProjectCreateRequest request) throws RuntimeException {
        validateTeamIdExists(request.teamId());

        var team = timeRepository.findById(request.teamId());
        var entity = new AtomicReference<ProjetosEntity>();
        var customerEntity = new AtomicReference<ClientesEntity>();
        var activityEntity = new AtomicReference<AtividadesEntity>();

        var optCustomer = clientesRepository.findById(request.customerId());
        if (Objects.nonNull(request.customerId()) && optCustomer.isEmpty())
            throw new RuntimeException("O Product Owner da requisição não existe na base de dados");

        Optional.ofNullable(request.customerId())
            .ifPresentOrElse(
                aLong -> {
                    var project = projetosRepository.saveAndFlush(new ProjetosEntity(
                            request.description(),
                            statusRepository.findById(DomainConstants.ON_CREATE_STATUS_ID).orElse(new StatusEntity()),
                            team.orElseGet(TimeEntity::new)
                        )
                    );

                    optCustomer.ifPresentOrElse(
                        customer -> {
                            customerEntity.set(customer);

                            var activity = atividadesRepository.saveAndFlush(
                                new AtividadesEntity(
                                    DomainConstants.SERVICE_LETTER_OPENING,
                                    BigDecimal.ZERO,
                                    statusRepository.findById(DomainConstants.STARTED_STATUS_ID).orElse(new StatusEntity())
                                )
                            );
                            activityEntity.set(activity);

                            var assoc = new AtividadeProjetoClienteEntity(customer, project, activity);
                            atividadeProjetoClienteRepository.saveAndFlush(assoc);

                            entity.set(project);
                        },

                        () -> entity.set(
                            projetosRepository.saveAndFlush(new ProjetosEntity(
                                    request.description(),
                                    statusRepository.findById(DomainConstants.ON_CREATE_STATUS_ID).orElse(new StatusEntity()),
                                    team.orElseGet(TimeEntity::new)
                                )
                            )
                        )
                    );
                },

                () -> entity.set(
                    projetosRepository.saveAndFlush(new ProjetosEntity(
                            request.description(),
                            statusRepository.findById(DomainConstants.ON_CREATE_STATUS_ID).orElse(new StatusEntity()),
                            team.orElseGet(TimeEntity::new)
                        )
                    )
                )
            );

        var activities = new LinkedList<AtividadesEntity>();
        activities.add(activityEntity.get());
        return new ProjectResponse(entity.get(), customerEntity.get(), activities);
    }

    private static void validateTeamIdExists(Long teamId) throws RuntimeException {
        if (Objects.isNull(teamId) || Objects.equals(0L, teamId))
            throw new RuntimeException(
                "O identificador do time responsável pelo projeto não pode ser nulo ou zero"
            );
    }

    @Transactional
    public ProjectResponse update(ProjectUpdateRequest request) throws RuntimeException {
        validateTeamIdExists(request.teamId());

        if (Objects.isNull(request.id()) || Objects.equals(0L, request.id()))
            throw new RuntimeException(
                "O identificador do projeto não pode ser nulo ou zero"
            );

        if (Objects.isNull(request.customerId()) || Objects.equals(0L, request.customerId()))
            throw new RuntimeException(
                "O identificador do Product Owner não pode ser nulo ou zero"
            );


        var project = projetosRepository.findById(request.id());
        if (project.isEmpty())
            throw new RuntimeException("O projeto não existe");

        var newTeam = timeRepository.findById(request.teamId());
        var newStatus = statusRepository.findById(request.statusId());
        var immutableCustomer = clientesRepository.findById(request.customerId());
        var updatedProject = new AtomicReference<ProjetosEntity>();
        updatedProject.set(project.get());

        project.ifPresent(projetosEntity -> {
            projetosEntity.setDescricao(
                Objects.isNull(request.description()) || request.description().isEmpty()
                    ? projetosEntity.getDescricao()
                    : request.description()
            );

            var team = newTeam.isEmpty() ? projetosEntity.getTime() : newTeam.get();
            projetosEntity.setTime(team);

            var status = newStatus.isEmpty() ? projetosEntity.getStatus() : newStatus.get();
            projetosEntity.setStatus(status);

            updatedProject.set(projetosRepository.saveAndFlush(projetosEntity));

        });

        return new ProjectResponse(updatedProject.get(), immutableCustomer.get(), null);
    }

    public ProjectResponse delete(Long id) {
        var project = projetosRepository.findById(id);
        if (project.isPresent()) {
            var assoc = atividadeProjetoClienteRepository.findByProjeto_Id(id);
            var activities = atividadesRepository.findAllByAssocList(assoc);

            var response = new ProjectResponse(project.get(), null, activities);

            atividadeProjetoClienteRepository.deleteAll(assoc);
            atividadesRepository.deleteAll(activities);
            projetosRepository.delete(project.get());

            return response;
        }

        return new ProjectResponse(new ProjetosEntity(), null, List.of());
    }

    public String getStatusDescriptionById(Long statusId) {
        if (!statusRepository.existsById(statusId))
            throw new RuntimeException("O status do projeto vindo na requisição, não existe");

        return statusRepository.findStatusDescriptionByStatusId(statusId);
    }

    public Page<ProjectResponse> getProjectsByStatusId(Long statusId, Optional<Long> customerId, Pageable pageable) {
        var projectResponseList = new LinkedList<ProjectResponse>();
        var projectsByStatusPage = projetosRepository.findAllByStatusNotInOrderByDescricao(
            List.of(new StatusEntity(statusId)), pageable
        );

        var customer = new AtomicReference<>(new ClientesEntity());
        if (customerId.isPresent() && clientesRepository.existsById(customerId.get())) {
            customer.set(
                clientesRepository.findById(customerId.get()).orElseGet(ClientesEntity::new)
            );
        }

        projectsByStatusPage.getContent().forEach(projetosEntity -> {
            var finishedStatus = new StatusEntity(FINISHED_STATUS);
            var activities = atividadeProjetoClienteRepository
                .findActivitiesByProjectAndCustomerWithProjectStatusNotEquals(
                    customer.get(), projetosEntity, finishedStatus
                );
                projectResponseList.add(
                    new ProjectResponse(projetosEntity, customer.get(), activities)
                );
            }
        );

        return new PageImpl<>(projectResponseList, pageable, projectsByStatusPage.getTotalElements());
    }
}
