package br.com.gpds.web.rest;

import br.com.gpds.domain.TimeEntity;
import br.com.gpds.domain.common.DomainConstants;
import br.com.gpds.service.TimeService;
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
public class TimeResource {

    private final TimeService timeService;

    public TimeResource(TimeService timeService) {
        this.timeService = timeService;
    }

    @GetMapping(value = DomainConstants.TEAMS + "listar", produces = {"application/json"})
    @Operation(
        summary = "Lista dos times de DevSecOps da Microsoft",
        description =
            "Lista trazida por nomes que contém o padrão do parâmetro de busca",
        operationId = "getTeamsByNameContainingParam",
        security = @SecurityRequirement(name = "bearerAuth"),
        responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "204", description = "No Content"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
        }
    )
    public List<TimeEntity> getTeamsByNameContainingParam(
        @Parameter(
            description = "Nome ou fragmento do nome do time de desenvolvimento, para uso como padrão de busca")
        @RequestParam("name") String name,
        HttpServletRequest request,
        final HttpServletResponse response
    ) throws ErrorResponseException {
        try {
            var header = request.getHeader(HttpHeaders.AUTHORIZATION);


            var teamsResponse = timeService.findTeamsByNameContaining(name);

            if (teamsResponse.isEmpty())
                response.setStatus(HttpStatus.NO_CONTENT.value());

            return teamsResponse;
        } catch (RuntimeException e) {
            throw new BadRequestAlertException(e.getMessage(), getClass().getName(), e.getLocalizedMessage());
        }
    }
}
