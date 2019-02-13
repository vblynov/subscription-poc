package com.vbl.poc.subscription.processor.zoo;

import com.vbl.poc.subscription.processor.core.Coordinator;
import com.vbl.poc.subscription.processor.core.NodeInfo;
import com.vbl.poc.subscription.processor.core.TopologyListener;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

public class ZookeeperCoordinator implements Coordinator {
    private static final Logger LOG = LoggerFactory.getLogger(ZookeeperCoordinator.class);

    private static final String PARENT = "/servers";
    private static final String ZOOKEEPER_HOST = "localhost:2181";
    private ZooKeeper zooKeeper;
    private TopologyListener listener;
    private AtomicBoolean registered = new AtomicBoolean(false);

    public boolean register(NodeInfo nodeInfo, TopologyListener listener) {
        this.listener = listener;
        if (registered.compareAndSet(false, true)) {
            try {
                CountDownLatch waitForConnection = new CountDownLatch(1);
                zooKeeper = new ZooKeeper(ZOOKEEPER_HOST, 5000, new ConnectionWatcher(listener, waitForConnection));
                waitForConnection.await();
                zooKeeper.getChildren(PARENT, new TopologyWatcher(listener, zooKeeper));
                zooKeeper.create(PARENT + "/" + nodeInfo.getNodeName(), SerializationUtils.serialize(nodeInfo), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
                listener.onRegistered();
            } catch (IOException | InterruptedException | KeeperException e) {
                registered.set(false);
                e.printStackTrace();
            }
        }
        return registered.get();
    }

    public boolean unregister(NodeInfo nodeInfo) {
        if (registered.compareAndSet(true, false)) {
            try {
                listener.onUnregister();
                zooKeeper.close();
            } catch (InterruptedException e) {
                registered.set(true);
                e.printStackTrace();
            }
        }
        return !registered.get();
    }

    private static class ConnectionWatcher implements Watcher {
        private final TopologyListener listener;
        private final CountDownLatch waitConnectionLatch;

        ConnectionWatcher(TopologyListener listener, CountDownLatch waitConnectionLatch) {
            this.listener = listener;
            this.waitConnectionLatch = waitConnectionLatch;
        }

        public void process(WatchedEvent event) {
            if (event.getType() == Event.EventType.None) {
                if (event.getState() == Event.KeeperState.SyncConnected) {
                    waitConnectionLatch.countDown();
                } else if (event.getState() == Event.KeeperState.Disconnected || event.getState() == Event.KeeperState.Expired) {
                    listener.onUnregister();
                }
            }
        }
    }

    private static class TopologyWatcher implements Watcher {
        private final TopologyListener listener;
        private final ZooKeeper zooKeeper;

        TopologyWatcher(TopologyListener listener, ZooKeeper zooKeeper) {
            this.listener = listener;
            this.zooKeeper = zooKeeper;
        }

        @Override
        public void process(WatchedEvent event) {
            if (event.getType() == Event.EventType.NodeChildrenChanged) {
                try {
                    List<String> nodes = zooKeeper.getChildren(PARENT, this);
                    List<NodeInfo> groupInfo = new ArrayList<>(nodes.size());
                    for (String node : nodes) {
                        NodeInfo nodeInfo = SerializationUtils.deserialize(zooKeeper.getData(PARENT + "/" + node, false, new Stat()));
                        groupInfo.add(nodeInfo);
                    }
                    listener.topologyChanged(groupInfo);
                } catch (KeeperException.ConnectionLossException | KeeperException.SessionExpiredException ex) {
                    LOG.debug("Connection lost ", ex);
                } catch (KeeperException | InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }
}
