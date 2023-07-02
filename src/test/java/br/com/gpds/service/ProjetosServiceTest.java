package br.com.gpds.service;

import br.com.gpds.domain.*;
import br.com.gpds.domain.common.DomainConstants;
import br.com.gpds.domain.request.ProjectCreateRequest;
import br.com.gpds.domain.request.ProjectListRequest;
import br.com.gpds.domain.request.ProjectUpdateRequest;
import br.com.gpds.domain.response.ProjectResponse;
import br.com.gpds.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjetosServiceTest {

    @Mock
    private ProjetosRepository projetosRepository;
    @Mock
    private ClientesRepository clientesRepository;
    @Mock
    private AtividadesRepository atividadesRepository;
    @Mock
    private AtividadeProjetoClienteRepository atividadeProjetoClienteRepository;
    @Mock
    private StatusRepository statusRepository;
    @Mock
    private TimeRepository timeRepository;

    @InjectMocks
    private ProjetosService projetosService;

    @BeforeEach
    void setUp() {
        try (var mocks = MockitoAnnotations.openMocks(this)) {
            projetosService = new ProjetosService(
                projetosRepository,
                clientesRepository,
                atividadesRepository,
                atividadeProjetoClienteRepository,
                statusRepository,
                timeRepository
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    @DisplayName("Should return a page of projects with the given status id and customer id")
    void getProjectsByStatusIdWithCustomerId() {
        var statusId = 5L;
        var customerId = 1L;
        var pageable = PageRequest.of(0, 10);

        var projectResponseList = new ArrayList<ProjectResponse>();
        var projectList = new ArrayList<ProjetosEntity>();
        var project1 = new ProjetosEntity(1L, "Project 1", new StatusEntity(), new TimeEntity());
        var project2 = new ProjetosEntity(2L, "Project 2", new StatusEntity(), new TimeEntity());
        projectResponseList.add(new ProjectResponse(project1, null, null));
        projectResponseList.add(new ProjectResponse(project2, null, null));
        projectList.addAll(List.of(project1, project2));

        when(projetosRepository.findAllByStatusNotInOrderByDescricao(List.of(new StatusEntity(statusId)), pageable))
            .thenReturn(new PageImpl<>(projectList, pageable, projectResponseList.size()));
        when(clientesRepository.existsById(customerId)).thenReturn(true);

        var result = projetosService.getProjectsByStatusId(statusId, Optional.of(customerId), pageable);

        assertEquals(projectResponseList.size(), result.getContent().size());
        verify(projetosRepository, times(1)).findAllByStatusNotInOrderByDescricao(
            List.of(new StatusEntity(statusId)), pageable
        );
        verify(clientesRepository, times(1)).existsById(customerId);
    }

    @Test
    @DisplayName("Should return a page of projects with the given status id and no customer id")
    void getProjectsByStatusIdWithNoCustomerId() {
        var statusId = 5L;
        var customerId = Optional.<Long>empty();
        var pageable = PageRequest.of(0, 10);

        var projectResponseList = new ArrayList<ProjectResponse>();
        var projectList = new ArrayList<ProjetosEntity>();
        var project1 = new ProjetosEntity(1L, "Project 1", new StatusEntity(1L), new TimeEntity(1L));
        var project2 = new ProjetosEntity(2L, "Project 2", new StatusEntity(1L), new TimeEntity(2L));
        projectResponseList.add(new ProjectResponse(project1, null, null));
        projectResponseList.add(new ProjectResponse(project2, null, null));
        projectList.addAll(List.of(project1, project2));

        when(projetosRepository.findAllByStatusNotInOrderByDescricao(List.of(new StatusEntity(statusId)), pageable))
            .thenReturn(new PageImpl<>(projectList, pageable, projectResponseList.size()));

        var result = projetosService.getProjectsByStatusId(statusId, customerId, pageable);

        assertEquals(projectResponseList.size(), result.getContent().size());
        assertEquals(projectResponseList.size(), result.getTotalElements());
        verify(projetosRepository, times(1)).findAllByStatusNotInOrderByDescricao(
            List.of(new StatusEntity(statusId)), pageable
        );
    }


    @Test
    @DisplayName("Should return the status description when the status id exists")
    void getStatusDescriptionByIdWhenStatusIdExists() {
        Long statusId = 1L;
        String statusDescription = "In Progress";

        when(statusRepository.existsById(statusId)).thenReturn(true);
        when(statusRepository.findStatusDescriptionByStatusId(statusId)).thenReturn(statusDescription);

        String result = projetosService.getStatusDescriptionById(statusId);

        assertEquals(statusDescription, result);
        verify(statusRepository, times(1)).existsById(statusId);
        verify(statusRepository, times(1)).findStatusDescriptionByStatusId(statusId);
    }

    @Test
    @DisplayName("Should throw an exception when the teamId is null or zero")
    void saveProjectWhenTeamIdIsNullThenThrowException() {
        var request = new ProjectCreateRequest(
            "Project description", null, 1L);

        // Act and Assert
        assertThrows(RuntimeException.class, () -> {
            projetosService.save(request);
        });
    }

    @Test
    @DisplayName("Should throw an exception when the teamId does not exist")
    void saveProjectWhenTeamIdDoesNotExistThenThrowException() {
        var request = new ProjectCreateRequest(
            "Project description",
            null,
            null
        );

        // Act and Assert
        assertThrows(RuntimeException.class, () -> {
            projetosService.save(request);
        });

        verifyNoMoreInteractions(timeRepository);
        verifyNoInteractions(clientesRepository, atividadesRepository, atividadeProjetoClienteRepository, statusRepository, projetosRepository);
    }

    @Test
    @DisplayName("Should save the project when the customerId is null")
    void saveProjectWhenCustomerIdIsNull() {
        var request = new ProjectCreateRequest(
            "Project Description", 1L, null);

        var projectEntity = new ProjetosEntity(
            "Project Description",
            new StatusEntity(),
            new TimeEntity()
        );

        when(projetosRepository.saveAndFlush(any(ProjetosEntity.class))).thenReturn(projectEntity);

        var response = projetosService.save(request);

        assertNotNull(response);
        assertNull(response.customer());

        verify(projetosRepository, times(1)).saveAndFlush(any(ProjetosEntity.class));
    }

    @Test
    @DisplayName("Should throw an exception when the customerId does not exist")
    void saveProjectWhenCustomerIdDoesNotExistThenThrowException() {
        var request = new ProjectCreateRequest(
            "Project Description", 1L, 0L);

        when(clientesRepository.findById(request.customerId())).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(RuntimeException.class, () -> {
            projetosService.save(request);
        });

        verify(clientesRepository, times(1)).findById(request.customerId());
        verify(projetosRepository, never()).saveAndFlush(any(ProjetosEntity.class));
        verify(atividadesRepository, never()).saveAndFlush(any(AtividadesEntity.class));
        verify(atividadeProjetoClienteRepository, never()).saveAndFlush(any(AtividadeProjetoClienteEntity.class));
    }

    @Test
    @DisplayName("Should save the project when the teamId and customerId are valid and exist")
    void saveProjectWhenTeamIdAndCustomerIdAreValid() {
        var request = new ProjectCreateRequest(
            "Test Project",
            1L,
            1L
        );

        var projectEntity = new ProjetosEntity(
            "Test Project",
            new StatusEntity(),
            new TimeEntity()
        );

        var customerEntity = new ClientesEntity(
            1L,
            "Test Customer"
        );

        var activityEntity = new AtividadesEntity(
            "Test Activity",
            BigDecimal.ZERO,
            new StatusEntity()
        );

        var activities = new ArrayList<AtividadesEntity>();
        activities.add(activityEntity);

        when(timeRepository.findById(request.teamId())).thenReturn(Optional.of(new TimeEntity()));
        when(clientesRepository.findById(request.customerId())).thenReturn(Optional.of(customerEntity));
        when(projetosRepository.saveAndFlush(any(ProjetosEntity.class))).thenReturn(projectEntity);
        when(atividadesRepository.saveAndFlush(any(AtividadesEntity.class))).thenReturn(activityEntity);
        when(atividadeProjetoClienteRepository.saveAndFlush(any(AtividadeProjetoClienteEntity.class))).thenReturn(new AtividadeProjetoClienteEntity());

        var response = projetosService.save(request);

        assertNotNull(response);
        assertEquals(projectEntity, response.project());
        assertEquals(customerEntity, response.customer());
        assertEquals(activities, response.projectActivities());

        verify(timeRepository, times(1)).findById(request.teamId());
        verify(clientesRepository, times(1)).findById(request.customerId());
        verify(projetosRepository, times(1)).saveAndFlush(any(ProjetosEntity.class));
        verify(atividadesRepository, times(1)).saveAndFlush(any(AtividadesEntity.class));
        verify(atividadeProjetoClienteRepository, times(1)).saveAndFlush(any(AtividadeProjetoClienteEntity.class));
    }

    @Test
    @DisplayName("Should save the project when the customerId is null or zero but teamId is valid")
    void saveProjectWhenCustomerIdIsNullButTeamIdIsValid() {
        var request = new ProjectCreateRequest(
            "Project description",
            1L,
            null
        );

        var projectEntity = new ProjetosEntity(
            "Project description",
            new StatusEntity(),
            new TimeEntity()
        );

        when(projetosRepository.saveAndFlush(any(ProjetosEntity.class))).thenReturn(projectEntity);
        when(statusRepository.findById(DomainConstants.ON_CREATE_STATUS_ID)).thenReturn(Optional.of(new StatusEntity()));
        when(timeRepository.findById(request.teamId())).thenReturn(Optional.of(new TimeEntity()));

        var response = projetosService.save(request);

        assertNotNull(response);
        assertEquals(projectEntity, response.project());
        assertNull(response.customer());
        assertTrue(response.projectActivities().stream().allMatch(Objects::isNull));
        verify(projetosRepository, times(1)).saveAndFlush(any(ProjetosEntity.class));
        verify(statusRepository, times(1)).findById(DomainConstants.ON_CREATE_STATUS_ID);
        verify(timeRepository, times(1)).findById(request.teamId());
    }

    @Test
    @DisplayName("Should throw an exception when the team does not exist")
    void saveProjectWhenTeamDoesNotExistThenThrowException() {
        var request = new ProjectCreateRequest(
            "Project description",
            0L,
            1L
        );

        // Act and Assert
        assertThrows(RuntimeException.class, () -> {
            projetosService.save(request);
        });

        verifyNoMoreInteractions(timeRepository);
        verifyNoInteractions(projetosRepository, clientesRepository, atividadesRepository, atividadeProjetoClienteRepository, statusRepository);
    }

    @Test
    @DisplayName("Should throw an exception when the customer does not exist")
    void saveProjectWhenCustomerDoesNotExistThenThrowException() {
        var request = new ProjectCreateRequest(
            "Project description",
            1L,
            100L
        );

        when(timeRepository.findById(request.teamId())).thenReturn(Optional.of(new TimeEntity()));
        when(clientesRepository.findById(request.customerId())).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(RuntimeException.class, () -> projetosService.save(request));

        // Verify
        verify(projetosRepository, never()).saveAndFlush(any());
        verify(atividadesRepository, never()).saveAndFlush(any());
        verify(atividadeProjetoClienteRepository, never()).saveAndFlush(any());
    }

    @Test
    @DisplayName("Should return an empty ProjectResponse when the project does not exist")
    void deleteWhenProjectDoesNotExist() {
        long projectId = 1L;
        when(projetosRepository.findById(projectId)).thenReturn(Optional.empty());

        var response = projetosService.delete(projectId);

        assertNotNull(response);
        assertEquals(0, response.project().getId());
        assertNull(response.customer());
        assertNotNull(response.projectActivities());
        assertEquals(0, response.projectActivities().size());

        verify(projetosRepository, times(1)).findById(projectId);
        verify(atividadeProjetoClienteRepository, never()).findByProjeto_Id(projectId);
        verify(atividadesRepository, never()).findAllByAssocList(any());
        verify(projetosRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Should delete the project and associated projectActivities when the project exists")
    void deleteWhenProjectExists() {
        var projectId = 1L;
        var project = new ProjetosEntity();
        project.setId(projectId);

        var assocList = new ArrayList<AtividadeProjetoClienteEntity>();
        var assoc1 = new AtividadeProjetoClienteEntity();
        assoc1.setId(1L);
        assoc1.setProjeto(project);
        assocList.add(assoc1);

        var activities = new ArrayList<AtividadesEntity>();
        var activity1 = new AtividadesEntity();
        activity1.setId(1L);
        activity1.setDescricao("Activity 1");
        activities.add(activity1);

        when(projetosRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(atividadeProjetoClienteRepository.findByProjeto_Id(projectId)).thenReturn(assocList);
        when(atividadesRepository.findAllByAssocList(assocList)).thenReturn(activities);

        var response = projetosService.delete(projectId);

        assertNotNull(response);
        assertEquals(project, response.project());
        assertNull(response.customer());
        assertEquals(activities, response.projectActivities());

        verify(atividadeProjetoClienteRepository, times(1)).deleteAll(assocList);
        verify(atividadesRepository, times(1)).deleteAll(activities);
        verify(projetosRepository, times(1)).delete(project);
    }

    @Test
    @DisplayName("Should throw an exception when projectId is null or zero")
    void updateProjectWhenProjectIdIsNullThenThrowException() {
        var request = new ProjectUpdateRequest(
            null, "New Description", 1L, 1L, 1L);

        // When/Then
        assertThrows(RuntimeException.class, () -> {
            projetosService.update(request);
        });
    }

    @Test
    @DisplayName("Should throw an exception when teamId is null or zero")
    void updateProjectWhenTeamIdIsNullThenThrowException() {
        var request = new ProjectUpdateRequest(
            1L, "Updated Description", 1L, null, 1L);

        // When/Then
        assertThrows(RuntimeException.class, () -> {
            projetosService.update(request);
        });
    }

    @Test
    @DisplayName("Should throw an exception when customerId is null or zero")
    void updateProjectWhenCustomerIdIsNullThenThrowException() {
        var projectId = 1L;
        Long customerId = null;
        var request = new ProjectUpdateRequest(
            projectId, "Updated Description", 2L, 3L, customerId);

        // Act and Assert
        assertThrows(RuntimeException.class, () -> {
            projetosService.update(request);
        });
    }

    @Test
    @DisplayName("Should throw an exception when project does not exist")
    void updateProjectWhenProjectDoesNotExistThenThrowException() {
        var projectId = 1L;
        var request = new ProjectUpdateRequest(
            projectId, "Updated Description", 2L, 3L, 4L);
        when(projetosRepository.findById(projectId)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(RuntimeException.class, () -> {
            projetosService.update(request);
        });

        // Verify
        verify(projetosRepository, times(1)).findById(projectId);
        verifyNoMoreInteractions(projetosRepository);
        verifyNoInteractions(clientesRepository, atividadesRepository, atividadeProjetoClienteRepository,
            statusRepository, timeRepository);
    }

    @Test
    @DisplayName("Should update the project when valid request is provided")
    void updateProjectWhenValidRequestIsProvided() {        // Create a valid request
        var request = new ProjectUpdateRequest(
            1L, "Updated Description", 2L, 3L, 4L);

        // Create a mock project
        var project = new ProjetosEntity(
            1L, "Old Description", new StatusEntity(), new TimeEntity());

        // Create a mock team
        var team = new TimeEntity();

        // Create a mock status
        var status = new StatusEntity();

        // Create a mock customer
        var customer = new ClientesEntity(4L, "Customer Name");

        // Create a mock activity
        var activity = new AtividadesEntity(
            "Activity Description", BigDecimal.ZERO, new StatusEntity());

        // Create a mock updated project
        var updatedProject = new ProjetosEntity(
            1L, "Updated Description", status, team);

        // Mock the repository methods
        when(projetosRepository.findById(request.id())).thenReturn(Optional.of(project));
        when(timeRepository.findById(request.teamId())).thenReturn(Optional.of(team));
        when(statusRepository.findById(request.statusId())).thenReturn(Optional.of(status));
        when(clientesRepository.findById(request.customerId())).thenReturn(Optional.of(customer));
        when(projetosRepository.saveAndFlush(any(ProjetosEntity.class))).thenReturn(updatedProject);

        // Call the update method
        var response = projetosService.update(request);

        // Verify the repository method calls
        verify(projetosRepository, times(1)).findById(request.id());
        verify(timeRepository, times(1)).findById(request.teamId());
        verify(statusRepository, times(1)).findById(request.statusId());
        verify(clientesRepository, times(1)).findById(request.customerId());
        verify(projetosRepository, times(1)).saveAndFlush(any(ProjetosEntity.class));

        // Verify the response
        assertNotNull(response);
        assertEquals(updatedProject, response.project());
        assertEquals(customer, response.customer());
        assertNull(response.projectActivities());
    }

    @Test
    @DisplayName("Should throw an exception when the teamId is zero")
    void saveProjectWhenTeamIdIsZeroThenThrowException() {
        var request = new ProjectCreateRequest(
            "Project description", 0L, null);

        assertThrows(RuntimeException.class, () -> {
            projetosService.save(request);
        });

        verifyNoInteractions(projetosRepository);
        verifyNoInteractions(clientesRepository);
        verifyNoInteractions(atividadesRepository);
        verifyNoInteractions(atividadeProjetoClienteRepository);
        verifyNoInteractions(statusRepository);
        verifyNoInteractions(timeRepository);
    }

    @Test
    @DisplayName("Should save the project when the customerId is valid and exists in the database")
    void saveProjectWhenCustomerIdIsValidAndExists() {
        var request = new ProjectCreateRequest(
            "Project description",
            1L,
            1L
        );

        var team = new TimeEntity();
        when(timeRepository.findById(request.teamId())).thenReturn(Optional.of(team));

        var customer = new ClientesEntity(1L, "Customer");
        when(clientesRepository.findById(request.customerId())).thenReturn(Optional.of(customer));

        var status = new StatusEntity();
        when(statusRepository.findById(DomainConstants.ON_CREATE_STATUS_ID)).thenReturn(Optional.of(status));

        var project = new ProjetosEntity(1L, "Project description", status, team);
        when(projetosRepository.saveAndFlush(any(ProjetosEntity.class))).thenReturn(project);

        var activity = new AtividadesEntity("Activity description", BigDecimal.ZERO, status);
        when(atividadesRepository.saveAndFlush(any(AtividadesEntity.class))).thenReturn(activity);

        AtividadeProjetoClienteEntity assoc = new AtividadeProjetoClienteEntity(customer, project, activity);
        when(atividadeProjetoClienteRepository.saveAndFlush(any(AtividadeProjetoClienteEntity.class))).thenReturn(assoc);

        var response = projetosService.save(request);

        assertNotNull(response);
        assertEquals(project, response.project());
        assertEquals(customer, response.customer());
        assertEquals(List.of(activity), response.projectActivities());

        verify(timeRepository, times(1)).findById(request.teamId());
        verify(clientesRepository, times(1)).findById(request.customerId());
        verify(statusRepository, times(1)).findById(DomainConstants.ON_CREATE_STATUS_ID);
        verify(projetosRepository, times(1)).saveAndFlush(any(ProjetosEntity.class));
        verify(atividadesRepository, times(1)).saveAndFlush(any(AtividadesEntity.class));
        verify(atividadeProjetoClienteRepository, times(1)).saveAndFlush(any(AtividadeProjetoClienteEntity.class));
    }

    @Test
    @DisplayName("Should throw an exception when the customer id is null or zero")
    void getAllProjetosByProjectRequestWhenCustomerIdIsNullThenThrowException() {
        var projectListRequest = new ProjectListRequest(
            "Project description", null, 0, 10, "asc"
        );
        var pageable = PageRequest.of(0, 10);

        assertThrows(RuntimeException.class, () -> {
            projetosService.getAllProjetosByProjectRequest(projectListRequest, pageable);
        });
    }

    @Test
    @DisplayName("Should return an empty page when there are no projectActivities for the given customer id")
    void getAllProjetosByProjectRequestWhenNoActivitiesThenReturnEmptyPage() {
        var projectListRequest = new ProjectListRequest(
            "Test Description", 1L, 0, 10, "asc"
        );
        var pageable = PageRequest.of(0, 10);

        var activities = new ArrayList<AtividadeProjetoClienteEntity>();

        when(atividadeProjetoClienteRepository.findByCliente_Id(projectListRequest.customerId()))
            .thenReturn(activities);

        var result = projetosService.getAllProjetosByProjectRequest(projectListRequest, pageable);

        assertTrue(result.isEmpty());
        verify(atividadeProjetoClienteRepository, times(1)).findByCliente_Id(projectListRequest.customerId());
        verify(projetosRepository, never()).findAllByAssocList(anyList(), any(Pageable.class));
    }

    @Test
    @DisplayName("Should return a page of projects when there are projectActivities for the given customer id")
    void getAllProjetosByProjectRequestWhenActivitiesExistThenReturnProjectsPage() {
        var projectListRequest = new ProjectListRequest(
            "Project description", 1L, 0, 10, "asc"
        );
        var pageable = PageRequest.of(0, 10);

        var activities = new ArrayList<AtividadeProjetoClienteEntity>();
        activities.add(new AtividadeProjetoClienteEntity(
            new ClientesEntity(1L, "Customer Name"),
            new ProjetosEntity(1L, "Project Description", new StatusEntity(), new TimeEntity()),
            new AtividadesEntity("Activity Description", BigDecimal.ZERO, new StatusEntity())
        ));

        when(atividadeProjetoClienteRepository.findByCliente_Id(projectListRequest.customerId())).thenReturn(activities);
        when(projetosRepository.findAllByAssocList(activities, pageable)).thenReturn(new PageImpl<>(List.of(new ProjetosEntity())));

        var result = projetosService.getAllProjetosByProjectRequest(projectListRequest, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        verify(atividadeProjetoClienteRepository, times(1)).findByCliente_Id(projectListRequest.customerId());
        verify(projetosRepository, times(1)).findAllByAssocList(activities, pageable);
    }
}
