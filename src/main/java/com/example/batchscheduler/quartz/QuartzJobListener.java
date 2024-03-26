package com.example.batchscheduler.quartz;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;

@Slf4j
public class QuartzJobListener implements JobListener {
    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        log.debug("----- Job 수행 전 -----");
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
        log.debug("----- Job 중단 -----");
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        log.debug("----- Job 수행 완료 후 -----");
    }
}
