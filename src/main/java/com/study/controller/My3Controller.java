package com.study.controller;

import com.study.comple.Completion;
import com.study.service.MyService;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.Netty4ClientHttpRequestFactory;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.async.DeferredResult;

@RestController
@RequestMapping("/my3")
@Slf4j
public class My3Controller {

    public static String SERVICE_URL = "http://localhost:8081/service?req={req}";
    // for chapter 5
    RestTemplate restTemplate = new RestTemplate();
    AsyncRestTemplate asyncRt = new AsyncRestTemplate(new Netty4ClientHttpRequestFactory(new NioEventLoopGroup(1)));

    @Autowired
    MyService myService;

    /**
     * 외부 api 호출 - async * 2 + 내부 async service + deferredResult
     * callback hell
     */
    @GetMapping("/rest")
    public DeferredResult<String> rest(int idx) {
        DeferredResult<String> dr = new DeferredResult<>();
        ListenableFuture<ResponseEntity<String>> future = asyncRt.getForEntity(SERVICE_URL, String.class, "hello " + idx);

        future.addCallback(s -> {
            String url2 = "http://localhost:8081/service2?req={req}";
            ListenableFuture<ResponseEntity<String>> future2 = asyncRt.getForEntity(url2, String.class, s.getBody());
            future2.addCallback(s2 -> {
                ListenableFuture<String> future3 = myService.work(s2.getBody());
                future3.addCallback(s3 -> {
                    dr.setResult(s3);
                }, e -> {
                    dr.setErrorResult(e.getMessage());
                });
            }, e -> {
                dr.setErrorResult(e.getMessage());
            });
        }, e -> {
            dr.setErrorResult(e.getMessage());
        });

        return dr;
    }

    @GetMapping("/rest2")
    public DeferredResult<String> rest2(int idx) {
        DeferredResult<String> dr = new DeferredResult<>();
        Completion.from(asyncRt.getForEntity(SERVICE_URL, String.class, "hello " + idx))
                  .andAccept(s -> dr.setResult(s.getBody()));
        return dr;
    }

    @GetMapping("/rest3")
    public DeferredResult<String> rest3(int idx) {
        DeferredResult<String> dr = new DeferredResult<>();
        String url22 = "http://localhost:8081/service2?req={req}";
        Completion.from(asyncRt.getForEntity(SERVICE_URL, String.class, "hello " + idx))
                  .andApply(s -> asyncRt.getForEntity(url22, String.class, s.getBody()))
                  .andAccept(s -> dr.setResult(s.getBody()));
        return dr;
    }

    @GetMapping("/rest4")
    public DeferredResult<String> rest4(int idx) {
        DeferredResult<String> dr = new DeferredResult<>();
        String url22 = "http://localhost:8081/service2?req={req}";
        Completion.from(asyncRt.getForEntity(SERVICE_URL, String.class, "hello " + idx))
                  .andApply(s -> asyncRt.getForEntity(url22, String.class, s.getBody()))
                  .andError(e -> dr.setErrorResult(e.toString()))
                  .andAccept(s -> dr.setResult(s.getBody()));
        return dr;
    }

    @GetMapping("/rest5")
    public DeferredResult<String> rest5(int idx) {
        DeferredResult<String> dr = new DeferredResult<>();
        String url22 = "http://localhost:8081/service2?req={req}";
        Completion.from(asyncRt.getForEntity(SERVICE_URL, String.class, "hello " + idx))
                  .andApply(s -> asyncRt.getForEntity(url22, String.class, s.getBody()))
//                  .andApply(s -> myService.work(s.getBody()))
                  .andError(e -> dr.setErrorResult(e.toString()))
                  .andAccept(s -> dr.setResult(s.getBody()));
        return dr;
    }
}
