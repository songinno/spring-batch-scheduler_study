package com.example.batchscheduler.quartz;

import com.example.batchscheduler.quartz.job.QuartzJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;


/*
* Quartz 학습용, 기존 Service 클래스
*
* */
@Slf4j
//@Configuration
@RequiredArgsConstructor
public class QuartzStudyService {

    private final Scheduler scheduler;

    @PostConstruct // # DI 완료 후에 실행되어야 하는 메서드에 부여
    public void init() {
        try {
            // ! 스케줄러 초기화 ( + DB clear)
            scheduler.clear();
            // ! Job Listener 등록
            scheduler.getListenerManager().addJobListener(new QuartzJobListener());
            // ! Trigger Listener 등록
            scheduler.getListenerManager().addTriggerListener(new QuartzTriggerListener());

            // ! Job에 필요한 Parameter 생성
            Map params = new HashMap();

            // # Job의 실행 횟수 및 실행 시간
            params.put("executeCount", 1);
            params.put("date", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            // ! Job 생성 및 Scheduler에 등록
            addJob(QuartzJob.class, "QuartzJob", "Quartz Job 테스트입니다.", params, "0/10 * * * * ?");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // * Job 등록 메서드
    public <T extends Job> void addJob(Class<? extends Job> job, String name, String desc, Map paramsMap, String cron) throws SchedulerException {
        JobDetail jobDetail = buildJobDetail(job, name, desc, paramsMap);
        Trigger trigger = buildCronTrigger(cron);
        if (scheduler.checkExists(jobDetail.getKey())) {
            scheduler.deleteJob(jobDetail.getKey());
        }
        scheduler.scheduleJob(jobDetail, trigger);

    }

    // * JobDetail 생성 메서드
    public <T extends Job> JobDetail buildJobDetail(Class<? extends Job> job, String name, String desc, Map paramsMap) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.putAll(paramsMap);

        return JobBuilder
                .newJob(job)
                .withIdentity(name)
                .withDescription(desc)
                .usingJobData(jobDataMap)
                .build();
    }

    // * Trigger 생성 메서드
    private Trigger buildCronTrigger(String cronExp) {
        return TriggerBuilder
                .newTrigger()
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExp))
                .build();
    }
}
