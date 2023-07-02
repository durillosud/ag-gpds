package br.com.gpds.web.rest;

import br.com.gpds.domain.common.DomainConstants;
import br.com.gpds.domain.response.JwtResponse;
import br.com.gpds.service.DummyAuthorizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(DomainConstants.AUTH_PATH)
@Validated
public class AuthorizationResource {

    private final DummyAuthorizationService dummyAuthorizationService;

    public AuthorizationResource(DummyAuthorizationService dummyAuthorizationService) {
        this.dummyAuthorizationService = dummyAuthorizationService;
    }

    @GetMapping("dummy-user")
    @Operation(
        summary = "Obtém um Json Web Token para um usuário anônimo",
        description =
            "Obtém um Json Web Token para um usuário `anônimo fictício`",
        operationId = "getJsonWebTokenForDummyUser",
        responses = {
            @ApiResponse(responseCode = "200", description = "OK")
        }
    )
    public JwtResponse getJsonWebTokenForDummyUser() {
        return dummyAuthorizationService.getJwtForDummyUser(true);
    }

}
