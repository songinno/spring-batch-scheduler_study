package com.example.batchscheduler.quartz;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.TriggerListener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
public class QuartzTriggerListener implements TriggerListener {
    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public void triggerFired(Trigger trigger, JobExecutionContext context) {
        log.debug("----- Trigger 실행 -----");
    }

    // * 결과가 true면 Job을 중단시키는 메서드
    @Override
    public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
        log.debug("----- Trigger 상태 체크 -----");
        JobDataMap map = context.getJobDetail().getJobDataMap();

        if (map.containsKey("executeCount")) {
            int executeCount = (int) map.get("executeCount");
            return executeCount > 5; // # 5보다 크면 Job 중단
        }
        return false;
    }

    @Override
    public void triggerMisfired(Trigger trigger) {
    }

    @Override
    public void triggerComplete(Trigger trigger, JobExecutionContext context, Trigger.CompletedExecutionInstruction triggerInstructionCode) {
        log.debug("----- Trigger 성공 -----");
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();

        // ! JobDataMap 다시 세팅
        int count = (int) dataMap.get("executeCount");
        dataMap.put("executeCount", ++count);
        dataMap.put("date", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }
}
