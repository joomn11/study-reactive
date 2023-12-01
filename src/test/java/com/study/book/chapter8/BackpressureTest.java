package com.study.book.chapter8;

import java.time.Duration;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.BufferOverflowStrategy;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@Slf4j
public class BackpressureTest {

    @Test
    void dataCountTest() {
        Flux.range(1, 5)
            .doOnRequest(data -> log.info("# doOnRequest: {}", data))
            .subscribe(new BaseSubscriber<Integer>() {
                @Override
                protected void hookOnSubscribe(Subscription subscription) {
                    request(1);
                }

                @SneakyThrows
                @Override
                protected void hookOnNext(Integer value) {
                    Thread.sleep(2000);
                    log.info("# hookOnNext: {}", value);
                    request(1);
                }
            });
    }

    @Test
    void typeTest() throws InterruptedException {
        Flux.interval(Duration.ofMillis(1L))
//            .onBackpressureError()
//            .doOnNext(data -> log.info("# doOnNext : {}", data))
//            .onBackpressureDrop(dropped -> log.info("# dropped: {}", dropped))
            .onBackpressureLatest()
            .publishOn(Schedulers.parallel())
            .subscribe(data -> {
                           try {
                               Thread.sleep(5L);
                           } catch (InterruptedException e) {
                           }
                           log.info("# onNext: {}", data);
                       },
                       error -> log.error("# onError", error));

        Thread.sleep(2000L);
    }

    @Test
    void bufferTypeTest() throws InterruptedException {
        Flux.interval(Duration.ofMillis(100L))
            .doOnNext(data -> log.info("# emitted by original Flux : {}", data))
            .onBackpressureBuffer(2,
                                  dropped -> log.info("** Overflow & Dropped: {} **", dropped),
                                  BufferOverflowStrategy.DROP_OLDEST)
            .doOnNext(data -> log.info("[ emitted by Buffer : {} ]", data))
            .publishOn(Schedulers.parallel(), false, 1)
            .subscribe(data -> {
                           try {
                               Thread.sleep(800L);
                           } catch (InterruptedException e) {
                           }
                           log.info("# onNext: {}", data);
                       },
                       error -> log.error("# onError", error));

        Thread.sleep(3000L);
    }
}
