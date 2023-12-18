package com.study.book.chapter13;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

@Slf4j
public class BackpressureTest {

    @Test
    void generateNumberTest() {
        StepVerifier
            .create(BackpressureTestExample.generateNumber(), 1L)
            .thenConsumeWhile(num -> num >= 1)
            .verifyComplete();
    }

    @Test
    void generateNumber2Test() {
        StepVerifier
            .create(BackpressureTestExample.generateNumber(), 1L)
            .thenConsumeWhile(num -> num >= 1)
            .expectError()
            .verifyThenAssertThat()
            .hasDroppedElements();
    }
}
