package ru.happyfat.integrity.trie;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import com.google.common.collect.Streams;

public class Node implements Serializable, Comparable<Node> {

    private Node parent = null;
    private Map<String, Node> children = new HashMap<>();
    private int level;

    private final FileInfo value;

    public Node(FileInfo value) {
        this.value = value;
    }

    public FileInfo getValue() {
        return value;
    }

    public void addChild(Node node) {
        node.addToParent(this);
        children.put(node.getValue().getName(), node);
    }

    private void addToParent(Node parent) {
        this.parent = parent;
        this.level = parent.level + 1;
    }

    public Node getParent() {
        return parent;
    }

    public int getLevel() {
        return level;
    }

    public Map<String, Node> getChildren() {
        return children;
    }

    public void setChildren(Map<String, Node> children) {
        this.children = children;
    }

    public Stream<Node> stream() {
        return Streams.concat(Stream.of(this), children.values().stream().flatMap(Node::stream));
    }

    @Override
    public int compareTo(Node o) {
        return getValue().getName().compareTo(o.getValue().getName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Node node = (Node) o;

        if (level != node.level) {
            return false;
        }
        return value != null ? value.equals(node.value) : node.value == null;
    }

    @Override
    public int hashCode() {
        int result = level;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }
}
