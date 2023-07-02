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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AtividadesServiceTest {

    @Mock
    private AtividadesRepository atividadesRepository;

    @Mock
    private StatusRepository statusRepository;

    @Mock
    private ClientesRepository clientesRepository;

    @Mock
    private AtividadeProjetoClienteRepository atividadeProjetoClienteRepository;

    @Mock
    private ProjetosRepository projetosRepository;

    @InjectMocks
    private AtividadesService atividadesService;


    @Test
    @DisplayName("Should return empty page when no activities found for given status id and customer id")
    void getActivitiesByStatusIdAndCustomerIdPaginatedWhenNoActivitiesFound() {
        var statusId = 1L;
        var customerId = Optional.<Long>of(1L);
        var pageable = PageRequest.of(0, 10);

        when(clientesRepository.existsById(customerId.get())).thenReturn(true);
        when(
            atividadesRepository.findAllByStatus_IdAndOptCustomer_IdOrderByDescricao(statusId, customerId, pageable)
        ).thenReturn(new PageImpl<>(List.of()));

        var result = atividadesService.getActivitiesByStatusIdAndCustomerIdPaginated(statusId, customerId, pageable);

        assertThat(result).isEmpty();
        verify(clientesRepository, times(1)).existsById(customerId.get());
        verify(atividadesRepository, times(1))
            .findAllByStatus_IdAndOptCustomer_IdOrderByDescricao(statusId, customerId, pageable);
    }

    @Test
    @DisplayName("Should return paginated activities by status id and customer id when both are present")
    void getActivitiesByStatusIdAndCustomerIdPaginatedWhenBothArePresent() {
        var statusId = 1L;
        var customerId = 1L;
        var pageable = PageRequest.of(0, 10);

        var statusEntity = new StatusEntity(statusId, "In Progress");
        var customerEntity = new ClientesEntity(customerId, "John Doe");
        var projectEntity = new ProjetosEntity(1L, "Project 1");
        var atividadesEntity = new AtividadesEntity(1L, "Activity 1", BigDecimal.valueOf(50), statusEntity);
        var atividadesList = List.of(atividadesEntity);
        var atividadesPage = new PageImpl<>(atividadesList, pageable, atividadesList.size());

        when(atividadesRepository.findAllByStatus_IdAndOptCustomer_IdOrderByDescricao(
                statusId, Optional.of(customerId), pageable
            )
        ).thenReturn(atividadesPage);

        var result = atividadesService.getActivitiesByStatusIdAndCustomerIdPaginated(
            statusId, Optional.of(customerId), pageable
        );

        assertEquals(atividadesList.size(), result.getContent().size());
        assertEquals(atividadesList.get(0).getId(), result.getContent().get(0).activity().getId());

        verify(atividadesRepository, times(1))
            .findAllByStatus_IdAndOptCustomer_IdOrderByDescricao(statusId, Optional.of(customerId), pageable);
    }

    @Test
    @DisplayName("Should throw a RuntimeException when the status id does not exist")
    void getStatusDescriptionByIdWhenStatusIdDoesNotExistThenThrowRuntimeException() {
        var statusId = 10L;
        when(statusRepository.existsById(statusId)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> atividadesService.getStatusDescriptionById(statusId));

        verify(statusRepository, times(1)).existsById(statusId);
    }

    @Test
    @DisplayName("Should return the status description when the status id exists")
    void getStatusDescriptionByIdWhenStatusIdExists() {
        var statusId = 1L;
        String statusDescription = "In Progress";

        when(statusRepository.existsById(statusId)).thenReturn(true);
        when(statusRepository.findStatusDescriptionByStatusId(statusId)).thenReturn(statusDescription);

        var result = atividadesService.getStatusDescriptionById(statusId);

        assertEquals(statusDescription, result);
        verify(statusRepository, times(1)).existsById(statusId);
        verify(statusRepository, times(1)).findStatusDescriptionByStatusId(statusId);
    }

    @Test
    @DisplayName("Should throw an exception when the project does not exist")
    void updateWhenProjectDoesNotExistThenThrowException() {
        var activityId = 1L;
        var projectId = 1L;
        var customerId = 1L;
        var statusId = 1L;
        var description = "Updated activity";
        var percentage = new BigDecimal("50.0");

        var activityUpdateRequest = new ActivityUpdateRequest(
            activityId, statusId, description, percentage, projectId, customerId
        );

        when(atividadesRepository.findById(activityId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> atividadesService.update(activityUpdateRequest));

        verify(atividadesRepository, never()).saveAndFlush(any(AtividadesEntity.class));
    }

    @Test
    @DisplayName("Should throw an exception when the Product Owner does not exist")
    void updateWhenProductOwnerDoesNotExistThenThrowException() {
        var activityId = 1L;
        var statusId = 2L;
        var projectId = 3L;
        var customerId = 4L;
        var description = "Updated activity";
        var percentage = new BigDecimal("50.0");

        var activityUpdateRequest = new ActivityUpdateRequest(
            activityId, statusId, description, percentage, projectId, customerId
        );

        when(atividadesRepository.findById(activityId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> atividadesService.update(activityUpdateRequest));

        verify(atividadesRepository, never()).saveAndFlush(any(AtividadesEntity.class));
    }

    @Test
    @DisplayName("Should throw an exception when the activity is not an activity of the project")
    void updateWhenActivityIsNotOfProjectThenThrowException() {
        var activityId = 1L;
        var projectId = 1L;
        var customerId = 1L;
        var statusId = 1L;
        var description = "Updated activity";
        var percentage = new BigDecimal("50.0");

        var activityUpdateRequest = new ActivityUpdateRequest(
            activityId, statusId, description, percentage, projectId, customerId
        );

        var activity = new AtividadesEntity(activityId, "Activity", new BigDecimal("25.0"), new StatusEntity(statusId));
        when(atividadesRepository.findById(activityId)).thenReturn(Optional.of(activity));
        when(projetosRepository.findById(projectId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> atividadesService.update(activityUpdateRequest));

        verify(atividadesRepository, never()).saveAndFlush(any(AtividadesEntity.class));
    }

    @Test
    @DisplayName("Should throw an exception when the customer does not exist")
    void saveActivityWhenCustomerDoesNotExistThenThrowException() {
        var activityCreateRequest = new ActivityCreateRequest(
            "Test Activity", BigDecimal.valueOf(50.0), 1L, 1L
        );

        when(clientesRepository.findById(activityCreateRequest.customerId())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> atividadesService.save(activityCreateRequest));

        verify(clientesRepository, times(1)).findById(activityCreateRequest.customerId());
        verify(atividadesRepository, never()).saveAndFlush(any(AtividadesEntity.class));
    }

    @Test
    @DisplayName("Should throw an exception when the project does not exist")
    void saveActivityWhenProjectDoesNotExistThenThrowException() {
        var activityCreateRequest = new ActivityCreateRequest(
            "Test Activity", BigDecimal.valueOf(50.0), 1L, 1L);

        assertThrows(RuntimeException.class, () -> atividadesService.save(activityCreateRequest));

        verify(atividadesRepository, never()).saveAndFlush(any(AtividadesEntity.class));
    }

    @Test
    @DisplayName("Should throw an exception when the project is not owned by the customer")
    void saveActivityWhenProjectIsNotOwnedByCustomerThenThrowException() {
        var activityCreateRequest = new ActivityCreateRequest(
            "Test Activity", BigDecimal.valueOf(50.0), 1L, 1L);

        when(statusRepository.findById(DomainConstants.STARTED_STATUS_ID))
            .thenReturn(Optional.of(new StatusEntity(DomainConstants.STARTED_STATUS_ID)));

        when(clientesRepository.findById(activityCreateRequest.customerId()))
            .thenReturn(Optional.of(new ClientesEntity(activityCreateRequest.customerId(), "Customer Name")));

        when(projetosRepository.findById(activityCreateRequest.projectId()))
            .thenReturn(Optional.of(new ProjetosEntity(activityCreateRequest.projectId(), "Project Description")));

        when(atividadeProjetoClienteRepository.existsByClienteAndProjeto(any(), any()))
            .thenReturn(false);

        assertThrows(RuntimeException.class, () -> atividadesService.save(activityCreateRequest));

        verify(atividadesRepository, never()).saveAndFlush(any());
    }

    @Test
    @DisplayName("Should save the activity when the customer and project exist and the project is owned by the customer")
    void saveActivityWhenCustomerAndProjectExistAndProjectIsOwnedByCustomer() {
        var activityCreateRequest = new ActivityCreateRequest(
            "Test Activity", BigDecimal.valueOf(50.0), 1L, 1L);

        var status = new StatusEntity(1L, "Started");
        when(statusRepository.findById(DomainConstants.STARTED_STATUS_ID)).thenReturn(Optional.of(status));

        var customer = new ClientesEntity(1L, "John Doe");
        when(clientesRepository.findById(activityCreateRequest.customerId())).thenReturn(Optional.of(customer));

        var project = new ProjetosEntity(1L, "Test Project");
        when(projetosRepository.findById(activityCreateRequest.projectId())).thenReturn(Optional.of(project));

        when(atividadeProjetoClienteRepository.existsByClienteAndProjeto(customer, project)).thenReturn(true);

        var savedActivity = new AtividadesEntity(1L, "Test Activity", BigDecimal.valueOf(50.0), status);
        when(atividadesRepository.saveAndFlush(any(AtividadesEntity.class))).thenReturn(savedActivity);

        var result = atividadesService.save(activityCreateRequest);

        assertNotNull(result);
        assertEquals(savedActivity, result.activity());
        assertEquals(project, result.project());
        assertEquals(customer, result.customer());

        verify(statusRepository, times(1)).findById(DomainConstants.STARTED_STATUS_ID);
        verify(clientesRepository, times(1)).findById(activityCreateRequest.customerId());
        verify(projetosRepository, times(1)).findById(activityCreateRequest.projectId());
        verify(atividadeProjetoClienteRepository, times(1)).existsByClienteAndProjeto(customer, project);
        verify(atividadesRepository, times(1)).saveAndFlush(any(AtividadesEntity.class));
    }

    @Test
    @DisplayName("Should throw an exception when the project does not belong to the Product Owner")
    void updateWhenProjectDoesNotBelongToProductOwnerThenThrowException() {
        var activityId = 1L;
        var statusId = 2L;
        var projectId = 3L;
        var customerId = 4L;

        var activityUpdateRequest = new ActivityUpdateRequest(
            activityId, statusId, "Updated Description", null, projectId, customerId
        );

        var activity = new AtividadesEntity(
            activityId, "Description", null, null
        );

        when(atividadesRepository.findById(activityId)).thenReturn(Optional.of(activity));

        assertThrows(
            RuntimeException.class,
            () -> atividadesService.update(activityUpdateRequest)
        );

        verify(atividadesRepository, never()).saveAndFlush(any());
    }

    @Test
    @DisplayName("Should return an empty response when the activity does not exist")
    void deleteWhenActivityDoesNotExist() {
        var id = 1L;
        when(atividadesRepository.findById(id)).thenReturn(Optional.empty());

        var response = atividadesService.delete(id);

        assertThat(response.activity()).isEqualTo(new AtividadesEntity());
        assertThat(response.project()).isEqualTo(new ProjetosEntity());
        assertThat(response.customer()).isEqualTo(new ClientesEntity());
        verify(atividadesRepository, times(1)).findById(id);
        verify(atividadeProjetoClienteRepository, never()).findByAtividade_Id(anyLong());
        verify(atividadeProjetoClienteRepository, never()).deleteAll(anyList());
        verify(atividadesRepository, never()).delete(any(AtividadesEntity.class));
    }

    @Test
    @DisplayName("Should delete the activity and return the response when the activity exists")
    void deleteWhenActivityExists() {
        var activityId = 1L;
        var activity = new AtividadesEntity();
        activity.setId(activityId);

        var project = new ProjetosEntity();
        var customer = new ClientesEntity();

        when(atividadesRepository.findById(activityId)).thenReturn(Optional.of(activity));
        when(atividadeProjetoClienteRepository.findByAtividade_Id(activityId)).thenReturn(List.of());

        var response = atividadesService.delete(activityId);

        assertNotNull(response);
        assertEquals(activity, response.activity());
        assertEquals(project, response.project());
        assertEquals(customer, response.customer());

        verify(atividadeProjetoClienteRepository, times(1)).deleteAll(anyList());
        verify(atividadesRepository, times(1)).delete(activity);
    }
}
