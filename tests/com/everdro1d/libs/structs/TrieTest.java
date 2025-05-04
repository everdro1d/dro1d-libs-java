package com.everdro1d.libs.structs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TrieTest {

    private Trie trie;

    @BeforeEach
    void setUp() {
        trie = new Trie();
    }

    @Test
    void insertAndContains() {
        assertFalse(trie.contains("apple"));
        trie.insert("apple");
        assertTrue(trie.contains("apple"));
        assertFalse(trie.contains("app")); // "app" not inserted yet
    }

    @Test
    void startsWith() {
        trie.insert("hello");
        trie.insert("helium");
        assertTrue(trie.startsWith("hel"));
        assertTrue(trie.startsWith("hello"));
        assertFalse(trie.startsWith("hex")); // nothing starts with "hex"
    }

    @Test
    void isEmptyAndClear() {
        assertTrue(trie.isEmpty());
        trie.insert("test");
        assertFalse(trie.isEmpty());

        trie.clear();
        assertTrue(trie.isEmpty());
        assertFalse(trie.contains("test")); // should not contain anything
    }

    @Test
    void removeLeafNode() {
        trie.insert("cat");
        trie.insert("cap");

        assertTrue(trie.contains("cap"));
        assertTrue(trie.remove("cap"));
        assertFalse(trie.contains("cap"));
        assertTrue(trie.contains("cat")); // "cat" should remain
    }

    @Test
    void removePrefixOnlyWord() {
        trie.insert("car");
        trie.insert("cart");
        trie.insert("carbon");

        assertTrue(trie.remove("car"));
        assertFalse(trie.contains("car"));

        assertTrue(trie.contains("cart"));
        assertTrue(trie.contains("carbon"));
        assertTrue(trie.startsWith("car")); // still a valid prefix
    }

    @Test
    void removeNonExistentWord() {
        trie.insert("dog");
        assertFalse(trie.remove("cat")); // "cat" was never inserted
        assertTrue(trie.contains("dog")); // "dog" should still be present
    }

    @Test
    void removeSharedPathWord() {
        trie.insert("inter");
        trie.insert("internet");
        trie.insert("internal");

        assertTrue(trie.remove("internet"));
        assertFalse(trie.contains("internet"));

        assertTrue(trie.contains("internal"));
        assertTrue(trie.contains("inter"));
    }

    @Test
    void removeOnlyWord() {
        trie.insert("solo");
        assertTrue(trie.contains("solo"));

        assertTrue(trie.remove("solo"));
        assertFalse(trie.contains("solo"));
        assertTrue(trie.isEmpty());
    }
}
