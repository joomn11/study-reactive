package com.study.comple;

import java.util.function.Function;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;

public class ApplyCompletion extends Completion {

    public Function<ResponseEntity<String>, ListenableFuture<ResponseEntity<String>>> fn;

    public ApplyCompletion(Function<ResponseEntity<String>, ListenableFuture<ResponseEntity<String>>> con) {
        this.fn = con;
    }

    @Override
    public void run(ResponseEntity<String> value) {
        ListenableFuture<ResponseEntity<String>> lf = fn.apply(value);
        lf.addCallback(s -> complete(s), e -> error(e));
    }
}
