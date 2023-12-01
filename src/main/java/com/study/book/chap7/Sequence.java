package com.study.book.chap7;

import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@Slf4j
public class Sequence {

    public static void main1(String[] args) throws InterruptedException {

        String[] strings = {"singerA", "singerB", "singerC", "singerD", "singerE"};
        log.info("# Begin concert");

        Flux<String> concertFlux = Flux.fromArray(strings)
                                       .delayElements(Duration.ofSeconds(1))
                                       .share();

        concertFlux.subscribe(singer -> log.info("# Subscribe1 is watching {}'s song", singer));

        Thread.sleep(2500);

        concertFlux.subscribe(singer -> log.info("# Subscribe2 is watching {}'s song", singer));

        Thread.sleep(3000);

    }

    public static void main(String[] args) throws InterruptedException {
        Flux.interval(Duration.ofMillis(1L))
//            .onBackpressureError()
//            .doOnNext(data -> log.info("# doOnNext : {}", data))
//            .onBackpressureDrop(dropped -> log.info("# dropped: {}", dropped))
            .onBackpressureLatest()
            .publishOn(Schedulers.parallel())
            .subscribe(data -> {
                           try {
                               Thread.sleep(5L);
                           } catch (InterruptedException e) {
                           }
                           log.info("# onNext: {}", data);
                       },
                       error -> log.error("# onError", error));

        Thread.sleep(2000L);
    }
}
