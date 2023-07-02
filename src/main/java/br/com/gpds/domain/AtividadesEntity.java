package br.com.gpds.domain;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "atividades", schema = "ag_cap_gpds")
@JsonRootName("Activities")
public class AtividadesEntity {
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
    @Basic
    @Column(name = "percentagem")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("percentage")
    private BigDecimal percentagem;
    @ManyToOne
    @JoinColumn(name = "status", referencedColumnName = "id")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private StatusEntity status;

    public AtividadesEntity() {
    }

    public AtividadesEntity(String descricao, BigDecimal percentagem, StatusEntity status) {
        this.descricao = descricao;
        this.percentagem = percentagem;
        this.status = status;
    }

    public AtividadesEntity(Long id, String descricao, BigDecimal percentagem, StatusEntity status) {
        this.id = id;
        this.descricao = descricao;
        this.percentagem = percentagem;
        this.status = status;
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

    public BigDecimal getPercentagem() {
        return percentagem;
    }

    public void setPercentagem(BigDecimal percentagem) {
        this.percentagem = percentagem;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AtividadesEntity that = (AtividadesEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(descricao, that.descricao) && Objects.equals(percentagem, that.percentagem);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, descricao, percentagem);
    }

    public StatusEntity getStatus() {
        return status;
    }

    public void setStatus(StatusEntity status) {
        this.status = status;
    }
}
