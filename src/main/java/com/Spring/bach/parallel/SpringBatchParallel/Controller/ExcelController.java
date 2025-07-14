package com.Spring.bach.parallel.SpringBatchParallel.Controller;

import com.Spring.bach.parallel.SpringBatchParallel.Service.ExcelService;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.JobExecution;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/1")
@AllArgsConstructor
public class ExcelController {

    private ExcelService excelService;

    @PostMapping(value = "/leer-stream", consumes = "multipart/form-data")
    public ResponseEntity<String> leerConStream(@RequestParam("archivo") MultipartFile archivo) throws Exception {
        return ResponseEntity.status(HttpStatus.OK).body(excelService.leerExcel(archivo));
    }

    @GetMapping("/status/{jobExecutionId}")
    public ResponseEntity<Map<?,?>> getJobStatus(@PathVariable String jobExecutionId) {
        return ResponseEntity.status(HttpStatus.OK).body(this.excelService.getStatusJob(jobExecutionId));

    }

}
