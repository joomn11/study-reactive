package com.study.book.chapter13;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class RecordTest {

    @Test
    void test() {
        StepVerifier
            .create(RecordTestExample.getCapitalizedCountry(Flux.just("korea", "england", "canada", "india")))
            .expectSubscription()
            .recordWith(ArrayList::new)
            .thenConsumeWhile(country -> !country.isEmpty())
            .consumeRecordedWith(
                countries -> {
                    assertThat(
                        countries.stream()
                                 .allMatch(country -> Character.isUpperCase(country.charAt(0))),
                        is(true)
                    );
                }
            )
            .expectComplete()
            .verify();
    }

    @Test
    void test2() {
        StepVerifier
            .create(RecordTestExample.getCapitalizedCountry(Flux.just("korea", "england", "canada", "india")))
            .expectSubscription()
            .recordWith(ArrayList::new)
            .thenConsumeWhile(country -> !country.isEmpty())
            .expectRecordedMatches(
                countries ->
                    countries.stream()
                             .allMatch(country -> Character.isUpperCase(country.charAt(0)))

            )
            .expectComplete()
            .verify();
    }
}
