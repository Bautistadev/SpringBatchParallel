package com.Spring.bach.parallel.SpringBatchParallel.Service;

import com.Spring.bach.parallel.SpringBatchParallel.Entity.HotCards;
import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
public class ExcelService {

    private final BatchJobService batchJobService;



    public ExcelService(BatchJobService batchJobService) {
        this.batchJobService = batchJobService;
    }

    public String leerExcel(MultipartFile archivo) throws Exception {

        try {
            String rutaArchivo = System.getProperty("java.io.tmpdir") + "/" + archivo.getOriginalFilename();
            File archivoDestino = new File(rutaArchivo);
            archivo.transferTo(archivoDestino);
            String requestId = UUID.randomUUID().toString();
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("requestId", requestId)
                    .addString("rutaArchivo", rutaArchivo)
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters();

            batchJobService.runJobAsync(jobParameters);
            return "Job lanzado con id: " +requestId ;


        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public Map<?,?> getStatusJob(String jobExecutionId){
        JobExecution execution = this.batchJobService.getJobStatus(jobExecutionId);
        return Map.of(
                "id", jobExecutionId,
                "status", execution.getStatus().toString()
        );
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
