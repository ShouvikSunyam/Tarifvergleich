package com.tarifvergleich.electricity.config;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class MultiThreadPoolConfig {

	@Bean(name = "orderStatusCronExecutor")
	public Executor orderStatusCronExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(40);
		executor.setMaxPoolSize(50);
		executor.setQueueCapacity(6000);
		executor.setThreadNamePrefix("ContractCronWorker-");
		executor.initialize();
		return executor;
	}
	
	@Bean(name = "virtualThreadExecutor")
    public Executor virtualThreadExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }
}
