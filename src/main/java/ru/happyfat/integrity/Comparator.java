package ru.happyfat.integrity;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import ru.happyfat.integrity.trie.TrieDiff;
import ru.happyfat.integrity.trie.TrieSupport;

public class Comparator {
    public static void main(String[] args) {
/*
        Set<Number> test1 = new HashSet<>();
        Set<Number> test2 = new HashSet<>();
        test1.add(1);
        test1.add(2);
        test1.add(3);
        test2.add(3);
        test2.add(4);
        test2.add(5);
        Sets.SetView<Number> difference = com.google.common.collect.Sets.intersection(test2, test1);
        difference.iterator().forEachRemaining(System.out::println);
        */

        if (args.length < 2) {
            System.err.println("Define two .dat files for comparing");
            return;
        }
        Path firstPath = defDat(args[0]);
        Path secondPath = Paths.get(args[1]);
        if (firstPath == null || secondPath == null) {
            return;
        }
        TrieSupport firstTrie = TrieSupport.loadFrom(firstPath);
        TrieSupport secondTrie = TrieSupport.loadFrom(secondPath);

        new TrieDiff().merge(firstTrie, secondTrie);
    }

    private static Path defDat(String path) {
        Path datPath = Paths.get(path);
        if (!Files.exists(datPath)) {
            System.err.println(path + " not found");
            return null;
        }
        return datPath;
    }
}
