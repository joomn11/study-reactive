package com.study.comple;

import java.util.function.Consumer;
import java.util.function.Function;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.concurrent.ListenableFuture;

@NoArgsConstructor
@Slf4j
public class Completion<S, T> {


    public Completion next;

    public static <S, T> Completion<S, T> from(ListenableFuture<T> lf) {
        Completion<S, T> c = new Completion<>();
        lf.addCallback(s -> {
            c.complete(s);
        }, e -> {
            c.error(e);
        });
        return c;
    }

    public void error(Throwable e) {
        if (next != null) {
            next.error(e);
        }
    }

    public void complete(T s) {
        if (next != null) {
            next.run(s);
        }
    }

    public void run(S value) {
    }

    public void andAccept(Consumer<T> con) {
        Completion<T, Void> c = new AcceptCompletion<>(con);
        this.next = c;
    }

    public Completion<T, T> andError(Consumer<Throwable> econ) {
        Completion<T, T> c = new ErrorCompletion<>(econ);
        this.next = c;
        return c;
    }

    public <V> Completion<T, V> andApply(Function<T, ListenableFuture<V>> fn) {
        Completion<T, V> c = new ApplyCompletion<>(fn);
        this.next = c;
        return c;
    }
}
