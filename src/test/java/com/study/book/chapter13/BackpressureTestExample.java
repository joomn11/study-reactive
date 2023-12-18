package com.study.book.chapter13;

import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink.OverflowStrategy;

public class BackpressureTestExample {

    public static Flux<Integer> generateNumber() {
        return Flux
            .create(
                emitter -> {
                    for (int i = 1; i <= 100; i++) {
                        emitter.next(i);
                    }
                },
                OverflowStrategy.ERROR
            );
    }
}
