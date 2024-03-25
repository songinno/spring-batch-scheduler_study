package com.example.batchscheduler.batch.schedule;

import com.example.batchscheduler.config.batchConfig.StatisticsBatchConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StatisticsBatchSchedule {
    private final JobLauncher jobLauncher;
    private final StatisticsBatchConfig batchConfig;
    private final JobExplorer jobExplorer; // # 메타 테이블에 대한 read only 쿼리 기능을 위한 인터페이스 -> run.id에 대한 메타테이블 접근 가능해짐

    @Scheduled(cron = "0/10 * * * * ?") // # 10초마다 Job 실행
    public void runStatisticsJob() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        jobLauncher.run(
                batchConfig.scheduledJob(),
                new JobParametersBuilder(jobExplorer)
                        .getNextJobParameters(batchConfig.scheduledJob()) // # Job을 넣어줌 -> Job의 상태를 바탕으로 JobParameters를 초기화
                        .toJobParameters()
        );
    }
}
