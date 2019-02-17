package com.vbl.poc.subscription.processor.core;

import com.vbl.poc.subscription.processor.zoo.ZookeeperCoordinator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.stream.Collectors;

public class SubscriptionProcessor implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(SubscriptionProcessor.class);
    private static final int DEGREES = 360;

    private final NodeInfo nodeInfo;
    private final Coordinator coordinator;
    private final SubscriptionRepository subscriptionRepository;
    private final CyclicBarrier shutdownLatch = new CyclicBarrier(2);

    private ArrayList<Subscription> activeSubscriptions = new ArrayList<>();


    public SubscriptionProcessor(String nodeName, SubscriptionRepository subscriptionRepository) {
        this.nodeInfo = new NodeInfo(nodeName);
        this.coordinator = new ZookeeperCoordinator();
        this.subscriptionRepository = subscriptionRepository;
    }

    public void run() {
        shutdownLatch.reset();
        coordinator.start(nodeInfo, new GroupListener());
        try {
            shutdownLatch.await();
            coordinator.stop();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
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

    private void assignSubscriptions(List<NodeInfo> activeNodes) {
        LOG.info("{} starts subscription assignment", nodeInfo.getNodeName());
        List<Subscription> existingSubscriptions = subscriptionRepository.getAllSubscriptions();
        Collections.sort(existingSubscriptions);
        Collections.sort(activeNodes);

        int subscriptionDelta = DEGREES / existingSubscriptions.size();
        int nodesDelta = (DEGREES / activeNodes.size()) + 1;

        int currentNodeIndex = Collections.binarySearch(activeNodes, nodeInfo);
        int minThreshold = currentNodeIndex * nodesDelta;
        int maxThreshold = Math.min((currentNodeIndex + 1) * nodesDelta, DEGREES);

        int startIndex = (int) Math.ceil((double) minThreshold / (double) subscriptionDelta);
        int endIndex = Math.min(existingSubscriptions.size() - 1, (int) Math.floor((double) maxThreshold / (double) subscriptionDelta));

        List<Subscription> assignedSubscriptions = new ArrayList<>();
        for (int i = startIndex; i <= endIndex; i++) {
            assignedSubscriptions.add(existingSubscriptions.get(i));
        }

        int i = 0;
        int j = 0;
        List<Subscription> toStart = new ArrayList<>();
        List<Subscription> toStop = new ArrayList<>();
        while (i < activeSubscriptions.size() || j < assignedSubscriptions.size()) {
            if (i >= activeSubscriptions.size()) {
                toStart.add(assignedSubscriptions.get(j++));
            } else if (j >= assignedSubscriptions.size()) {
                toStop.add(activeSubscriptions.get(i++));
            } else {
                Subscription activeSubscription = activeSubscriptions.get(i);
                Subscription assignedSubscription = assignedSubscriptions.get(j);
                int compareResult = activeSubscription.compareTo(assignedSubscription);
                if (compareResult < 0) {
                    toStop.add(activeSubscription);
                    i++;
                } else if (compareResult > 0) {
                    toStart.add(assignedSubscription);
                    j++;
                } else {
                    i++;
                    j++;
                }
            }
        }
        for (Subscription subscription : toStop) {
            subscription.stop();

        }
        for (Subscription subscription : toStart) {
            subscription.start();
        }
        activeSubscriptions = new ArrayList<>(assignedSubscriptions);
        String subs = activeSubscriptions.stream().map(Subscription::getName).collect(Collectors.joining(","));
        LOG.info("{} have subscriptions {}", nodeInfo.getNodeName(), subs);
    }

    private class GroupListener implements TopologyListener {

        @Override
        public void topologyChanged(Collection<NodeInfo> nodes) {
            String nodeGroup = nodes.stream().map(NodeInfo::getNodeName).collect(Collectors.joining(", "));
            List<NodeInfo> nodesList = new ArrayList<>(nodes);
            LOG.info("{} observes topology change: {}", nodeInfo.getNodeName(), nodeGroup);
            assignSubscriptions(nodesList);
        }

        @Override
        public void onDisconnected() {
            LOG.info("{} disconnected", nodeInfo.getNodeName());
        }
    }

}
