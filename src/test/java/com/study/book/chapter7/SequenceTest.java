package com.study.book.chapter7;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import java.net.URI;
import java.time.Duration;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
public class SequenceTest {

    private static Mono<String> getWorldTime(URI worldTimeUri) {
        return WebClient.create()
                        .get()
                        .uri(worldTimeUri)
                        .retrieve()
                        .bodyToMono(String.class)
                        .map(response -> {
                            DocumentContext jsonContext = JsonPath.parse(response);
                            String dateTime = jsonContext.read("$.datetime");
                            return dateTime;
                        });
    }

    @Test
    void coldTest() throws InterruptedException {
        Flux<String> coldFlux = Flux.fromIterable(Arrays.asList("korea", "japan", "chinese"))
                                    .map(String::toLowerCase);

        coldFlux.subscribe(country -> log.info("# Subscribe1: {}", country));
        log.info("----------------------------------");

        Thread.sleep(2000L);
        coldFlux.subscribe(country -> log.info("# Subscribe1: {}", country));

    }

    @Test
    void hotTest() throws InterruptedException {
        String[] strings = {"singerA", "singerB", "singerC", "singerD", "singerE"};
        log.info("# Begin concert");

        Flux<String> concertFlux = Flux.fromArray(strings)
                                       .delayElements(Duration.ofSeconds(1)) // parallel scheduler
                                       .share(); // cold to hot

        concertFlux.subscribe(singer -> log.info("# Subscribe1 is watching {}'s song", singer));

        Thread.sleep(2500);

        concertFlux.subscribe(singer -> log.info("# Subscribe2 is watching {}'s song", singer));

        Thread.sleep(3000);
    }

    @Test
    void coldHttpTest() throws InterruptedException {
        URI worldTimeUri = UriComponentsBuilder.newInstance().scheme("http")
                                               .host("worldtimeapi.org")
                                               .port(80)
                                               .path("/api/timezone/Asia/Seoul")
                                               .build()
                                               .encode()
                                               .toUri();

        Mono<String> mono = getWorldTime(worldTimeUri);
        mono.subscribe(dateTime -> log.info("# dateTime 1: {}", dateTime));
        Thread.sleep(2000);

        mono.subscribe(dateTime -> log.info("# dateTime 2: {}", dateTime));
        Thread.sleep(2000);

    }

    @Test
    void hotHttpTest() throws InterruptedException {
        URI worldTimeUri = UriComponentsBuilder.newInstance().scheme("http")
                                               .host("worldtimeapi.org")
                                               .port(80)
                                               .path("/api/timezone/Asia/Seoul")
                                               .build()
                                               .encode()
                                               .toUri();

        Mono<String> mono = getWorldTime(worldTimeUri).cache();
        mono.subscribe(dateTime -> log.info("# dateTime 1: {}", dateTime));
        Thread.sleep(2000);

        mono.subscribe(dateTime -> log.info("# dateTime 2: {}", dateTime));
        Thread.sleep(2000);

    }
}
