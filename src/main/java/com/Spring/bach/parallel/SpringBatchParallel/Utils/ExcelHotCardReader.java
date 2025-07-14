package com.Spring.bach.parallel.SpringBatchParallel.Utils;

import com.Spring.bach.parallel.SpringBatchParallel.Entity.DataPipeline;
import com.Spring.bach.parallel.SpringBatchParallel.Entity.HotCards;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import java.io.FileInputStream;
import java.util.Iterator;

public class ExcelHotCardReader implements ItemReader<DataPipeline> {

    private Iterator<Row> filaIterator;
    private final DataFormatter formatter = new DataFormatter();
    private String filePath;

    public ExcelHotCardReader(String rutaArchivo) throws Exception {
        FileInputStream fis = new FileInputStream(rutaArchivo);
        Workbook workbook = new XSSFWorkbook(fis);
        Sheet sheet = workbook.getSheetAt(0);
        this.filaIterator = sheet.iterator();
        this.filePath = rutaArchivo;

        if (filaIterator.hasNext()) filaIterator.next();// saltar encabezado
    }

    @Override
    public DataPipeline read() throws Exception {
        if (!filaIterator.hasNext()) return null;

        Row fila = filaIterator.next();
        HotCards hotCards = HotCards.builder()
                .pan(getCellValue(fila, 0))
                .originUI(getCellValue(fila, 1))
                .modoEntrada(getCellValue(fila, 2))
                .appOrigen(getCellValue(fila, 3))
                .fechaCreacion(getCellValue(fila, 4))
                .validez(getCellValue(fila, 5))
                .marcaTiempo(getCellValue(fila, 6))
                .accion(getCellValue(fila, 7))
                .comentario(getCellValue(fila, 8))
                .usuario(getCellValue(fila, 9))
                .build();
        DataPipeline data = new DataPipeline(this.filePath,hotCards);
        return data;
    }

    private String getCellValue(Row row, int index) {
        Cell cell = row.getCell(index);
        return (cell != null) ? formatter.formatCellValue(cell) : "";
    }
}
