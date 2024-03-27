package com.example.batchscheduler.quartz.job;

import com.example.batchscheduler.domain.market.MarketRepository;
import com.example.batchscheduler.quartz.QuartzServiceImpl;
import com.example.batchscheduler.utils.BeanUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

/*
* Batch의 Job을 실행하는 Quartz Job 클래스
* */

@Slf4j
@Component
@PersistJobDataAfterExecution // # Job 동작 중 JobDataMap을 변경할 경우 사용
@DisallowConcurrentExecution // # Job 수행 도중, Job이 병렬적으로 실행되는 것을 막아주는 기능 (클러스터링 환경에서는 애너테이션 작동X)
public class QuartzBatchJob extends QuartzJobBean {

    @Autowired
    private BeanUtil beanUtil;

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private JobExplorer jobExplorer; // # 메타 테이블에 대한 read only 쿼리 기능을 위한 인터페이스 -> run.id에 대한 메타테이블 접근 가능해짐


    // * Quartz의 Job 수행 로직 작성
    @SneakyThrows
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        log.debug("----- executeInternal() -----");

        // ! Quartz - JobDataMap에서 Job 이름을 꺼내오고, 그 이름으로 Batch의 Job을 가져옴
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();

        Job batchJob = (Job) beanUtil.getBean((String) dataMap.getString("jobName"));

        // ! Batch Job 실행
        jobLauncher.run(
                batchJob,
                new JobParametersBuilder(jobExplorer)
                        .getNextJobParameters(batchJob)
                        .toJobParameters()
        );
    }
}
