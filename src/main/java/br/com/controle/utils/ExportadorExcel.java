package br.com.controle.utils;

import br.com.controle.dao.GastoDAO;
import br.com.controle.dao.ReceitaDAO;
import br.com.controle.dao.CategoriaDAO;
import br.com.controle.model.Gasto;
import br.com.controle.model.Receita;
import br.com.controle.model.Categoria;
import br.com.controle.service.RelatorioService;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Utilitário para exportação de dados para Excel.
 * Gera um arquivo .xlsx com abas de Gastos, Receitas e Resumo.
 * 
 * @author Douglas Lira
 * @version 3.0
 */
public class ExportadorExcel {
    
    private static final GastoDAO gastoDAO = new GastoDAO();
    private static final ReceitaDAO receitaDAO = new ReceitaDAO();
    private static final CategoriaDAO categoriaDAO = new CategoriaDAO();
    private static final RelatorioService relatorioService = new RelatorioService();
    
    // Cores para formatação
    private static final short COR_TITULO = IndexedColors.DARK_BLUE.getIndex();
    private static final short COR_CABECALHO = IndexedColors.LIGHT_BLUE.getIndex();
    private static final short COR_POSITIVO = IndexedColors.GREEN.getIndex();      
    private static final short COR_NEGATIVO = IndexedColors.RED.getIndex();        

    /**
     * Exporta todos os dados para um arquivo Excel.
     * O arquivo é salvo na pasta do projeto com o nome: relatorio_YYYYMMDD_HHMMSS.xlsx
     * 
     * @return Caminho do arquivo gerado
     */
    public static String exportar() {
        try (Workbook workbook = new XSSFWorkbook()) {
            
            // Criar as três abas
            Sheet sheetGastos = workbook.createSheet("📊 Gastos");
            Sheet sheetReceitas = workbook.createSheet("💰 Receitas");
            Sheet sheetResumo = workbook.createSheet("📈 Resumo");
            
            // Preencher cada aba
            preencherAbaGastos(workbook, sheetGastos);
            preencherAbaReceitas(workbook, sheetReceitas);
            preencherAbaResumo(workbook, sheetResumo);
            
            // Ajustar largura das colunas automaticamente
            for (int i = 0; i < 6; i++) {
                sheetGastos.autoSizeColumn(i);
                sheetReceitas.autoSizeColumn(i);
            }
            for (int i = 0; i < 4; i++) {
                sheetResumo.autoSizeColumn(i);
            }
            
            // Gerar nome do arquivo com data e hora
            String nomeArquivo = gerarNomeArquivo();
            
            // Salvar arquivo
            try (FileOutputStream fileOut = new FileOutputStream(nomeArquivo)) {
                workbook.write(fileOut);
            }
            
            System.out.println("\n✅ Relatório Excel gerado com sucesso!");
            System.out.println("📁 Arquivo salvo: " + nomeArquivo);
            return nomeArquivo;
            
        } catch (IOException e) {
            System.err.println("❌ Erro ao gerar arquivo Excel: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Preenche a aba de Gastos.
     */
    private static void preencherAbaGastos(Workbook workbook, Sheet sheet) {
        // Estilos
        CellStyle estiloTitulo = criarEstiloTitulo(workbook);
        CellStyle estiloCabecalho = criarEstiloCabecalho(workbook);
        CellStyle estiloData = criarEstiloData(workbook);
        CellStyle estiloValor = criarEstiloValor(workbook);
        
        // Linha do título
        Row tituloRow = sheet.createRow(0);
        Cell tituloCell = tituloRow.createCell(0);
        tituloCell.setCellValue("RELATÓRIO DE GASTOS");
        tituloCell.setCellStyle(estiloTitulo);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 5));
        
        // Linha em branco
        sheet.createRow(1);
        
        // Linha de data
        Row dataRow = sheet.createRow(2);
        Cell dataLabel = dataRow.createCell(0);
        dataLabel.setCellValue("Data de geração:");
        dataLabel.setCellStyle(estiloCabecalho);
        
        Cell dataValor = dataRow.createCell(1);
        dataValor.setCellValue(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        dataValor.setCellStyle(estiloData);
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 1, 5));
        
        // Linha em branco
        sheet.createRow(3);
        
        // Cabeçalho da tabela
        Row cabecalhoRow = sheet.createRow(4);
        String[] colunas = {"ID", "DESCRIÇÃO", "VALOR (R$)", "DATA", "CATEGORIA", "LIMITE CATEGORIA"};
        
        for (int i = 0; i < colunas.length; i++) {
            Cell cell = cabecalhoRow.createCell(i);
            cell.setCellValue(colunas[i]);
            cell.setCellStyle(estiloCabecalho);
        }
        
        // Dados
        List<Gasto> gastos = gastoDAO.listarTodos();
        int rowNum = 5;
        double totalGastos = 0;
        
        for (Gasto g : gastos) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(g.getId());
            row.createCell(1).setCellValue(g.getDescricao());
            
            Cell valorCell = row.createCell(2);
            valorCell.setCellValue(g.getValor());
            valorCell.setCellStyle(estiloValor);
            totalGastos += g.getValor();
            
            row.createCell(3).setCellValue(DateUtils.exibir(g.getData()));
            row.createCell(4).setCellValue(g.getNomeCategoria());
            
            // Buscar limite da categoria
            Categoria cat = categoriaDAO.buscarPorId(g.getCategoriaId());
            String limite = (cat != null && cat.getLimiteMensal() != null) 
                ? String.format("R$ %.2f", cat.getLimiteMensal()) 
                : "Sem limite";
            row.createCell(5).setCellValue(limite);
        }
        
