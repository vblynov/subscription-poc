package com.vbl.poc.subscription.processor.core;

import java.util.List;

public interface SubscriptionRepository {

    List<Subscription> getAllSubscriptions();

    void updateSubscription(Subscription subscription);

}
