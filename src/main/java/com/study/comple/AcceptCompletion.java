package com.study.comple;

import java.util.function.Consumer;

public class AcceptCompletion<S> extends Completion<S, Void> {

    public Consumer<S> con;

    public AcceptCompletion(Consumer<S> con) {
        this.con = con;
    }

    @Override
    public void run(S value) {
        con.accept(value);
    }
}
