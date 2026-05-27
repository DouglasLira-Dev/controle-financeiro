package br.com.controle.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateUtils {
    
    // Formato que queremos: dd/MM/yyyy
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    // Converter LocalDate para String no formato dd/MM/yyyy
    public static String formatar(LocalDate data) {
        if (data == null) return null;
        return data.format(FORMATTER);
    }
    
    // Converter String dd/MM/yyyy para LocalDate
    public static LocalDate parse(String dataStr) {
        if (dataStr == null || dataStr.trim().isEmpty()) return null;
        try {
            return LocalDate.parse(dataStr, FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Data inválida! Use o formato dd/MM/yyyy (ex: 25/05/2026)");
        }
    }
    
    // Obter primeiro dia do mês de uma data (para cálculos)
    public static LocalDate primeiroDiaMes(LocalDate data) {
        return data.withDayOfMonth(1);
    }
    
    // Obter último dia do mês de uma data (para cálculos)
    public static LocalDate ultimoDiaMes(LocalDate data) {
        return data.withDayOfMonth(data.lengthOfMonth());
    }
    
    // Formatar para exibição (igual ao formatar)
    public static String exibir(LocalDate data) {
        return formatar(data);
    }
}