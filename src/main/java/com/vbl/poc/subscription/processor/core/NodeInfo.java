package com.vbl.poc.subscription.processor.core;

import java.io.Serializable;

/**
 * The container for node-specific data. Must include at list node name, unique among the nodes
 */
public class NodeInfo implements Comparable<NodeInfo>, Serializable {

    private final String nodeName;

    NodeInfo(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getNodeName() {
        return nodeName;
    }

    @Override
    public int compareTo(NodeInfo o) {
        return nodeName.compareTo(o.nodeName);
    }
}
