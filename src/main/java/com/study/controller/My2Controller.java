package com.study.controller;

import com.study.service.MyService;
import io.netty.channel.nio.NioEventLoopGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.Netty4ClientHttpRequestFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.async.DeferredResult;

@RestController
public class My2Controller {

    public static String SERVICE_URL = "http://localhost:8081/service?req={req}";
    // for chapter 5
    RestTemplate restTemplate = new RestTemplate();
    AsyncRestTemplate asyncRt = new AsyncRestTemplate(new Netty4ClientHttpRequestFactory(new NioEventLoopGroup(1)));

    @Autowired
    MyService myService;

    /**
     * 그냥 내부 component 호출
     */
    @GetMapping("/rest")
    public String rest(int idx) {
        return "rest" + idx;
    }

    /**
     * 외부 api 호출 - sync
     * servlet thread 에서 처리
     */
    @GetMapping("/rest2")
    public String rest2(int idx) {
        return restTemplate.getForObject(SERVICE_URL, String.class, "Hello " + idx);
    }

    /**
     * 외부 api 호출 - async
     * 다만, 다량의 worker thread 만들어서 처리를 한다
     */
    @GetMapping("/rest3")
    public ListenableFuture<ResponseEntity<String>> rest3(int idx) {
        return asyncRt.getForEntity(SERVICE_URL, String.class, "hello " + idx);
    }

    /**
     * 외부 api 호출 - async + deferredResult
     * 다량의 worker thread 만들지 않고 deferred 저장해두고 dr.setResult 호출되면 처리함
     */
    @GetMapping("/rest4")
    public DeferredResult<String> rest4(int idx) {
        DeferredResult<String> dr = new DeferredResult<>();
        ListenableFuture<ResponseEntity<String>> future = asyncRt.getForEntity(SERVICE_URL, String.class, "hello " + idx);

        future.addCallback(s -> {
            dr.setResult(s.getBody() + " / work");
        }, e -> {
            dr.setErrorResult(e.getMessage());
        });

        return dr;
    }

    /**
     * 외부 api 호출 - async * 2 + deferredResult
     * 응답으로 받은 정보를 기반으로 한번 더 async call
     */
    @GetMapping("/rest5")
    public DeferredResult<String> rest5(int idx) {
        DeferredResult<String> dr = new DeferredResult<>();
        ListenableFuture<ResponseEntity<String>> future = asyncRt.getForEntity(SERVICE_URL, String.class, "hello " + idx);

        future.addCallback(s -> {
            String url2 = "http://localhost:8081/service2?req={req}";
            ListenableFuture<ResponseEntity<String>> future2 = asyncRt.getForEntity(url2, String.class, s.getBody());
            future2.addCallback(s2 -> {
                dr.setResult(s2.getBody() + "/work");
            }, e -> {
                dr.setErrorResult(e.getMessage());
            });

        }, e -> {
            dr.setErrorResult(e.getMessage());
        });

        return dr;
    }

    /**
     * 외부 api 호출 - async * 3 + deferredResult
     * 응답으로 받은 정보를 기반으로 추가적인 async call
     */
    @GetMapping("/rest6")
    public DeferredResult<String> rest6(int idx) {
        DeferredResult<String> dr = new DeferredResult<>();
        ListenableFuture<ResponseEntity<String>> future = asyncRt.getForEntity(SERVICE_URL, String.class, "hello " + idx);

        future.addCallback(s -> {
            String url2 = "http://localhost:8081/service2?req={req}";
            ListenableFuture<ResponseEntity<String>> future2 = asyncRt.getForEntity(url2, String.class, s.getBody());
            future2.addCallback(s2 -> {
                String url3 = "http://localhost:8081/service2?req={req}";
                ListenableFuture<ResponseEntity<String>> future3 = asyncRt.getForEntity(url3, String.class, s2.getBody());
                future3.addCallback(s3 -> {
                    dr.setResult(s3.getBody() + "/work");
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

    /**
     * 외부 api 호출 - async * 2 + 내부 async service + deferredResult
     */
    @GetMapping("/rest7")
    public DeferredResult<String> rest7(int idx) {
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

    @Bean
    public ThreadPoolTaskExecutor myThreadPool() {
        ThreadPoolTaskExecutor te = new ThreadPoolTaskExecutor();
        te.setCorePoolSize(1);
        te.setMaxPoolSize(1);
        te.initialize();
        return te;
    }
}
