package com.study.tobby.chap2;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

public class ReactorEx {

    @Test
    void test() {
        Flux.<Integer>create(emitter -> {
                emitter.next(1);
                emitter.next(2);
                emitter.next(3);
                emitter.complete();
            })
            .log()
            .map(i -> i * 10)
            .reduce(0, (a, b) -> a + b)
            .log()
            .subscribe(System.out::println);
    }
}
