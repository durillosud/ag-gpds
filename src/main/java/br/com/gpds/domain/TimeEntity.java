package br.com.gpds.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import jakarta.persistence.*;

import java.util.Collection;
import java.util.Objects;

@Entity
@Table(name = "time", schema = "ag_cap_gpds")
@JsonRootName("MicrosoftDevSecOpsTeam")
public class TimeEntity {
    @Basic
    @Column(name = "nome")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("name")
    private String nome;
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private long id;
    @OneToMany(mappedBy = "time")
    @JsonIgnore
    private Collection<ProjetosEntity> projetos;

    public TimeEntity() {
    }

    public TimeEntity(Long id) {
        this.id = id;
    }

    public TimeEntity(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimeEntity that = (TimeEntity) o;
        return id == that.id && Objects.equals(nome, that.nome);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome, id);
    }

    public Collection<ProjetosEntity> getProjetos() {
        return projetos;
    }

    public void setProjetos(Collection<ProjetosEntity> projetos) {
        this.projetos = projetos;
    }
}
