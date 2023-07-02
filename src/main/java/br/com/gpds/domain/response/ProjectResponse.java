package br.com.gpds.domain.response;

import br.com.gpds.domain.AtividadesEntity;
import br.com.gpds.domain.ClientesEntity;
import br.com.gpds.domain.ProjetosEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Collection;

public record ProjectResponse(
    @Schema(description = "Projeto de desenvolvimento de sistema") ProjetosEntity project,
    @Schema(description = "Project Owner do projeto em desenvovimento")
    @JsonInclude(JsonInclude.Include.NON_NULL) ClientesEntity customer,
    @Schema(description = "Lista de atividades do projeto do cliente")
    @JsonInclude(JsonInclude.Include.NON_EMPTY) Collection<AtividadesEntity> projectActivities
) {
}
