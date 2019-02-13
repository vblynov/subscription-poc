package com.vbl.poc.subscription.processor.core;

import java.io.Serializable;

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
