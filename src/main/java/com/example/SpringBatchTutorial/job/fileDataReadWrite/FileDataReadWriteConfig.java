package com.example.SpringBatchTutorial.job.fileDataReadWrite;

import com.example.SpringBatchTutorial.job.fileDataReadWrite.dto.Player;
import com.example.SpringBatchTutorial.job.fileDataReadWrite.dto.PlayerYears;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import java.util.List;

/**
 * desc : 주문 테이블 -> 정산 테이블 데이터 이관
 * run: --spring.batch.job.names=fileReadWriteJob
 */
@Configuration
@RequiredArgsConstructor
public class FileDataReadWriteConfig {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job fileReadWriteJob(Step fileReadWriteStep){
        return jobBuilderFactory.get("fileReadWriteJob")
                .incrementer(new RunIdIncrementer())
                .start(fileReadWriteStep)
                .build();
    }

    @JobScope
    @Bean
    public Step fileReadWriteStep(FlatFileItemReader playerItemReader,
                                  ItemProcessor playerItemProcessor,
                                  FlatFileItemWriter playerItemWriter){
        return stepBuilderFactory.get("fileReadWriteStep")
                .<Player, PlayerYears>chunk(5)
                .reader(playerItemReader)
                .processor(playerItemProcessor)
                .writer(playerItemWriter)
                .build();
    }

    @Bean
    @StepScope // Step 실행 중에만 사용될 수 있도록 범위를 제한한다.
    public FlatFileItemReader<Player> playerItemReader(){
        return new FlatFileItemReaderBuilder<Player>()
                .name("playerItemReader") // 이름 지정. 디버깅이나 모니터링 시 식별할 수 있음.
                .resource(new FileSystemResource("Player.csv")) // 읽어올 File의 경로
                .lineTokenizer(new DelimitedLineTokenizer()) // 구분자로 필드를 나누는 방식을 설정. 기본 구분자는 쉼표(,) 임
                .fieldSetMapper(new PlayerFieldSetMapper()) // CSV 의 각 행을 Player 객체에 값 setting
                .linesToSkip(1) // 컬럼행은 건너 뛴다. -> 데이터는 2번째 줄 부터 읽는다.
                .build();
    }



    /* 데이터 가공, 변환 */
    // ItemProcessor<Player, PlayerYears> 입력: Player,  출력: PlayerYears
    @StepScope
    @Bean
    public ItemProcessor<Player, PlayerYears> playerItemProcessor(){
        return new ItemProcessor<Player, PlayerYears>() { // 익명클래스 사용
            @Override
            public PlayerYears process(Player item) throws Exception { // 인터페이스의 process 메서드를 구현
                return new PlayerYears(item);
            }
        };
    }

    /* 객체를 텍스트 파일로 변환하여 출력 */
    @Bean
    @StepScope
    public FlatFileItemWriter<PlayerYears> playerItemWriter(){
        BeanWrapperFieldExtractor<PlayerYears> fieldExtractor = new BeanWrapperFieldExtractor<>();
        fieldExtractor.setNames(new String[]{"ID", "lastName", "position", "yearsExperience"}); // 추출할 필드의 이름을 지정
        fieldExtractor.afterPropertiesSet(); // 필드 설정이 완료되었음을 알림.

        // 데이터 필드들을 구분자로 연결하여 한 줄의 텍스트로 만든다.
        DelimitedLineAggregator<PlayerYears> lineAggregator = new DelimitedLineAggregator<>();
        lineAggregator.setDelimiter(",");
        lineAggregator.setFieldExtractor(fieldExtractor);

        // 작성할 파일의 경로
        FileSystemResource outputResource = new FileSystemResource("player_output.txt");

        return new FlatFileItemWriterBuilder<PlayerYears>()
                .name("playerItemWriter")
                .resource(outputResource)
                .lineAggregator(lineAggregator) // 데이터를 텍스트 줄로 변환하는 방식을 지정한다.
                .build();
    }
}
