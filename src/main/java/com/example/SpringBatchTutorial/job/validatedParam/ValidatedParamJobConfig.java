package com.example.SpringBatchTutorial.job.validatedParam;

import com.example.SpringBatchTutorial.job.validatedParam.validator.FileParamValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.CompositeJobParametersValidator;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

/**
 * desc : 파일 이름 파라미터 전달 그리고 검증
 * run: --spring.batch.job.names=validatedParamJob -fileName=test.csv
 */
@Configuration
@RequiredArgsConstructor
public class ValidatedParamJobConfig {


    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job validatedParamJob(Step validatedParamStep){
        return jobBuilderFactory.get("validatedParamJob")
                .incrementer(new RunIdIncrementer())
//                .validator(new FileParamValidator()) // 하나만 전달
                .validator(multipleValidator())
                .start(validatedParamStep)
                .build();
    }

    // 여러개의 validator 등록
    private CompositeJobParametersValidator multipleValidator(){
        CompositeJobParametersValidator validator = new CompositeJobParametersValidator();
        validator.setValidators(Arrays.asList(new FileParamValidator()));

        return validator;
    }

    @Bean
    public Step validatedParamStep(Tasklet validatedParamTasklet){
        return stepBuilderFactory.get("validatedParamStep")
                .tasklet(validatedParamTasklet)
                .build();
    }

    @StepScope
    @Bean
    public Tasklet validatedParamTasklet(@Value("#{jobParameters['fileName']}") String fileName){
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                System.out.println(fileName);
                System.out.println("validated Param Tasklet");
                return RepeatStatus.FINISHED;
            }
        };
    }

}
