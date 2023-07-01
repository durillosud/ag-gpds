package br.com.gpds.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record JwtResponse(
    @Schema(description = "Json Web Token") String token
) {
}
