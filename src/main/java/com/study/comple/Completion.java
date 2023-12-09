package com.study.comple;

import java.util.function.Consumer;
import java.util.function.Function;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;

@NoArgsConstructor
@Slf4j
public class Completion {


    public Completion next;

    public static Completion from(ListenableFuture<ResponseEntity<String>> lf) {
        Completion c = new Completion();
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

    public void complete(ResponseEntity<String> s) {
        if (next != null) {
            next.run(s);
        }
    }

    public void run(ResponseEntity<String> value) {
    }

    public void andAccept(Consumer<ResponseEntity<String>> con) {
        Completion c = new AcceptCompletion(con);
        this.next = c;
    }

    public Completion andError(Consumer<Throwable> econ) {
        Completion c = new ErrorCompletion(econ);
        this.next = c;
        return c;
    }

    public Completion andApply(Function<ResponseEntity<String>, ListenableFuture<ResponseEntity<String>>> fn) {
        Completion c = new ApplyCompletion(fn);
        this.next = c;
        return c;
    }
}
