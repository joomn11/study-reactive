package com.study.book.chapter12;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.core.scheduler.Schedulers;

@Slf4j
public class DebuggingTest {

    public static Map<String, String> fruits = new HashMap<>();

    static {
        fruits.put("banana", "바나나");
        fruits.put("apple", "사과");
        fruits.put("pear", "배");
        fruits.put("grape", "포도");
    }

    private static Flux<Integer> multiply(Flux<Integer> source, Flux<Integer> other) {
        return source.zipWith(other, (x, y) -> x / y);
    }

    private static Flux<Integer> plus(Flux<Integer> source) {
        return source.map(num -> num + 2);
    }

    @Test
    void onOperatorDebugTest() throws InterruptedException {
        Hooks.onOperatorDebug();

        Flux.fromArray(new String[]{"BANANAS", "APPLES", "PEARS", "MELONS"})
            .subscribeOn(Schedulers.boundedElastic())
            .publishOn(Schedulers.parallel())
            .map(String::toLowerCase)
            .map(fruit -> fruit.substring(0, fruit.length() - 1))
            .map(fruits::get)
            .map(translated -> "맛있는 " + translated)
            .subscribe(
                log::info,
                e -> log.error("# onError : ", e)
            );

        Thread.sleep(100L);
    }

    @Test
    void tracebackTest() {
        Flux
            .just(2, 4, 6, 8)
            .zipWith(Flux.just(1, 2, 4, 0), (x, y) -> x / y)
            .checkpoint()
            .map(num -> num + 2)
            .checkpoint()
            .subscribe(
                data -> log.info("# onNext: {}", data),
                error -> log.error("# onError:", error)
            );
    }

    @Test
    void descriptionTest() {
        Flux
            .just(2, 4, 6, 8)
            .zipWith(Flux.just(1, 2, 4, 0), (x, y) -> x / y)
            .checkpoint("Example12_4.zipWith.checkpoint", true)
            .map(num -> num + 2)
            .checkpoint("Example12_4.map.checkpoint", true)
            .subscribe(
                data -> log.info("# onNext: {}", data),
                error -> log.error("# onError:", error)
            );
    }

    @Test
    void differentOperatorChainTest() {

        Flux<Integer> source = Flux.just(2, 4, 6, 8);
        Flux<Integer> other = Flux.just(2, 4, 6, 0);

        Flux<Integer> multiplySource = multiply(source, other);
        Flux<Integer> plusSource = plus(multiplySource).checkpoint();

        plusSource.subscribe(
            data -> log.info("# onNext: {}", data),
            error -> log.error("# onError: {}", error)
        );
    }

    @Test
    void logTest() throws InterruptedException {
        Hooks.onOperatorDebug();

        Flux.fromArray(new String[]{"BANANAS", "APPLES", "PEARS", "MELONS"})
            .subscribeOn(Schedulers.boundedElastic())
            .publishOn(Schedulers.parallel())
            .map(String::toLowerCase)
            .map(fruit -> fruit.substring(0, fruit.length() - 1))
            .log("Fruit.Substring", Level.FINE)
//            .log()
            .map(fruits::get)
            .map(translated -> "맛있는 " + translated)
            .subscribe(
                log::info,
                e -> log.error("# onError : ", e)
            );

        Thread.sleep(100L);
    }
}
