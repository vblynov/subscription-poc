package com.vbl.poc.subscription.processor.core;

import java.util.concurrent.atomic.AtomicBoolean;

public class Subscription implements Comparable<Subscription> {
    private final String name;
    private final int token;
    private AtomicBoolean running = new AtomicBoolean(false);

    public Subscription(String name, int token) {
        this.name = name;
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public int getToken() {
        return token;
    }

    public boolean isRunning() {
        return running.get();
    }

    public boolean start() {
        if (running.compareAndSet(false, true)) {
            System.out.println(String.format("Subscription %s wit token %s is started", name, token));
            return true;
        } else {
            System.out.println(String.format("Subscription %s is already running", name));
        }
        return false;
    }

    public boolean stop() {
        if (running.compareAndSet(false, true)) {
            System.out.println(String.format("Subscription %s wit token %s is stopped", name, token));
            return true;
        } else {
            System.out.println(String.format("Subscription %s is already stopped", name));
        }
        return false;
    }

    @Override
    public int compareTo(Subscription o) {
        return name.compareTo(o.name);
    }
}
