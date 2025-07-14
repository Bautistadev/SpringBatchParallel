package com.Spring.bach.parallel.SpringBatchParallel.Entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class DataPipeline {
    private String fileName;
    private Object object;
}
