package br.com.controle.model;

import br.com.controle.utils.DateUtils;

import java.time.LocalDate;


public class Gasto {
    private int id;
    private String descricao;
    private double valor;
    private LocalDate data;
    private int categoriaId; // ID da categoria (chave estrangeira)
    private String nomeCategoria; // nome da categoria (apenas para exibição, não salva)

    public Gasto() {}

    public Gasto(String descricao, double valor, LocalDate data, int categoriaId) {
        this.descricao = descricao;
        this.valor = valor;
        this.data = data;
        this.categoriaId = categoriaId;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }
    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }
    public int getCategoriaId() { return categoriaId; }
    public void setCategoriaId(int categoriaId) { this.categoriaId = categoriaId; }
    public String getNomeCategoria() { return nomeCategoria; }
    public void setNomeCategoria(String nomeCategoria) { this.nomeCategoria = nomeCategoria; }

    @Override
    public String toString() {
        return String.format("[%d] %s - R$ %.2f (%s) - %s",
                id, descricao, valor, nomeCategoria, DateUtils.exibir(data));
    }
}