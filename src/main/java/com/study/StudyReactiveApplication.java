package com.study;

import java.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

@SpringBootApplication
@Slf4j
@EnableAsync
public class StudyReactiveApplication {

    @Autowired
    MyService myService;

    public static void main(String[] args) {
        SpringApplication.run(StudyReactiveApplication.class, args);
//        try (ConfigurableApplicationContext c = SpringApplication.run(StudyReactiveApplication.class, args)) {
//
//        }
    }

//    @Bean
//    ApplicationRunner applicationRunner() {
//        return args -> {
//            log.info("run()");
////            String rst = myService.hello();
////            Future<String> rst = myService.hello2();
//            ListenableFuture<String> rst = myService.hello3();
////            log.info("exit: " + rst.isDone());
////            log.info("result: " + rst.get());
//            rst.addCallback(
//                s -> log.info("result :" + s),
//                e -> log.info("error :" + e.getMessage())
//            );
//            log.info("exit ");
//        };
//    }

    @Bean
    ThreadPoolTaskExecutor tp() {
        ThreadPoolTaskExecutor te = new ThreadPoolTaskExecutor();
        te.setCorePoolSize(10);
        te.setMaxPoolSize(100);
        te.setQueueCapacity(200);
        te.setThreadGroupName("my-thread-");
        te.setThreadNamePrefix("my-thread-");
        te.initialize();
        return te;
    }

    @Component
    public static class MyService {


        public String hello() throws InterruptedException {
            log.info("hello()");
            Thread.sleep(2000);
            return "hello";
        }

        @Async // this default set use SimpleAsyncTaskExecutor, this is only for test and study
        public Future<String> hello2() throws InterruptedException {
            log.info("hello()");
            Thread.sleep(2000);
            return new AsyncResult<>("hello");
        }

        @Async("tp")
        public ListenableFuture<String> hello3() throws InterruptedException {
            log.info("hello()");
//            Thread.sleep(2000);
            return new AsyncResult<>("hello");
        }
    }

}
