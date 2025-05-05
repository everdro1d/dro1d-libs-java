package com.everdro1d.libs.structs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
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
    void constructor_WithListOfStrings() {
        List<String> words = Arrays.asList("apple", "app", "application");
        Trie trieWithList = new Trie(words);

        assertTrue(trieWithList.contains("apple"));
        assertTrue(trieWithList.contains("app"));
        assertTrue(trieWithList.contains("application"));
        assertFalse(trieWithList.contains("banana")); // Not in the list
    }

    @Test
    void insert_SingleWord() {
        trie.insert("test");
        assertTrue(trie.contains("test"));
        assertFalse(trie.contains("tes")); // Partial word not inserted
    }

    @Test
    void insert_MultipleWords() {
        List<String> words = Arrays.asList("cat", "car", "cart");
        trie.insert(words);

        assertTrue(trie.contains("cat"));
        assertTrue(trie.contains("car"));
        assertTrue(trie.contains("cart"));
        assertFalse(trie.contains("carbon")); // Not in the list
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
    void removeAll_AllKeysPresent() {
        trie.insert("apple");
        trie.insert("app");
        trie.insert("application");

        List<String> keys = Arrays.asList("apple", "app", "application");
        assertTrue(trie.removeAll(keys));
        assertTrue(trie.isEmpty());
    }

    @Test
    void removeAll_SomeKeysMissing() {
        trie.insert("apple");
        trie.insert("app");

        List<String> keys = Arrays.asList("apple", "app", "application");
        assertFalse(trie.removeAll(keys));
        assertFalse(trie.contains("apple"));
        assertFalse(trie.contains("app"));
        assertFalse(trie.contains("application"));
    }

    @Test
    void removeAll_EmptyList() {
        List<String> keys = List.of();
        assertTrue(trie.removeAll(keys)); // Removing nothing should succeed
        assertTrue(trie.isEmpty());
    }

    @Test
    void removeAll_NoKeysPresent() {
        trie.insert("apple");
        trie.insert("app");

        List<String> keys = Arrays.asList("banana", "application", "car");
        assertFalse(trie.removeAll(keys));
        assertTrue(trie.contains("apple"));
        assertTrue(trie.contains("app"));
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

    @Test
    void containsAll_AllKeysPresent() {
        trie.insert("apple");
        trie.insert("app");
        trie.insert("application");

        List<String> keys = Arrays.asList("apple", "app", "application");
        assertTrue(trie.containsAll(keys));
    }

    @Test
    void containsAll_SomeKeysMissing() {
        trie.insert("apple");
        trie.insert("app");

        List<String> keys = Arrays.asList("apple", "app", "application");
        assertFalse(trie.containsAll(keys));
    }

    @Test
    void containsAll_EmptyList() {
        List<String> keys = List.of();
        assertTrue(trie.containsAll(keys)); // Empty list should always return true
    }

    @Test
    void containsAny_AnyKeyPresent() {
        trie.insert("apple");
        trie.insert("app");

        List<String> keys = Arrays.asList("banana", "app", "application");
        assertTrue(trie.containsAny(keys));
    }

    @Test
    void containsAny_NoKeysPresent() {
        trie.insert("apple");
        trie.insert("app");

        List<String> keys = Arrays.asList("banana", "application", "car");
        assertFalse(trie.containsAny(keys));
    }

    @Test
    void containsAny_EmptyList() {
        List<String> keys = List.of();
        assertFalse(trie.containsAny(keys)); // Empty list should always return false
    }
}
