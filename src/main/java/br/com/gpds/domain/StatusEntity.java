package br.com.gpds.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import jakarta.persistence.*;

import java.util.Collection;
import java.util.Objects;

@Entity
@Table(name = "status", schema = "ag_cap_gpds")
@JsonRootName("Status")
public class StatusEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long id;
    @Basic
    @Column(name = "descricao")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("description")
    private String descricao;
    @OneToMany(mappedBy = "status")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("projectActivities")
    private Collection<AtividadesEntity> atividades;
    @OneToMany(mappedBy = "status")
    @JsonIgnore
    private Collection<ProjetosEntity> projetos;

    public StatusEntity() {
    }


    public StatusEntity(Long id, String descricao) {
        this.id = id;
        this.descricao = descricao;
    }

    public StatusEntity(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StatusEntity that = (StatusEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(descricao, that.descricao);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, descricao);
    }

    public Collection<AtividadesEntity> getAtividades() {
        return atividades;
    }

    public void setAtividades(Collection<AtividadesEntity> atividades) {
        this.atividades = atividades;
    }

    public Collection<ProjetosEntity> getProjetos() {
        return projetos;
    }

    public void setProjetos(Collection<ProjetosEntity> projetos) {
        this.projetos = projetos;
    }
}
