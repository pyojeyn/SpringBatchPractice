package com.example.SpringBatchTutorial.job.jobListener;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * desc : HelloWold 를 출력
 * run: --spring.batch.job.names=jobListenerJob
 */


@Configuration
@RequiredArgsConstructor
public class JobListenerConfig {


    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job jobListenerJob(){
        return jobBuilderFactory.get("jobListenerJob")
                .incrementer(new RunIdIncrementer())
                .start(jobListenerStep())
                .listener(new JobLoggerListener())
                .build();
    }

    @Bean
    public Step jobListenerStep(){
        return stepBuilderFactory.get("jobListenerStep")
                .tasklet(jobListenerTasklet())
                .build();
    }

    @StepScope
    @Bean
    public Tasklet jobListenerTasklet(){
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                System.out.println("job Listener Tasklet");
                return RepeatStatus.FINISHED;
            }
        };
    }

}
