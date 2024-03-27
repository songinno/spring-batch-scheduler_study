package com.example.batchscheduler.quartz.job;

import com.example.batchscheduler.domain.market.Market;
import com.example.batchscheduler.domain.market.MarketRepository;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
//@Component
@PersistJobDataAfterExecution // # Job 동작 중 JobDataMap을 변경할 경우 사용
@DisallowConcurrentExecution // # Job 수행 도중, Job이 병렬적으로 실행되는 것을 막아주는 기능 (클러스터링 환경에서는 애너테이션 작동X)
public class QuartzJob implements Job {

//    @Autowired
    private MarketRepository marketRepository;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.debug("----- Quartz Job Executed -----");

        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        log.debug("dataMap date : {}", dataMap.get("date"));
        log.debug("dataMap executeCount : {}", dataMap.get("executeCount"));

        // ! Market 테이블에 데이터 INSERT
        Market market = new Market("market_" + dataMap.get("executeCount") + "_" + dataMap.get("date"), ((int) dataMap.get("executeCount") * 1000));
        marketRepository.save(market);

    }
}
