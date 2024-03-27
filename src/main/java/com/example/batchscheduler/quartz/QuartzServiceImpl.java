package com.example.batchscheduler.quartz;

import com.example.batchscheduler.quartz.job.QuartzBatchJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;


/*
* Batch + Quartz 학습
* */
@Slf4j
@Service
@RequiredArgsConstructor
public class QuartzServiceImpl implements QuartzService {

    /* TODO - 확인 사항
     * 1. DI
     *   1) QuartzStarter를 사용하는 문서에서는 schedulerFactoryBean을 주입 받아서,
     *       init() 메서드에서 scheduler = schedulerFactoryBean.getScheduler(); 로 초기화
     *   2) QuartzStarter를 사용하지 않는 문서에서는 아래와 같이 Scheduler 자체를 생성자 주입 받음
     *       - 직접 주입 대상을 설정해준 적은 없음
     * 2. scheduler.start() 메서드
     *       1) 1-2)의 문서에서는 따로 메서드를 호출하지 않아도 스케줄(Job)이 실행이 됨
     *           - QuartzConfig의 schedulerFactoryBean.setAutoStartup(true); 때문일 것으로 추정
     * */

    /*@Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    private Scheduler scheduler;*/

    private final Scheduler scheduler;

    private static final String CRON_EXP = "0/10 * * * * ?";

    @Override
    @PostConstruct
    public boolean init() {
        try {
            // ! 1. clear
            this.clear();

            // ! 2. Listener 등록
            this.addListener(new QuartzJobListener(), new QuartzTriggerListener());

            // ! 3. Parameter 정의
            Map params = new HashMap();
            // # Job 실행 횟수 및 실행 시간
            params.put("executeCount", 1);
            params.put("date", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            params.put("jobName", "scheduledJob"); // # QuatzBatchJob에서 찾기 위해 파라미터에 넣음 (JobDetail의 name 속성을 직접 가져올 수 있는 메서드가 없음)

            // # QuartzJob에서 Batch

            // ! 4. 스케줄 등록
            String description = "Batch + Quartz 테스트";
            // # 2번째 인자(name)를 Batch의 Job의 이름과 동일하게 지정
            this.register(QuartzBatchJob.class, "scheduledJob", description, params, CRON_EXP);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public void register(Class<? extends Job> job, String name, String description, Map params, String cronExp) throws SchedulerException {
        // ! JobDetail과 Trigger 생성
        JobDetail jobDetail = createJobDetail(job, name, description, params);
        Trigger trigger = createCronTrigger(cronExp);

        if (scheduler.checkExists(jobDetail.getKey())) {
            scheduler.deleteJob(jobDetail.getKey());
        }

        // ! 스케줄러에 스케줄 등록
        scheduler.scheduleJob(jobDetail, trigger);
    }


    @Override
    public <T extends Job> JobDetail createJobDetail(Class<? extends Job> job, String name, String description, Map params) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.putAll(params);

        return JobBuilder
                .newJob(job)
                .withIdentity(name)
                .withDescription(description)
                .usingJobData(jobDataMap)
                .build();
    }

    @Override
    public Trigger createCronTrigger(String cronExp) {
        return TriggerBuilder
                .newTrigger()
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExp))
                .build();
    }

    @Override
    public void start() throws SchedulerException {
        if (scheduler != null && !scheduler.isStarted()) {
            scheduler.start();
        }
    }

    @Override
    public void shutdown() throws SchedulerException, InterruptedException {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }

    @Override
    public void restart() throws SchedulerException {
        scheduler.standby(); // # standby() 이후 start() 시, misfire된 트리거들을 무시
        this.init();
        this.start();
    }

    @Override
    public void clear() throws SchedulerException {
        scheduler.clear();
    }

    @Override
    public void addListener(JobListener jobListener, TriggerListener triggerListener) throws SchedulerException {
        // ! JobListener 등록
        scheduler.getListenerManager().addJobListener(jobListener);
        // ! TriggerListener 등록
        scheduler.getListenerManager().addTriggerListener(triggerListener);
    }
}
