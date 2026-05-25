package br.com.controle.dao;

import br.com.controle.database.ConnectionFactory;
import br.com.controle.model.Gasto;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class GastoDAO {

    public void adicionar(Gasto gasto) {
        String sql = "INSERT INTO gastos (descricao, valor, data, categoria) VALUES (?,?,?,?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, gasto.getDescricao());
            ps.setDouble(2, gasto.getValor());
            ps.setString(3, gasto.getData().toString());
            ps.setString(4, gasto.getCategoria());
            ps.executeUpdate();
            System.out.println("Gasto adicionado.");
        } catch (SQLException e) {
            System.err.println("Erro: " + e.getMessage());
        }
    }

    public List<Gasto> listarTodos() {
        List<Gasto> lista = new ArrayList<>();
        String sql = "SELECT * FROM gastos ORDER BY data DESC";
        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Gasto g = new Gasto();
                g.setId(rs.getInt("id"));
                g.setDescricao(rs.getString("descricao"));
                g.setValor(rs.getDouble("valor"));
                g.setData(LocalDate.parse(rs.getString("data")));
                g.setCategoria(rs.getString("categoria"));
                lista.add(g);
            }
        } catch (SQLException e) {
            System.err.println("Erro: " + e.getMessage());
        }
        return lista;
    }

    public void atualizar(Gasto gasto) {
        String sql = "UPDATE gastos SET descricao=?, valor=?, data=?, categoria=? WHERE id=?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, gasto.getDescricao());
            ps.setDouble(2, gasto.getValor());
            ps.setString(3, gasto.getData().toString());
            ps.setString(4, gasto.getCategoria());
            ps.setInt(5, gasto.getId());
            if (ps.executeUpdate() > 0)
                System.out.println("Atualizado.");
            else
                System.out.println("ID não encontrado.");
        } catch (SQLException e) {
            System.err.println("Erro: " + e.getMessage());
        }
    }

    public void excluir(int id) {
        String sql = "DELETE FROM gastos WHERE id=?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            if (ps.executeUpdate() > 0)
                System.out.println("Removido.");
            else
                System.out.println("ID não existe.");
        } catch (SQLException e) {
            System.err.println("Erro: " + e.getMessage());
        }
    }

    public double totalMesAtual() {
        LocalDate hoje = LocalDate.now();
        String inicio = hoje.withDayOfMonth(1).toString();
        String fim = hoje.withDayOfMonth(hoje.lengthOfMonth()).toString();
        String sql = "SELECT SUM(valor) as total FROM gastos WHERE data BETWEEN ? AND ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, inicio);
            ps.setString(2, fim);
            ResultSet rs = ps.executeQuery();
            return rs.getDouble("total");
        } catch (SQLException e) {
            return 0.0;
        }
    }
}