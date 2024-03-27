package com.example.batchscheduler.quartz;
import org.springframework.beans.factory.annotation.Autowired;

public class QuartzStarter {
    
    @Autowired
    private QuartzServiceImpl quartzService;
    
    public void init() throws Exception {
        boolean initResult = quartzService.init();
        if (initResult) quartzService.start();
    }

    private void destroy() throws Exception {
        quartzService.shutdown();
    }

    private void restart() throws Exception {
        quartzService.restart();
    }
}
