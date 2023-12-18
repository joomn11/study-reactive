package com.study.book.chapter13;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Slf4j
public class ContextTest {

    @Test
    void test() {
        Mono<String> source = Mono.just("hello");

        StepVerifier
            .create(
                ContextTestExample.getSecretMessage(source)
                                  .contextWrite(ctx -> ctx.put("secretMessage", "hello, reactor"))
                                  .contextWrite(ctx -> ctx.put("secretKey", "aGVsbG8="))
            )
            .expectSubscription()
            .expectAccessibleContext()
            .hasKey("secretMessage")
            .hasKey("secretKey")
            .then()
            .expectNext("hello, reactor")
            .expectComplete()
            .verify();

    }
}
