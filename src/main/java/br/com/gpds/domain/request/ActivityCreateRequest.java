package br.com.gpds.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;

@Validated
@Valid
public record ActivityCreateRequest(
    @Schema(description = "Descrição da atividade do projeto")
    @Length(min = 3, max = 1024) @NotBlank @NotNull String description,
    @Schema(description = "Percentual de completude da atividade")
    @DecimalMin("0.0") @DecimalMax("99.9") @NotNull BigDecimal percentage,
    @Schema(description = "Idendificador do projeto") @NotNull Long projectId,
    @Schema(description = "Identificador do Product Owner (cliente) do projeto")
    @NotNull Long customerId
) {
}
