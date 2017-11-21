package ru.happyfat.integrity.trie;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Node implements Serializable {

    private Node parent = null;
    private Set<Node> children = new HashSet<>();
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
        children.add(node);
    }

    private void addToParent(Node parent) {
        this.parent = parent;
        this.level = parent.level + 1;
    }

    public void print() {
        String p = StringUtils.repeat("..", level) + value;
        if (!value.isDirectory()) {
            System.out.println(p + StringUtils.leftPad(value.getMd5(), 110 - p.length()));
        } else {
            System.out.println(p);
        }
        children.forEach(Node::print);
    }


    public long count() {
        return 1 + children.stream().collect(Collectors.summarizingLong(Node::count)).getSum();
    }

    public void forEach(Consumer<Node> consumer) {
        consumer.accept(this);
        children.forEach(node -> node.forEach(consumer));
    }
}
