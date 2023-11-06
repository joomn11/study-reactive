package com.study.tobby.chap3;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

@Slf4j
public class SchedulerEx {

    @Test
    @DisplayName("publisher 사이에 chaining 관련된 예시")
    void test() {
        Publisher<Integer> pub = sub -> {
            sub.onSubscribe(new Subscription() {
                @Override
                public void request(long n) {
                    log.info("pub request()");
                    sub.onNext(1);
                    sub.onNext(2);
                    sub.onNext(3);
                    sub.onNext(4);
                    sub.onNext(5);
                    sub.onComplete();
                }

                @Override
                public void cancel() {
                }
            });
        };

        Publisher<Integer> subOnPub = sub -> {
            ExecutorService es = Executors.newSingleThreadExecutor(new CustomizableThreadFactory() {
                @Override
                public String getThreadNamePrefix() {
                    return "subOn-";
                }
            });
            es.execute(() -> pub.subscribe(sub));
        };

        Publisher<Integer> pubOnPub = sub -> subOnPub.subscribe(new Subscriber<Integer>() {
            ExecutorService es = Executors.newSingleThreadExecutor(new CustomizableThreadFactory() {
                @Override
                public String getThreadNamePrefix() {
                    return "pubOn-";
                }
            });

            @Override
            public void onSubscribe(Subscription s) {
                sub.onSubscribe(s);
            }

            @Override
            public void onNext(Integer integer) {
                es.execute(() -> sub.onNext(integer));
            }

            @Override
            public void onError(Throwable t) {
                es.execute(() -> sub.onError(t));
                es.shutdown();
            }

            @Override
            public void onComplete() {
                es.execute(() -> sub.onComplete());
                es.shutdown();
            }
        });

        Subscriber<Integer> sub = new Subscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription s) {
                log.info("sub onSubscribe ");
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Integer i) {
                log.info("sub onNext :{}", i);
            }

            @Override
            public void onError(Throwable t) {
                log.info("sub onError : {}", t);
            }

            @Override
            public void onComplete() {
                log.info("sub onComplete ");
            }
        };
//        subOnPub.subscribe(sub);
        pubOnPub.subscribe(sub);
        log.info("exit");
    }
}
