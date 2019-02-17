package com.vbl.poc.subscription.processor.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Subscription representation
 */
public class Subscription implements Comparable<Subscription> {
    private static final Logger LOG = LoggerFactory.getLogger(Subscription.class);

    private final String name;
    private AtomicBoolean running = new AtomicBoolean(false);

    public Subscription(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void start(String nodeName) {
        if (running.compareAndSet(false, true)) {
            LOG.info("{}: Subscription {} started", nodeName, name);
        } else {
            LOG.error("{}: Trying to start subscription {} which is already started", nodeName, name);
        }
    }

    public void stop(String nodeName) {
        if (running.compareAndSet(true, false)) {
            LOG.info("{}: Subscription {} stopped", nodeName, name);
        } else {
            LOG.error("{}: Trying to stop subscription {} which is already stopped", nodeName, name);
        }
    }

    @Override
    public int compareTo(Subscription o) {
        return name.compareTo(o.name);
    }
}
