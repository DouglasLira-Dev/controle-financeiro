package br.com.controle.view;

import br.com.controle.dao.CategoriaDAO;
import br.com.controle.dao.GastoDAO;
import br.com.controle.model.Categoria;
import br.com.controle.utils.DateUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class MenuCategoria {
    private static final Scanner sc = new Scanner(System.in);
    private static final CategoriaDAO categoriaDAO = new CategoriaDAO();
    private static final GastoDAO gastoDAO = new GastoDAO();

    public static void executar() {
        int op;
        do {
            System.out.println("\n--- GERENCIAR CATEGORIAS ---");
            System.out.println("1. Adicionar categoria");
            System.out.println("2. Listar categorias");
            System.out.println("3. Editar categoria");
            System.out.println("4. Excluir categoria");
            System.out.println("5. Ver gastos por categoria (mês atual)");
            System.out.println("0. Voltar");
            System.out.print("Escolha: ");
            op = lerInteiro();

            switch (op) {
                case 1 -> adicionarCategoria();
                case 2 -> listarCategorias();
                case 3 -> editarCategoria();
                case 4 -> excluirCategoria();
                case 5 -> verGastosPorCategoria();
                case 0 -> System.out.println("Voltando...");
                default -> System.out.println("Opção inválida!");
            }
        } while (op != 0);
    }

    private static void adicionarCategoria() {
        System.out.print("Nome da categoria: ");
        String nome = sc.nextLine();
        
        System.out.print("Limite mensal (deixe em branco se não tiver limite): R$ ");
        String limiteStr = sc.nextLine();
        Double limite = null;
        
        if (!limiteStr.trim().isEmpty()) {
            try {
                limite = Double.parseDouble(limiteStr.replace(",", "."));
            } catch (NumberFormatException e) {
                System.out.println("Valor inválido! Categoria criada sem limite.");
            }
        }
        
        Categoria categoria = new Categoria(nome, limite);
        categoriaDAO.adicionar(categoria);
    }

    private static void listarCategorias() {
        List<Categoria> categorias = categoriaDAO.listarTodas();
        
        if (categorias.isEmpty()) {
            System.out.println("Nenhuma categoria cadastrada.");
            return;
        }
        
        System.out.println("\n--- CATEGORIAS CADASTRADAS ---");
        for (Categoria c : categorias) {
            double gastoMes = categoriaDAO.totalGastoNoMes(c.getId());
            
            if (c.getLimiteMensal() != null) {
                System.out.printf("[%d] %s - Limite: R$ %.2f | Gasto no mês: R$ %.2f",
                        c.getId(), c.getNome(), c.getLimiteMensal(), gastoMes);
                if (gastoMes > c.getLimiteMensal()) {
                    System.out.print(" ⚠️ EXCEDEU!");
                }
                System.out.println();
            } else {
                System.out.printf("[%d] %s - Sem limite | Gasto no mês: R$ %.2f%n",
                        c.getId(), c.getNome(), gastoMes);
            }
        }
    }

    private static void editarCategoria() {
        listarCategorias();
        System.out.print("ID da categoria a editar: ");
        int id = lerInteiro();
        
        Categoria categoria = categoriaDAO.buscarPorId(id);
        if (categoria == null) {
            System.out.println("Categoria não encontrada!");
            return;
        }
        
        System.out.print("Novo nome (Enter para manter '" + categoria.getNome() + "'): ");
        String nome = sc.nextLine();
        if (!nome.trim().isEmpty()) {
            categoria.setNome(nome);
        }
        
        System.out.print("Novo limite mensal (Enter para manter ");
        if (categoria.getLimiteMensal() != null) {
            System.out.printf("R$ %.2f", categoria.getLimiteMensal());
        } else {
            System.out.print("sem limite");
        }
        System.out.print("): R$ ");
        
        String limiteStr = sc.nextLine();
        if (!limiteStr.trim().isEmpty()) {
            try {
                double limite = Double.parseDouble(limiteStr.replace(",", "."));
                categoria.setLimiteMensal(limite);
            } catch (NumberFormatException e) {
                System.out.println("Valor inválido! Mantendo limite anterior.");
            }
        }
        
        categoriaDAO.atualizar(categoria);
    }

    private static void excluirCategoria() {
        listarCategorias();
        System.out.print("ID da categoria a excluir: ");
        int id = lerInteiro();
        
        categoriaDAO.excluir(id);
    }

    private static void verGastosPorCategoria() {
        List<Categoria> categorias = categoriaDAO.listarTodas();
        
        if (categorias.isEmpty()) {
            System.out.println("Nenhuma categoria cadastrada.");
            return;
        }
        
        LocalDate hoje = LocalDate.now();
        String mesAno = String.format("%d/%d", hoje.getMonthValue(), hoje.getYear());
        
        System.out.println("\n--- GASTOS POR CATEGORIA - " + mesAno + " ---");
        
        double totalGeral = 0;
        
        for (Categoria c : categorias) {
            double gastoMes = categoriaDAO.totalGastoNoMes(c.getId());
            totalGeral += gastoMes;
            
            if (c.getLimiteMensal() != null) {
                System.out.printf("%s: R$ %.2f (Limite: R$ %.2f)",
                        c.getNome(), gastoMes, c.getLimiteMensal());
                if (gastoMes > c.getLimiteMensal()) {
                    System.out.print(" ⚠️ EXCEDEU!");
                }
                System.out.println();
            } else {
                System.out.printf("%s: R$ %.2f (Sem limite)%n", c.getNome(), gastoMes);
            }
        }
        
        System.out.println("----------------------------------------");
        System.out.printf("TOTAL GERAL: R$ %.2f%n", totalGeral);
    }

    private static int lerInteiro() {
        while (!sc.hasNextInt()) {
            System.out.print("Digite um número válido: ");
            sc.next();
        }
        int num = sc.nextInt();
        sc.nextLine();
        return num;
    }
}