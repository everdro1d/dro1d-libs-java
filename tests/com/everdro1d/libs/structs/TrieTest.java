package com.everdro1d.libs.structs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

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

    @Test
    void listKeys_AllKeys() {
        trie.insert("apple");
        trie.insert("app");
        trie.insert("application");

        List<String> result = trie.listKeys();
        assertEquals(3, result.size());
        assertTrue(result.contains("apple"));
        assertTrue(result.contains("app"));
        assertTrue(result.contains("application"));
    }

    @Test
    void listKeys_EmptyTrie() {
        List<String> result = trie.listKeys();
        assertTrue(result.isEmpty());
    }

    @Test
    void listKeys_SingleKey() {
        trie.insert("solo");

        List<String> result = trie.listKeys();
        assertEquals(1, result.size());
        assertTrue(result.contains("solo"));
    }

    @Test
    void listKeys_MultipleKeys() {
        trie.insert("cat");
        trie.insert("car");
        trie.insert("cart");
        trie.insert("carbon");

        List<String> result = trie.listKeys();
        assertEquals(4, result.size());
        assertTrue(result.contains("cat"));
        assertTrue(result.contains("car"));
        assertTrue(result.contains("cart"));
        assertTrue(result.contains("carbon"));
    }

    @Test
    void listKeysMatching_ExactMatch() {
        trie.insert("apple");
        trie.insert("app");
        trie.insert("application");

        List<String> result = trie.listKeysMatching("app");
        assertEquals(3, result.size());
        assertTrue(result.contains("app"));
        assertTrue(result.contains("apple"));
        assertTrue(result.contains("application"));
    }

    @Test
    void listKeysMatching_PrefixMatch() {
        trie.insert("car");
        trie.insert("cart");
        trie.insert("carbon");
        trie.insert("cat");

        List<String> result = trie.listKeysMatching("car");
        assertEquals(3, result.size());
        assertTrue(result.contains("car"));
        assertTrue(result.contains("cart"));
        assertTrue(result.contains("carbon"));
    }

    @Test
    void listKeysMatching_NoMatch() {
        trie.insert("dog");
        trie.insert("cat");
        trie.insert("fish");

        List<String> result = trie.listKeysMatching("elephant");
        assertTrue(result.isEmpty());
    }

    @Test
    void listKeysMatching_EmptyPrefix() {
        trie.insert("hello");
        trie.insert("world");
        trie.insert("java");

        List<String> result = trie.listKeysMatching("");
        assertEquals(3, result.size());
        assertTrue(result.contains("hello"));
        assertTrue(result.contains("world"));
        assertTrue(result.contains("java"));
    }

    @Test
    void listKeysMatching_PartialMatch() {
        trie.insert("inter");
        trie.insert("internet");
        trie.insert("internal");
        trie.insert("interval");

        List<String> result = trie.listKeysMatching("inte");
        assertEquals(4, result.size());
        assertTrue(result.contains("inter"));
        assertTrue(result.contains("internet"));
        assertTrue(result.contains("internal"));
        assertTrue(result.contains("interval"));
    }
}
