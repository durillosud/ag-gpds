package br.com.gpds.web.rest;


import br.com.gpds.domain.ClientesEntity;
import br.com.gpds.domain.common.DomainConstants;
import br.com.gpds.web.rest.errors.BadRequestAlertException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping(DomainConstants.API_PATH)
@Validated
public class ClientesResource {

    @GetMapping(value = DomainConstants.CUSTOMERS + "listar", produces = { "application/json" })
    @Operation(
        summary = "Lista paginada com clientes",
        description = "Lista paginada com clientes:<br/>" +
            "<ul><li>filtro por `nome`;</li><br/>" +
            "<li>paginação `nº da página`, `tamanho da página`, `campo de ordenação`, `tipo de ordenação`;</li></ul>",
        operationId = "getAllClientesPaginated",
        responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
        }
    )
    public Page<ClientesEntity> getAllClientesPaginated(
        @Parameter(description = "Nome do cliente")
        @RequestParam(required = false, defaultValue = "") String name,
        @Parameter(description = "Número da página")
        @RequestParam(required = false, defaultValue = "0") int page,
        @Parameter(description = "Quantidade de itens por página")
        @RequestParam(required = false, defaultValue = "10") int pageSize,
        @Parameter(description = "Tipo de ordenação") @RequestParam(required = false, defaultValue = "asc") String tipoOrdem,
        UriComponentsBuilder uriBuilder,
        HttpServletResponse response
    ) throws ErrorResponseException {
        try {
            return Page.empty();
        } catch (ErrorResponseException e) {
            throw new BadRequestAlertException("message", "entity", "error");
        }
    }
}
