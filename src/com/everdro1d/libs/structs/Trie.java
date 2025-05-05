/**
 * https://www.geeksforgeeks.org/trie-insert-and-search/
 * https://www.geeksforgeeks.org/trie-memory-optimization-using-hash-map/
 * https://www.geeksforgeeks.org/trie-delete/
 * https://www.geeksforgeeks.org/trie-meaning-in-dsa/
 *
 */
package com.everdro1d.libs.structs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

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
         * The character that this node represents.
         */
        char character;

        /**
         * char map of this node's child nodes.
         */
        HashMap<Character, TrieNode> child;

        TrieNode(char character) {
            isEndOfWord = false;
            child = new HashMap<>();
            this.character = character;
        }

        TrieNode() {
            child = new HashMap<>();
        }

        /**
         * Checks whether the node has any children.
         * @return false if node has any children, true otherwise
         */
        private boolean isEmpty() {
            return child.isEmpty();
        }

        private Collection<TrieNode> getChildren() {
            return child.values();
        }
    }

    /**
     * Root node of the Trie.
     */
    TrieNode root;

    /**
     * Creates a new Trie without any values.
     */
    public Trie() {
        root = new TrieNode();
    }

    /**
     * Creates a new Trie containing the keys given in the list.
     * @param list list of key the tree should be init with
     */
    public Trie(List<String> list) {
        root = new TrieNode();
        for (String key : list) {
            insert(key);
        }
    }

    /**
     * Inserts the given key into the Trie, creating nodes where necessary.
     * @param key key to insert
     */
    public void insert(String key) {
        TrieNode currentNode = root;

        for (char character : key.toCharArray()) {
            currentNode = currentNode.child.computeIfAbsent(
                    character, k -> new TrieNode(character)
            );
        }
        currentNode.isEndOfWord = true;
    }

    /**
     * Checks if the given key exists in the tree.
     * @param key key to check for
     * @return true if all the nodes of the key exist in the Trie and the last node is EOW.
     */
    public boolean contains(String key) {
        return search(key, true);
    }

    /**
     * Checks if any keys in the tree start with the given prefix.
     * @param prefix prefix to check for
     * @return true if any key starts with or matches the prefix
     */
    public boolean startsWith(String prefix) {
        return search(prefix, false);
    }

    // ---
    private boolean search(String key, boolean exact) {
        TrieNode currentNode = root;

        for (char character : key.toCharArray()) {
            currentNode = currentNode.child.get(character);

            if (currentNode == null) {
                return false;
            }
        }

        return !exact || currentNode.isEndOfWord;
    }
    // ---

    /**
     * Checks if the Trie is empty.
     * @return true if the root node has no children, else false.
     */
    public boolean isEmpty() {
        return root.isEmpty();
    }

    /**
     * Clears the children of the root node.
     */
    public void clear() {
        root.child.clear();
    }

    /**
     * Removes the given key from the Trie.
     * @param key key to remove
     * @return true if the key was removed, false otherwise (including key does not exist)
     */
    public boolean remove(String key) {
        return removeHelper(root, key, 0).keyRemoved;
    }

    // ---
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
            return new RemovalResult(true, currentNode.isEmpty()); // key removed, del if empty
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

        boolean shouldDeleteCurrentNode = currentNode.isEmpty() && !currentNode.isEndOfWord;
        return new RemovalResult(childResult.keyRemoved, shouldDeleteCurrentNode);
    }
    // ---

    /**
     * List the keys in a Trie.
     * @return A list of all the keys in the Trie
     */
    public List<String> listKeys() {
        return listKeysMatching("");
    }

    /**
     * List the keys in a Trie that match the prefix.
     * @param prefix prefix to match
     * @return List of all matching keys in the Trie
     */
    public List<String> listKeysMatching(String prefix) {
        List<String> list = new ArrayList<>();
        StringBuffer stringAssembler = new StringBuffer();
        TrieNode currentNode = root;

        for (char character : prefix.toCharArray()) {
            currentNode = currentNode.child.get(character);

            if (currentNode == null) {
                return list;
            }

            stringAssembler.append(character);
        }

        listKeysHelper(currentNode, list, stringAssembler);
        return list;
    }

    // ---
    private void listKeysHelper(TrieNode currentNode, List<String> list, StringBuffer stringAssembler) {
        if (currentNode.isEndOfWord) {
            list.add(stringAssembler.toString());
        }

        if (currentNode.isEmpty()) {
            return;
        }

        for (TrieNode childNode : currentNode.getChildren()) {
            // branch into child nodes and append
            listKeysHelper(childNode, list, stringAssembler.append(childNode.character));
            // reset to childless state before probing next child
            stringAssembler.setLength(stringAssembler.length() - 1);
        }
    }
    // ---
}
