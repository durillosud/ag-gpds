package br.com.gpds.web.rest;

import br.com.gpds.domain.common.DomainConstants;
import br.com.gpds.domain.request.ProjectCreateRequest;
import br.com.gpds.domain.request.ProjectListRequest;
import br.com.gpds.domain.request.ProjectUpdateRequest;
import br.com.gpds.domain.response.ProjectResponse;
import br.com.gpds.service.ProjetosService;
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
public class ProjetosResource {

    private final ProjetosService projetosService;

    public ProjetosResource(ProjetosService projetosService) {
        this.projetosService = projetosService;
    }

    @GetMapping(
        value = DomainConstants.PROJECTS + "ativos/por-status/{statusId}/listar",
        produces = {"application/json"}
    )
    @Operation(
        summary = "Lista paginada de projetos em aberto (não finalizados) de clientes, por `status`",
        description =
            "Lista paginada de projetos em aberto (não finalizados) de clientes, por `status`:<br/>" +
                "<ul><li>filtro por `cliente`;</li><br/>" +
                "<li>paginação `nº da página (page)`, `tamanho da página (pageSize)`, " +
                "`tipo de ordenação (order), exemplo [asc, desc]`;" +
                "</li></ul>",
        operationId = "getActiveProjectsByStatusIdFilteredByCustomerSortedAndPaginated",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "204", description = "No Content"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
        }
    )
    public Page<ProjectResponse> getActiveProjectsByStatusIdFilteredByCustomerSortedAndPaginated(
        @Parameter(description = "Identificador do `status` do projeto")
        @PathVariable("statusId") Long statusId,
        @Parameter(description = "Identificador do Product Owner do projeto")
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
            var orderField = projetosService.getStatusDescriptionById(statusId);
            var sort = orderField.isEmpty()
                ? Sort.unsorted()
                : WebResourceUtils.getOrdersWhenOrderFieldIsNotEmpty(orderField, order);

            customerId = getNullableCustomerId(customerId);

            var projectResponse = projetosService.getProjectsByStatusId(
                statusId, Optional.ofNullable(customerId), PageRequest.of(page, pageSize, sort)
            );

            if (!projectResponse.hasContent()) {
                response.setStatus(HttpStatus.NO_CONTENT.value());
            }

            return projectResponse;
        } catch (RuntimeException e) {
            throw new BadRequestAlertException(e.getMessage(), getClass().getName(), e.getLocalizedMessage());
        }
    }

    @PostMapping(value = DomainConstants.PROJECTS + "listar", produces = {"application/json"})
    @Operation(
        summary = "Lista paginada de projetos por clientes",
        description =
            "Lista paginada de projetos por `Product Owners (clientes)`:<br/>" +
                "<ul><li>paginação `nº da página (page)`, `tamanho da página (pageSize)`, " +
                "`tipo de ordenação (order), exemplo [asc, desc]`;" +
                "</li></ul>",
        operationId = "getProjectsByProjectRequestSortedAndPaginated",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "204", description = "No Content"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
        }
    )
    public Page<ProjectResponse> getProjectsByProjectRequestSortedAndPaginated(
        @RequestBody @Valid ProjectListRequest projectListRequest,
        HttpServletRequest request,
        final HttpServletResponse response
    ) throws ErrorResponseException {
        try {
            var header = request.getHeader(HttpHeaders.AUTHORIZATION);
            var sort = projectListRequest.description().isEmpty() ? Sort.unsorted() :
                WebResourceUtils.getOrdersWhenOrderFieldIsNotEmpty(
                    projectListRequest.description(), projectListRequest.order()
                );

            var projectResponse = projetosService.getAllProjetosByProjectRequest(
                projectListRequest, PageRequest.of(projectListRequest.page(), projectListRequest.pageSize(), sort)
            );

            if (!projectResponse.hasContent()) {
                response.setStatus(HttpStatus.NO_CONTENT.value());
            }

            return projectResponse;
        } catch (RuntimeException e) {
            throw new BadRequestAlertException(e.getMessage(), getClass().getName(), e.getLocalizedMessage());
        }
    }

    @PostMapping(value = DomainConstants.PROJECT + "criar", produces = {"application/json"})
    @Operation(
        summary = "Salva um novo projeto para um cliente e um time de desenvolvimento na base de dados",
        description =
            "Tenta salvar um novo projeto para um cliente e um time de desenvolvimento na base de dados",
        operationId = "saveProject",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error"),
        }
    )
    public ProjectResponse saveProject(
        @RequestBody @Valid ProjectCreateRequest projectCreateRequest,
        HttpServletRequest request,
        HttpServletResponse response
    ) throws ErrorResponseException {
        try {
            var header = request.getHeader(HttpHeaders.AUTHORIZATION);

            var savedProject = projetosService.save(
                new ProjectCreateRequest(
                    projectCreateRequest.description(),
                    projectCreateRequest.teamId(),
                    getNullableCustomerId(projectCreateRequest.customerId())
                )
            );
            Optional.ofNullable(savedProject)
                .flatMap(
                    entity -> Optional.of(entity.project())
                )
                .ifPresentOrElse(
                    aLong -> response.setStatus(HttpStatus.CREATED.value()),
                    () -> response.setStatus(HttpStatus.NOT_MODIFIED.value())
                );

            return savedProject;
        } catch (RuntimeException e) {
            throw new BadRequestAlertException(e.getMessage(), getClass().getName(), e.getLocalizedMessage());
        }
    }

    private static Long getNullableCustomerId(Long customerId) {
        return 0L == customerId ? null : customerId;
    }

    @PostMapping(value = DomainConstants.PROJECT + "atualizar", produces = {"application/json"})
    @Operation(
        summary = "Atualiza os dados de um projeto de desenvolvimento na base de dados",
        description =
            "Tenta atualizar os dados de um projeto de desenvolvimento na base de dados",
        operationId = "updateProject",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error"),
        }
    )
    public ProjectResponse updateProject(
        @RequestBody @Valid ProjectUpdateRequest projectUpdateRequest,
        HttpServletRequest request,
        HttpServletResponse response
    ) {
        try {
            var header = request.getHeader(HttpHeaders.AUTHORIZATION);

            return projetosService.update(projectUpdateRequest);
        } catch (RuntimeException e) {
            throw new BadRequestAlertException(e.getMessage(), getClass().getName(), e.getLocalizedMessage());
        }
    }

    @PostMapping(value = DomainConstants.PROJECT + "{id}/deletar", produces = {"application/json"})
    @Operation(
        summary = "Apaga os dados de um projeto de um cliente da base de dados",
        description =
            "Tenta apagar os dados de um projeto de um cliente da base de dados",
        operationId = "deleteProject",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error"),
        }
    )
    public ProjectResponse deleteProject(
        @PathVariable("id")
        @NotNull(message = "O parâmetro não pode ser nulo")
        @Min(value = 1, message = "O parâmetro deve ser maior que zero") Long id,
        HttpServletRequest request,
        HttpServletResponse response
    ) {
        try {
            var header = request.getHeader(HttpHeaders.AUTHORIZATION);

            return projetosService.delete(id);
        } catch (RuntimeException e) {
            throw new BadRequestAlertException(e.getMessage(), getClass().getName(), e.getMessage());
        }
    }
}
