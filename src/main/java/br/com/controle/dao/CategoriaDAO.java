package br.com.controle.dao;

import br.com.controle.database.ConnectionFactory;
import br.com.controle.model.Categoria;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import br.com.controle.utils.DateUtils;

public class CategoriaDAO {
    
    // Método para adicionar uma nova categoria
    public void adicionar(Categoria categoria) {
        String sql = "INSERT INTO categorias (nome, limite_mensal) VALUES (?, ?)";
        
        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, categoria.getNome());

            if (categoria.getLimiteMensal() != null) {
                ps.setDouble(2, categoria.getLimiteMensal());
            } else {
                ps.setNull(2, Types.REAL);
            }

            ps.executeUpdate();
            System.out.println("Categoria adicionada com sucesso!");
        } catch (SQLException e) {
            System.err.println("Erro ao adicionar categoria: " + e.getMessage());
        }
    }

    public List<Categoria> listarTodas(){
        List<Categoria> lista = new ArrayList<>();
        String sql = "SELECT * FROM categorias ORDER BY nome";

        try (Connection conn = ConnectionFactory.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Categoria c = new Categoria();
                c.setId(rs.getInt("id"));
                c.setNome(rs.getString("nome"));

                double limite = rs.getDouble("limite_mensal");
                if (rs.wasNull()) {
                    c.setLimiteMensal(null);
                } else {
                    c.setLimiteMensal(limite);
                }

                lista.add(c);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar categorias: " + e.getMessage());
        }
        return lista;
    }
    // Busca categoria por id
    public Categoria buscarPorId(int id) {
        String sql = "SELECT * FROM categorias WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Categoria c = new Categoria();
                c.setId(rs.getInt("id"));
                c.setNome(rs.getString("nome"));

                double limite = rs.getDouble("limite_mensal");
                if (rs.wasNull()) {
                    c.setLimiteMensal(null);
                } else {
                    c.setLimiteMensal(limite);
                }

                return c;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar categoria: " + e.getMessage());
        }
        return null;
    }
    // Atualizar categoria
    public void atualizar(Categoria categoria) {
        String sql = "UPDATE categorias SET nome = ?, limite_mensal = ? WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, categoria.getNome());

            if (categoria.getLimiteMensal() != null) {
                ps.setDouble(2, categoria.getLimiteMensal());
            } else {
                ps.setNull(2, Types.REAL);
            }

            ps.setInt(3, categoria.getId());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Categoria atualizada com sucesso!");
            } else {
                System.out.println("Nenhuma categoria encontrada com o ID: " + categoria.getId());
            }
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar categoria: " + e.getMessage());
        }
    }
    // Excluir categoria (apenas se não tiver gastos associados
    public void excluir(int id) {
        //Verifica se existem gastos com esta categoria
        String checkSql = "SELECT COUNT (*) FROM gastos WHERE categoria_id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement checkPs = conn.prepareStatement(checkSql)) {

            checkPs.setInt(1, id);
            ResultSet rs = checkPs.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("Não é possível excluir esta categoria. Existem gastos associados a ela.");
                return;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao verificar gastos associados: " + e.getMessage());
            return;
        }
        // Se não houver gastos associados, prossegue com a exclusão
        String deleteSql = "DELETE FROM categorias WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement deletePs = conn.prepareStatement(deleteSql)) {

            deletePs.setInt(1, id);
            int rows = deletePs.executeUpdate();

            if (rows > 0) {
                System.out.println("Categoria excluída com sucesso!");
            } else {
                System.out.println("Nenhuma categoria encontrada com o ID: " + id);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao excluir categoria: " + e.getMessage());
        }
    }
    // Calcular total gasto no mês para uma categoria
    public double totalGastoNoMes(int categoriaId) {
        LocalDate hoje = LocalDate.now();

        String inicio = DateUtils.formatar(hoje.withDayOfMonth(1));
        String fim = DateUtils.formatar(hoje.withDayOfMonth(hoje.lengthOfMonth()));

        String sql = "SELECT SUM(valor) as total FROM gastos WHERE categoria_id = ? AND data BETWEEN ? AND ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, categoriaId);
            ps.setString(2, inicio);
            ps.setString(3, fim);

            ResultSet rs = ps.executeQuery();
            return rs.getDouble("total");

        } catch (SQLException e) {
            System.err.println("Erro ao calcular total gasto da categoria: " + e.getMessage());
            return 0.0;
        }
    }
    //Verifica se a categoria excedeu o limite mensal
    public boolean excedeuLimite(int categoriaId) {
        Categoria cat = buscarPorId(categoriaId);

        if (cat == null || cat.getLimiteMensal() == null) {
            return false;
        }

        double totalGasto = totalGastoNoMes(categoriaId);
        return totalGasto > cat.getLimiteMensal();
    }
}
