// dro1dDev - created: 2025-05-17

package com.everdro1d.libs.core;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class UtilsTest {

    @Test
    void reverseKeyFromValueInMap() {
        Map<String, Integer> map = new HashMap<>();
        map.put("key1", 1);
        map.put("key2", 2);
        map.put("key3", 1);

        // Test for a value that exists
        String result = Utils.reverseKeyFromValueInMap(1, map);
        assertNotNull(result);
        assertTrue(result.equals("key1") || result.equals("key3"));

        // Test for a value that does not exist
        result = Utils.reverseKeyFromValueInMap(3, map);
        assertNull(result);

        // Test for null value
        result = Utils.reverseKeyFromValueInMap(null, map);
        assertNull(result);

        // Test for empty map
        result = Utils.reverseKeyFromValueInMap(1, new HashMap<>());
        assertNull(result);
    }

    @Test
    void reverseKeysFromValueInMap() {
        Map<String, String> map = new HashMap<>();
        map.put("key1", "value1");
        map.put("key2", "value2");
        map.put("key3", "value1");

        // Test for a value that exists
        String[] results = Utils.reverseKeysFromValueInMap("value1", map);
        assertNotNull(results);
        assertEquals(2, results.length);
        assertArrayEquals(new String[]{"key1", "key3"}, results);

        // Test for a value that does not exist
        results = Utils.reverseKeysFromValueInMap("value3", map);
        assertNull(results);

        // Test for null value
        results = Utils.reverseKeysFromValueInMap(null, map);
        assertNull(results);

        // Test for empty map
        results = Utils.reverseKeysFromValueInMap("value1", new HashMap<>());
        assertNull(results);
    }

    @Test
    void validateVersionTest() {
        String version = "1.0.0";
        String expectedVersion = "^(\\d+\\.\\d+\\.\\d+)";
        assertTrue(Utils.validateVersion(version, expectedVersion));

        version = "1.0";
        assertFalse(Utils.validateVersion(version, expectedVersion));

        version = "1.0.1024";
        assertTrue(Utils.validateVersion(version, expectedVersion));

        version = "1.0.0-alpha";
        assertFalse(Utils.validateVersion(version, expectedVersion));
    }
}