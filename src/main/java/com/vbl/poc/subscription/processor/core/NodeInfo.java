package com.vbl.poc.subscription.processor.core;

public class NodeInfo implements Comparable<NodeInfo> {
    private final String nodeName;

    public NodeInfo(String nodeName) {
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
