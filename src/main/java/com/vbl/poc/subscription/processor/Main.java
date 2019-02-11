package com.vbl.poc.subscription.processor;

import com.vbl.poc.subscription.processor.core.SubscriptionProcessor;
import com.vbl.poc.subscription.processor.core.SubscriptionRepository;
import com.vbl.poc.subscription.processor.repo.InMemorySubscriptionRepository;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        SubscriptionRepository repository = new InMemorySubscriptionRepository();
        SubscriptionProcessor[] processors = new SubscriptionProcessor[3];
        processors[0] = new SubscriptionProcessor("Node1", repository);
        processors[1] = new SubscriptionProcessor("Node2", repository);
        processors[2] = new SubscriptionProcessor("Node3", repository);

        for (SubscriptionProcessor processor : processors) {
            new Thread(processor).start();
        }

        System.in.read();

        for (SubscriptionProcessor processor : processors) {
            processor.stop();
        }
    }

}
