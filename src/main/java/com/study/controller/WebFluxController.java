package com.study.controller;

import com.study.service.MyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
@RequestMapping("/webflux")
public class WebFluxController {

    public static String URL1 = "http://localhost:8081/service?req={req}";
    public static String URL2 = "http://localhost:8081/service2?req={req}";

    @Autowired
    MyService myService;

    WebClient client = WebClient.create();

    @GetMapping("/rest")
    public Mono<String> rest(int idx) {
        return client.get()
                     .uri(URL1, idx)
                     .exchange()
                     .flatMap(response -> response.bodyToMono(String.class));
    }

    @GetMapping("/rest2")
    public Mono<String> rest2(int idx) {
        return client.get()
                     .uri(URL1, idx)
                     .exchange()
                     .flatMap(response -> response.bodyToMono(String.class))
                     .flatMap(response -> client.get().uri(URL2, response).exchange())
                     .flatMap(response -> response.bodyToMono(String.class));
    }


}
