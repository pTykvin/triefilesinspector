package ru.happyfat.integrity.trie;

import org.apache.commons.codec.digest.DigestUtils;
import ru.happyfat.integrity.ConsoleIO;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class TrieSupport {

    private final ConsoleIO console = ConsoleIO.I;
    private final Trie trie;
    private AtomicInteger counter;

    public TrieSupport(Path root) {
        trie = createTrie(root);
    }

    private TrieSupport(Trie trie) {
        this.trie = trie;
    }

    private Trie createTrie(Path root) {
        try {
            FileInfo value = byPath(root);
            Node rootNode = new Node(value);
            counter = new AtomicInteger();
            return new Trie(recursiveCreate(rootNode));
        } finally {
            console.printok("Search files");
        }
    }

    private Node recursiveCreate(Node rootNode) {
        Path root = Paths.get(rootNode.getValue().getPath());
        getStream(root)
                .map(this::byPath)
                .map(Node::new)
                .forEach(node -> {
                    rootNode.addChild(node);
                    console.print("Search files " + counter.incrementAndGet());
                    if (node.getValue().isDirectory()) {
                        recursiveCreate(node);
                    }
                });
        return rootNode;
    }

    private static Stream<Path> getStream(Path root) {
        try {
            return StreamSupport.stream(Files.newDirectoryStream(root).spliterator(), true);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private FileInfo byPath(Path path) {
        FileInfo result = new FileInfo();
        File file = path.toFile();
        result.setDirectory(file.isDirectory());
        result.setName(path.getFileName().toString());
        result.setPath(path.toString());
        return result;
    }


    public void md5Calculate() {
        AtomicInteger inspected = new AtomicInteger();
        trie.getRootNode().forEach(node -> {
            if (!node.getValue().isDirectory()) {
                String md5 = getMd5(new File(node.getValue().getPath()));
                node.getValue().setMd5(md5);
            }
            console.print("Computation of hashes " + (inspected.incrementAndGet() * 100) / counter.get() + "%");
        });
        console.printok("Computation of hashes");
    }

    private static String getMd5(File file) {
        try (FileInputStream fis = new FileInputStream(file)) {
            return DigestUtils.md5Hex(fis);
        } catch (IOException e) {
            throw new IllegalArgumentException("Processing file: " + file.getAbsolutePath(), e);
        }
    }

    public void saveToFile(String dest) {
        try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(Paths.get(dest)))) {
            console.print("Saving to " + dest + "");
            oos.writeObject(trie);
            console.printok("Saving to " + dest + "");
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }

    }

    public void print() {
        trie.getRootNode().print();
    }

    public static TrieSupport loadFrom(Path rootPath) {
        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(rootPath))) {
            return new TrieSupport((Trie) ois.readObject());
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }
}

