package br.com.controle.service;

import br.com.controle.dao.GastoDAO;
import br.com.controle.dao.ReceitaDAO;
import br.com.controle.dao.CategoriaDAO;
import br.com.controle.model.Categoria;
import br.com.controle.utils.DateUtils;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Serviço de relatórios financeiros.
 * Responsável por cálculos e geração de resumos do sistema.
 * 
 * @author Douglas Lira
 * @version 3.0
 */
public class RelatorioService {
    
    private final GastoDAO gastoDAO;
    private final ReceitaDAO receitaDAO;
    private final CategoriaDAO categoriaDAO;
    
    /**
     * Construtor - inicializa os DAOs necessários.
     */
    public RelatorioService() {
        this.gastoDAO = new GastoDAO();
        this.receitaDAO = new ReceitaDAO();
        this.categoriaDAO = new CategoriaDAO();
    }
    
    // ========== MÉTODOS PRINCIPAIS ==========
    
    /**
     * Calcula o saldo do mês atual.
     * Saldo = Total de Receitas - Total de Gastos
     * 
     * @return Saldo do mês (pode ser positivo ou negativo)
     */
    public double calcularSaldoMensal() {
        LocalDate hoje = LocalDate.now();
        double totalReceitas = receitaDAO.totalReceitasPorMes(hoje.getMonthValue(), hoje.getYear());
        double totalGastos = gastoDAO.totalMesAtual();
        return totalReceitas - totalGastos;
    }
    
    /**
     * Calcula o saldo de um mês específico.
     * 
     * @param mes Mês (1-12)
     * @param ano Ano (ex: 2026)
     * @return Saldo do mês específico
     */
    public double calcularSaldoPorMes(int mes, int ano) {
        double totalReceitas = receitaDAO.totalReceitasPorMes(mes, ano);
        double totalGastos = gastoDAO.totalMesPorData(mes, ano);
        return totalReceitas - totalGastos;
    }
    
    /**
     * Gera um resumo completo do mês atual.
     * 
     * @return Mapa com todos os indicadores do mês
     */
    public Map<String, Object> gerarResumoMensal() {
        Map<String, Object> resumo = new HashMap<>();
        LocalDate hoje = LocalDate.now();
        
        double totalReceitas = receitaDAO.totalReceitasPorMes(hoje.getMonthValue(), hoje.getYear());
        double totalGastos = gastoDAO.totalMesPorData(hoje.getMonthValue(), hoje.getYear());
        double saldo = totalReceitas - totalGastos;
        
        resumo.put("mes", hoje.getMonthValue());
        resumo.put("ano", hoje.getYear());
        resumo.put("nomeMes", getNomeMes(hoje.getMonthValue()));
        resumo.put("totalReceitas", totalReceitas);
        resumo.put("totalGastos", totalGastos);
        resumo.put("saldo", saldo);
        resumo.put("status", saldo >= 0 ? "POSITIVO" : "NEGATIVO");
        
        return resumo;
    }
    
    /**
     * Gera um resumo formatado para exibição no console.
     * 
     * @return String formatada com o resumo financeiro
     */
    public String gerarResumoFormatado() {
        Map<String, Object> resumo = gerarResumoMensal();
        
        StringBuilder sb = new StringBuilder();
        sb.append("\n").append("=".repeat(60));
        sb.append("\n              📊 RESUMO FINANCEIRO");
        sb.append("\n").append("=".repeat(60));
        sb.append(String.format("\n   📅 Mês: %s/%d", resumo.get("nomeMes"), resumo.get("ano")));
        sb.append(String.format("\n   💰 Total de Receitas:   R$ %.2f", resumo.get("totalReceitas")));
        sb.append(String.format("\n   💸 Total de Despesas:   R$ %.2f", resumo.get("totalGastos")));
        sb.append("\n").append("-".repeat(60));
        sb.append(String.format("\n   📊 SALDO DO MÊS:        R$ %.2f", resumo.get("saldo")));
        
        String status = (boolean) resumo.get("status") ? "✅ SUPERAVIT" : "❌ DEFICIT";
        sb.append(String.format("\n   🏷️ Status: %s", status));
        sb.append("\n").append("=".repeat(60));
        
        return sb.toString();
    }
    
    // ========== MÉTODOS PARA RELATÓRIO DE GASTOS POR CATEGORIA ==========
    
    /**
     * Gera relatório de gastos por categoria no mês atual.
     * 
     * @return Mapa com categoria -> total gasto
     */
    public Map<String, Double> gerarGastosPorCategoria() {
        Map<String, Double> relatorio = new HashMap<>();
        List<Categoria> categorias = categoriaDAO.listarTodas();
        
        for (Categoria cat : categorias) {
            double total = categoriaDAO.totalGastoNoMes(cat.getId());
            if (total > 0) {
                relatorio.put(cat.getNome(), total);
            }
        }
        
        return relatorio;
    }
    
    /**
     * Gera relatório formatado de gastos por categoria.
     * 
     * @return String formatada com os gastos por categoria
     */
    public String gerarRelatorioCategoriasFormatado() {
        Map<String, Double> gastosPorCategoria = gerarGastosPorCategoria();
        List<Categoria> categorias = categoriaDAO.listarTodas();
        
        StringBuilder sb = new StringBuilder();
        sb.append("\n").append("=".repeat(60));
        sb.append("\n         📊 GASTOS POR CATEGORIA");
        sb.append("\n").append("=".repeat(60));
        
        boolean temGastos = false;
        for (Categoria cat : categorias) {
            double total = categoriaDAO.totalGastoNoMes(cat.getId());
            if (cat.getLimiteMensal() != null) {
                sb.append(String.format("\n   %-20s R$ %8.2f  (Limite: R$ %.2f)",
                        truncarTexto(cat.getNome(), 20), total, cat.getLimiteMensal()));
                if (total > cat.getLimiteMensal()) {
                    sb.append(" ⚠️ EXCEDEU!");
                }
            } else {
                sb.append(String.format("\n   %-20s R$ %8.2f  (Sem limite)",
                        truncarTexto(cat.getNome(), 20), total));
            }
            if (total > 0) temGastos = true;
        }
        
        if (!temGastos) {
            sb.append("\n   📭 Nenhum gasto registrado no mês.");
        }
        
        sb.append("\n").append("=".repeat(60));
        return sb.toString();
    }
    
    // ========== MÉTODOS AUXILIARES ==========
    
    /**
     * Converte número do mês para nome em português.
     * 
     * @param mes Número do mês (1-12)
     * @return Nome do mês por extenso
     */
    private String getNomeMes(int mes) {
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
    private String truncarTexto(String texto, int tamanhoMaximo) {
        if (texto == null) return "";
        if (texto.length() <= tamanhoMaximo) return texto;
        return texto.substring(0, tamanhoMaximo - 3) + "...";
    }
}