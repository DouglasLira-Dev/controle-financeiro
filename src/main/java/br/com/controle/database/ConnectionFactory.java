package br.com.controle.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;  // ← IMPORT ADICIONADO

public class ConnectionFactory {
    private static final String URL = "jdbc:sqlite:controle.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void initDatabase() {
        // Cria tabela categorias
        String sqlCategorias = """
            CREATE TABLE IF NOT EXISTS categorias (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nome TEXT UNIQUE NOT NULL,
                limite_mensal REAL
            )
        """;
        
        // Cria tabela gastos com categoria_id
        String sqlGastos = """
            CREATE TABLE IF NOT EXISTS gastos (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                descricao TEXT NOT NULL,
                valor REAL NOT NULL,
                data TEXT NOT NULL,
                categoria_id INTEGER,
                FOREIGN KEY(categoria_id) REFERENCES categorias(id)
            )
        """;
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            stmt.execute(sqlCategorias);
            stmt.execute(sqlGastos);
            
            // Chama a migração de dados
            migrarDadosAntigos(conn);
            
            System.out.println("Banco inicializado com sucesso!");
            
        } catch (SQLException e) {
            System.err.println("Erro ao inicializar banco: " + e.getMessage());
        }
    }

    private static void migrarDadosAntigos(Connection conn) {
        try {
            // Verifica se a coluna 'categoria' existe na tabela gastos
            boolean colunaAntigaExiste = false;
            ResultSet rs = conn.createStatement().executeQuery("PRAGMA table_info(gastos)");
            while (rs.next()) {
                if ("categoria".equals(rs.getString("name"))) {
                    colunaAntigaExiste = true;
                    break;
                }
            }
            
            // Se a coluna antiga existe, inicia a migração
            if (colunaAntigaExiste) {
                System.out.println("Coluna 'categoria' encontrada. Iniciando migração de dados...");

                // Cria categoria padrão "Outros"
                conn.createStatement().execute("INSERT OR IGNORE INTO categorias (nome, limite_mensal) VALUES ('Outros', NULL)");

                // Busca categorias únicas da coluna antiga e insere na tabela categorias
                ResultSet rsCat = conn.createStatement().executeQuery("SELECT DISTINCT categoria FROM gastos WHERE categoria IS NOT NULL AND categoria != ''");
                while (rsCat.next()) {
                    String nome = rsCat.getString("categoria");
                    // CORREÇÃO 1: Fechar parênteses do VALUES
                    conn.createStatement().execute("INSERT OR IGNORE INTO categorias (nome, limite_mensal) VALUES ('" + nome + "', NULL)");
                }
                
                // CORREÇÃO 2: Adicionar espaço antes do WHERE e remover o +
                conn.createStatement().execute("UPDATE gastos SET categoria_id = (SELECT id FROM categorias WHERE nome = gastos.categoria) WHERE categoria IS NOT NULL AND categoria != ''");

                System.out.println("Migração de dados concluída com sucesso!");
            }
        } catch (SQLException e) {
            System.err.println("Erro durante a migração de dados: " + e.getMessage());
        }
    }
}