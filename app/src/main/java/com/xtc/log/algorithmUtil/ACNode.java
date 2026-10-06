package com.xtc.log.algorithmUtil;

import java.util.HashMap;
import java.util.Map;

/** One node of the Aho-Corasick automaton built by {@link ACAutomaton}. */
class ACNode {

    public char data;
    public ACNode fail;
    public boolean isEndingChar = false;
    public int length = -1;
    public Map<Character, ACNode> children = new HashMap<Character, ACNode>();

    ACNode() {
    }
}
