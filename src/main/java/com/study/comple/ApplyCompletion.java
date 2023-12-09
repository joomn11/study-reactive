package com.study.comple;

import java.util.function.Function;
import org.springframework.util.concurrent.ListenableFuture;

public class ApplyCompletion<S, T> extends Completion<S, T> {

    public Function<S, ListenableFuture<T>> fn;

    public ApplyCompletion(Function<S, ListenableFuture<T>> con) {
        this.fn = con;
    }

    @Override
    public void run(S value) {
        ListenableFuture<T> lf = fn.apply(value);
        lf.addCallback(s -> complete(s), e -> error(e));
    }
}
