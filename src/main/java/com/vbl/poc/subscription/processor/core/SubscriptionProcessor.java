package com.vbl.poc.subscription.processor.core;

import com.vbl.poc.subscription.processor.zoo.ZookeeperCoordinator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.stream.Collectors;

public class SubscriptionProcessor implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(SubscriptionProcessor.class);

    private final NodeInfo nodeInfo;
    private final Coordinator coordinator;
    private final SubscriptionRepository subscriptionRepository;
    private final CyclicBarrier shutdownLatch = new CyclicBarrier(2);

    private ArrayList<Subscription> activeSubscriptions;


    public SubscriptionProcessor(String nodeName, SubscriptionRepository subscriptionRepository) {
        this.nodeInfo = new NodeInfo(nodeName);
        this.coordinator = new ZookeeperCoordinator();
        this.subscriptionRepository = subscriptionRepository;
    }

    public void run() {
        shutdownLatch.reset();
        boolean registered = coordinator.register(nodeInfo, new GroupListener());
        if (registered) {
            try {
                shutdownLatch.await();
                coordinator.unregister(nodeInfo);
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        try {
            shutdownLatch.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return nodeInfo.getNodeName();
    }

    private class GroupListener implements TopologyListener {

        @Override
        public void topologyChanged(Collection<NodeInfo> nodes) {
            String nodeGroup = nodes.stream().map(NodeInfo::getNodeName).collect(Collectors.joining(", "));
            LOG.info("{} observes topology change: {}", nodeInfo.getNodeName(), nodeGroup);
        }

        @Override
        public void onRegistered() {
            LOG.info("{} registered", nodeInfo.getNodeName());
        }

        @Override
        public void onUnregister() {
            LOG.info("{} unregistered", nodeInfo.getNodeName());
        }
    }

}
