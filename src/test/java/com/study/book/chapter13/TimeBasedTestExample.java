package com.study.book.chapter13;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

@Slf4j
public class TimeBasedTestExample {

    public static Flux<Tuple2<String, Integer>> getCovid19Count(Flux<Long> source) {
        return source.flatMap(
            notUse -> Flux.just(
                Tuples.of("seoul", 10),
                Tuples.of("kyungi", 1),
                Tuples.of("kangwon", 5),
                Tuples.of("chuncheon", 3),
                Tuples.of("kyungsang", 6),
                Tuples.of("junla", 5),
                Tuples.of("inchen", 8),
                Tuples.of("dejun", 2),
                Tuples.of("degu", 1),
                Tuples.of("busan", 3)
            )
        );
    }

    public static Flux<Tuple2<String, Integer>> getVoteCount(Flux<Long> source) {
        return source.zipWith(
                         Flux.just(
                             Tuples.of("seoul", 10),
                             Tuples.of("kyungi", 1),
                             Tuples.of("kangwon", 5),
                             Tuples.of("chuncheon", 3),
                             Tuples.of("kyungsang", 6),
                             Tuples.of("junla", 5),
                             Tuples.of("inchen", 8),
                             Tuples.of("dejun", 2),
                             Tuples.of("degu", 1),
                             Tuples.of("busan", 3)
                         )
                     )
                     .map(Tuple2::getT2);
    }
}
