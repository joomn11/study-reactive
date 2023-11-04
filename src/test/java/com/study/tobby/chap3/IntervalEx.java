package com.study.tobby.chap3;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

@Slf4j
public class IntervalEx {

    @Test
    @DisplayName("takePub를 통해서 pub를 한번 래핑 ,subscription request 부분을 새로운 thread 할당 ")
    void test() throws InterruptedException {
        Publisher<Integer> pub = s -> s.onSubscribe(new Subscription() {
            int no = 0;
            boolean canceled = false;

            @Override
            public void request(long n) {
                ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
                exec.scheduleAtFixedRate(() -> {
                    if (canceled) {
                        exec.shutdown();
                        return;
                    }
                    s.onNext(no++);
                }, 0, 300, TimeUnit.MILLISECONDS);
            }

            @Override
            public void cancel() {
                canceled = true;
            }
        });

        Publisher<Integer> takePub = sub -> pub.subscribe(new Subscriber<>() {
            Subscription subscription;

            @Override
            public void onSubscribe(Subscription s) {
                subscription = s;
                sub.onSubscribe(s);
            }

            @Override
            public void onNext(Integer i) {
                sub.onNext(i);
                if (i >= 10) {
                    subscription.cancel();
                }
            }

            @Override
            public void onError(Throwable t) {
                sub.onError(t);
            }

            @Override
            public void onComplete() {
                sub.onComplete();
            }
        });

        Subscriber<Integer> sub = new Subscriber<>() {
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
                log.info("sub onError : {0}", t);

            }

            @Override
            public void onComplete() {
                log.info("sub onComplete ");

            }
        };
        takePub.subscribe(sub);
//        pub.subscribe(sub);
        TimeUnit.MILLISECONDS.sleep(5000);
    }
}
