package br.com.controle.model;

public class Categoria {
    private int id;
    private String nome;
    private Double limiteMensal;

    //construtor vazio
    public Categoria() {}

    //construtor com parâmetros
    public Categoria(String nome, Double limiteMensal) {
        this.nome = nome;
        this.limiteMensal = limiteMensal;
    }
    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public Double getLimiteMensal() { return limiteMensal; }
    public void setLimiteMensal(Double limiteMensal) { this.limiteMensal = limiteMensal; }

    @Override
    public String toString() {
        if (limiteMensal != null) {
            return String.format("[%d] %s - Limite: R$ %.2f", id, nome, limiteMensal);
        } else { 
            return String.format("[%d] %s - Sem limite ", id, nome);
        }
    }
}
