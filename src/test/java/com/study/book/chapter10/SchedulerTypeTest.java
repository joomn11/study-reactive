package com.study.book.chapter10;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@Slf4j
public class SchedulerTypeTest {

    private static Flux<Integer> doTask(String taskName) {
        return Flux.fromArray(new Integer[]{1, 3, 5, 7, 9, 11})
//                   .publishOn(Schedulers.single())
                   .publishOn(Schedulers.newSingle("new-single", true))
                   .filter(d -> d > 0)
                   .doOnNext(d -> log.info("# {} - doOnNext filter: {}", taskName, d))
                   .map(d -> d * 10)
                   .doOnNext(d -> log.info("# {} - doOnNext map: {}", taskName, d));
    }

    @Test
    void immediateTest() {
        Flux.fromArray(new Integer[]{1, 3, 5, 7, 9, 11, 13, 15, 17, 19, 21, 23, 25})
            .publishOn(Schedulers.parallel())
            .filter(d -> d > 0)
            .doOnNext(d -> log.info("# doOnNext filter: {}", d))
            .publishOn(Schedulers.immediate())
            .map(d -> d * 10)
            .doOnNext(d -> log.info("# doOnNext map: {}", d))
            .doOnSubscribe(d -> log.info("# doOnSubscribe !! "))
            .subscribe(d -> log.info("# onNext: {}", d));

    }

    @Test
    void singleTest() throws InterruptedException {
        doTask("task1")
            .subscribe(d -> log.info("# onNext: {}", d));

        doTask("task2")
            .subscribe(d -> log.info("# onNext: {}", d));

        Thread.sleep(200L);
    }
}
