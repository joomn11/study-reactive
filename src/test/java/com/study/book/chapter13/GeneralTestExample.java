package com.study.book.chapter13;

import reactor.core.publisher.Flux;

public class GeneralTestExample {

    public static Flux<String> saHello() {
        return Flux.just("hello", "reactor");
    }

    public static Flux<Integer> divideByTwo(Flux<Integer> source) {
        return source
            .zipWith(
                Flux.just(2, 2, 2, 2, 0),
                (x, y) -> x / y
            );
    }

    public static Flux<Integer> takeNumber(Flux<Integer> source, long n) {
        return source.take(n);
    }
}
