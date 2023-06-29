package br.com.gpds.domain;

import jakarta.persistence.*;

import java.util.Collection;
import java.util.Objects;

@Entity
@Table(name = "status", schema = "ag_cap_gpds", catalog = "GPDS")
public class StatusEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private long id;
    @Basic
    @Column(name = "descricao")
    private String descricao;
    @OneToMany(mappedBy = "status")
    private Collection<AtividadesEntity> atividades;
    @OneToMany(mappedBy = "status")
    private Collection<ProjetosEntity> projetosById;

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
        StatusEntity that = (StatusEntity) o;
        return id == that.id && Objects.equals(descricao, that.descricao);
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

    public Collection<ProjetosEntity> getProjetosById() {
        return projetosById;
    }

    public void setProjetosById(Collection<ProjetosEntity> projetosById) {
        this.projetosById = projetosById;
    }
}
