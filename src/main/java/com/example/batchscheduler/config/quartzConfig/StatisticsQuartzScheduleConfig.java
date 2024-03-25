package com.example.batchscheduler.config.quartzConfig;

import com.example.batchscheduler.quartz.job.StatisticsJob;
import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class StatisticsQuartzScheduleConfig {
    private final ApplicationContext applicationContext;

    @Bean
    public void start() throws SchedulerException {
        // ! Job에서 Bean을 가져와 사용하기 위해 활용
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("applicationContext", applicationContext);

        // ! Job을 이용하여 JobDetail 생성
        JobDetail jobDetail =
                JobBuilder
                        .newJob(StatisticsJob.class) // # 실행될 Job
                        .setJobData(jobDataMap) // # Job에서 활용될 jobDataMap
                        .withIdentity("statisticsJob", "statisticsJobGroup") // # 식별을 위한 Identity : name, group
                        .build();

        // ! 스케줄링을 위한 Trigger 등록
        Trigger trigger =
                TriggerBuilder
                        .newTrigger()
                        .withIdentity("statisticsTrigger", "statisticsTriggerGroup")
                        .startNow() // # 실행과 동시에 스케줄링 시작
                        .withSchedule(
                                SimpleScheduleBuilder
                                        .simpleSchedule()
                                        .withIntervalInSeconds(10)
                                        .repeatForever()
                        )
                        .build();

        // ! Job과 Trigger 관리를 위한 Scheduler 생성
        Scheduler scheduler = new StdSchedulerFactory().getScheduler();
        scheduler.start();
        scheduler.scheduleJob(jobDetail, trigger);
    }
}
