package br.com.gpds.domain;

import jakarta.persistence.*;

import java.util.Collection;
import java.util.Objects;

@Entity
@Table(name = "time", schema = "ag_cap_gpds")
public class TimeEntity {
    @Basic
    @Column(name = "nome")
    private String nome;
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private long id;
    @OneToMany(mappedBy = "time")
    private Collection<ProjetosEntity> projetos;

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
