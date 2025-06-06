package com.leetcode.practice.trees;

import java.sql.SQLOutput;

public class TrieImpl {

    public static void main(String[] args) {

        Trie trie = new Trie();

        trie.insert("apple");
        System.out.println(trie.search("apple"));
        System.out.println(trie.search("app"));
        System.out.println(trie.startsWith("app"));
        trie.insert("app");
        System.out.println(trie.search("app"));
    }


}
