package com.vbl.poc.subscription.processor.zoo;

import com.vbl.poc.subscription.processor.core.Coordinator;
import com.vbl.poc.subscription.processor.core.NodeInfo;
import com.vbl.poc.subscription.processor.core.TopologyListener;
import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class ZookeeperCoordinator implements Coordinator {
    private static final String PARENT = "/servers/";
    private static final String ZOOKEEPER_HOST = "localhost:2181";
    private ZooKeeper zooKeeper;
    private TopologyListener listener;

    public synchronized boolean register(NodeInfo nodeInfo, TopologyListener listener) {
        this.listener = listener;
        try {
            CountDownLatch waitForConnection = new CountDownLatch(1);
            zooKeeper = new ZooKeeper(ZOOKEEPER_HOST, 5000, new ZookeeperWatcher(listener, waitForConnection));
            waitForConnection.await();
            zooKeeper.create(PARENT + nodeInfo.getNodeName(), new byte[1], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            listener.onRegistered();
        } catch (IOException | InterruptedException | KeeperException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    public synchronized boolean unregister(NodeInfo nodeInfo) {
        try {
            listener.onUnregister();
            zooKeeper.close();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    private static class ZookeeperWatcher implements Watcher {
        private final TopologyListener listener;
        private final CountDownLatch waitConnectionLatch;

        ZookeeperWatcher(TopologyListener listener, CountDownLatch waitConnectionLatch) {
            this.listener = listener;
            this.waitConnectionLatch = waitConnectionLatch;
        }

        public void process(WatchedEvent event) {
            if (event.getType() == Event.EventType.None) {
                // connection events
                if (event.getState() == Event.KeeperState.SyncConnected) {
                    waitConnectionLatch.countDown();
                } else if (event.getState() == Event.KeeperState.Disconnected || event.getState() == Event.KeeperState.Expired) {
                    listener.onUnregister();
                }
            } else {
                // node state events here
            }
        }
    }
}
