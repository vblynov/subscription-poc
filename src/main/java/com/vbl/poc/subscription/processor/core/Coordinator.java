package com.vbl.poc.subscription.processor.core;

public interface Coordinator {

    void start(NodeInfo nodeInfo, TopologyListener listener);

    void stop();

}
