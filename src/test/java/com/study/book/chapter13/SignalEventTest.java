package com.study.book.chapter13;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.test.StepVerifierOptions;

@Slf4j
public class SignalEventTest {

    @Test
    void test() {
        StepVerifier
            .create(Mono.just("hello reactor"))
            .expectNext("hello reactor")
            .expectComplete()
            .verify();
    }

    @Test
    void sayhelloTest() {
        StepVerifier
            .create(GeneralTestExample.saHello())
            .expectSubscription()
            .as("# expect subscription")
            .expectNext("hi")
            .as("# expect hi")
            .expectNext("reactor")
            .as("# expect reactor")
            .verifyComplete();
    }

    @Test
    void dividebytwoTest() {
        Flux<Integer> source = Flux.just(2, 4, 6, 8, 10);
        StepVerifier
            .create(GeneralTestExample.divideByTwo(source))
            .expectSubscription()
//            .expectNext(1)
//            .expectNext(2)
//            .expectNext(3)
//            .expectNext(4)
            .expectNext(1, 2, 3, 4)
            .expectError()
            .verify();
    }


    @Test
    void takeNumberTest() {
        Flux<Integer> source = Flux.range(0, 1000);
        StepVerifier
            .create(GeneralTestExample.takeNumber(source, 500),
                    StepVerifierOptions.create().scenarioName("verify from 0 to 499")
            )
            .expectSubscription()
            .expectNext(0)
            .expectNextCount(498)
            .expectNext(500)
            .expectComplete()
            .verify();
    }
}
