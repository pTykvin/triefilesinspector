package ru.happyfat.integrity.trie;

import java.io.Serializable;

public class Trie implements Serializable {
    private final Node rootNode;

    Trie(Node rootNode) {
        this.rootNode = rootNode;
    }

    public Node getRootNode() {
        return rootNode;
    }
}
