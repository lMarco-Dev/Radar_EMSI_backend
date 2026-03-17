package com.emsi.reporte.service;

import com.emsi.reporte.model.Reporte;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ExcelService {

    public ByteArrayInputStream exportarReportes(List<Reporte> reportes) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Reportes SST");

            // Estilo para la cabecera (Fondo azul oscuro, texto blanco, negrita)
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Font headerFont = workbook.createFont();
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            // Títulos de las columnas
            String[] columnas = {"Folio", "Fecha", "Empresa", "Área", "Turno", "Clasificación", "Causa Raíz", "Estado", "Descripción"};
            Row headerRow = sheet.createRow(0);

            for (int col = 0; col < columnas.length; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(columnas[col]);
                cell.setCellStyle(headerStyle);
            }

            // Llenar los datos de los reportes
            int rowIdx = 1;
            for (Reporte r : reportes) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(r.getFolio());
                row.createCell(1).setCellValue(r.getFechaOcurrido() != null ? r.getFechaOcurrido().toString() : "");
                row.createCell(2).setCellValue(r.getEmpresa().getNombre());
                row.createCell(3).setCellValue(r.getArea() != null ? r.getArea() : "");
                row.createCell(4).setCellValue(r.getTurno() != null ? r.getTurno().name() : "");
                row.createCell(5).setCellValue(r.getTipoComportamiento().getNombre());
                row.createCell(6).setCellValue(r.getCausa() != null ? r.getCausa().getNombre() : "N/A");
                row.createCell(7).setCellValue(r.getEstado().name());
                row.createCell(8).setCellValue(r.getDescripcionComportamiento());
            }

            // Auto-ajustar el ancho de las columnas
            for (int col = 0; col < columnas.length; col++) {
                sheet.autoSizeColumn(col);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException("Error al generar el archivo Excel: " + e.getMessage());
        }
    }
}