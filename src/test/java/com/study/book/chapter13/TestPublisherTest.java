package com.study.book.chapter13;

import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import reactor.test.publisher.TestPublisher;

@Slf4j
public class TestPublisherTest {

    private static List<Integer> getDataSource() {
        return Arrays.asList(2, 4, 6, 8, null);
    }

    @Test
    void divideByTwoTest() {
        TestPublisher<Integer> source = TestPublisher.create();

        StepVerifier
            .create(GeneralTestExample.divideByTwo(source.flux()))
            .expectSubscription()
            .then(() -> source.emit(2, 4, 6, 8, 10))
            .expectNext(1, 2, 3, 4)
            .expectError()
            .verify();
    }

    @Test
    void divideByTwo2Test() {
        TestPublisher<Integer> source = TestPublisher.create();
//        TestPublisher<Integer> source = TestPublisher.createNoncompliant(Violation.ALLOW_NULL);

        StepVerifier
            .create(GeneralTestExample.divideByTwo(source.flux()))
            .expectSubscription()
            .then(() -> getDataSource().stream().forEach(data -> source.next(data)))
            .expectNext(1, 2, 3, 4, 5)
            .expectError()
            .verify();
    }
}
