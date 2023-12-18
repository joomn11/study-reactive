package com.study.book.chapter13;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
public class PublisherProbeTestExample {

    public static Mono<String> processTask(Mono<String> main, Mono<String> standby) {
        return main.flatMap(msg -> Mono.just(msg))
                   .switchIfEmpty(standby);
    }

    public static Mono<String> supplyMainPower() {
        return Mono.empty();
    }

    public static Mono<String> supplyStandbyPower() {
        return Mono.just("# supply Standby Power");
    }
}
