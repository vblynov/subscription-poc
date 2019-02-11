package com.vbl.poc.subscription.processor.repo;

import com.vbl.poc.subscription.processor.core.Subscription;
import com.vbl.poc.subscription.processor.core.SubscriptionRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemorySubscriptionRepository implements SubscriptionRepository {

    private static Map<String, Subscription> SUBSCRIPTIONS = new HashMap<>();

    static {
        SUBSCRIPTIONS.put("A", new Subscription("A", 0));
        SUBSCRIPTIONS.put("B", new Subscription("B", 0));
        SUBSCRIPTIONS.put("C", new Subscription("C", 0));
        SUBSCRIPTIONS.put("D", new Subscription("D", 0));
        SUBSCRIPTIONS.put("E", new Subscription("E", 0));
        SUBSCRIPTIONS.put("F", new Subscription("F", 0));
        SUBSCRIPTIONS.put("G", new Subscription("G", 0));
        SUBSCRIPTIONS.put("H", new Subscription("H", 0));
        SUBSCRIPTIONS.put("I", new Subscription("I", 0));
        SUBSCRIPTIONS.put("J", new Subscription("J", 0));
        SUBSCRIPTIONS.put("K", new Subscription("K", 0));
        SUBSCRIPTIONS.put("L", new Subscription("L", 0));
        SUBSCRIPTIONS.put("M", new Subscription("M", 0));
        SUBSCRIPTIONS.put("N", new Subscription("N", 0));
        SUBSCRIPTIONS.put("O", new Subscription("O", 0));
        SUBSCRIPTIONS.put("P", new Subscription("P", 0));
        SUBSCRIPTIONS.put("Q", new Subscription("Q", 0));
        SUBSCRIPTIONS.put("R", new Subscription("R", 0));
        SUBSCRIPTIONS.put("S", new Subscription("S", 0));
        SUBSCRIPTIONS.put("T", new Subscription("T", 0));
    }

    @Override
    public List<Subscription> getAllSubscriptions() {
        return new ArrayList<>(SUBSCRIPTIONS.values());
    }

    @Override
    public void updateSubscription(Subscription subscription) {
        SUBSCRIPTIONS.put(subscription.getName(), subscription);
    }
}
