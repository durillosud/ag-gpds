package br.com.gpds.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import jakarta.persistence.*;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "projetos", schema = "ag_cap_gpds")
@JsonRootName("Project")
public class ProjetosEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private long id;
    @Basic
    @Column(name = "descricao")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("description")
    private String descricao;
    @ManyToOne
    @JoinColumn(name = "status", referencedColumnName = "id")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private StatusEntity status;
    @ManyToOne
    @JoinColumn(name = "time", referencedColumnName = "id", nullable = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("devSecOpsTeam")
    private TimeEntity time;

    @OneToMany(mappedBy = "projeto")
    @JsonIgnore
    private Collection<AtividadeProjetoClienteEntity> atividadeProjetoClientes;

    public ProjetosEntity() {
    }

    public ProjetosEntity(String descricao, StatusEntity status, TimeEntity time) {
        this.descricao = descricao;
        this.status = status;
        this.time = time;
    }

    public ProjetosEntity(String descricao, StatusEntity status, TimeEntity time, AtividadeProjetoClienteEntity atividadeProjetoCliente) {
        this.descricao = descricao;
        this.status = status;
        this.time = time;
        this.atividadeProjetoClientes = List.of(atividadeProjetoCliente);
    }

    public ProjetosEntity(long id, String descricao, StatusEntity status, TimeEntity time) {
        this.id = id;
        this.descricao = descricao;
        this.status = status;
        this.time = time;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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
        ProjetosEntity that = (ProjetosEntity) o;
        return id == that.id && Objects.equals(descricao, that.descricao);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, descricao);
    }

    public StatusEntity getStatus() {
        return status;
    }

    public void setStatus(StatusEntity status) {
        this.status = status;
    }

    public TimeEntity getTime() {
        return time;
    }

    public void setTime(TimeEntity time) {
        this.time = time;
    }
}
