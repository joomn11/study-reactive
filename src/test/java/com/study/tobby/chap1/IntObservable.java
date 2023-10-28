package com.study.tobby.chap1;

import java.util.Observable;

public class IntObservable extends Observable implements Runnable {

    @Override
    public void run() {
        for (int i = 1; i <= 10; i++) {
            setChanged();
            notifyObservers(i);
        }
    }
}
