// dro1dDev - created: 2025-05-09

package com.everdro1d.libs.structs;

import com.everdro1d.libs.structs.Trie;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

class TrieTest {

    @Test
    void testInsertAndContains() {
        Trie<String> trie = new Trie<>();
        trie.insert("apple", "fruit");
        assertTrue(trie.contains("apple"));
        assertFalse(trie.contains("app"));
    }

    @Test
    void testInsertListAndContainsAll() {
        Trie<String> trie = new Trie<>();
        List<String> keys = Arrays.asList("apple", "app", "application");
        trie.insert(keys);
        assertTrue(trie.containsAll(keys));
        assertFalse(trie.containsAll(Arrays.asList("apple", "banana")));
    }

    @Test
    void testInsertMapAndRetrieveValue() {
        Trie<String> trie = new Trie<>();
        Map<String, String> map = Map.of("apple", "fruit", "car", "vehicle");
        trie.insert(map);
        assertTrue(trie.contains("apple"));
        assertTrue(trie.contains("car"));
    }

    @Test
    void testStartsWith() {
        Trie<String> trie = new Trie<>();
        trie.insert("apple", "fruit");
        trie.insert("app", "prefix");
        assertTrue(trie.startsWith("app"));
        assertFalse(trie.startsWith("banana"));
    }

    @Test
    void testRemove() {
        Trie<String> trie = new Trie<>();
        trie.insert("apple", "fruit");
        assertTrue(trie.remove("apple"));
        assertFalse(trie.contains("apple"));
        assertFalse(trie.remove("banana"));
    }

    @Test
    void testRemoveAll() {
        Trie<String> trie = new Trie<>();
        List<String> keys = Arrays.asList("apple", "app", "application");
        trie.insert(keys);
        assertTrue(trie.removeAll(keys));
        assertTrue(trie.isEmpty());
    }

    @Test
    void testClear() {
        Trie<String> trie = new Trie<>();
        trie.insert("apple", "fruit");
        trie.insert("app", "prefix");
        trie.clear();
        assertTrue(trie.isEmpty());
    }

    @Test
    void testListKeys() {
        Trie<String> trie = new Trie<>();
        trie.insert("apple", "fruit");
        trie.insert("app", "prefix");
        List<String> keys = trie.listKeys();
        assertTrue(keys.contains("apple"));
        assertTrue(keys.contains("app"));
    }

    @Test
    void testListKeysMatching() {
        Trie<String> trie = new Trie<>();
        trie.insert("apple", "fruit");
        trie.insert("app", "prefix");
        trie.insert("banana", "fruit");
        List<String> keys = trie.listKeysMatching("app");
        assertTrue(keys.contains("apple"));
        assertTrue(keys.contains("app"));
        assertFalse(keys.contains("banana"));
    }

    @Test
    void testListKeysMatchingLimited() {
        Trie<String> trie = new Trie<>();
        trie.insert("apple", "fruit");
        trie.insert("app", "prefix");
        trie.insert("apricot", "fruit");
        trie.insert("ap", "prefix");

        List<String> keys = trie.listKeysMatching("ap", 2);
        assertEquals(2, keys.size());

        assertTrue(keys.contains("ap"));
        assertTrue(keys.contains("app"));
        assertFalse(keys.contains("apple"));
        assertFalse(keys.contains("apricot"));
    }

    @Test
    void testGet() {
        Trie<String> trie = new Trie<>();
        trie.insert("apple", "fruit");
        trie.insert("car", "vehicle");

        assertEquals("fruit", trie.get("apple"));
        assertEquals("vehicle", trie.get("car"));
        assertNull(trie.get("banana")); // Key not present
    }

    @Test
    void testSet() {
        Trie<String> trie = new Trie<>();
        trie.insert("apple", "fruit");

        assertTrue(trie.set("apple", "newFruit")); // Update existing key
        assertEquals("newFruit", trie.get("apple"));

        assertFalse(trie.set("banana", "fruit")); // Key does not exist
        assertNull(trie.get("banana"));
    }

    @Test
    void testOverloadedInsertList() {
        Trie<String> trie = new Trie<>();
        List<String> keys = Arrays.asList("apple", "app", "application");
        trie.insert(keys);

        assertTrue(trie.contains("apple"));
        assertTrue(trie.contains("app"));
        assertTrue(trie.contains("application"));
    }

    @Test
    void testOverloadedInsertMap() {
        Trie<String> trie = new Trie<>();
        Map<String, String> map = Map.of("apple", "fruit", "car", "vehicle");
        trie.insert(map);

        assertEquals("fruit", trie.get("apple"));
        assertEquals("vehicle", trie.get("car"));
        assertNull(trie.get("banana")); // Key not present
    }
}