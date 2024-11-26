package com.example.SpringBatchTutorial.job.helloWorld;


import com.example.SpringBatchTutorial.SpringBatchTestConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ActiveProfiles("test") // yaml - local 인지 test 인지 명시.
@SpringBatchTest
@SpringBootTest(classes = {SpringBatchTestConfig.class, HelloWorldJobConfig.class})
class HelloWorldJobConfigTest {


    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils; // 인텔리가 인식 못함.

    @Test
    public void success() throws Exception {
        //when
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        //then
        Assertions.assertEquals(jobExecution.getExitStatus(), ExitStatus.COMPLETED);
    }
}