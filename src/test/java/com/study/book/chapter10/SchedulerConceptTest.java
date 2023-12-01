package com.study.book.chapter10;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@Slf4j
public class SchedulerConceptTest {

    @Test
    void withoutScheduler() {
        Flux.fromArray(new Integer[]{1, 3, 5, 7, 9, 11, 13, 15, 17, 19, 21, 23, 25})
            .doOnNext(d -> log.info("# doOnNext fromArray: {}", d))
            .filter(d -> d > 0)
            .doOnNext(d -> log.info("# doOnNext filter: {}", d))
            .map(d -> d * 10)
            .doOnNext(d -> log.info("# doOnNext map: {}", d))
            .subscribe(d -> log.info("# onNext: {}", d));
    }

    @Test
    void publishOnScheduler() {
        Flux.fromArray(new Integer[]{1, 3, 5, 7, 9, 11, 13, 15, 17, 19, 21, 23, 25})
            .doOnNext(d -> log.info("# doOnNext fromArray: {}", d))
            .publishOn(Schedulers.parallel())
            .filter(d -> d > 0)
            .doOnNext(d -> log.info("# doOnNext filter: {}", d))
            .publishOn(Schedulers.parallel())
            .map(d -> d * 10)
            .doOnNext(d -> log.info("# doOnNext map: {}", d))
            .subscribe(d -> log.info("# onNext: {}", d));
    }

    @Test
    void togetherScheduler() {
        Flux.fromArray(new Integer[]{1, 3, 5, 7, 9, 11, 13, 15, 17, 19, 21, 23, 25})
            .subscribeOn(Schedulers.boundedElastic())
            .doOnNext(d -> log.info("# doOnNext fromArray: {}", d))
            .filter(d -> d > 0)
            .doOnNext(d -> log.info("# doOnNext filter: {}", d))
            .publishOn(Schedulers.parallel())
            .map(d -> d * 10)
            .doOnNext(d -> log.info("# doOnNext map: {}", d))
            .doOnSubscribe(d -> log.info("# doOnSubscribe !! "))
            .subscribe(d -> log.info("# onNext: {}", d));
    }
}
