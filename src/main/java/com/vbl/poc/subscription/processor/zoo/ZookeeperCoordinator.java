package com.vbl.poc.subscription.processor.zoo;

import com.vbl.poc.subscription.processor.core.Coordinator;
import com.vbl.poc.subscription.processor.core.NodeInfo;
import com.vbl.poc.subscription.processor.core.TopologyListener;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class ZookeeperCoordinator implements Coordinator {

    private static final String ZOOKEEPER_HOST = "localhost:2181";
    private ZooKeeper zooKeeper;

    public synchronized boolean register(NodeInfo nodeInfo, TopologyListener listener) {
        try {
            CountDownLatch waitForConnection = new CountDownLatch(1);
            zooKeeper = new ZooKeeper(ZOOKEEPER_HOST, 1000, new ZookeeperWatcher(listener, waitForConnection));
            waitForConnection.await();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    public synchronized boolean unregister(NodeInfo nodeInfo) {
        try {
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
            if (event.getState() == Event.KeeperState.SyncConnected) {
                waitConnectionLatch.countDown();
            } else if (event.getState() == Event.KeeperState.Disconnected || event.getState() == Event.KeeperState.Expired) {
                listener.onUnregister();
            }
        }
    }
}
