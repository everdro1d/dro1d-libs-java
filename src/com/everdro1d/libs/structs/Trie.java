/**
 * https://www.geeksforgeeks.org/trie-insert-and-search/
 * https://www.geeksforgeeks.org/trie-memory-optimization-using-hash-map/
 * https://www.geeksforgeeks.org/trie-delete/
 *
 */
package com.everdro1d.libs.structs;

import java.util.HashMap;

/**
 * <h2>Definition</h2>
 * Trie data structure is defined as a Tree based data structure that is used for storing some collection of strings and performing efficient search operations on them.
 * <p>The word Trie is derived from reTRIEval, which means finding something or obtaining it.
 * <h2>Properties of Trie:
 * <ul>
 *     <li>Prefix-based: Tries are prefix-based data structures that can efficiently handle prefix matching, prefix searching, and prefix deletion.</li>
 *     <li>Tree-based: Trie is a tree-structured data structure making it easily representable.</li>
 *     <li>Fast-Search: Trie has search time complexity O(m), where m is the length of the key.</li>
 *     <li>Memory requirement: Trie has more space requirements, but it can be optimized,</li>
 *     <li>Easy to implement: Its iterative approach makes it easier to implement.</li>
 * </ul>
 * <h2>Applications of Trie:</h2>
 * Trie is involved wherever string manipulation or processing is involved. Here are a few usages of trie:
 * <ul>
 *     <li>Autocomplete: Tries are commonly used in auto-complete applications, such as when typing in a search bar.</li>
 *     <li>Spell checking: By storing a large dictionary of words in a trie, it can be used to quickly check if a given word is spelled correctly.</li>
 *     <li>IP routing: Tries are also used in IP routing, where the prefix of an IP address is used to determine the network address to forward packets.</li>
 *     <li>Text compression: Tries can also be used in text compression algorithms such as Huffman coding.</li>
 * </ul>
 * <h2>Advantages of Trie:</h2>
 * <ul>
 *     <li>Tries can be optimized to use less memory by compressing common prefixes into a single node, reducing the overall size of the data structure.</li>
 *     <li>Tries can easily handle partial matches, prefix searches, and autocomplete operations.</li>
 *     <li>Tries offer fast and efficient search operations for large collections of strings or words. For example, to search a single name in a student database of universities.</li>
 *     <li>Tries can store additional data associated with each key in the leaf nodes, making it easy to access additional information about the keys.</li>
 * </ul>
 * <h2>Disadvantages of Trie:</h2>
 * <ul>
 *     <li>Tries can be memory-intensive, especially for large collections of long strings, as each node requires additional memory to store its character value and pointer references.</li>
 *     <li>Tries may require extra time and space to build or update the data structure when keys are added or deleted.</li>
 *     <li>Tries may be slower than hash tables or binary search trees for exact match operations.</li>
 * </ul>
 */
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
