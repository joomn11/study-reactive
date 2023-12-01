package com.study.book.chapter5;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import java.net.URI;
import java.util.Collections;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ReactorIntro {

    @Test
    void test() {
        Flux.just("Hello", "Reactor")
            .map(data -> data.toLowerCase())
//            .subscribe(data -> System.out.println(data))
            .subscribe(System.out::println);

        Mono.just("Hello Reactor")
            .subscribe(System.out::println);

        Mono.empty()
            .subscribe(none -> System.out.println("# emitted onNext signal"),
                       error -> {
                       },
                       () -> System.out.println("# emitted onComplete signal"));

        Mono.just("Hello")
            .subscribe(none -> System.out.println("# emitted onNext signal"),
                       error -> {
                       },
                       () -> System.out.println("# emitted onComplete signal"));
    }

    @Test
    @DisplayName("Mono 활용 - Non-blocking I/O는 아니지만, Mono를 활용하여 요청과 응답을 하나의 Operator 체인으로 표현")
    void mono() {
        URI worldTimeUri = UriComponentsBuilder.newInstance()
                                               .scheme("http")
                                               .host("worldtimeapi.org")
                                               .port(80)
                                               .path("/api/timezone/Asia/Seoul")
                                               .build()
                                               .encode()
                                               .toUri();
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        Mono.just(
                restTemplate.exchange(worldTimeUri,
                                      HttpMethod.GET,
                                      new HttpEntity<String>(headers),
                                      String.class)
            )
            .map(response -> {
                DocumentContext jsonContext = JsonPath.parse(response.getBody());
                String dateTime = jsonContext.read("$.datetime");
                return dateTime;
            })
            .subscribe(data -> System.out.println("# emitted data: " + data),
                       error -> System.out.println("# error: " + error),
                       () -> System.out.println("# emitted onComplete signal"));
    }

    @Test
    void flux() {
        Flux.just(6, 9, 13)
            .map(num -> num % 2)
            .subscribe(System.out::println);

        Flux.fromArray(new Integer[]{3, 6, 7, 9})
            .filter(num -> num > 6)
            .map(num -> num * 2)
            .subscribe(System.out::println);

        Flux<String> flux = Mono.justOrEmpty("Steve")
                                .concatWith(Mono.justOrEmpty("Jobs"))
                                .concatWith(Mono.justOrEmpty(null));
        flux.subscribe(System.out::println);

        Flux.concat(
                Flux.just("Mercury", "Venus", "Earth"),
                Flux.just("Mercury", "Venus", "Earth"),
                Flux.just("Mercury", "Venus", "Earth")
            )
            .collectList()
            .subscribe(System.out::println);
    }
}
