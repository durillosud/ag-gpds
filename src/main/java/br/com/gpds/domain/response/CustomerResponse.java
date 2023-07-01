package br.com.gpds.domain.response;

import br.com.gpds.domain.ClientesEntity;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;


public record CustomerResponse(
    @Schema(description = "Mensagem customizada de retorno")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String message,
    @Schema(description = "Entidade da resposta")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("data")
    ClientesEntity entity
) {
}
