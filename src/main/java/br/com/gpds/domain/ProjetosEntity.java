package br.com.gpds.domain;

import jakarta.persistence.*;

import java.util.Collection;
import java.util.Objects;

@Entity
@Table(name = "projetos", schema = "ag_cap_gpds", catalog = "GPDS")
public class ProjetosEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Long id;
    @Basic
    @Column(name = "descricao")
    private String descricao;
    @OneToMany(mappedBy = "projeto")
    private Collection<AtividadesEntity> atividades;
    @ManyToOne
    @JoinColumn(name = "cliente", referencedColumnName = "id")
    private ClientesEntity cliente;
    @ManyToOne
    @JoinColumn(name = "status", referencedColumnName = "id")
    private StatusEntity status;

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
        ProjetosEntity that = (ProjetosEntity) o;
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

    public ClientesEntity getCliente() {
        return cliente;
    }

    public void setCliente(ClientesEntity cliente) {
        this.cliente = cliente;
    }

    public StatusEntity getStatus() {
        return status;
    }

    public void setStatus(StatusEntity status) {
        this.status = status;
    }
}
