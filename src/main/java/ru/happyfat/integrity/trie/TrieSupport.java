package ru.happyfat.integrity.trie;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import ru.happyfat.integrity.ConsoleIO;
import ru.happyfat.integrity.TrieUtils;

public class TrieSupport {

    private final ConsoleIO console = ConsoleIO.I;
    private final Trie trie;
    private AtomicInteger counter;

    public TrieSupport(Path root) {
        trie = createTrie(root);
    }

    public TrieSupport(Trie trie) {
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
        result.setSize(file.length());
        return result;
    }

    public void md5Calculate() {
        AtomicInteger inspected = new AtomicInteger();
        TrieUtils.forEach(node -> {
            if (!node.getValue().isDirectory()) {
                String md5 = getMd5(new File(node.getValue().getPath()));
                node.getValue().setMd5(md5);
            }
            console.print("Computation of hashes " + (inspected.incrementAndGet() * 100) / counter.get() + "%");
        }, trie.getRootNode());
        console.printok("Computation of hashes");
    }

    private static String getMd5(File file) {
        try (FileInputStream fis = new FileInputStream(file)) {
            return DigestUtils.md5Hex(fis);
        } catch (Exception e) {
            return "";
            //throw new IllegalArgumentException("Processing file: " + file.getAbsolutePath(), e);
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


    public static TrieSupport loadFrom(Path rootPath) {
        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(rootPath))) {
            return new TrieSupport((Trie) ois.readObject());
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    public Trie getTrie() {
        return trie;
    }
}

