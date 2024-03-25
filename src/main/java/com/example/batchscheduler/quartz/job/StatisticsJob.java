package com.example.batchscheduler.quartz.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;

public class StatisticsJob implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        // ! Schedule Config에 넣어둔 ApplicationContext를 가져와서 Bean 호출에 활용
        ApplicationContext applicationContext
                = (ApplicationContext) context.getJobDetail().getJobDataMap().get("applicationContext");


    }
}
