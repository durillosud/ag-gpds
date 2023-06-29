package br.com.gpds.domain;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "atividade_projeto_cliente", schema = "ag_cap_gpds", catalog = "GPDS")
public class AtividadeProjetoClienteEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Long id;
    @ManyToOne
    @JoinColumn(name = "cliente", referencedColumnName = "id", nullable = false)
    private ClientesEntity cliente;
    @ManyToOne
    @JoinColumn(name = "projeto", referencedColumnName = "id", nullable = false)
    private ProjetosEntity projeto;
    @ManyToOne
    @JoinColumn(name = "atividade", referencedColumnName = "id", nullable = false)
    private AtividadesEntity atividade;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AtividadeProjetoClienteEntity that = (AtividadeProjetoClienteEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public ClientesEntity getCliente() {
        return cliente;
    }

    public void setCliente(ClientesEntity cliente) {
        this.cliente = cliente;
    }

    public ProjetosEntity getProjeto() {
        return projeto;
    }

    public void setProjeto(ProjetosEntity projeto) {
        this.projeto = projeto;
    }

    public AtividadesEntity getAtividade() {
        return atividade;
    }

    public void setAtividade(AtividadesEntity atividade) {
        this.atividade = atividade;
    }
}
