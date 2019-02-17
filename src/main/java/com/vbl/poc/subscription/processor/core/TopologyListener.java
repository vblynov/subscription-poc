package com.vbl.poc.subscription.processor.core;

import java.util.Collection;

public interface TopologyListener {

    void topologyChanged(Collection<NodeInfo> nodes);

    void onDisconnected();
}
