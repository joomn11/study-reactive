package com.study.comple;

import java.util.function.Consumer;
import org.springframework.http.ResponseEntity;

public class AcceptCompletion extends Completion {

    public Consumer<ResponseEntity<String>> con;

    public AcceptCompletion(Consumer<ResponseEntity<String>> con) {
        this.con = con;
    }

    @Override
    public void run(ResponseEntity<String> value) {
        con.accept(value);
    }
}
