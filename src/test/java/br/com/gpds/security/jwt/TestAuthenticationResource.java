package br.com.gpds.security.jwt;

import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing testing authentication token.
 */
@RestController
@RequestMapping("/test/api")
public class TestAuthenticationResource {

    /**
     * {@code GET  /authenticate} : check if the authentication token correctly validates
     *
     * @return ok.
     */
    @GetMapping("/authenticate")
    public String isAuthenticated() {
        return "ok";
    }
}
