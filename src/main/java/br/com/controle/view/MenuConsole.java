package br.com.controle.view;

import br.com.controle.dao.CategoriaDAO;
import br.com.controle.dao.GastoDAO;
import br.com.controle.model.Categoria;
import br.com.controle.model.Gasto;
import br.com.controle.database.ConnectionFactory;
import br.com.controle.view.MenuCategoria;

import java.time.LocalDate;
import br.com.controle.utils.DateUtils;
import java.util.List;
import java.util.Scanner;

public class MenuConsole {
    private static final Scanner sc = new Scanner(System.in);
    private static final GastoDAO dao = new GastoDAO();

    public static void main(String[] args) {
        ConnectionFactory.initDatabase();
        int op;
        do {
            System.out.println("\n===== CONTROLE DE GASTOS =====");
            System.out.println("1. Adicionar gasto");
            System.out.println("2. Listar todos os gastos");
            System.out.println("3. Editar gasto");
            System.out.println("4. Excluir gasto");
            System.out.println("5. Total gasto no mês atual");
            System.out.println("6. Gerenciar categorias");
            System.out.println("0. Sair");
            System.out.print("Escolha: ");
            op = lerInteiro();

            switch (op) {
                case 1 -> adicionarGasto();
                case 2 -> listarGastos();
                case 3 -> editarGasto();
                case 4 -> excluirGasto();
                case 5 -> mostrarTotalMes();
                case 6 -> MenuCategoria.executar();
                case 0 -> System.out.println("Até logo!");
                default -> System.out.println("Opção inválida.");
            }
        } while (op != 0);
        sc.close();
    }
    private static int escolherCategoria() {
        CategoriaDAO catDAO = new CategoriaDAO();
        List<Categoria> categorias = catDAO.listarTodas();

        if (categorias.isEmpty()) {
            System.out.println("Nenhuma categoria disponível. Cadastre uma categoria primeiro.");
            return -1;
        }

        System.out.println("\n--- CATEGORIAS DISPONÍVEIS ---");
        for (Categoria c : categorias) {
            System.out.println(c);
        }

        System.out.print("Digite o ID da categoria: ");
        int categoriaId = lerInteiro();
        // Verificar se o ID existe na lista de categorias
       Categoria escolhida = catDAO.buscarPorId(categoriaId);
        if (escolhida == null) {
            System.out.println("ID da categoria inválido. Operação cancelada.");
            return -1;
        }
        return categoriaId;
    }

    private static void adicionarGasto() {
        System.out.print("Descrição: ");
        String desc = sc.nextLine();
        System.out.print("Valor: ");
        double valor = lerDouble();
        System.out.print("Data (dd/MM/yyyy): ");
        LocalDate data = lerData();
        
        // Listar categorias para o usuário escolher
        int categoriaId = escolherCategoria();
        if (categoriaId == -1) {
            System.out.println("Operação cancelada. Nenhuma categoria selecionada.");
            return;
        }

        Gasto g = new Gasto(desc, valor, data, categoriaId);
        dao.adicionar(g);
        // Verifica se excedeu o limite da categoria
        CategoriaDAO catDAO = new CategoriaDAO();
        if (catDAO.excedeuLimite(categoriaId)) {
            Categoria cat = catDAO.buscarPorId(categoriaId);
            System.out.println("⚠️  ALERTA: O gasto excedeu o limite mensal da categoria '" + cat.getNome() + "'! ⚠️");
        }
    }

    private static void listarGastos() {
        List<Gasto> lista = dao.listarTodos();
        if (lista.isEmpty()) {
            System.out.println("Nenhum gasto cadastrado.");
        } else {
            System.out.println("\n--- LISTA DE GASTOS ---");
            for (Gasto g : lista) {
                System.out.println(g);
            }
        }
    }

    private static void editarGasto() {
        listarGastos();
        System.out.print("ID do gasto a editar: ");
        int id = lerInteiro();
        System.out.print("Nova descrição: ");
        String desc = sc.nextLine();
        System.out.print("Novo valor: ");
        double valor = lerDouble();
        System.out.print("Nova data (dd/MM/yyyy): ");
        LocalDate data = lerData();
        // Escolher nova categoria
        int categoriaId = escolherCategoria();
        if (categoriaId == -1) {
            return;
        }

        Gasto g = new Gasto(desc, valor, data, categoriaId);
        g.setId(id);
        dao.atualizar(g);
    }

    private static void excluirGasto() {
        listarGastos();
        System.out.print("ID do gasto a excluir: ");
        int id = lerInteiro();
        dao.excluir(id);
    }

    private static void mostrarTotalMes() {
        double total = dao.totalMesAtual();
        System.out.printf("Total gasto no mês corrente: R$ %.2f%n", total);
    }

    private static int lerInteiro() {
        while (!sc.hasNextInt()) {
            System.out.print("Digite um número válido: ");
            sc.next();
        }
        int num = sc.nextInt();
        sc.nextLine(); // limpar buffer
        return num;
    }

    private static double lerDouble() {
        while (!sc.hasNextDouble()) {
            System.out.print("Digite um valor numérico válido: ");
            sc.next();
        }
        double num = sc.nextDouble();
        sc.nextLine();
        return num;
    }

    private static LocalDate lerData() {
        while (true) {
            try {
                String dataStr = sc.nextLine();
                return DateUtils.parse(dataStr);
            } catch (IllegalArgumentException e) {
                System.out.print("Formato inválido! Use dd/MM/yyyy: ");
            }
        }
    }
}