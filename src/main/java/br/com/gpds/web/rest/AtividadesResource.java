package br.com.gpds.web.rest;

import br.com.gpds.domain.common.DomainConstants;
import br.com.gpds.domain.request.ActivityCreateRequest;
import br.com.gpds.domain.request.ActivityUpdateRequest;
import br.com.gpds.domain.response.ActivityResponse;
import br.com.gpds.service.AtividadesService;
import br.com.gpds.web.rest.errors.BadRequestAlertException;
import br.com.gpds.web.utils.WebResourceUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping(DomainConstants.APP_PATH)
@Validated
public class AtividadesResource {
    private final AtividadesService atividadesService;

    public AtividadesResource(AtividadesService atividadesService) {
        this.atividadesService = atividadesService;
    }

    @GetMapping(value = DomainConstants.ACTIVITIES + "por-status/{statusId}/listar", produces = {"application/json"})
    @Operation(
        summary = "Lista paginada de atividades de projetos de clientes, por estado",
        description =
            "Lista paginada de atividades de por projetos por estado:<br/>" +
                "<ul><li>paginação `nº da página (page)`, `tamanho da página (pageSize)`, " +
                "`tipo de ordenação (order), exemplo [asc, desc]`;" +
                "</li></ul>",
        operationId = "getActivitiesByStatusIdAndCustomerIdSortedAndPaginated",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "204", description = "No Content"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
        }
    )
    public Page<ActivityResponse> getActivitiesByStatusIdAndCustomerIdSortedAndPaginated(
        @Parameter(description = "Identificador do `status` da atividade do projeto")
        @PathVariable("statusId") Long statusId,
        @Parameter(description = "Identificador do Product Owner relativo à atividade do projeto")
        @RequestParam(value = "customerId", required = false) Long customerId,
        @Parameter(description = "Número da página")
        @RequestParam(required = false, defaultValue = "0") int page,
        @Parameter(description = "Quantidade de itens por página")
        @RequestParam(required = false, defaultValue = "10") int pageSize,
        @Parameter(description = "Tipo de ordenação", schema = @Schema(format = "enum"))
        @RequestParam(required = false, defaultValue = "asc") String order,
        HttpServletRequest request,
        final HttpServletResponse response
    ) throws ErrorResponseException {
        try {
            var header = request.getHeader(HttpHeaders.AUTHORIZATION);
            var orderField = atividadesService.getStatusDescriptionById(statusId);
            var sort = orderField.isEmpty()
                ? Sort.unsorted()
                : WebResourceUtils.getOrdersWhenOrderFieldIsNotEmpty(orderField, order);

            customerId = getNullableCustomerId(customerId);

            var activityResponse = atividadesService.getActivitiesByStatusIdAndCustomerIdPaginated(
                statusId, Optional.ofNullable(customerId), PageRequest.of(page, pageSize, sort)
            );

            if (!activityResponse.hasContent()) {
                response.setStatus(HttpStatus.NO_CONTENT.value());
            }

            return activityResponse;
        } catch (RuntimeException e) {
            throw new BadRequestAlertException(e.getMessage(), getClass().getName(), e.getLocalizedMessage());
        }
    }

    private static Long getNullableCustomerId(Long customerId) {
        customerId = 0L == customerId ? null : customerId;
        return customerId;
    }

    @PostMapping(value = DomainConstants.ACTIVITY + "criar", produces = {"application/json"})
    @Operation(
        summary = "Salva uma nova atividade para um projeto de um cliente na base de dados",
        description =
            "Tenta salvar uma nova atividade para um projeto de um cliente na base de dados",
        operationId = "saveActivity",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error"),
        }
    )
    public ActivityResponse saveActivity(
        @RequestBody @Valid ActivityCreateRequest activityCreateRequest,
        HttpServletRequest request,
        HttpServletResponse response
    ) throws ErrorResponseException {
        try {
            var header = request.getHeader(HttpHeaders.AUTHORIZATION);

            response.setStatus(HttpStatus.NOT_MODIFIED.value());

            var activityResponse = atividadesService.save(
                activityCreateRequest
            );
            response.setStatus(HttpStatus.CREATED.value());

            return activityResponse;
        } catch (RuntimeException e) {
            throw new BadRequestAlertException(e.getMessage(), getClass().getName(), e.getLocalizedMessage());
        }
    }

    @PostMapping(value = DomainConstants.ACTIVITY + "atualizar", produces = {"application/json"})
    @Operation(
        summary = "Atualiza os dados de uma atividade de projeto de desenvolvimento na base de dados",
        description =
            "Tenta atualizar os dados de uma atividade de um projeto de desenvolvimento na base de dados",
        operationId = "updateActivity",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error"),
        }
    )
    public ActivityResponse updateActivity(
        @RequestBody @Valid ActivityUpdateRequest activityUpdateRequest,
        HttpServletRequest request,
        HttpServletResponse response
    ) {
        try {
            var header = request.getHeader(HttpHeaders.AUTHORIZATION);

            return atividadesService.update(activityUpdateRequest);
        } catch (RuntimeException e) {
            throw new BadRequestAlertException(e.getMessage(), getClass().getName(), e.getLocalizedMessage());
        }
    }

    @PostMapping(value = DomainConstants.ACTIVITY + "{id}/deletar", produces = {"application/json"})
    @Operation(
        summary = "Apaga os dados de uma atividade de um projeto de um cliente da base de dados",
        description =
            "Tenta apagar os dados de uma atividade de um projeto de um cliente da base de dados",
        operationId = "deleteActivity",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error"),
        }
    )
    public ActivityResponse deleteActivity(
        @PathVariable("id")
        @NotNull(message = "O parâmetro não pode ser nulo")
        @Min(value = 1, message = "O parâmetro deve ser maior que zero") Long id,
        HttpServletRequest request,
        HttpServletResponse response
    ) {
        try {
            var header = request.getHeader(HttpHeaders.AUTHORIZATION);

            return atividadesService.delete(id);
        } catch (RuntimeException e) {
            throw new BadRequestAlertException(e.getMessage(), getClass().getName(), e.getMessage());
        }
    }
}
