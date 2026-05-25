package br.com.controle.model;

import java.time.LocalDate;

public class Gasto {
    private int id;
    private String descricao;
    private double valor;
    private LocalDate data;
    private String categoria;

    public Gasto() {}

    public Gasto(String descricao, double valor, LocalDate data, String categoria) {
        this.descricao = descricao;
        this.valor = valor;
        this.data = data;
        this.categoria = categoria;
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
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    @Override
    public String toString() {
        return String.format("[%d] %s - R$ %.2f (%s) - %s",
                id, descricao, valor, categoria, data);
    }
}