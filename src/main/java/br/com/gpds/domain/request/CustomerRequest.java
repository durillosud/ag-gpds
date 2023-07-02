package br.com.gpds.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;

@Validated
@Valid
public record CustomerRequest(
    @Schema(description = "Identificador do cliente") @Min(1) Long id,
    @Schema(description = "Nome do cliente") @Length(min = 3, max = 1024) @NotBlank @NotNull String name
) {
}
