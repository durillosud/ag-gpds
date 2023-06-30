package br.com.gpds.domain;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "projetos", schema = "ag_cap_gpds", catalog = "GPDS")
public class ProjetosEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private long id;
    @Basic
    @Column(name = "descricao")
    private String descricao;
    @ManyToOne
    @JoinColumn(name = "status", referencedColumnName = "id")
    private StatusEntity status;
    @ManyToOne
    @JoinColumn(name = "time", referencedColumnName = "id", nullable = false)
    private TimeEntity time;

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
