package com.Spring.bach.parallel.SpringBatchParallel.Service;

import com.Spring.bach.parallel.SpringBatchParallel.Entity.HotCards;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.*;

@Service
public class ExcelService {

    public List<List<String>> leerExcel(MultipartFile archivo) throws Exception {

        List<List<String>> filas = new ArrayList<>();

        try(InputStream is = archivo.getInputStream();
            Workbook workbook = new XSSFWorkbook(is)
        ){
            Sheet hoja = workbook.getSheetAt(0);
            hoja.forEach(e->{
                List<String> cellsValue = new ArrayList<>();
                e.forEach(i->{
                    String value = leerCeldaComoTexto(i);
                    cellsValue.add(value);
                });
                filas.add(cellsValue);
            });
        }


        return filas;
    }

    private String leerCeldaComoTexto(Cell celda) {
        if (celda == null) return "";

        switch (celda.getCellType()) {
            case STRING:
                return celda.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(celda)) {
                    return celda.getLocalDateTimeCellValue().toString(); // Podés usar DateTimeFormatter acá
                } else {
                    return String.valueOf(celda.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(celda.getBooleanCellValue());
            case BLANK:
                return "";
            default:
                return "";
        }
    }
}
