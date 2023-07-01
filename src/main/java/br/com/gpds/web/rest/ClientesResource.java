package br.com.gpds.web.rest;


import br.com.gpds.domain.ClientesEntity;
import br.com.gpds.domain.common.DomainConstants;
import br.com.gpds.domain.query.filter.ClientesFilter;
import br.com.gpds.domain.request.CustomerRequest;
import br.com.gpds.domain.response.CustomerResponse;
import br.com.gpds.service.ClientesService;
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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping(DomainConstants.APP_PATH)
@Validated
public class ClientesResource {

    private final ClientesService clientesService;

    public ClientesResource(ClientesService clientesService) {
        this.clientesService = clientesService;
    }

    @GetMapping(value = DomainConstants.CUSTOMERS + "listar", produces = {"application/json"})
    @Operation(
        summary = "Lista paginada com clientes",
        description =
            "Lista paginada com clientes:<br/>" +
                "<ul><li>filtro por `nome`;</li><br/>" +
                "<li>paginação `nº da página (page)`, `tamanho da página (pageSize)`, " +
                "`tipo de ordenação (order), exemplo [asc, desc]`;" +
                "</li></ul>",
        operationId = "getAllCustomersFilteredAndPaginated",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
        }
    )
    public Page<ClientesEntity> getAllCustomersFilteredAndPaginated(
        @Parameter(description = "Nome do cliente")
        @RequestParam(required = false, defaultValue = "") String name,
        @Parameter(description = "Número da página")
        @RequestParam(required = false, defaultValue = "0") int page,
        @Parameter(description = "Quantidade de itens por página")
        @RequestParam(required = false, defaultValue = "10") int pageSize,
        @Parameter(description = "Tipo de ordenação", schema = @Schema(format = "enum"))
        @RequestParam(required = false, defaultValue = "asc") String order,
        HttpServletRequest request,
        HttpServletResponse response
    ) throws ErrorResponseException {
        try {
            var header = request.getHeader(HttpHeaders.AUTHORIZATION);
            var sort = name.isEmpty() ? Sort.unsorted() : WebResourceUtils.getOrdersWhenOrderFieldIsNotEmpty(name, order);

            return clientesService.getAllClientesByFilters(
                new ClientesFilter(null, name), PageRequest.of(page, pageSize, sort)
            );
        } catch (BadRequestAlertException e) {
            throw new BadRequestAlertException(e.getMessage(), getClass().getName(), e.getDetailMessageCode());
        }
    }

    @PostMapping(value = DomainConstants.CUSTOMERS + "criar", produces = {"application/json"})
    @Operation(
        summary = "Salva um novo cliente na base de dados",
        description =
            "Tenta salvar um novo cliente na base de dados",
        operationId = "saveCustomer",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error"),
        }
    )
    public ClientesEntity saveCustomer(
        @RequestBody @Valid CustomerRequest customerRequest,
        HttpServletRequest request,
        HttpServletResponse response
    ) throws ErrorResponseException {
        try {
            var header = request.getHeader(HttpHeaders.AUTHORIZATION);

            var savedCustomer = clientesService.saveCustomer(
                new CustomerRequest(null, customerRequest.name())
            );
            Optional.ofNullable(savedCustomer)
                .ifPresent(clientesEntity ->
                    Optional.ofNullable(clientesEntity.getId())
                        .ifPresent(aLong -> response.setStatus(HttpStatus.CREATED.value()))
                );

            return savedCustomer;
        } catch (ErrorResponseException e) {
            throw new BadRequestAlertException(e.getBody().getTitle(), getClass().getName(), e.getDetailMessageCode());
        }
    }

    @PostMapping(value = DomainConstants.CUSTOMERS + "atualizar", produces = {"application/json"})
    @Operation(
        summary = "Atualiza os dados de um cliente na base de dados",
        description =
            "Tenta atualizar os dados de um cliente na base de dados",
        operationId = "updateCustomer",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error"),
        }
    )
    public ClientesEntity updateCustomer(
        @RequestBody @Valid CustomerRequest customerRequest,
        HttpServletRequest request,
        HttpServletResponse response
    ) {
        try {
            var header = request.getHeader(HttpHeaders.AUTHORIZATION);

            return clientesService.updateCustomer(customerRequest);
        } catch (RuntimeException e) {
            throw new BadRequestAlertException(e.getMessage(), getClass().getName(), e.getMessage());
        }
    }

    @PostMapping(value = DomainConstants.CUSTOMER + "{id}/deletar", produces = {"application/json"})
    @Operation(
        summary = "Apaga os dados de um cliente da base de dados",
        description =
            "Tenta apagar os dados de um cliente da base de dados",
        operationId = "updateCustomer",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error"),
        }
    )
    public CustomerResponse deleteCustomer(
        @PathVariable("id"  )
        @NotNull(message = "O parâmetro não pode ser nulo")
        @Min(value = 1, message = "O parâmetro deve ser maior que zero") Long id,
        HttpServletRequest request,
        HttpServletResponse response
    ) {
        try {
            var header = request.getHeader(HttpHeaders.AUTHORIZATION);

            return clientesService.deleteCustomer(id);
        } catch (RuntimeException e) {
            throw new BadRequestAlertException(e.getMessage(), getClass().getName(), e.getMessage());
        }
    }
}
