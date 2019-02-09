package com.vbl.poc.subscription.processor.core;

import java.util.concurrent.atomic.AtomicBoolean;

public class Subscription {
    private final String name;
    private final String token;
    private AtomicBoolean running = new AtomicBoolean(false);

    public Subscription(String name, String token) {
        this.name = name;
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public String getToken() {
        return token;
    }

    public boolean isRunning() {
        return running.get();
    }

    public boolean start() {
        return running.compareAndSet(false, true);
    }

    public boolean stop() {
        return running.compareAndSet(true, false);
    }
}
