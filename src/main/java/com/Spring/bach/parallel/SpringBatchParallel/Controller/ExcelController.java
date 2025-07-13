package com.Spring.bach.parallel.SpringBatchParallel.Controller;

import com.Spring.bach.parallel.SpringBatchParallel.Service.ExcelService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/1")
@AllArgsConstructor
public class ExcelController {

    private ExcelService excelService;

    @PostMapping(value = "/leer-stream", consumes = "multipart/form-data")
    public ResponseEntity<List<List<String>>> leerConStream(@RequestParam("archivo") MultipartFile archivo) throws Exception {

        return ResponseEntity.status(HttpStatus.OK).body( excelService.leerExcel(archivo));
    }
}
