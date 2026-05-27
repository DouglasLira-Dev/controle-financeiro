package br.com.controle.dao;

import br.com.controle.database.ConnectionFactory;
import br.com.controle.model.Gasto;
import br.com.controle.utils.DateUtils;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class GastoDAO {

    public void adicionar(Gasto gasto) {
        String sql = "INSERT INTO gastos (descricao, valor, data, categoria_id) VALUES (?,?,?,?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, gasto.getDescricao());
            ps.setDouble(2, gasto.getValor());
            ps.setString(3, DateUtils.formatar(gasto.getData()));
            ps.setInt(4, gasto.getCategoriaId());
            ps.executeUpdate();

            System.out.println("Gasto adicionado.");

        } catch (SQLException e) {
            System.err.println("Erro: " + e.getMessage());
        }
    }

    public List<Gasto> listarTodos() {
        List<Gasto> lista = new ArrayList<>();
        // Consulta SQL com JOIN para obter o nome da categoria junto com os gastos
        String sql = "SELECT g.*, c.nome as nomeCategoria " + "FROM gastos g " + " LEFT JOIN categorias c ON g.categoria_id = c.id " + "ORDER BY g.data DESC";

        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Gasto g = new Gasto();
                g.setId(rs.getInt("id"));
                g.setDescricao(rs.getString("descricao"));
                g.setValor(rs.getDouble("valor"));
                g.setData(DateUtils.parse(rs.getString("data")));
                g.setCategoriaId(rs.getInt("categoria_id"));
                g.setNomeCategoria(rs.getString("nomeCategoria")); // nome da categoria do JOIN
                lista.add(g);
            }
        } catch (SQLException e) {
            System.err.println("Erro: " + e.getMessage());
        }
        return lista;
    }

    public void atualizar(Gasto gasto) {
        String sql = "UPDATE gastos SET descricao=?, valor=?, data=?, categoria_id = ? WHERE id=?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, gasto.getDescricao());
            ps.setDouble(2, gasto.getValor());
            ps.setString(3, DateUtils.formatar(gasto.getData()));
            ps.setInt(4, gasto.getCategoriaId());
            ps.setInt(5, gasto.getId());

            int rows = ps.executeUpdate();
            if (rows > 0)
                System.out.println("Gasto atualizado com sucesso!");
            else
                System.out.println("Gasto não encontrado.");

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar gasto: " + e.getMessage());
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
        String inicio = DateUtils.formatar(hoje.withDayOfMonth(1));
        String fim = DateUtils.formatar(hoje.withDayOfMonth(hoje.lengthOfMonth()));

        String sql = "SELECT SUM(valor) as total FROM gastos WHERE data BETWEEN ? AND ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, inicio);
            ps.setString(2, fim);
            ResultSet rs = ps.executeQuery();
            return rs.getDouble("total");

        } catch (SQLException e) {
            System.err.println("Erro ao calcular total do mês: " + e.getMessage());
            return 0.0;
        }
    }
}