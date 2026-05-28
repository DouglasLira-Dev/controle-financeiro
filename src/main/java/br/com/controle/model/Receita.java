package br.com.controle.model;

import br.com.controle.utils.DateUtils;

import java.time.LocalDate;

/*
 * Classe que representa uma receita financeira. Ela possui atributos como descrição, valor, data e categoria.
 * A categoria é uma string simples, pois as receitas não estão vinculadas a categorias específicas como os gastos. A data é armazenada como LocalDate para facilitar manipulações e formatações. 
 * Uma receita é qualquer entrada de dinheiro, como salário, venda ou qualquer outra fonte de renda. Ela pode ser associada a uma categoria para fins de organização, mas isso é opcional.
 */
public class Receita {
    // Atributos privados
    private int id;  // ID único da receita, gerado automaticamente pelo banco de dados
    private String descricao; // Descrição da receita, como "Salário", "Venda de produto", etc.
    private double valor; // Valor da receita, representado como um número decimal
    private LocalDate data;  // Data da receita, armazenada como LocalDate para facilitar manipulações e formatações
    private String categoria; // Categoria como string simples

    // Construtor sem parametros (necessário para o DAO)
    public Receita() {}

    // Construtor com parâmetros para facilitar a criação de objetos Receita
    public Receita(String descricao, double valor, LocalDate data, String categoria) {
        this.descricao = descricao;
        this.valor = valor;
        this.data = data;
        this.categoria = categoria;
    }

    // Getters e Setters para acessar e modificar os atributos privados
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    // Método toString para facilitar a exibição das informações da receita
    @Override
    public String toString() {
       return String.format("[%d] %s - R$ %.2f (%s) - %s",
            id, descricao, valor, categoria, DateUtils.exibir(data)
       );
    }
}
