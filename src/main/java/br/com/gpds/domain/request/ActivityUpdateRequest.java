package br.com.gpds.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;

@Validated
@Valid
public record ActivityUpdateRequest(
    @Schema(description = "Identificador da atividade") @NotNull Long id,
    @Schema(description = "Identificador do status da atividade") Long statusId,
    @Schema(description = "Descrição da atividade do projeto")
    @Length(min = 3, max = 1024) String description,
    @Schema(description = "Percentual de completude da atividade")
    @DecimalMin("0.0") @DecimalMax("99.9") BigDecimal percentage,
    @Schema(description = "Idendificador do projeto") Long projectId,
    @Schema(description = "Identificador do Product Owner (cliente) do projeto") @NotNull Long customerId
) {
}
