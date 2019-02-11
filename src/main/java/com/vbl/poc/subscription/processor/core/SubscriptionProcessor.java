package com.vbl.poc.subscription.processor.core;

import com.vbl.poc.subscription.processor.zoo.ZookeeperCoordinator;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class SubscriptionProcessor implements Runnable {
    private final NodeInfo nodeInfo;
    private final Coordinator coordinator;
    private final SubscriptionRepository subscriptionRepository;
    private final CountDownLatch shutdownLatch = new CountDownLatch(1);

    private ArrayList<Subscription> activeSubscriptions;


    public SubscriptionProcessor(String nodeName, SubscriptionRepository subscriptionRepository) {
        this.nodeInfo = new NodeInfo(nodeName);
        this.coordinator = new ZookeeperCoordinator();
        this.subscriptionRepository = subscriptionRepository;
    }

    public void run() {
        boolean registered = coordinator.register(nodeInfo, new GroupListener());
        if (registered) {
            try {
                shutdownLatch.await();
                coordinator.unregister(nodeInfo);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        shutdownLatch.countDown();
    }

    private class GroupListener implements TopologyListener {

        @Override
        public void nodeAdded(NodeInfo nodeInfo) {

        }

        @Override
        public void nodeRemoved(NodeInfo nodeInfo) {

        }

        @Override
        public void onRegistered() {
            System.out.println(String.format("%s : Node registered", nodeInfo.getNodeName()));
        }

        @Override
        public void onUnregister() {
            System.out.println(String.format("%s : Node unregistered", nodeInfo.getNodeName()));
        }
    }

}
