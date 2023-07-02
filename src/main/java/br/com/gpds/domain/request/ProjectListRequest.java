package br.com.gpds.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

@Validated
@Valid
public record ProjectListRequest(
    @Schema(description = "Descrição do projeto") String description,
    @Schema(description = "Identificador do Product Owner (cliente) do projeto") @NotNull Long customerId,
    @Schema(description = "Número da página", defaultValue = "0") int page,
    @Schema(description = "Quantidade de itens por página", defaultValue = "10") int pageSize,
    @Schema(description = "Tipo de ordenação", format = "enum", defaultValue = "asc") String order
) {
}