        // Linha de total
        Row totalRow = sheet.createRow(rowNum + 1);
        Cell totalLabel = totalRow.createCell(1);
        totalLabel.setCellValue("TOTAL GERAL:");
        totalLabel.setCellStyle(estiloCabecalho);
        
        Cell totalValor = totalRow.createCell(2);
        totalValor.setCellValue(totalGastos);
        totalValor.setCellStyle(estiloValor);
    }
    
    /**
     * Preenche a aba de Receitas.
     */
    private static void preencherAbaReceitas(Workbook workbook, Sheet sheet) {
        CellStyle estiloTitulo = criarEstiloTitulo(workbook);
        CellStyle estiloCabecalho = criarEstiloCabecalho(workbook);
        CellStyle estiloValor = criarEstiloValor(workbook);
        
        // Título
        Row tituloRow = sheet.createRow(0);
        Cell tituloCell = tituloRow.createCell(0);
        tituloCell.setCellValue("RELATÓRIO DE RECEITAS");
        tituloCell.setCellStyle(estiloTitulo);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));
        
        sheet.createRow(1);
        
        // Data
        Row dataRow = sheet.createRow(2);
        Cell dataLabel = dataRow.createCell(0);
        dataLabel.setCellValue("Data de geração:");
        dataLabel.setCellStyle(estiloCabecalho);
        
        Cell dataValor = dataRow.createCell(1);
        dataValor.setCellValue(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 1, 4));
        
        sheet.createRow(3);
        
        // Cabeçalho
        Row cabecalhoRow = sheet.createRow(4);
        String[] colunas = {"ID", "DESCRIÇÃO", "VALOR (R$)", "DATA", "CATEGORIA"};
        
        for (int i = 0; i < colunas.length; i++) {
            Cell cell = cabecalhoRow.createCell(i);
            cell.setCellValue(colunas[i]);
            cell.setCellStyle(estiloCabecalho);
        }
        
        // Dados
        List<Receita> receitas = receitaDAO.listar();
        int rowNum = 5;
        double totalReceitas = 0;
        
        for (Receita r : receitas) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(r.getId());
            row.createCell(1).setCellValue(r.getDescricao());
            
            Cell valorCell = row.createCell(2);
            valorCell.setCellValue(r.getValor());
            valorCell.setCellStyle(estiloValor);
            totalReceitas += r.getValor();
            
            row.createCell(3).setCellValue(DateUtils.exibir(r.getData()));
            row.createCell(4).setCellValue(r.getCategoria());
        }
        
        // Total
        Row totalRow = sheet.createRow(rowNum + 1);
        Cell totalLabel = totalRow.createCell(1);
        totalLabel.setCellValue("TOTAL GERAL:");
        totalLabel.setCellStyle(estiloCabecalho);
        
        Cell totalValor = totalRow.createCell(2);
        totalValor.setCellValue(totalReceitas);
        totalValor.setCellStyle(estiloValor);
    }
    
    /**
     * Preenche a aba de Resumo.
     */
    private static void preencherAbaResumo(Workbook workbook, Sheet sheet) {
        CellStyle estiloTitulo = criarEstiloTitulo(workbook);
        CellStyle estiloCabecalho = criarEstiloCabecalho(workbook);
        CellStyle estiloValor = criarEstiloValor(workbook);
        CellStyle estiloPositivo = criarEstiloPositivo(workbook);
        CellStyle estiloNegativo = criarEstiloNegativo(workbook);
        
        // Título
        Row tituloRow = sheet.createRow(0);
        Cell tituloCell = tituloRow.createCell(0);
        tituloCell.setCellValue("RESUMO FINANCEIRO");
        tituloCell.setCellStyle(estiloTitulo);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 2));
        
        sheet.createRow(1);
        
        // Data
        Row dataRow = sheet.createRow(2);
        Cell dataLabel = dataRow.createCell(0);
        dataLabel.setCellValue("Data de geração:");
        dataLabel.setCellStyle(estiloCabecalho);
        
        Cell dataValor = dataRow.createCell(1);
        dataValor.setCellValue(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 1, 2));
        
        sheet.createRow(3);
        
        // Totais
        LocalDate hoje = LocalDate.now();
        double totalReceitas = receitaDAO.totalReceitasPorMes(hoje.getMonthValue(), hoje.getYear());
        double totalGastos = gastoDAO.totalMesPorData(hoje.getMonthValue(), hoje.getYear());
        double saldo = totalReceitas - totalGastos;
        
        int rowNum = 5;
        Row rowReceitas = sheet.createRow(rowNum++);
        rowReceitas.createCell(0).setCellValue("💰 TOTAL DE RECEITAS");
        Cell receitasValor = rowReceitas.createCell(1);
        receitasValor.setCellValue(totalReceitas);
        receitasValor.setCellStyle(estiloValor);
        
        Row rowGastos = sheet.createRow(rowNum++);
        rowGastos.createCell(0).setCellValue("💸 TOTAL DE GASTOS");
        Cell gastosValor = rowGastos.createCell(1);
        gastosValor.setCellValue(totalGastos);
        gastosValor.setCellStyle(estiloValor);
        
        Row rowSaldo = sheet.createRow(rowNum++);
        rowSaldo.createCell(0).setCellValue("📊 SALDO DO MÊS");
        Cell saldoValor = rowSaldo.createCell(1);
        saldoValor.setCellValue(saldo);
        saldoValor.setCellStyle(saldo >= 0 ? estiloPositivo : estiloNegativo);
        
        // Status
        rowNum++;
        Row rowStatus = sheet.createRow(rowNum);
        rowStatus.createCell(0).setCellValue("🏷️ STATUS");
        Cell statusValor = rowStatus.createCell(1);
        String status = saldo >= 0 ? "SUPERÁVIT (Economia positiva)" : "DÉFICIT (Gastos maiores que receitas)";
        statusValor.setCellValue(status);
        
        // Gastos por categoria
        rowNum += 2;
        Row tituloCatRow = sheet.createRow(rowNum);
        tituloCatRow.createCell(0).setCellValue("GASTOS POR CATEGORIA");
        tituloCatRow.createCell(0).setCellStyle(estiloCabecalho);
        
        rowNum++;
        List<Categoria> categorias = categoriaDAO.listarTodas();
        for (Categoria cat : categorias) {
            double total = categoriaDAO.totalGastoNoMes(cat.getId());
            if (total > 0) {
                Row catRow = sheet.createRow(rowNum++);
                catRow.createCell(0).setCellValue("   • " + cat.getNome());
                Cell catValor = catRow.createCell(1);
                catValor.setCellValue(total);
                catValor.setCellStyle(estiloValor);
            }
        }
    }
    
    // ========== MÉTODOS DE ESTILIZAÇÃO ==========
    
    private static CellStyle criarEstiloTitulo(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 14);
        font.setColor(COR_TITULO);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }
    
    private static CellStyle criarEstiloCabecalho(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(COR_CABECALHO);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }
    
    private static CellStyle criarEstiloValor(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.RIGHT);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }
    
    private static CellStyle criarEstiloData(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }
    
    private static CellStyle criarEstiloPositivo(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(COR_POSITIVO);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.RIGHT);
        return style;
    }
    
    private static CellStyle criarEstiloNegativo(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(COR_NEGATIVO);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.RIGHT);
        return style;
    }
    
    /**
     * Gera nome do arquivo com timestamp.
     * Formato: relatorio_YYYYMMDD_HHMMSS.xlsx
     */
    private static String gerarNomeArquivo() {
        LocalDate agora = LocalDate.now();
        String timestamp = java.time.LocalDateTime.now()
            .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        return "relatorio_" + timestamp + ".xlsx";
    }
}