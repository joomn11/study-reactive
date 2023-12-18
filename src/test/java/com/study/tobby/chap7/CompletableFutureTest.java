package com.study.tobby.chap7;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class CompletableFutureTest {

    @Test
    void test() throws ExecutionException, InterruptedException {
//        CompletableFuture<Integer> f = CompletableFuture.completedFuture(2);
        CompletableFuture<Integer> f = new CompletableFuture<>();
//        f.complete(2);
        f.completeExceptionally(new RuntimeException());
        System.out.println(f.get() + "");
    }

    @Test
    void test2() throws InterruptedException {
        CompletableFuture
            .runAsync(() -> log.info("runAsync"))
            .thenRun(() -> log.info("thenRun1"))
            .thenRun(() -> log.info("thenRun2"));

        log.info("exit !!");

//        Thread.sleep(100L);
        ForkJoinPool.commonPool().shutdown();
        ForkJoinPool.commonPool().awaitTermination(10, TimeUnit.SECONDS);
    }

    @Test
    void test3() throws InterruptedException {
        CompletableFuture
            .supplyAsync(() -> {
                log.info("supplyAsync");
//                if (1 == 1) {
//                    throw new RuntimeException();
//                }
                return 1;
            })
            .thenCompose(s -> {
                log.info("thenApply : {}", s);
                return CompletableFuture.completedFuture(s + 1);
            })
            .thenApply(s2 -> {
                log.info("thenApply2 : {}", s2);
                return s2 * 2;
            })
            .exceptionally(e -> -10)
            .thenAccept(s3 -> log.info("thenAccept : {}", s3));

        log.info("exit !!");

        Thread.sleep(100L);
//        ForkJoinPool.commonPool().shutdown();
//        ForkJoinPool.commonPool().awaitTermination(10, TimeUnit.SECONDS);
    }

    @Test
    void test4() throws InterruptedException {
        ExecutorService es = Executors.newFixedThreadPool(10);
        CompletableFuture
            .supplyAsync(() -> {
                log.info("supplyAsync");
                return 1;
            }, es)
            .thenCompose(s -> {
                log.info("thenApply : {}", s);
                return CompletableFuture.completedFuture(s + 1);
            })
            .thenApplyAsync(s2 -> {
                log.info("thenApply2 : {}", s2);
                return s2 * 2;
            }, es)
            .exceptionally(e -> -10)
            .thenAcceptAsync(s3 -> log.info("thenAccept : {}", s3), es);

        log.info("exit !!");

//        es.shutdown();
//        es.awaitTermination(5, TimeUnit.SECONDS);

        Thread.sleep(100L);
//        ForkJoinPool.commonPool().shutdown();
//        ForkJoinPool.commonPool().awaitTermination(10, TimeUnit.SECONDS);

    }

}
