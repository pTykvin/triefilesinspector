package ru.happyfat.integrity;

import org.apache.commons.lang3.StringUtils;
import ru.happyfat.integrity.trie.TrieSupport;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Optional;

public class Scanner {

    private final ConsoleIO console = ConsoleIO.I;

    public static void main(String[] args) {
        try {
            TrieSupport trieSupport = new Scanner().scan(args);
            Optional.ofNullable(trieSupport).ifPresent(support -> support.saveToFile("scan_result.dat"));
        } catch (Exception e) {
            System.out.println();
            e.printStackTrace();
        } finally {
            ConsoleIO.I.pressEnterToContinue();
        }
    }

    private TrieSupport scan(String[] args) {
        console.printHeader();
        String root = defRoot(args);
        Path rootPath = Paths.get(root);
        if (Files.exists(rootPath)) {
            if (rootPath.getFileName().toString().endsWith(".dat")) {
                TrieSupport ts = TrieSupport.loadFrom(rootPath);
                TrieUtils.print(ts.getTrie());
                return null;
            }
            TrieSupport trieSupport = new TrieSupport(rootPath);
            trieSupport.md5Calculate();
            return trieSupport;
        }
        System.out.println("Root not found");
        return null;
    }

    private String defRoot(String[] args) {
        String path;
        if (args.length > 0) {
            path = args[0];
        } else {
            path = console.input("Define root for scanning or .dat file for print result. Default [.]: ");
            if (StringUtils.isEmpty(path)) {
                path = ".";
            }
        }
        return path;
    }
}
