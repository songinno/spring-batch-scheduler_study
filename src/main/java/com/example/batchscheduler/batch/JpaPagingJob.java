package com.example.batchscheduler.batch;

import com.example.batchscheduler.domain.Dept;
import com.example.batchscheduler.domain.Dept2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.item.database.orm.JpaNativeQueryProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class JpaPagingJob {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;

    private int chunkSize = 10;

    // * Job
    @Bean
    public Job jpaPagingItemReaderJob() {
        return jobBuilderFactory.get("jpaPagingJob")
                .start(jpaPagingItemReaderStep1())
                .build();
    }

    // * Step
    @Bean
    public Step jpaPagingItemReaderStep1() {
        return stepBuilderFactory.get("jpaPagingStep1")
//                .<Dept, Dept>chunk(chunkSize) // # <In, Out>
                .<Dept, Dept2>chunk(chunkSize) // # <In, Out>
                .reader(jpaPagingDbItemReader())
                .processor(jpaPagingItemProcessor()) // # 가공
//                .writer(jpaPagingPrintItemWriter()) // # 단순 출력
                .writer(jpaPagingDBItemWirter()) // # 실제 DB에 쓰기
                .build();
    }

    // * Reader
    @Bean
    public JpaPagingItemReader<Dept> jpaPagingDbItemReader() {

        String nativeQuery = "SELECT * FROM dept ORDER BY dept_no ASC";

        //  ! Native Query 이용
        JpaNativeQueryProvider<Dept> nativeQueryProvider = new JpaNativeQueryProvider<>();
        nativeQueryProvider.setSqlQuery(nativeQuery);
        nativeQueryProvider.setEntityClass(Dept.class);

        return new JpaPagingItemReaderBuilder<Dept>()
                .name("jpaPagingDbItemReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(chunkSize)
                .queryString("SELECT d FROM Dept d order by dept_no asc") // # HQL
//                .queryProvider(nativeQueryProvider) // # Native Query
                .build();
    }

    // * Writer - 콘솔에 단순 출력
    @Bean
    public ItemWriter<Dept> jpaPagingPrintItemWriter() {
        return list -> {
            for (Dept dept : list) {
                log.debug(dept.toString());
            }
        };
    }

    // * Processor
    @Bean
    public ItemProcessor<Dept, Dept2> jpaPagingItemProcessor() {
        return dept -> {
            // # 데이터 가공 로직
            return new Dept2(dept.getDeptNo(), "processed_" + dept.getDName(), "processed_" + dept.getLoc());
        };
    }

    // * Writer - 실제 DB에 쓰기
    @Bean
    public JpaItemWriter<Dept2> jpaPagingDBItemWirter() {
        JpaItemWriter<Dept2> jpaItemWriter = new JpaItemWriter<>();
        jpaItemWriter.setEntityManagerFactory(entityManagerFactory);
        return jpaItemWriter;
    }
}
