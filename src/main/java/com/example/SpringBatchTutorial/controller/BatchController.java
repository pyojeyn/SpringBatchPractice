package com.example.SpringBatchTutorial.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("batch")
public class BatchController {

    private final JobLauncher jobLauncher;

    @Qualifier("validatedParamJob")
    private final Job validatedParamJob;


    @GetMapping
    public Map<String, Object> runJob(@RequestParam String fileName) throws Exception{

        // Job Parameter 생성
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("fileName", fileName)
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();


        // Job 실행
        JobExecution jobExecution = jobLauncher.run(validatedParamJob, jobParameters);

        Map<String, Object> response = new HashMap<>();
        response.put("jobId", jobExecution.getJobId());
        response.put("status", jobExecution.getStatus());
        response.put("exitStatus", jobExecution.getExitStatus().getExitCode());
        return response;

    }
}
