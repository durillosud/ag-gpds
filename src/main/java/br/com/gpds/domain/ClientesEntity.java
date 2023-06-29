package br.com.gpds.domain;

import jakarta.persistence.*;

import java.util.Collection;
import java.util.Objects;

@Entity
@Table(name = "clientes", schema = "ag_cap_gpds", catalog = "GPDS")
public class ClientesEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Long id;
    @Basic
    @Column(name = "nome")
    private String nome;
    @OneToMany(mappedBy = "cliente")
    private Collection<ProjetosEntity> projetos;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientesEntity that = (ClientesEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(nome, that.nome);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nome);
    }

    public Collection<ProjetosEntity> getProjetos() {
        return projetos;
    }

    public void setProjetos(Collection<ProjetosEntity> projetos) {
        this.projetos = projetos;
    }
}
