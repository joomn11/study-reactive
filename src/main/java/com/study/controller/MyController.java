package com.study.controller;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

@RestController
@Slf4j
public class MyController {

    Queue<DeferredResult<String>> queue = new LinkedList<>();

    @GetMapping("/hello")
    public String hello() throws InterruptedException {
        Thread.sleep(2000);
        return "hello";
    }

    @GetMapping("/callable")
    public Callable<String> callable() {
        log.info("callable");
        return () -> {
            log.info("async");
            Thread.sleep(2000);
            return "hello";
        };
    }

    @GetMapping("/dr")
    public DeferredResult<String> dr() {
        log.info("dr");
        DeferredResult<String> dr = new DeferredResult<>(60 * 1000L);
        queue.add(dr);
        return dr;
    }

    @GetMapping("/dr/count")
    public String drCount() {
        return String.valueOf(queue.size());
    }

    @GetMapping("/dr/event")
    public String drEvent(String msg) {
        for (DeferredResult<String> dr : queue) {
            dr.setResult("Hello " + msg);
        }
        queue.clear();
        return "OK";
    }

    @GetMapping("/emitter")
    public ResponseBodyEmitter emitter() {
        ResponseBodyEmitter emitter = new ResponseBodyEmitter();
        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                for (int i = 0; i < 50; i++) {
                    emitter.send("<p> Stream " + i + " <p>");
                    Thread.sleep(300);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        return emitter;
    }
}
