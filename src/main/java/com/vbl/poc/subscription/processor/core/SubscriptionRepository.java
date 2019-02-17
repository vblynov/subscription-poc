package com.vbl.poc.subscription.processor.core;

import java.util.List;

/**
 * Repository for storing subscriptions
 */
public interface SubscriptionRepository {

    /**
     * Returns the list of all subscriptions. The list will contain no duplicates, but no assumption as for ordering should be made
     * @return un-ordered list of subscriptions
     */
    List<Subscription> getAllSubscriptions();

}
