package eng.project.peoplehubdemo.configuration;

import eng.project.peoplehubdemo.properties.AsyncProperties;
import eng.project.peoplehubdemo.properties.ImportExecutorProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfiguration implements AsyncConfigurer {

    @Bean(name = "taskExecutor")
    public Executor taskExecutor(AsyncProperties asyncProperties) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(asyncProperties.getCorePoolSize());
        executor.setMaxPoolSize(asyncProperties.getMaxPoolSize());
        executor.setQueueCapacity(asyncProperties.getQueueCapacity());
        executor.setThreadNamePrefix(asyncProperties.getThreadNamePrefix());
        executor.initialize();
        return new DelegatingSecurityContextAsyncTaskExecutor(executor);
    }

    @Bean(name = "importTaskExecutor")
    public Executor importTaskExecutor(ImportExecutorProperties importExecutorProperties) {
        ThreadPoolTaskExecutor importExecutor = new ThreadPoolTaskExecutor();
        importExecutor.setCorePoolSize(importExecutorProperties.getCorePoolSize());
        importExecutor.setMaxPoolSize(importExecutorProperties.getMaxPoolSize());
        importExecutor.setQueueCapacity(importExecutorProperties.getQueueCapacity());
        importExecutor.setThreadNamePrefix(importExecutorProperties.getThreadNamePrefix());
        importExecutor.initialize();
        return new DelegatingSecurityContextAsyncTaskExecutor(importExecutor);
    }
}


