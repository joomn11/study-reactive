package com.study.book.chapter13;

import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

@Slf4j
public class TimeBasedTest {

    @Test
    void getCovid19CountTest() {
        StepVerifier
            .withVirtualTime(() -> TimeBasedTestExample.getCovid19Count(
                                 Flux.interval(Duration.ofHours(1)).take(1)
                             )
            )
            .expectSubscription()
            .then(
                () -> VirtualTimeScheduler
                    .get()
                    .advanceTimeBy(Duration.ofHours(1))
            )
            .expectNextCount(10)
            .expectComplete()
            .verify();
    }

    @Test
    void getCovid19Count2Test() {
        StepVerifier
            .withVirtualTime(() -> TimeBasedTestExample.getCovid19Count(
                                 Flux.interval(Duration.ofMinutes(1)).take(1)
                             )
            )
            .expectSubscription()
            .expectNextCount(10)
            .expectComplete()
            .verify(Duration.ofSeconds(3));
    }

    @Test
    void getVoteCountTest() {
        StepVerifier
            .withVirtualTime(
                () -> TimeBasedTestExample.getVoteCount(Flux.interval(Duration.ofMinutes(1)))
            )
            .expectSubscription()
            .expectNoEvent(Duration.ofMinutes(1))
            .expectNoEvent(Duration.ofMinutes(1))
            .expectNoEvent(Duration.ofMinutes(1))
            .expectNoEvent(Duration.ofMinutes(1))
            .expectNoEvent(Duration.ofMinutes(1))
            .expectNextCount(5)
            .expectComplete()
            .verify();
    }
}
