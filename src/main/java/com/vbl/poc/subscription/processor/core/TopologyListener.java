package com.vbl.poc.subscription.processor.core;

import java.util.Collection;

/**
 * Listener which is notified by coordinator about connection and topology changes
 */
public interface TopologyListener {
    /**
     * Notification about topology changes (nodes connecting and disconnecting)
     * @param nodes nodes, that are detected as active
     */
    void topologyChanged(Collection<NodeInfo> nodes);

    /**
     * Connection to distributed synchronization service provider is down
     */
    void onDisconnected();
}
