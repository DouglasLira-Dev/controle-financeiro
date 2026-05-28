package br.com.controle.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utilitário para backup automático do banco de dados SQLite.
 * Cria cópias de segurança do arquivo controle.db na pasta backup/
 * 
 * @author Douglas Lira
 * @version 3.0
 */
public class BackupUtil {
    
    // Nome do arquivo de banco de dados
    private static final String NOME_BANCO = "controle.db";
    
    // Pasta onde os backups serão salvos
    private static final String PASTA_BACKUP = "backup";
    
    // Formato do timestamp para nome do arquivo
    private static final DateTimeFormatter FORMATO_TIMESTAMP = 
        DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    
    /**
     * Realiza o backup do banco de dados.
     * Cria a pasta backup se não existir e gera um arquivo com timestamp.
     * 
     * @return Caminho do arquivo de backup gerado, ou null se falhar
     */
    public static String fazerBackup() {
        try {
            // 1. Verificar se o banco de dados existe
            Path bancoOrigem = Paths.get(NOME_BANCO);
            if (!Files.exists(bancoOrigem)) {
                System.out.println("⚠️ Banco de dados não encontrado. Nenhum backup foi criado.");
                return null;
            }
            
            // 2. Criar pasta de backup se não existir
            Path pastaBackup = Paths.get(PASTA_BACKUP);
            if (!Files.exists(pastaBackup)) {
                Files.createDirectories(pastaBackup);
                System.out.println("📁 Pasta 'backup' criada com sucesso!");
            }
            
            // 3. Gerar nome do arquivo de backup com timestamp
            String timestamp = LocalDateTime.now().format(FORMATO_TIMESTAMP);
            String nomeBackup = String.format("controle_%s.db", timestamp);
            Path arquivoBackup = pastaBackup.resolve(nomeBackup);
            
            // 4. Copiar o arquivo
            Files.copy(bancoOrigem, arquivoBackup, StandardCopyOption.REPLACE_EXISTING);
            
            // 5. Obter tamanho do arquivo
            long tamanhoBytes = Files.size(arquivoBackup);
            String tamanhoFormatado = formatarTamanho(tamanhoBytes);
            
            System.out.println("\n✅ BACKUP REALIZADO COM SUCESSO!");
            System.out.println("📁 Local: " + arquivoBackup.toAbsolutePath());
            System.out.println("📦 Tamanho: " + tamanhoFormatado);
            System.out.println("🕐 Data: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
            
            return arquivoBackup.toString();
            
        } catch (IOException e) {
            System.err.println("❌ Erro ao fazer backup: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Lista todos os backups disponíveis na pasta backup.
     */
    public static void listarBackups() {
        Path pastaBackup = Paths.get(PASTA_BACKUP);
        
        if (!Files.exists(pastaBackup)) {
            System.out.println("📭 Nenhum backup encontrado. Pasta 'backup' não existe.");
            return;
        }
        
        try {
            var arquivos = Files.list(pastaBackup)
                .filter(Files::isRegularFile)
                .filter(p -> p.toString().endsWith(".db"))
                .sorted((a, b) -> b.getFileName().compareTo(a.getFileName()))
                .toList();
            
            if (arquivos.isEmpty()) {
                System.out.println("📭 Nenhum arquivo de backup encontrado.");
                return;
            }
            
            System.out.println("\n" + "=".repeat(60));
            System.out.println("         📋 LISTA DE BACKUPS DISPONÍVEIS");
            System.out.println("=".repeat(60));
            
            int count = 1;
            for (Path p : arquivos) {
                String nome = p.getFileName().toString();
                long tamanho = Files.size(p);
                String data = extrairDataDoNome(nome);
                System.out.printf("%2d. %s - %s - %s%n", 
                    count++, nome, formatarTamanho(tamanho), data);
            }
            
            System.out.println("=".repeat(60));
            
        } catch (IOException e) {
            System.err.println("❌ Erro ao listar backups: " + e.getMessage());
        }
    }
    
    /**
     * Restaura um backup específico.
     * 
     * @param nomeArquivo Nome do arquivo de backup (ex: controle_20260528_115530.db)
     * @return true se restaurado com sucesso, false caso contrário
     */
    public static boolean restaurarBackup(String nomeArquivo) {
        Path arquivoBackup = Paths.get(PASTA_BACKUP, nomeArquivo);
        Path bancoAtual = Paths.get(NOME_BANCO);
        
        if (!Files.exists(arquivoBackup)) {
            System.out.println("❌ Arquivo de backup não encontrado: " + nomeArquivo);
            return false;
        }
        
        try {
            // Fazer backup do banco atual antes de restaurar (segurança)
            if (Files.exists(bancoAtual)) {
                fazerBackup(); // Backup automático do estado atual
            }
            
            // Copiar backup para o banco atual
            Files.copy(arquivoBackup, bancoAtual, StandardCopyOption.REPLACE_EXISTING);
            
            System.out.println("\n✅ BANCO DE DADOS RESTAURADO COM SUCESSO!");
            System.out.println("📁 Origem: " + arquivoBackup.getFileName());
            System.out.println("🔄 Reinicie o programa para usar o banco restaurado.");
            
            return true;
            
        } catch (IOException e) {
            System.err.println("❌ Erro ao restaurar backup: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Extrai a data do nome do arquivo de backup.
     * Formato esperado: controle_YYYYMMDD_HHMMSS.db
     * 
     * @param nome Nome do arquivo
     * @return Data formatada (dd/MM/yyyy HH:mm:ss)
     */
    private static String extrairDataDoNome(String nome) {
        try {
            // Remove "controle_" e ".db" para pegar o timestamp
            String timestamp = nome.replace("controle_", "").replace(".db", "");
            // Formato original: yyyyMMdd_HHmmss
            DateTimeFormatter formatoEntrada = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
            LocalDateTime data = LocalDateTime.parse(timestamp, formatoEntrada);
            // Formato de saída: dd/MM/yyyy HH:mm:ss
            return data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        } catch (Exception e) {
            return "Data desconhecida";
        }
    }
    
    /**
     * Formata o tamanho do arquivo em KB ou MB.
     * 
     * @param bytes Tamanho em bytes
     * @return String formatada (ex: "1.5 MB" ou "256 KB")
     */
    private static String formatarTamanho(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.2f KB", bytes / 1024.0);
        } else {
            return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
        }
    }
    
    /**
     * Verifica a integridade do banco de dados.
     * Usa o comando PRAGMA integrity_check do SQLite.
     * 
     * @return true se o banco está íntegro, false caso contrário
     */
    public static boolean verificarIntegridade() {
        try {
            var conn = br.com.controle.database.ConnectionFactory.getConnection();
            var stmt = conn.createStatement();
            var rs = stmt.executeQuery("PRAGMA integrity_check");
            
            if (rs.next()) {
                String resultado = rs.getString(1);
                if ("ok".equals(resultado)) {
                    System.out.println("✅ Banco de dados íntegro.");
                    return true;
                } else {
                    System.out.println("⚠️ Banco de dados com problemas: " + resultado);
                    return false;
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Erro ao verificar integridade: " + e.getMessage());
        }
        return false;
    }
}