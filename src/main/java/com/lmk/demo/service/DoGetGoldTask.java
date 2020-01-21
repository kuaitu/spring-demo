package com.lmk.demo.service;

import com.lmk.demo.task.GoldTask;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration      //1.主要用于标记配置类，兼备Component的效果。
@EnableScheduling   // 2.开启定时任务
public class DoGetGoldTask {
    // 添加定时任务

    // 每隔5秒（0/5 * * * * ?）
    // 每隔1分钟执行一次(0 */1 * * * ?)
    // 每天的0点、13点、18点、21点都执行一次(0 0 0,13,18,21 * * ?)
    // 运行命令：java -jar -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5006 demo-0.0.1-SNAPSHOT.jar
    @Scheduled(cron = "0 0 0,13,18,21 * * ?")
    private void configureTasks() {
        GoldTask.doJob();
    }
}
