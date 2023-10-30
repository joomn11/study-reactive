package com.study.tobby.chap2;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;


public class PubSub {

    @Test
    void test1() {
        Publisher<Integer> pub = iterPub(
            Stream.iterate(1, i -> i + 1)
                  .limit(10)
                  .collect(Collectors.toList())
        );

        Publisher<Integer> mapPub = mapPub(pub, i -> i * 10);
//        Publisher<String> mapPub2 = mapPub(mapPub, i -> "[" + -i + "]");
//        Publisher<Integer> sumPub = sumPub(mapPub2);
//        Publisher<String> reducePub = reducePub(mapPub, "", (a, b) -> a + "-" + b);
        Publisher<StringBuilder> reducePub = reducePub(mapPub, new StringBuilder(), (a, b) -> a.append(b + ","));

        Subscriber<StringBuilder> sub = logSub();

        reducePub.subscribe(sub);
    }

    private <T, R> Publisher<R> reducePub(Publisher<T> pub, R init, BiFunction<R, T, R> bf) {
        return new Publisher<R>() {
            @Override
            public void subscribe(Subscriber<? super R> sub) {
                pub.subscribe(new DelegateSub<T, R>(sub) {
                    R result = init;

                    @Override
                    public void onNext(T i) {
                        result = bf.apply(result, i);
                    }

                    @Override
                    public void onComplete() {
                        sub.onNext(result);
                        sub.onComplete();
                    }
                });
            }
        };
    }

//    private Publisher<Integer> sumPub(Publisher<Integer> pub) {
//        return new Publisher<Integer>() {
//            @Override
//            public void subscribe(Subscriber<? super Integer> sub) {
//                pub.subscribe(new DelegateSub(sub) {
//                    int sum = 0;
//
//                    @Override
//                    public void onNext(Integer i) {
//                        sum += i;
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        sub.onNext(sum);
//                        sub.onComplete();
//                    }
//                });
//            }
//        };
//    }

    private <T, R> Publisher<R> mapPub(Publisher<T> pub, Function<T, R> f) {
        return new Publisher<R>() {
            @Override
            public void subscribe(Subscriber<? super R> sub) {
//                pub.subscribe(sub);
                pub.subscribe(new DelegateSub<T, R>(sub) {
                    @Override
                    public void onNext(T i) {
                        sub.onNext(f.apply(i));
                    }
                });
            }
        };
    }

    private <T> Subscriber<T> logSub() {
        return new Subscriber<T>() {
            @Override
            public void onSubscribe(Subscription s) {
                System.out.println(Thread.currentThread().getName() + "  onSubscribe: ");
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(T i) {
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
