package com.vbl.poc.subscription.processor.core;

public interface Coordinator {

    boolean register(NodeInfo nodeInfo, TopologyListener listener);

    boolean unregister(NodeInfo nodeInfo);

}
