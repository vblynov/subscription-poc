package com.vbl.poc.subscription.processor.core;

import java.util.Collection;

/**
 * Interface to be implemented by distributed synchronization service provider (DSSP)
 * The instance of coordinator are not shared between nodes, coordinator is allowed to cache node's info
 */
public interface Coordinator {

    /**
     * Starts the coordinator service and registers the node inside DSSP. Method is asynchronous and is supposed to return immediately
     * Upon successfull registration node should be notified via {@link TopologyListener#topologyChanged(Collection)} method
     * @param nodeInfo contains node-specific data
     * @param listener listeners which provides callback for DSSP changes
     */
    void start(NodeInfo nodeInfo, TopologyListener listener);

    /**
     * Registers subscription as owned by the current node. Method is synchronous and blocks until registration is successful or error happened.
     * Node must not start the subscription unless it is successfully registered
     * @param subscription subscription to register
     */
    void registerSubscription(Subscription subscription);

    /**
     * Unregisteres currenltly owned subscription in DSSP. Method is synchronous and blocks until the subscription is unregistered or error happened
     * Node must gracefully stop subscription before calling this method
     * @param subscription subscription to unregister
     */
    void unregisterSubscription(Subscription subscription);

    /**
     * Stops coordination service, removing node from DSSP and closing connection to it. Implementation must call {@link TopologyListener#onDisconnected()} method
     */
    void stop();

}
