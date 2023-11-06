package com.study.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

@Service
public class MyService {

    @Async(value = "myThreadPool")
    public ListenableFuture<String> work(String req) {
        return new AsyncResult<>(req + " / asyncwork");
    }
}
