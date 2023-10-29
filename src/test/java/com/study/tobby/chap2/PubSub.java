package com.study.tobby.chap2;

import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;


public class PubSub {

    @Test
    void test1() {
        Publisher<Integer> pub = iterPub(Stream.iterate(1, i -> i + 1).limit(10).collect(Collectors.toList()));
        Subscriber<Integer> sub = logSub();
        pub.subscribe(sub);
    }

    private Subscriber<Integer> logSub() {
        return new Subscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription s) {
                System.out.println(Thread.currentThread().getName() + "  onSubscribe: ");
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Integer i) {
                System.out.println(Thread.currentThread().getName() + "  onNext: " + i);
            }

            @Override
            public void onError(Throwable t) {
                System.out.println(Thread.currentThread().getName() + "  onError: " + t);
            }

            @Override
            public void onComplete() {
                System.out.println(Thread.currentThread().getName() + "  onComplete: ");
            }
        };
    }

    private Publisher<Integer> iterPub(Iterable<Integer> iter) {
        return new Publisher<>() {
            @Override
            public void subscribe(Subscriber<? super Integer> sub) {
                sub.onSubscribe(new Subscription() {
                    @Override
                    public void request(long n) {
                        try {
                            iter.forEach(i -> sub.onNext(i));
                            sub.onComplete();
                        } catch (Throwable e) {
                            sub.onError(e);
                        }
                    }

                    @Override
                    public void cancel() {
                    }
                });
            }
        };
    }
}
