package ru.happyfat.integrity;

import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;

import ru.happyfat.integrity.trie.FileInfo;
import ru.happyfat.integrity.trie.Node;
import ru.happyfat.integrity.trie.Trie;

public class TrieUtils {

    public static void forEach(Consumer<Node> consumer, Node node) {
        consumer.accept(node);
        node.getChildren().forEach((name, node1) -> forEach(consumer, node1));
    }

    public static void print(Trie trie) {
        forEach(node -> {
            FileInfo value = node.getValue();
            String p = StringUtils.repeat("..", node.getLevel()) + value;
            if (!value.isDirectory()) {
                System.out.println(p + StringUtils.leftPad(value.getMd5(), 110 - p.length()));
            } else {
                System.out.println(p);
            }
        }, trie.getRootNode());
    }

    public static String getPath(Node node) {
        return getPathRecursive(node, null);
    }

    private static String getPathRecursive(Node node, String currentName) {
        String name;
        String nodeName = node.getValue().getName();
        name = currentName == null ? nodeName : nodeName + "/" + currentName;
        Node parent = node.getParent();
        if (parent != null) {
            return getPathRecursive(parent, name);
        } else {
            return "./" + name;
        }
    }
}
