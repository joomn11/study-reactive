package com.study.comple;

import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ErrorCompletion<T> extends Completion<T, T> {

    public Consumer<Throwable> econ;

    public ErrorCompletion(Consumer<Throwable> econ) {
        this.econ = econ;
    }

    @Override
    public void run(T value) {
        if (next != null) {
            next.run(value);
        }
    }

    @Override
    public void error(Throwable e) {
        econ.accept(e);
    }
}
