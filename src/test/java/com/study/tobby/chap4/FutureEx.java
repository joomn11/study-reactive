package com.study.tobby.chap4;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class FutureEx {

    public static void main(String[] args) throws InterruptedException {
        ExecutorService es = Executors.newCachedThreadPool();

        CallbackFutureTask ft = new CallbackFutureTask(() -> {
            Thread.sleep(1000);
            log.info("async");
            if (1 == 1) {
                throw new RuntimeException();
            }
            return "hello";
        }
            , rst -> log.info("[onSuccess]  " + rst)
            , e -> log.info("[onError]" + e));

        es.execute(ft);
        es.shutdown();
//        Thread.sleep(2000);
    }

    @Test
    void test() throws InterruptedException {
        ExecutorService es = Executors.newCachedThreadPool();

        es.execute(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {

            }
            log.info("async");
        });

        Thread.sleep(2000);
        log.info("exit");
    }

    @Test
    void callableTest() throws InterruptedException, ExecutionException {
        ExecutorService es = Executors.newCachedThreadPool();

        Future<String> future = es.submit(() -> {
            Thread.sleep(1000);
            log.info("async");
            return "hello";
        });

        log.info(future.isDone() + " ");
        log.info("exit");

        String result = future.get(); // blocking
        log.info(result);
//        Thread.sleep(2000);
    }

    @Test
    void futureTaskTest() throws InterruptedException, ExecutionException {
        ExecutorService es = Executors.newCachedThreadPool();

        FutureTask<String> futureTask = new FutureTask<String>(() -> {
            Thread.sleep(1000);
            log.info("async");
            return "hello";
        }) {
            @Override
            protected void done() {
                try {
                    log.info("[done]" + get() + " ");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        es.execute(futureTask);
        es.shutdown();
        Thread.sleep(2000);
    }

    @Test
    void futureTaskTest2() throws InterruptedException, ExecutionException {
        ExecutorService es = Executors.newCachedThreadPool();

        CallbackFutureTask ft = new CallbackFutureTask(() -> {
            Thread.sleep(1000);
            log.info("async");
            if (1 == 1) {
                throw new RuntimeException("async error ! ");
            }
            return "hello";
        }
            , rst -> log.info("[onSuccess]  " + rst)
            , e -> log.info("[onError]" + e.getMessage()));

        es.execute(ft);
        es.shutdown();
        Thread.sleep(2000);
    }

    interface SuccessCallback {

        void onSuccess(String result);
    }

    interface ExceptionCallback {

        void onError(Throwable e);
    }

    public static class CallbackFutureTask extends FutureTask<String> {

        SuccessCallback sc;
        ExceptionCallback ec;

        public CallbackFutureTask(Callable<String> callable, SuccessCallback sc, ExceptionCallback ec) {
            super(callable);
            this.sc = Objects.requireNonNull(sc);
            this.ec = Objects.requireNonNull(ec);
        }

        @Override
        protected void done() {
            try {
                log.info("[done]");
                sc.onSuccess(get());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (ExecutionException e) {
                ec.onError(e.getCause());
            }
        }
    }

}
