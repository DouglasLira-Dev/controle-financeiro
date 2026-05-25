package br.com.controle.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectionFactory {
    private static final String URL = "jdbc:sqlite:controle.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void initDatabase() {
        String sql = "CREATE TABLE IF NOT EXISTS gastos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "descricao TEXT NOT NULL," +
                "valor REAL NOT NULL," +
                "data TEXT NOT NULL," +
                "categoria TEXT" +
                ")";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Banco inicializado.");
        } catch (SQLException e) {
            System.err.println("Erro: " + e.getMessage());
        }
    }
}