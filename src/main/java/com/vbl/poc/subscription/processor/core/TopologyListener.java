package com.vbl.poc.subscription.processor.core;

import com.vbl.poc.subscription.processor.core.NodeInfo;

import java.util.Collection;

public interface TopologyListener {

    void topologyChanged(Collection<NodeInfo> nodes);

    void onRegistered();

    void onUnregister();
}
