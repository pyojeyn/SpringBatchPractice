package com.example.SpringBatchTutorial.job.dbDataReadWrite.TrMigrationConfig;

import com.example.SpringBatchTutorial.core.domain.accounts.Accounts;
import com.example.SpringBatchTutorial.core.domain.accounts.AccountsRepository;
import com.example.SpringBatchTutorial.core.domain.orders.Orders;
import com.example.SpringBatchTutorial.core.domain.orders.OrdersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * desc : 주문 테이블 -> 정산 테이블 데이터 이관
 * run: --spring.batch.job.names=trMigrationJob
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class TrMigrationConfig {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    private final OrdersRepository ordersRepository;

    private final AccountsRepository accountsRepository;

    @Bean
    public Job trMigrationJob(Step trMigrationStep){
        return jobBuilderFactory.get("trMigrationJob")
                .incrementer(new RunIdIncrementer())
                .start(trMigrationStep)
                .build();
    }

    @JobScope
    @Bean
    public Step trMigrationStep(ItemReader trOrdersReader,
                                ItemProcessor trOrderProcessor,
                                ItemWriter trOrdersWriter){
        return stepBuilderFactory.get("TrMigrationStep")
                .<Orders, Accounts>chunk(5) // 5개의 데이터 단위로 처리를 하겠다. (처리할 데이터의 트랜잭션 개수 -> 5)
                // <읽어올데이터타입, 쓸데이터타입>
                .reader(trOrdersReader)// 데이터를 읽어와서
                .processor(trOrderProcessor)// 처리를 해주고
                .writer(trOrdersWriter) // 다시 write 해줌.
                .build();
    }


//    @StepScope
//    @Bean
//    public RepositoryItemWriter<Accounts> trOrdersWriter(){
//        return new RepositoryItemWriterBuilder<Accounts>()
//                .repository(accountsRepository)
//                .methodName("save")
//                .build();
//    }

    @StepScope
    @Bean
    // 위에 메소드처럼 Repository 사용 안하고 개발자가 직접 입력.
    public ItemWriter<Accounts> trOrdersWriter(){
        return new ItemWriter<Accounts>() {
            @Override
            public void write(List<? extends Accounts> items) throws Exception {
                items.forEach(item -> accountsRepository.save(item));
            }
        };
    }


    // 주문 객체를 정산 객체로 변경하는건지 주문객체에 있는 데이터를 정산 객체 데이터로 넣겠다는건지 뭔지;;;;
    @StepScope
    @Bean
    public ItemProcessor<Orders, Accounts> trOrderProcessor(){
        return new ItemProcessor<Orders, Accounts>() {
            @Override
            public Accounts process(Orders item) throws Exception {
                return new Accounts(item);
            }
        };
    }



//@JobScope
//@Bean
//public Step trMigrationStep(ItemReader<Orders> trOrdersReader){
//    return stepBuilderFactory.get("trMigrationStep")
//            .<Orders, Orders>chunk(5)
//            .reader(trOrdersReader)
//            .writer(new ItemWriter<Orders>() {
//                @Override
//                public void write(List<? extends Orders> items) throws Exception {
//                    if (items.isEmpty()) {
//                        System.out.println("No items found in the reader!");
//                    } else {
//                        items.forEach(System.out::println);
//                    }
//                }
//            })
//            .build();
//}

    @StepScope
    @Bean
    public RepositoryItemReader<Orders> trOrdersReader(){
        return new RepositoryItemReaderBuilder<Orders>()
                .name("trOrdersReader")
                .repository(ordersRepository)
                .methodName("findAll")
                .pageSize(5) // 보통 chunk 사이즈와 동일하게 작성
                .arguments(Arrays.asList()) // 레포지토리 메서드에 넘길 파라미터?
                .sorts(Collections.singletonMap("id", Sort.Direction.ASC))
                .build();

    }
}
