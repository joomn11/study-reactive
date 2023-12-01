package com.study.book.chapter9;

import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.publisher.Sinks.EmitFailureHandler;
import reactor.core.scheduler.Schedulers;

@Slf4j
public class SinksTest {

    private static String doTask(int taskNumber) {
        // now tasking
        // complete to task
        return "task " + taskNumber + " result";
    }

    @Test
    void createTest() throws InterruptedException {
        int tasks = 6;

        Flux.create((FluxSink<String> sink) -> {
                IntStream.range(1, tasks)
                         .forEach(n -> sink.next(doTask(n)));
            })
            .subscribeOn(Schedulers.boundedElastic())
            .doOnNext(n -> log.info("# create(): {}", n))
            .publishOn(Schedulers.parallel())
            .map(result -> result + " success")
            .doOnNext(n -> log.info("# map(): {}", n))
            .publishOn(Schedulers.parallel())
            .subscribe(data -> log.info("# onNext: {}", data));

        Thread.sleep(5000L);

    }

    @Test
    void sinkTest() throws InterruptedException {
        int tasks = 6;

        Sinks.Many<String> unicastSink = Sinks.many().unicast().onBackpressureBuffer();
        Flux<String> fluxView = unicastSink.asFlux();

        IntStream.range(1, tasks)
                 .forEach(n -> {
                     new Thread(() -> { // sink is thread safe operator !
                         unicastSink.emitNext(doTask(n), EmitFailureHandler.FAIL_FAST);
                         log.info("# emitted: {}", n);
                     }).start();
                     try {
                         Thread.sleep(100L);
                     } catch (InterruptedException e) {
                         log.error(e.getMessage());
                     }
                 });
        fluxView.publishOn(Schedulers.parallel())
                .map(rst -> rst + " success!")
                .doOnNext(n -> log.info("# map(): {}", n))
                .publishOn(Schedulers.parallel())
                .subscribe(data -> log.info("# onNext: {}", data));

        Thread.sleep(200L);
    }

    @Test
    void sinkOneTest() {
        Sinks.One<String> sinkOne = Sinks.one();
        Mono<String> mono = sinkOne.asMono();

        sinkOne.emitValue("Hello Reactor", EmitFailureHandler.FAIL_FAST);
        sinkOne.emitValue("Hello Reactor", EmitFailureHandler.FAIL_FAST);

        mono.subscribe(data -> log.info("# subscriber1: {}", data));
        mono.subscribe(data -> log.info("# subscriber2: {}", data));
    }

    @Test
    void sinkManyTest() {
        Sinks.Many<Integer> unicastSink = Sinks.many().unicast().onBackpressureBuffer();
        Flux<Integer> fluxView = unicastSink.asFlux();

        unicastSink.emitNext(1, EmitFailureHandler.FAIL_FAST);
        unicastSink.emitNext(2, EmitFailureHandler.FAIL_FAST);

        fluxView.subscribe(data -> log.info("# subscriber1: {}", data));

        unicastSink.emitNext(3, EmitFailureHandler.FAIL_FAST);
        fluxView.subscribe(data -> log.info("# subscriber2: {}", data));

    }

    @Test
    void multiManyTest() {
        Sinks.Many<Integer> multiSink = Sinks.many().multicast().onBackpressureBuffer();
        Flux<Integer> fluxView = multiSink.asFlux();

        multiSink.emitNext(1, EmitFailureHandler.FAIL_FAST);
        multiSink.emitNext(2, EmitFailureHandler.FAIL_FAST);

        fluxView.subscribe(data -> log.info("# subscriber1: {}", data));

        multiSink.emitNext(3, EmitFailureHandler.FAIL_FAST);
        fluxView.subscribe(data -> log.info("# subscriber2: {}", data));

    }

    @Test
    void multiReplyManyTest() {
        Sinks.Many<Integer> replySink = Sinks.many().replay().limit(2);
        Flux<Integer> fluxView = replySink.asFlux();

        replySink.emitNext(1, EmitFailureHandler.FAIL_FAST);
        replySink.emitNext(2, EmitFailureHandler.FAIL_FAST);
        replySink.emitNext(3, EmitFailureHandler.FAIL_FAST);

        fluxView.subscribe(data -> log.info("# subscriber1: {}", data));

        replySink.emitNext(4, EmitFailureHandler.FAIL_FAST);
        fluxView.subscribe(data -> log.info("# subscriber2: {}", data));

    }
}
