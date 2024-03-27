package com.example.batchscheduler.config.quartzConfig;

import com.example.batchscheduler.quartz.AutoWiringSpringBeanJobFactory;
import com.example.batchscheduler.quartz.QuartzStarter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

/*
 * Scheduler를 생성하는 SchedulerFactoryBean을 스프링 Bean으로 등록
 * datasource를 실환경에서 사용중인 datasource로 설정
 * 서버 동작 시, QuartzStarter Bean이 생성되며, 생성 시 init() 메서드 실행
 * init() 메서드 안에서 스케줄 등록 + 스케줄러 실행을 동작
 *
 * */

@Slf4j
@Configuration
@RequiredArgsConstructor
public class QuartzConfig {
    private final DataSource dataSource;
    private final ApplicationContext applicationContext;
    private final PlatformTransactionManager platformTransactionManager;

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() {
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();

        AutoWiringSpringBeanJobFactory autoWiringSpringBeanJobFactory = new AutoWiringSpringBeanJobFactory();
        autoWiringSpringBeanJobFactory.setApplicationContext(applicationContext);

        schedulerFactoryBean.setJobFactory(autoWiringSpringBeanJobFactory);
        schedulerFactoryBean.setDataSource(dataSource);
        schedulerFactoryBean.setOverwriteExistingJobs(true);
        schedulerFactoryBean.setAutoStartup(true);
        schedulerFactoryBean.setTransactionManager(platformTransactionManager);
        schedulerFactoryBean.setQuartzProperties(quartzProperties());

        return schedulerFactoryBean;
    }

    private Properties quartzProperties() {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("quartz.properties"));
        Properties properties = null;
        try {
            propertiesFactoryBean.afterPropertiesSet();
            properties = propertiesFactoryBean.getObject();
        } catch (IOException e) {
            e.printStackTrace();
            log.error("quartzProperties parse error : {}", e);
        }
        return properties;
    }

    @Bean(initMethod = "init", destroyMethod = "destroy")
    public QuartzStarter quartzStarter() {
        return new QuartzStarter();
    }
}
