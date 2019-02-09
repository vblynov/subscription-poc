package com.vbl.poc.subscription.processor.core;

import com.vbl.poc.subscription.processor.core.NodeInfo;

public interface TopologyListener {

    void nodeAdded(NodeInfo nodeInfo);

    void nodeRemoved(NodeInfo nodeInfo);

    void onRegistered();

    void onUnregister();
}
