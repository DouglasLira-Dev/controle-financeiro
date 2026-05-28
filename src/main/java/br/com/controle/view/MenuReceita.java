package br.com.controle.view;

import br.com.controle.dao.ReceitaDAO;
import br.com.controle.model.Receita;
import br.com.controle.utils.DateUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class MenuReceita {
    
    //scanner para ler a entrada do usuário
    private static final Scanner sc = new Scanner(System.in);

    //instância do DAO para interagir com o banco de dados
    private static final ReceitaDAO receitaDAO = new ReceitaDAO();


    // --- Método principal para executar o menu de receitas ---
    public static void executar() {
        int opcao;
        do {
            exibirMenu();
            opcao = lerInteiro();

            switch (opcao) {
                case 1 -> adicionarReceita();
                case 2 -> listarReceitas();
                case 3 -> editarReceita();
                case 4 -> excluirReceita();
                case 5 -> mostrarTotalReceitas();
                case 0 -> System.out.println("🔙 Voltando ao menu principal...");
                default -> System.out.println("❌ Opção inválida! Tente novamente.");
            }
        } while (opcao != 0);
    }
    private static void exibirMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("        💰 GERENCIAR RECEITAS FINANCEIRAS");
        System.out.println("=".repeat(50));
        System.out.println("1. 📥 Adicionar receita");
        System.out.println("2. 📋 Listar receitas");
        System.out.println("3. ✏️ Editar receita");
        System.out.println("4. 🗑️ Excluir receita");
        System.out.println("5. 💵 Mostrar total de receitas no mês");
        System.out.println("0. 🔙 Voltar");
        System.out.println("-".repeat(50));
        System.out.print("👉 Escolha uma opção: ");
    }
    // Métodos de cada funcionalidade do menu (adicionar, listar, editar, excluir, mostrar total)
    private static void adicionarReceita() {
        System.out.println("\n--- ADICIONAR NOVA RECEITA ---");

        System.out.print("📝 Descrição da receita: ");
        String descricao = sc.nextLine();

        System.out.print("💰Valor da receita: R$ ");
        double valor = lerDouble();

        System.out.print("📅 Data da receita (dd/MM/yyyy): ");
        LocalDate data = lerData();

        System.out.print("🏷️ Categoria (opcional): ");
        String categoria = sc.nextLine();

        Receita receita = new Receita(descricao, valor, data, categoria);
        receitaDAO.adicionar(receita);
    }
    private static void listarReceitas() {
        List<Receita> receitas = receitaDAO.listar();
        
        if (receitas.isEmpty()) {
            System.out.println("\n📭 Nenhuma receita cadastrada ainda.");
            return;
        }
        
        System.out.println("\n" + "=".repeat(70));
        System.out.println("                     📋 LISTA DE RECEITAS");
        System.out.println("=".repeat(70));
        System.out.printf("%-5s %-25s %-12s %-12s %-15s%n", "ID", "DESCRIÇÃO", "VALOR", "DATA", "CATEGORIA");
        System.out.println("-".repeat(70));
        
        for (Receita r : receitas) {
            System.out.printf("%-5d %-25s R$ %-9.2f %-12s %-15s%n",
                    r.getId(),
                    truncarTexto(r.getDescricao(), 25),
                    r.getValor(),
                    DateUtils.exibir(r.getData()),
                    truncarTexto(r.getCategoria(), 15));
        }
        
        System.out.println("=".repeat(70));
        System.out.printf("📊 TOTAL DE RECEITAS: %d registros%n", receitas.size());
        System.out.println("=".repeat(70));
    }
    
    /**
     * Edita uma receita existente.
     * Busca pelo ID, mostra os dados atuais e solicita novos valores.
     */
    private static void editarReceita() {
        System.out.println("\n--- EDITAR RECEITA ---");
        
        // Primeiro, lista todas as receitas para o usuário ver os IDs
        listarReceitas();
        
        System.out.print("🆔 ID da receita a editar: ");
        int id = lerInteiro();
        
        Receita receita = receitaDAO.buscarPorId(id);
        if (receita == null) {
            System.out.println("❌ Receita não encontrada com ID: " + id);
            return;
        }
        
        System.out.println("\n📌 Dados atuais:");
        System.out.println("   Descrição: " + receita.getDescricao());
        System.out.println("   Valor: R$ " + String.format("%.2f", receita.getValor()));
        System.out.println("   Data: " + DateUtils.exibir(receita.getData()));
        System.out.println("   Categoria: " + receita.getCategoria());
        
        System.out.println("\n✏️ Digite os novos dados (deixe em branco para manter o atual):");
        
        System.out.print("📝 Nova descrição [" + receita.getDescricao() + "]: ");
        String descricao = sc.nextLine();
        if (!descricao.trim().isEmpty()) {
            receita.setDescricao(descricao);
        }
        
        System.out.print("💰 Novo valor [R$ " + String.format("%.2f", receita.getValor()) + "]: R$ ");
        String valorStr = sc.nextLine();
        if (!valorStr.trim().isEmpty()) {
            try {
                double valor = Double.parseDouble(valorStr.replace(",", "."));
                receita.setValor(valor);
            } catch (NumberFormatException e) {
                System.out.println("⚠️ Valor inválido! Mantendo o anterior.");
            }
        }
        
        System.out.print("📅 Nova data [" + DateUtils.exibir(receita.getData()) + "]: ");
        String dataStr = sc.nextLine();
        if (!dataStr.trim().isEmpty()) {
            try {
                LocalDate data = DateUtils.parse(dataStr);
                receita.setData(data);
            } catch (IllegalArgumentException e) {
                System.out.println("⚠️ Data inválida! Mantendo a anterior.");
            }
        }
        
        System.out.print("🏷️ Nova categoria [" + receita.getCategoria() + "]: ");
        String categoria = sc.nextLine();
        if (!categoria.trim().isEmpty()) {
            receita.setCategoria(categoria);
        }
        
        receitaDAO.atualizar(receita);
    }
    
    /**
     * Exclui uma receita.
     * Solicita o ID e confirma antes de excluir.
     */
    private static void excluirReceita() {
        System.out.println("\n--- EXCLUIR RECEITA ---");
        
        // Lista todas as receitas para o usuário ver os IDs
        listarReceitas();
        
        System.out.print("🆔 ID da receita a excluir: ");
        int id = lerInteiro();
        
        Receita receita = receitaDAO.buscarPorId(id);
        if (receita == null) {
            System.out.println("❌ Receita não encontrada com ID: " + id);
            return;
        }
        
        System.out.println("\n⚠️ CONFIRMAÇÃO DE EXCLUSÃO ⚠️");
        System.out.println("   Receita: " + receita.getDescricao());
        System.out.println("   Valor: R$ " + String.format("%.2f", receita.getValor()));
        System.out.print("\n   Digite 'SIM' para confirmar a exclusão: ");
        String confirmacao = sc.nextLine();
        
        if (confirmacao.equalsIgnoreCase("SIM")) {
            receitaDAO.excluir(id);
        } else {
            System.out.println("❌ Exclusão cancelada.");
        }
    }
    
    private static void mostrarTotalReceitas() {
    LocalDate hoje = LocalDate.now();
    double total = receitaDAO.totalReceitasPorMes(hoje.getMonthValue(), hoje.getYear());
    
    String mesNome = getNomeMes(hoje.getMonthValue());
    
    System.out.println("\n" + "=".repeat(50));
    System.out.println("         📊 TOTAL DE RECEITAS");
    System.out.println("=".repeat(50));
    System.out.printf("   📅 Mês: %s/%d%n", mesNome, hoje.getYear());
    System.out.printf("   💰 Total recebido: R$ %.2f%n", total);
    System.out.println("=".repeat(50));
}
    
    //  MÉTODOS AUXILIARES - para leitura de dados, formatação e validação
    
    /**
     * Converte número do mês para nome em português.
     * 
     * @param mes Número do mês (1-12)
     * @return Nome do mês por extenso
     */
    private static String getNomeMes(int mes) {
        String[] meses = {
            "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
            "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"
        };
        return meses[mes - 1];
    }
    
    /**
     * Trunca um texto se for maior que o tamanho máximo.
     * 
     * @param texto Texto original
     * @param tamanhoMaximo Tamanho máximo permitido
     * @return Texto truncado com "..." se necessário
     */
    private static String truncarTexto(String texto, int tamanhoMaximo) {
        if (texto == null) return "";
        if (texto.length() <= tamanhoMaximo) return texto;
        return texto.substring(0, tamanhoMaximo - 3) + "...";
    }
    
    /**
     * Lê um número inteiro do teclado com validação.
     * 
     * @return Número inteiro digitado pelo usuário
     */
    private static int lerInteiro() {
        while (!sc.hasNextInt()) {
            System.out.print("❌ Digite um número válido: ");
            sc.next();
        }
        int numero = sc.nextInt();
        sc.nextLine(); // limpa o buffer
        return numero;
    }
    
    /**
     * Lê um número decimal (double) do teclado com validação.
     * 
     * @return Número double digitado pelo usuário
     */
    private static double lerDouble() {
        while (!sc.hasNextDouble()) {
            System.out.print("❌ Digite um valor numérico válido: ");
            sc.next();
        }
        double numero = sc.nextDouble();
        sc.nextLine(); // limpa o buffer
        return numero;
    }
    
    /**
     * Lê uma data no formato dd/MM/yyyy com validação.
     * 
     * @return LocalDate representando a data digitada
     */
    private static LocalDate lerData() {
        while (true) {
            try {
                String dataStr = sc.nextLine();
                return DateUtils.parse(dataStr);
            } catch (IllegalArgumentException e) {
                System.out.print("❌ " + e.getMessage() + " Tente novamente: ");
            }
        }
    }
}
