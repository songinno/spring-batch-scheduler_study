package com.example.batchscheduler.config.batchConfig;

import com.example.batchscheduler.batch.UniqueRunIdIncrementer;
import com.example.batchscheduler.domain.dept.Dept;
import com.example.batchscheduler.domain.statistics.HourlyCountStatistics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.*;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class StatisticsBatchJobConfig {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;

    // ! 한번에 처리할 Item의 양
    private int pageSize = 10;

    // * Job
    @Bean
    public Job scheduledJob() {
        return jobBuilderFactory.get("scheduledJob")
                .start(scheduledStep(null))
                .incrementer(new UniqueRunIdIncrementer())
                .preventRestart() // # Job 실행 실패 시, 재시작 막기
                .build();
    }

    // * Step
    @Bean
    @JobScope
    public Step scheduledStep(@Value("#{jobParameters[requestDate]}") String requestDate) {
        return stepBuilderFactory.get("scheduledStep")
                .<Dept, HourlyCountStatistics>chunk(pageSize)
                .reader(scheduledJobItemReader())
                .processor(scheduledJobItemProcessor())
                .writer(scheduledJobItemWriter())
                .build();
    }

    // * ItemReader
    @Bean
    public JpaPagingItemReader<Dept> scheduledJobItemReader() {
        return new JpaPagingItemReaderBuilder<Dept>()
                .name("scheduledJobItemReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(pageSize)
                .queryString("select count(d) as count from Dept d")
                .build();
    }

    // * JpaItemWriter
    /*
    * JpaItemWriter는 넘어온 Entity를 데이터베이스에 반영하기만 함
    *   1. Write 이전 단계(Processor)에서 미리 HourlyCountStatistics 엔터티를 만들어서 보내줘야 함
    *   2. 또는, processor를 거치지 않고, ItemWirter 타입으로 writer() 메서드를 정의하고
    *       받아온 List에서 get(0)으로 값을 꺼내서, 그걸로 엔터티를 만든 후, Repository의 save() 메서드로 직접 insert
    * */
    @Bean
    public JpaItemWriter<HourlyCountStatistics> scheduledJobItemWriter() {
        JpaItemWriter<HourlyCountStatistics> itemWriter = new JpaItemWriter<>();
        itemWriter.setEntityManagerFactory(entityManagerFactory);
        return itemWriter;
    }

    @Bean
    public ItemProcessor scheduledJobItemProcessor() { // # ItemProcessor에 제너릭을 지정하지 않아야 Input의 엔터티 타입(Dept)이 아닌, 쿼리 결과 count의 타입(Long)을 그대로 가져올 수 있음
        return item -> {
            int count = Integer.parseInt(String.valueOf(item)); // # DB에서 가져온 Long 타입(기본)을 String으로 우선 변환, int 타입으로 변환
            return new HourlyCountStatistics("dept", count); // # 생성자 파라미터 : (String relTbl, int count)
        };
    }

}

