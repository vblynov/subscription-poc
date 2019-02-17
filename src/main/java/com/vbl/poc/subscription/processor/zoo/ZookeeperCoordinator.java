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
import java.util.concurrent.atomic.AtomicBoolean;

public class ZookeeperCoordinator implements Coordinator, Watcher {
    private static final Logger LOG = LoggerFactory.getLogger(ZookeeperCoordinator.class);
    private TopologyListener listener;
    private NodeInfo nodeInfo;

    private static final String PARENT = "/servers";
    private static final String ZOOKEEPER_HOST = "localhost:2181";

    private volatile ZooKeeper zooKeeper;
    private AtomicBoolean running = new AtomicBoolean(false);

    public void start(NodeInfo nodeInfo, TopologyListener listener) {
        if (running.compareAndSet(false, true)) {
            this.listener = listener;
            this.nodeInfo = nodeInfo;
            try {
                zooKeeper = new ZooKeeper(ZOOKEEPER_HOST, 5000, this);
            } catch (IOException e) {
                LOG.error("Exception throw ", e);
                running.set(false);
            }
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
                    zooKeeper.getChildren(PARENT, this);
                    zooKeeper.create(PARENT + "/" + nodeInfo.getNodeName(), SerializationUtils.serialize(nodeInfo), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
                } else if (event.getState() == Event.KeeperState.Expired) {
                    stop();
                }
            } else if (event.getType() == Event.EventType.NodeChildrenChanged) {
                List<String> nodes = zooKeeper.getChildren(PARENT, this);
                List<NodeInfo> groupInfo = new ArrayList<>(nodes.size());
                for (String node : nodes) {
                    NodeInfo nodeInfo = SerializationUtils.deserialize(zooKeeper.getData(PARENT + "/" + node, false, new Stat()));
                    groupInfo.add(nodeInfo);
                }
                listener.topologyChanged(groupInfo);

            }
        } catch (KeeperException.ConnectionLossException | KeeperException.SessionExpiredException ex) {
            stop();
        } catch (KeeperException | InterruptedException ex) {
            LOG.error("Exception thrown {}", ex);
        }
    }
}
