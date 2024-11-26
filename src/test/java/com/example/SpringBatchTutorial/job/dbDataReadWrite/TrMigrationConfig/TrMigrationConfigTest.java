package com.example.SpringBatchTutorial.job.dbDataReadWrite.TrMigrationConfig;

import com.example.SpringBatchTutorial.SpringBatchTestConfig;
import com.example.SpringBatchTutorial.core.domain.accounts.AccountsRepository;
import com.example.SpringBatchTutorial.core.domain.orders.Orders;
import com.example.SpringBatchTutorial.core.domain.orders.OrdersRepository;
import com.example.SpringBatchTutorial.job.helloWorld.HelloWorldJobConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureHttpGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;


import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;


@RunWith(SpringRunner.class)
@ActiveProfiles("test") // yaml - local 인지 test 인지 명시.
@SpringBatchTest
@SpringBootTest(classes = {SpringBatchTestConfig.class, TrMigrationConfig.class})
class TrMigrationConfigTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private AccountsRepository accountsRepository;

    @AfterEach
    public void cleanUpEach() {
        ordersRepository.deleteAll();
        accountsRepository.deleteAll();
    }


    @Test()
    public void success_noData() throws Exception{
        // when
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        //then
        Assertions.assertEquals(jobExecution.getExitStatus(), ExitStatus.COMPLETED);
        Assertions.assertEquals(0,accountsRepository.count());
    }

    @Test
    public void success_existData() throws Exception{
        // given
        Orders order1 = new Orders(1, "kakao gift", 15000, new Date());
        Orders order2 = new Orders(2, "naver gift", 15000, new Date());

        ordersRepository.save(order1);
        ordersRepository.save(order2);

        // when
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        // then
        Assertions.assertEquals(jobExecution.getExitStatus(), ExitStatus.COMPLETED);
        Assertions.assertEquals(2, accountsRepository.count());
    }

}