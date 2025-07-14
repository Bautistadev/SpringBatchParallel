package com.Spring.bach.parallel.SpringBatchParallel.Configuration;

import com.Spring.bach.parallel.SpringBatchParallel.Entity.DataPipeline;
import com.Spring.bach.parallel.SpringBatchParallel.Entity.HotCards;
import com.Spring.bach.parallel.SpringBatchParallel.Utils.ExcelHotCardReader;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.File;
import java.io.IOException;


@Configuration
public class BatchConfig {

    @Bean
    @StepScope
    public ItemReader<DataPipeline> excelReader(@Value("#{jobParameters['rutaArchivo']}") String rutaArchivo) throws Exception {
        if (rutaArchivo == null) {
            return () -> null; // No hace nada, no rompe
        }
        return new ExcelHotCardReader(rutaArchivo);
    }

    @Bean
    public ItemProcessor<DataPipeline, DataPipeline> processor() {
        return card -> {
            System.out.println("Procesando: " + card);
            return card;
        };
    }

    @Bean
    public ItemWriter<DataPipeline> writer() {
        return items -> {
            // Por simplicidad no hacemos nada aquí
            Thread.sleep(10000);
            // Podrías guardar en BD o archivo
        };
    }

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);     // Número de hilos activos
        executor.setMaxPoolSize(8);      // Máximo hilos
        executor.setQueueCapacity(10);
        executor.setThreadNamePrefix("batch-thread-");
        executor.initialize(); // o el número de hilos que desees
        return executor;
    }
    @Bean
    public Step step1(ItemReader<DataPipeline> reader,TaskExecutor taskExecutor,
                      ItemProcessor<DataPipeline, DataPipeline> processor,ItemWriter<DataPipeline> itemWriter,StepListener stepListener, PlatformTransactionManager transactionManager, JobRepository jobRepository) {

        return new StepBuilder("step1",jobRepository)
                .<DataPipeline, DataPipeline>chunk(500,transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(itemWriter)
                .faultTolerant()
                    .skipLimit(5)
                    .skip(Exception.class)
                    .retryLimit(3)
                    .retry(IOException.class)
                .taskExecutor(taskExecutor)
                .listener(stepListener())
                .listener(new SkipListener<DataPipeline,DataPipeline>() {
                    @Override
                    public void onSkipInRead(Throwable t) {
                        System.err.println("Error leyendo ítem: " + t.getMessage());
                    }

                    @Override
                    public void onSkipInWrite(DataPipeline item, Throwable t) {
                        System.err.println("Error escribiendo ítem: " + item + " - Error: " + t.getMessage());
                    }

                    @Override
                    public void onSkipInProcess(DataPipeline item, Throwable t) {
                        System.err.println("Error procesando ítem: " + item + " - Error: " + t.getMessage());
                    }
                })
                .build();
    }

    @Bean
    public StepExecutionListener stepListener() {
        return new StepExecutionListener() {
            @Override
            public void beforeStep(StepExecution stepExecution) {
                System.out.println("Iniciando step: " + stepExecution.getStepName());
            }

            @Override
            public ExitStatus afterStep(StepExecution stepExecution) {
                System.out.println("Finalizó step: " + stepExecution.getStepName() +
                        " con status: " + stepExecution.getExitStatus());
                String rutaArchivo = stepExecution.getJobParameters().getString("rutaArchivo");
                if ( rutaArchivo!= null) {
                    File archivo = new File(rutaArchivo);
                    if (archivo.exists()) {
                        boolean borrado = archivo.delete();
                        if (borrado) {
                            System.out.println("Archivo temporal eliminado: " + rutaArchivo);
                        } else {
                            System.err.println("No se pudo eliminar el archivo: " + rutaArchivo);
                        }
                    } else {
                        System.out.println("El archivo no existía: " + rutaArchivo);
                    }
                }
                return stepExecution.getExitStatus();
            }
        };
    }

    @Bean
    public Job job(Step step,JobRepository jobRepository){
        return new JobBuilder("job1",jobRepository).start(step).build();
    }
}
