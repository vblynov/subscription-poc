package com.vbl.poc.subscription.processor.core;

public class Subscription implements Comparable<Subscription> {
    private final String name;
    private final int token;

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


    public void start() {

    }

    public void stop() {
    }

    @Override
    public int compareTo(Subscription o) {
        return name.compareTo(o.name);
    }
}
