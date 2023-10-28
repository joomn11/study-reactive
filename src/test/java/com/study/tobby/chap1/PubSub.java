package com.study.tobby.chap1;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class PubSub {

    @Test
    void test1() throws InterruptedException {
        Iterable<Integer> itr = List.of(1, 2, 3, 4, 5);

        ExecutorService es = Executors.newSingleThreadExecutor();

        Publisher p = new Publisher() {
            @Override
            public void subscribe(Subscriber s) {
                Iterator<Integer> iterator = itr.iterator();

                s.onSubscribe(new Subscription() {
                    @Override
                    public void request(long n) {
                        es.execute(() -> {
                            int i = 0;
                            while (i++ < n) {
                                if (iterator.hasNext()) {
                                    s.onNext(iterator.next());
                                } else {
                                    s.onComplete();
                                    break;
                                }
                            }
                        });
                    }

                    @Override
                    public void cancel() {

                    }
                });

            }
        };

        Subscriber<Integer> s = new Subscriber<Integer>() {
            Subscription subscription;

            @Override
            public void onSubscribe(Subscription s) {
                this.subscription = s;
                System.out.println(Thread.currentThread().getName() + "  onSubscribe");
//                s.request(Long.MAX_VALUE);
                this.subscription.request(1);
            }

            @Override
            public void onNext(Integer integer) {
                System.out.println(Thread.currentThread().getName() + "  onNext " + integer);
                this.subscription.request(1);
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("onError " + t.getMessage());

            }

            @Override
            public void onComplete() {
                System.out.println(Thread.currentThread().getName() + "  onComplete ");

            }
        };
        p.subscribe(s);

        es.awaitTermination(5, TimeUnit.SECONDS);
        es.shutdown();
    }
}
