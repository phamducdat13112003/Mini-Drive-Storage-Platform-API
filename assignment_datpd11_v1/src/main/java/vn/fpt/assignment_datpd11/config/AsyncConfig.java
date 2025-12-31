package vn.fpt.assignment_datpd11.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Cấu hình cho xử lý bất đồng bộ (async processing)
 * 
 * Cung cấp thread pool để xử lý các tác vụ bất đồng bộ như:
 * - Tạo file zip cho download thư mục
 * - Các tác vụ nặng khác
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    /**
     * Bean cấu hình thread pool cho async processing
     * 
     * Cấu hình:
     * - Core pool size: 5 threads
     * - Max pool size: 10 threads
     * - Queue capacity: 100 tasks
     * 
     * @return Executor với thread pool đã cấu hình
     */
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("async-");
        executor.initialize();
        return executor;
    }
}

