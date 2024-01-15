package com.study.book.chapter14;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
public class GenerateSeq {

    @Test
    void test() {
//        Mono.justOrEmpty(null)
        Mono.justOrEmpty(Optional.ofNullable(null))
            .subscribe(data -> {
                       },
                       e -> {
                       },
                       () -> log.info("# onComplete"));
    }

    @Test
    void test2() {
//        Flux.fromIterable(List.of(1, 2, 3, 4, 5))
        Flux.fromStream(List.of(1, 2, 3, 4, 5).stream())
            .subscribe(data ->
                           log.info("data ={}", data));
    }

    @Test
    void deferTest1() throws InterruptedException {
        log.info("started time:{}", LocalDateTime.now());
        Mono<LocalDateTime> justMono = Mono.just(LocalDateTime.now());
        Mono<LocalDateTime> deferMono = Mono.defer(() -> Mono.just(LocalDateTime.now()));

        Thread.sleep(2000L);

        justMono.subscribe(d -> log.info("#onNext just1: {}", d));
        deferMono.subscribe(d -> log.info("#onNext defer1: {}", d));
        Thread.sleep(2000L);

        justMono.subscribe(d -> log.info("#onNext just2: {}", d));
        deferMono.subscribe(d -> log.info("#onNext defer2: {}", d));
    }


    @Test
    void deferTest2() throws InterruptedException {
        log.info("started time:{}", LocalDateTime.now());

        Mono.just("Hello")
            .doOnNext(d -> log.info("beforeDelay"))
            .delayElement(Duration.ofSeconds(3))
            .doOnNext(d -> log.info("afterDelay"))
//            .switchIfEmpty(sayDefault())
            .switchIfEmpty(Mono.defer(() -> sayDefault()))
            .subscribe(d -> log.info("#onNext: {}", d));

        Thread.sleep(4000L);
    }

    private Mono<String> sayDefault() {
        log.info("#Say HI");
        return Mono.just("Hi");
    }
}
