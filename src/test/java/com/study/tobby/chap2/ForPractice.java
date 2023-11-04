package com.study.tobby.chap2;

import java.util.Iterator;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class ForPractice {

    @Test
    void test() {
        Iterable<Integer> iter = List.of(1, 2, 3, 4, 5);
        Publisher pub = new Publisher() {
            @Override
            public void subscribe(Subscriber sub) {
                Iterator<Integer> iterator = iter.iterator();
                // 해당 요청이 오면 어떻게 동작할건지 정의되어 있음 ..
                // 이 동작은 pub가 정의하는게 맞을듯 .. sub가 이 요청을 하면 나는 이렇게 해야지
                // 그리고 그걸 sub에게 건내서 sub가 pub에게 요청을 보내고 싶은 경우에 사용함 pub-sub 사이에 매개체
                Subscription subscription = new Subscription() {

                    @Override
                    public void request(long n) {
                        System.out.println("[request] ");
                        if (iterator.hasNext()) {
                            sub.onNext(iterator.next());
                        } else {
                            sub.onComplete();
                        }

                    }

                    @Override
                    public void cancel() {

                    }
                };
                sub.onSubscribe(subscription);
            }
        };

        Subscriber<Integer> sub = new Subscriber<>() {
            Subscription subscription;
            int REQUEST_NUM = 1;

            @Override
            public void onSubscribe(Subscription subscription) {
                System.out.println("[sub][onSubscribe] ");
                this.subscription = subscription;
                this.subscription.request(REQUEST_NUM); // 요청준비가 되었어?! 그럼 이 만큼 메시지 줘!
            }

            @Override
            public void onNext(Integer i) {
                System.out.println("[sub][onNext] " + i);
                subscription.request(REQUEST_NUM);
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onComplete() {
                System.out.println("[sub][onComplete]");
            }
        };

        pub.subscribe(sub);
    }
}
