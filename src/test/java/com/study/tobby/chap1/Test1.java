package com.study.tobby.chap1;

import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

@SuppressWarnings("deprecation")
public class Test1 {

    // iterable <-> observable (duality)
    //  pull         push
    // pull : 받는쪽에서 땡겨옴
    // push : 넣는쪽에서 알려줌

    // observable 한계
    // 1. complete ?
    // 2. error ?

    // reactive-stream
    // interface 역할 , 그 구현체가 rx-java, reactor .. 등등
    // 이번 강의에서는 reactive-stream 구현한 엔진을 만들어보자, 그러면 원리를 이해하고 다른 구현체를 사용하기 쉽다

    @Test
    void test1() {
        Iterable<Integer> iter = () -> new Iterator<Integer>() {
            final static int MAX = 10;
            int i = 0;

            @Override
            public boolean hasNext() {
                return i < MAX;
            }

            @Override
            public Integer next() {
                return ++i;
            }
        };

        for (Integer i : iter) {
            System.out.println(i);
        }

//        for (Iterator<Integer> it = iter.iterator(); it.hasNext(); ) {
//            System.out.println(it.next());
//        }
    }

    @Test
    void test2() throws InterruptedException {
        Observer observer = new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                System.out.println(Thread.currentThread().getName() + "  " + arg);
            }
        };

        IntObservable io = new IntObservable();
        io.addObserver(observer);

        ExecutorService es = Executors.newSingleThreadExecutor();
        es.execute(io);
//        io.run();

        System.out.println(Thread.currentThread().getName() + "   EXIT ");

        es.awaitTermination(2, TimeUnit.SECONDS);
        es.shutdown();
    }
}
