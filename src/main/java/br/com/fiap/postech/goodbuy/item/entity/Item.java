package br.com.fiap.postech.goodbuy.item.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "tb_item")
public class Item {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;
    @Column(name = "nome", nullable = false)
    private String nome;
    @Column(name = "preco", nullable = false)
    private Double preco;
    @Column(name = "descricao", nullable = false)
    private String descricao;
    @Column(name = "categoria", nullable = false)
    private String categoria;
    @Column(name = "url_imagem", nullable = false)
    private String urlImagem;
    @Column(name = "quantidade", nullable = false)
    private Long quantidade;

    public Item() {
        super();
    }

    public Item(String nome, Double preco, String descricao, String categoria, String urlImagem, Long quantidade) {
        this();
        this.nome = nome;
        this.preco = preco;
        this.descricao = descricao;
        this.categoria = categoria;
        this.urlImagem = urlImagem;
        this.quantidade = quantidade;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item item)) return false;
        return Objects.equals(id, item.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getUrlImagem() {
        return urlImagem;
    }

    public void setUrlImagem(String urlImagem) {
        this.urlImagem = urlImagem;
    }

    public Long getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Long quantidade) {
        this.quantidade = quantidade;
    }
}
