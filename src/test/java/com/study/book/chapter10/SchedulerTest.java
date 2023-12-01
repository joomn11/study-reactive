package com.study.book.chapter10;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@Slf4j
public class SchedulerTest {

    @Test
    void subscribeOnTest() throws InterruptedException {
        Flux.fromArray(new Integer[]{1, 3, 5, 7})
            .subscribeOn(Schedulers.boundedElastic())
            .doOnNext(data -> log.info("# doOnNext: {}", data))
            .doOnSubscribe(subscription -> log.info("# doOnSubscribe"))
            .subscribe(data -> log.info("# onNext: {}", data));

        Thread.sleep(500);
    }

    @Test
    void publishOnTest() throws InterruptedException {
        Flux.fromArray(new Integer[]{1, 3, 5, 7})
            .doOnNext(data -> log.info("# doOnNext: {}", data))
            .doOnSubscribe(subscription -> log.info("# doOnSubscribe"))
            .publishOn(Schedulers.parallel())
            .subscribe(data -> log.info("# onNext: {}", data));

        Thread.sleep(500);
    }

    @Test
        // doOnSubscribe subscribeOn 위에 있음 수행되는 스레드가 달라진다 ..
    void test() throws InterruptedException {
        Flux.fromArray(new Integer[]{1, 3, 5, 7})
            .doOnNext(data -> log.info("# doOnNext: {}", data))
            .doOnSubscribe(subscription -> log.info("# doOnSubscribe"))
            .subscribeOn(Schedulers.boundedElastic())
            .publishOn(Schedulers.parallel())
            .subscribe(data -> log.info("# onNext: {}", data));

        Thread.sleep(500);
    }

    @Test
    void parallelTest() throws InterruptedException {
        Flux.fromArray(new Integer[]{1, 3, 5, 7, 9, 11, 13, 15, 17, 19, 21, 23, 25})
            .parallel(4)
            .runOn(Schedulers.parallel())
//            .doOnNext(data -> log.info("# doOnNext: {}", data))
//            .doOnSubscribe(subscription -> log.info("# doOnSubscribe"))
            .subscribe(data -> log.info("# onNext: {}", data));

        Thread.sleep(500);
    }

}
