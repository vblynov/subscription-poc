package com.vbl.poc.subscription.processor.zoo;

import com.vbl.poc.subscription.processor.core.*;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Coordinator implemetation for Zookeeper
 */
public class ZookeeperCoordinator implements Coordinator, Watcher {
    private static final Logger LOG = LoggerFactory.getLogger(ZookeeperCoordinator.class);
    private TopologyListener listener;
    private NodeInfo nodeInfo;

    private static final String PARENT = "/servers";
    private static final String SUBS = "/subs";
    private static final String ZOOKEEPER_HOST = "localhost:2181";
    private static final int DEFAULT_SESSION_TIMEOUT = 5000;

    private volatile ZooKeeper zooKeeper;
    private AtomicBoolean running = new AtomicBoolean(false);

    public void start(NodeInfo nodeInfo, TopologyListener listener) {
        if (running.compareAndSet(false, true)) {
            this.listener = listener;
            this.nodeInfo = nodeInfo;
            try {
                zooKeeper = new ZooKeeper(ZOOKEEPER_HOST, DEFAULT_SESSION_TIMEOUT, this);
            } catch (IOException e) {
                LOG.error("Exception throw ", e);
                running.set(false);
            }
        } else {
            throw new CoordinatorException("Coordinator is already running");
        }
    }

    @Override
    public void registerSubscription(Subscription subscription) {
        if (running.get()) {
            try {
                String path = createNodeIfAbsent(subscription);
                List<String> children = zooKeeper.getChildren(path, false);
                while (!children.isEmpty() && !nodeInfo.getNodeName().equals(children.get(0))) {
                    Thread.sleep(10);
                    children = zooKeeper.getChildren(path, false);
                }
                if (children.isEmpty()) {
                    zooKeeper.create(path + "/" + nodeInfo.getNodeName(), new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
                }

            } catch (KeeperException | InterruptedException ex) {
                throw new CoordinatorException(ex);
            }
        } else {
            throw new CoordinatorException("Coordinator is not running");
        }
    }

    @Override
    public void unregisterSubscription(Subscription subscription) {
        if (running.get()) {
            try {
                String path = createNodeIfAbsent(subscription);
                List<String> children = zooKeeper.getChildren(path, false);
                if (!children.isEmpty() && nodeInfo.getNodeName().equals(children.get(0))) {
                    zooKeeper.delete(path + "/" +nodeInfo.getNodeName(), -1);
                }
            } catch (KeeperException | InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        } else {
            throw new CoordinatorException("Coordinator is not running");
        }
    }

    public void stop() {
        if (running.compareAndSet(true, false)) {
            try {
                listener.onDisconnected();
                zooKeeper.close();
            } catch (InterruptedException e) {
                LOG.error("Exception thrown ", e);
            }
        }
    }

    @Override
    public void process(WatchedEvent event) {
        try {
            if (event.getType() == Event.EventType.None) {
                if (event.getState() == Event.KeeperState.SyncConnected) {
                    List<String> children = zooKeeper.getChildren(PARENT, this);
                    if (!children.contains(nodeInfo.getNodeName())) {
                        zooKeeper.create(PARENT + "/" + nodeInfo.getNodeName(), SerializationUtils.serialize(nodeInfo), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
                    } else {
                        notifyTopologyChange(children);
                    }
                } else if (event.getState() == Event.KeeperState.Expired) {
                    stop();
                } else if (event.getState() == Event.KeeperState.Disconnected) {
                    listener.onDisconnected();
                }
            } else if (event.getType() == Event.EventType.NodeChildrenChanged) {
                notifyTopologyChange(zooKeeper.getChildren(PARENT, this));
            }
        } catch (KeeperException.ConnectionLossException | KeeperException.SessionExpiredException ex) {
            stop();
        } catch (KeeperException | InterruptedException ex) {
            LOG.error("Exception thrown {}", ex);
        }
    }

    private void notifyTopologyChange(List<String> nodes) throws KeeperException, InterruptedException {
        List<NodeInfo> groupInfo = new ArrayList<>(nodes.size());
        for (String node : nodes) {
            NodeInfo nodeInfo = SerializationUtils.deserialize(zooKeeper.getData(PARENT + "/" + node, false, new Stat()));
            groupInfo.add(nodeInfo);
        }
        listener.topologyChanged(groupInfo);
    }

    private String createNodeIfAbsent(Subscription subscription) {
        String path = SUBS + "/" + subscription.getName();
        try {
            if (zooKeeper.exists(SUBS + "/" + subscription.getName(), false) == null) {
                zooKeeper.create(path, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        } catch (KeeperException.NodeExistsException ex) {
            LOG.info("Node already created");
        } catch (InterruptedException | KeeperException ex) {
            throw new RuntimeException(ex);
        }
        return path;
    }
}
