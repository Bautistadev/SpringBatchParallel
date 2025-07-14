package com.Spring.bach.parallel.SpringBatchParallel.Service;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class BatchJobService {

    private final JobLauncher jobLauncher;
    private JobExplorer jobExplorer;
    private final Job job;


    public BatchJobService(JobLauncher jobLauncher, Job job,JobExplorer jobExplorer) {
        this.jobLauncher = jobLauncher;
        this.jobExplorer = jobExplorer;
        this.job = job;
    }

    @Async
    public void runJobAsync(JobParameters params) {
        try{
            JobExecution execution = jobLauncher.run(job, params);
        } catch (Exception e) {
            throw new RuntimeException("Error lanzando job", e);
        }

    }

    public JobExecution getJobStatus( String requestId) {
        return jobExplorer.getJobInstances(job.getName(), 0, 1000).stream()
                .flatMap(instance -> jobExplorer.getJobExecutions(instance).stream())
                .filter(execution -> requestId.equals(execution.getJobParameters().getString("requestId")))
                .findFirst()
                .orElse(null);
    }
}
