package com.example.SpringBatchTutorial.conditionalStepJob;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.config.Task;

/**
 * desc: step 결과의 따른 다음 step 분기 처리
 * 각 단계에서의 상태(성공, 실패 등)에 따라 흐름이 결정되는 조건부 단계 전환
 * run param: --job.name=conditionalStepJob
 */
@RequiredArgsConstructor
@Configuration
public class ConditionalStepJobConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job conditionalStepJob(Step conditionalStartStep,
                                  Step conditionalAllStep,
                                  Step conditionalFailStep,
                                  Step conditionalCompletedStep){
        return jobBuilderFactory.get("conditionalStepJob")
                .incrementer(new RunIdIncrementer())
                .start(conditionalStartStep)
                .on("FAILED").to(conditionalFailStep)//conditionalStartStep이 실패하면 conditionalFailStep로 이동.
                .from(conditionalStartStep)
                .on("COMPLETED").to(conditionalCompletedStep)//conditionalStartStep이 완료되면 conditionalCompletedStep로 이동
                .from(conditionalStartStep)
                .on("*").to(conditionalAllStep)// 완료도 아니고 실패도 아닌 나머지 모든 상태에서 conditionalAllStep로 이동.
                .end().build();
    }
    // FAILED, COMPLETED, * 는 Spring Batch의 Exit Status 를 기준으로 결정됨.


    @JobScope
    @Bean
    public Step conditionalStartStep(){
        return stepBuilderFactory.get("conditionalStartStep")
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
//                          System.out.println("conditional Start Step");
//                        return RepeatStatus.FINISHED;
                        throw new Exception("Exception!!");
                    }
                }).build();
    }

    @Bean
    @JobScope
    public Step conditionalAllStep(){
        return stepBuilderFactory.get("conditionalAllStep")
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                        System.out.println("conditional All Step");
                        return RepeatStatus.FINISHED;
                    }
                }).build();
    }


    @Bean
    @JobScope
    public Step conditionalFailStep(){
        return stepBuilderFactory.get("conditionalFailStep")
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                        System.out.println("conditional Fail Step");
                        return RepeatStatus.FINISHED;
                    }
                }).build();
    }

    @Bean
    @JobScope
    public Step conditionalCompletedStep(){
        return stepBuilderFactory.get("conditionalCompletedStep")
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                        System.out.println("conditional Completed Step");
                        return RepeatStatus.FINISHED;
                    }
                }).build();
    }





}
