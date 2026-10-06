package com.xtc.log.algorithmUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Aho-Corasick automaton used to spot sensitive keywords inside a log message.
 *
 * <p>Built once from the device keyword list, then queried for every record the
 * {@code InterceptLogger} sees.
 */
public class ACAutomaton {

    private ACNode root = new ACNode();

    /** Inserts every pattern, then wires the failure links breadth-first. */
    public void buildACAutomaton(String[] patterns) {
        for (String pattern : patterns) {
            ACNode node = this.root;
            for (char c : pattern.toCharArray()) {
                if (!node.children.containsKey(Character.valueOf(c))) {
                    ACNode child = new ACNode();
                    child.data = c;
                    node.children.put(Character.valueOf(c), child);
                }
                node = node.children.get(Character.valueOf(c));
            }
            node.isEndingChar = true;
            node.length = pattern.length();
        }
        LinkedList<ACNode> queue = new LinkedList<ACNode>();
        for (ACNode child : this.root.children.values()) {
            child.fail = this.root;
            queue.add(child);
        }
        while (!queue.isEmpty()) {
            ACNode current = queue.poll();
            for (ACNode child : current.children.values()) {
                char c = child.data;
                ACNode fail = current.fail;
                while (fail != null && !fail.children.containsKey(Character.valueOf(c))) {
                    fail = fail.fail;
                }
                if (fail == null) {
                    child.fail = this.root;
                } else {
                    child.fail = fail.children.get(Character.valueOf(c));
                }
                queue.add(child);
            }
        }
    }

    /** Returns every pattern occurrence found inside {@code text}. */
    public List<String> searchPatterns(String text) {
        ArrayList<String> matches = new ArrayList<String>();
        ACNode node = this.root;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            while (node != this.root && !node.children.containsKey(Character.valueOf(c))) {
                node = node.fail;
            }
            if (node.children.containsKey(Character.valueOf(c))) {
                node = node.children.get(Character.valueOf(c));
            }
            for (ACNode match = node; match != this.root; match = match.fail) {
                if (match.isEndingChar) {
                    matches.add(text.substring((i - match.length) + 1, i + 1));
                }
            }
        }
        return matches;
    }
}
