package br.com.controle.dao;

import br.com.controle.database.ConnectionFactory;
import br.com.controle.model.Receita;
import br.com.controle.utils.DateUtils;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReceitaDAO {
    // Método para adicionar uma nova receita ao banco de dados
    public void adicionar(Receita receita) {
        String sql = "INSERT INTO receitas (descricao, valor, data, categoria) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // substitui os parâmetros da query pelos valores da receita
            pstmt.setString(1, receita.getDescricao()); // descrição da receita
            pstmt.setDouble(2, receita.getValor()); // valor da receita
            pstmt.setString(3, DateUtils.formatar(receita.getData())); // data da receita formatada como string
            pstmt.setString(4, receita.getCategoria()); // categoria da receita como string simples

            // Executa o INSERT no banco de dados
            pstmt.executeUpdate();
            System.out.println("✅Receita adicionada com sucesso!");
        } catch (SQLException e) {
            System.out.println("❌Erro ao adicionar receita: " + e.getMessage());
        }
    }
    // Método para listar todas as receitas do banco de dados
    public List<Receita> listar() {
        List<Receita> receitas = new ArrayList<>();
        String sql = "SELECT * FROM receitas ORDER BY data DESC";
        
        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            // percorre cada linha do resultado da query e cria objetos Receita para cada registro encontrado
            while (rs.next()) {
                Receita receita = new Receita();
                receita.setId(rs.getInt("id")); // ID da receita
                receita.setDescricao(rs.getString("descricao")); // descrição da receita
                receita.setValor(rs.getDouble("valor")); // valor da receita
                receita.setData(DateUtils.parse(rs.getString("data"))); // data da receita convertida de string para LocalDate
                receita.setCategoria(rs.getString("categoria")); // categoria como string simples
                receitas.add(receita); // adiciona a receita à lista de receitas
            }
        } catch (SQLException e) {
            System.out.println("❌Erro ao listar receitas: " + e.getMessage());
        }
        
        return receitas;
    }
    // Método Buscar receita por ID
    public Receita buscarPorId(int id) {
        String sql = "SELECT * FROM receitas WHERE id = ?";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id); // define o parâmetro da query com o ID da receita
            ResultSet rs = pstmt.executeQuery(); // executa a query
            
            if (rs.next()) { // se encontrar um registro com o ID fornecido
                Receita receita = new Receita();
                receita.setId(rs.getInt("id")); // ID da receita
                receita.setDescricao(rs.getString("descricao")); // descrição da receita
                receita.setValor(rs.getDouble("valor")); // valor da receita
                receita.setData(DateUtils.parse(rs.getString("data"))); // data da receita convertida de string para LocalDate
                receita.setCategoria(rs.getString("categoria")); // categoria como string simples
                return receita; // retorna a receita encontrada
            }
        } catch (SQLException e) {
            System.out.println("❌Erro ao buscar receita por ID: " + e.getMessage());
        }
        
        return null; // retorna null se não encontrar a receita ou ocorrer um erro
    }
    // Método Atualizar receita existente no banco de dados
    public void atualizar(Receita receita) {
        String sql = "UPDATE receitas SET descricao = ?, valor = ?, data = ?, categoria = ? WHERE id = ?";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // substitui os parâmetros da query pelos valores atualizados da receita
            pstmt.setString(1, receita.getDescricao()); // descrição da receita
            pstmt.setDouble(2, receita.getValor()); // valor da receita
            pstmt.setString(3, DateUtils.formatar(receita.getData())); // data da receita formatada como string
            pstmt.setString(4, receita.getCategoria()); // categoria da receita como string simples
            pstmt.setInt(5, receita.getId()); // ID da receita a ser atualizada

            // Executa o UPDATE no banco de dados
            int rows = pstmt.executeUpdate(); // retorna o número de linhas afetadas pela atualização
            if (rows > 0) {
                System.out.println("✅Receita atualizada com sucesso!");
            } else {
                System.out.println("⚠️Nenhuma receita encontrada com o ID fornecido.");
            }
        } catch (SQLException e) {
            System.out.println("❌Erro ao atualizar receita: " + e.getMessage());
        }
    }
    // Método Excluir receita do banco de dados
    public void excluir(int id) {
        String sql = "DELETE FROM receitas WHERE id = ?";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id); // define o parâmetro da query com o ID da receita a ser excluída
            int rows = pstmt.executeUpdate(); // retorna o número de linhas afetadas pela exclusão

            if (rows > 0) {
                System.out.println("✅Receita excluída com sucesso!");
            } else {
                System.out.println("⚠️Nenhuma receita encontrada com o ID fornecido.");
            }
        } catch (SQLException e) {
            System.out.println("❌Erro ao excluir receita: " + e.getMessage());
        }
    }
    // Método para calcular o total de receitas por mês/ano especificos
    public double totalReceitasPorMes(int mes, int ano) {
        LocalDate data = LocalDate.of(ano,mes,1);
        String inicio = DateUtils.formatar(data.withDayOfMonth(1));
        String fim = DateUtils.formatar(data.withDayOfMonth(data.lengthOfMonth()));

        String sql = "SELECT SUM(valor) AS total FROM receitas WHERE data BETWEEN ? AND ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, inicio); // data de início do mês
            pstmt.setString(2, fim); // data de fim do mês
            ResultSet rs = pstmt.executeQuery(); // executa a query
            
            return rs.getDouble("total"); // retorna o total de receitas para o período especificado
           
        } catch (SQLException e) {
            System.out.println("❌Erro ao calcular total de receitas por mês: " + e.getMessage());
            return 0.0; // retorna 0 se ocorrer um erro ou não houver receitas para o período
        }
    }
}
