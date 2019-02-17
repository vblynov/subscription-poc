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
        SUBSCRIPTIONS.put("A", new Subscription("A"));
        SUBSCRIPTIONS.put("B", new Subscription("B"));
        SUBSCRIPTIONS.put("C", new Subscription("C"));
        SUBSCRIPTIONS.put("D", new Subscription("D"));
        SUBSCRIPTIONS.put("E", new Subscription("E"));
        SUBSCRIPTIONS.put("F", new Subscription("F"));
        SUBSCRIPTIONS.put("G", new Subscription("G"));
        SUBSCRIPTIONS.put("H", new Subscription("H"));
        SUBSCRIPTIONS.put("I", new Subscription("I"));
        SUBSCRIPTIONS.put("J", new Subscription("J"));
        SUBSCRIPTIONS.put("K", new Subscription("K"));
        SUBSCRIPTIONS.put("L", new Subscription("L"));
        SUBSCRIPTIONS.put("M", new Subscription("M"));
        SUBSCRIPTIONS.put("N", new Subscription("N"));
        SUBSCRIPTIONS.put("O", new Subscription("O"));
        SUBSCRIPTIONS.put("P", new Subscription("P"));
        SUBSCRIPTIONS.put("Q", new Subscription("Q"));
        SUBSCRIPTIONS.put("R", new Subscription("R"));
        SUBSCRIPTIONS.put("S", new Subscription("S"));
        SUBSCRIPTIONS.put("T", new Subscription("T"));
    }

    @Override
    public List<Subscription> getAllSubscriptions() {
        return new ArrayList<>(SUBSCRIPTIONS.values());
    }

}
