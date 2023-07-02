package br.com.gpds.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import jakarta.persistence.*;

import java.util.Collection;
import java.util.Objects;

@Entity
@Table(name = "clientes", schema = "ag_cap_gpds")
@JsonRootName("Customer")
public class ClientesEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long id;
    @Basic
    @Column(name = "nome")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("name")
    private String nome;

    @OneToMany(mappedBy = "cliente")
    @JsonIgnore
    private Collection<AtividadeProjetoClienteEntity> atividadeProjetoCliente;

    public ClientesEntity() {
    }

    public ClientesEntity(Long id) {
        this.id = id;
    }

    public ClientesEntity(Long id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public ClientesEntity(String nome) {
        this.nome = nome;
    }

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

    public Collection<AtividadeProjetoClienteEntity> getAtividadeProjetoCliente() {
        return atividadeProjetoCliente;
    }
}
