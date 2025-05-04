/**
 * https://www.geeksforgeeks.org/trie-insert-and-search/
 * https://www.geeksforgeeks.org/trie-memory-optimization-using-hash-map/
 * https://www.geeksforgeeks.org/trie-delete/
 *
 */
package com.everdro1d.libs.structs;

import java.util.HashMap;

public class Trie {

    private static class TrieNode {
        /**
         * Denotes whether this node is the end of a key in the Trie.
         */
        boolean isEndOfWord;
        /**
         * char map of this node's child nodes.
         */
        HashMap<Character, TrieNode> child;

        TrieNode() {
            isEndOfWord = false;
            child = new HashMap<>();
        }

        /**
         * Checks whether the node has any children.
         * @return true if node has any children, false if not
         */
        private boolean hasChildren() {
            return !child.isEmpty();
        }
    }

    /**
     * Root node of the Trie.
     */
    TrieNode root;

    public Trie() {
        root = new TrieNode();
    }

    /**
     * Inserts the given key into the Trie, creating nodes where necessary.
     * @param key key to insert
     */
    public void insert(String key) {
        TrieNode currentNode = root;

        for (char character : key.toCharArray()) {
            currentNode = currentNode.child.computeIfAbsent(
                    character, k -> new TrieNode()
            );
        }
        currentNode.isEndOfWord = true;
    }

    /**
     * Checks if the given key exists in the tree.
     * @param key key to check for
     * @return true if all the nodes of the key exist in the tree and the last node is EOW.
     */
    public boolean contains(String key) {
        if (root == null) {
            return false;
        }

        TrieNode currentNode = root;

        for (char character : key.toCharArray()) {

            if (!currentNode.child.containsKey(character)) {
                return false;
            }

            currentNode = currentNode.child.get(character);
        }

        return currentNode.isEndOfWord;
    }

    /**
     * Checks if any keys in the tree start with the given prefix.
     * @param prefix prefix to check for
     * @return true if any key starts with or matches the prefix
     */
    public boolean startsWith(String prefix) {
        if (root == null) {
            return false;
        }

        TrieNode currentNode = root;

        for (char character : prefix.toCharArray()) {

            if (!currentNode.child.containsKey(character)) {
                return false;
            }

            currentNode = currentNode.child.get(character);
        }

        return true;
    }

    /**
     * Checks if the Trie is empty.
     * @return true if the root node has no children, else false.
     */
    public boolean isEmpty() {
        return root.child.isEmpty();
    }

    /**
     * Clears the children of the root node.
     */
    public void clear() {
        root.child.clear();
    }

    public boolean remove(String key) {
        return removeHelper(root, key, 0).keyRemoved;
    }

    private static class RemovalResult {
        boolean keyRemoved;
        boolean deleteCurrentNode;

        RemovalResult(boolean keyRemoved, boolean deleteCurrentNode) {
            this.keyRemoved = keyRemoved;
            this.deleteCurrentNode = deleteCurrentNode;
        }
    }

    private RemovalResult removeHelper(TrieNode currentNode, String key, int index) {
        if (index == key.length()) {
            if (!currentNode.isEndOfWord) {
                return new RemovalResult(false, false); // key not exist
            }
            currentNode.isEndOfWord = false;
            return new RemovalResult(true, !currentNode.hasChildren()); // key removed, del if empty
        }

        char character = key.charAt(index);
        TrieNode childNode = currentNode.child.get(character);
        if (childNode == null) {
            return new RemovalResult(false, false); // key not found
        }

        RemovalResult childResult = removeHelper(childNode, key, index + 1);

        if (childResult.deleteCurrentNode) {
            currentNode.child.remove(character);
        }

        boolean shouldDeleteCurrentNode = !currentNode.hasChildren() && !currentNode.isEndOfWord;
        return new RemovalResult(childResult.keyRemoved, shouldDeleteCurrentNode);
    }
}
