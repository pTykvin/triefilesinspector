package ru.happyfat.integrity.trie;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;

import de.vandermeer.asciitable.AsciiTable;
import de.vandermeer.asciitable.CWC_LongestWordMin;
import ru.happyfat.integrity.TrieUtils;

public class TrieDiff {

    enum REASON {
        CASH_HASNT,
        BUILD_HASNT,
        MODIFIED
    }

    public Trie merge(TrieSupport first, TrieSupport second) {

        Trie t1 = first.getTrie();
        Trie t2 = second.getTrie();

        Node n1 = t1.getRootNode();
        Node n2 = t2.getRootNode();
        brunchComparing(n1, n2);
        return null;
    }

    public boolean brunchComparing(Node fromBuild, Node fromCash) {



        Map<String, Node> flatBuild = fromBuild.getChildren().get("www").stream().collect(Collectors.toMap(TrieUtils::getPath, Function.identity()));
        Map<String, Node> flatCash = fromCash.getChildren().get("www").stream().collect(Collectors.toMap(TrieUtils::getPath, Function.identity()));

        //        Map<String, Node> fromBuildChildren = fromBuild.getChildren();
//        Map<String, Node> fromCashChildren = fromCash.getChildren();
//

        AsciiTable at = new AsciiTable();
        at.addRule();
        MapDifference<String, Node> build = Maps.difference(flatBuild, flatCash);
        build.entriesDiffering().forEach((s, nodeValueDifference) -> {
            FileInfo buildFile = nodeValueDifference.leftValue().getValue();
            FileInfo cashFile = nodeValueDifference.rightValue().getValue();
            at.addRow(
                s,
                buildFile.getMd5(),
                buildFile.getSize(),
                cashFile.getMd5(),
                cashFile.getSize());
            at.addRule();
        });
        build.entriesOnlyOnLeft().forEach((s, node) -> {
            at.addRow(s, Optional.ofNullable(node.getValue().getMd5()).orElse(""), node.getValue().getSize(), "", "");
        });

        build.entriesOnlyOnRight().forEach((s, node) -> {
            at.addRow(s, "", "", Optional.ofNullable(node.getValue().getMd5()).orElse(""), node.getValue().getSize());
        });

        at.getRenderer().setCWC(new CWC_LongestWordMin(new int[]{ 60, -1, -1, -1, -1 }));
        System.out.println(at.render());

        return false;
    }

//    private boolean compare(Node n1, Node n2, Node diffRoot) {
//        if (isEquals(n1, n2)) {
//            Set<Node> c1 = n1.getChildren();
//            Set<Node> c2 = n2.getChildren();
//            Sets.SetView<Node> diff = Sets.symmetricDifference(c1, c2);
//            if (!diff.isEmpty()) {
//                diffRoot.setChildren(diff);
//            }
//            Set<Node> intersection = Sets.intersection(c1, c2);
//            return !diff.isEmpty() || intersection.stream().filter(n -> n.getValue().isDirectory()).anyMatch(node -> {
//                boolean result = compare(
//                    c1.stream().filter(c -> c.equals(node)).findFirst().get(),
//                    c2.stream().filter(c -> c.equals(node)).findFirst().get(),
//                    node);
//                if (result) {
//                    diffRoot.addChild(node);
//                }
//                return result;
//            });
//        } else {
//            return true;
//        }
//    }

    private boolean isEquals(Node n1, Node n2) {
        return n1.getValue().equals(n2.getValue());
    }
}
