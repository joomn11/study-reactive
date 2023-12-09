package com.study.comple;

import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;

@Slf4j
public class ErrorCompletion extends Completion {

    public Consumer<Throwable> econ;

    public ErrorCompletion(Consumer<Throwable> econ) {
        this.econ = econ;
    }

    @Override
    public void run(ResponseEntity<String> value) {
        if (next != null) {
            next.run(value);
        }
    }

    @Override
    public void error(Throwable e) {
        econ.accept(e);
    }
}
