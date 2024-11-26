package com.example.SpringBatchTutorial;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;

// 전역으로 사용하기 위해.
@Configuration
@EnableAutoConfiguration
@EnableBatchProcessing
public class SpringBatchTestConfig {
}
