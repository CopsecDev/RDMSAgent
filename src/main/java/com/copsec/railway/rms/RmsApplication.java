package com.copsec.railway.rms;

import org.springframework.boot.Banner.Mode;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class RmsApplication {

	/**
	 * 配置线程池执行scheduler
	 * @return
	 */
	@Bean(destroyMethod = "shutdown")
	public ThreadPoolTaskScheduler taskScheduler(){

		ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
		taskScheduler.setPoolSize(32);
		taskScheduler.setWaitForTasksToCompleteOnShutdown(false);
		taskScheduler.setAwaitTerminationSeconds(30);
		taskScheduler.setThreadNamePrefix("copsecThreads-");
		return taskScheduler;
	}
	public static void main(String[] args) {

		SpringApplication app = new SpringApplication(RmsApplication.class);
		app.setBannerMode(Mode.OFF);
		app.run(args);
	}

}
