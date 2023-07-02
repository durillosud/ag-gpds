package br.com.gpds.web.rest;

import br.com.gpds.domain.StatusEntity;
import br.com.gpds.domain.common.DomainConstants;
import br.com.gpds.service.StatusService;
import br.com.gpds.web.rest.errors.BadRequestAlertException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(DomainConstants.APP_PATH)
@Validated
public class StatusResource {
    private final StatusService statusService;

    public StatusResource(StatusService statusService) {
        this.statusService = statusService;
    }

    @GetMapping(value = DomainConstants.STATUSES + "listar", produces = {"application/json"})
    @Operation(
        summary = "Lista dos estados possíveis dos projetos e/ou atividades de projetos",
        description =
            "Lista dos estados possíveis dos projetos e/ou atividades de projetos",
        operationId = "getAllStatuses",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "204", description = "No Content")
        }
    )
    public List<StatusEntity> getAllStatuses(
        @Parameter(
            description = "Nome ou fragmento do nome do time de desenvolvimento, para uso como padrão de busca")
        @RequestParam("name") String name,
        HttpServletRequest request,
        final HttpServletResponse response
    ) throws ErrorResponseException {
        try {
            var header = request.getHeader(HttpHeaders.AUTHORIZATION);


            var statusResponse = statusService.findAll();

            if (statusResponse.isEmpty())
                response.setStatus(HttpStatus.NO_CONTENT.value());

            return statusResponse;
        } catch (RuntimeException e) {
            throw new BadRequestAlertException(e.getMessage(), getClass().getName(), e.getLocalizedMessage());
        }
    }
}
