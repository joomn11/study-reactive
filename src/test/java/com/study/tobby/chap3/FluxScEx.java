package com.study.tobby.chap3;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@Slf4j
public class FluxScEx {

    @Test
    @DisplayName("reactor  publishOn,subscribeOn - 새로운 스레드 할당 ")
    void test() {
        Flux.range(1, 10)
            .publishOn(Schedulers.newSingle("pub"))
            .log()
            .subscribeOn(Schedulers.newSingle("sub"))
            .subscribe(i -> log.info(i + ","));
    }

    @Test
    @DisplayName("flux 선언 후 바로 다음으로 넘어간다, exit 먼저이고 flux subscribe 실행됨")
    void test2() throws InterruptedException {
        Flux.interval(Duration.ofMillis(200))
            .take(10)
            .subscribe(i -> log.info(i + " "));

        log.info("exit");
        TimeUnit.SECONDS.sleep(3);
    }

    @Test
    @DisplayName("위에 flux 비교로 자체 스레드 실행한 예시 ")
    void test3() throws InterruptedException {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {

            }
            log.info("hello");
            System.out.println("hello");
        });
        log.info("exit");
        TimeUnit.SECONDS.sleep(3);
    }
}
