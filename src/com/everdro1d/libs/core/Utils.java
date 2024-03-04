package com.everdro1d.libs.core;

import java.awt.*;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public class Utils {
    private Utils() {}


    // ----------------------------------------- TODO -----------------------------------------
    // Modular utility functions


    /**
     * Open a link in the default browser.
     * @param url the link to open
     * @param debug whether to print debug information
     */
    public static void openLink(String url, boolean debug) {
        try {
            if (debug) System.out.println("Opening link: " + url);
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception e) {
            if (debug) e.printStackTrace(System.err);
        }
    }

    /**
     * Get the current time as a string.
     * @return the current time as a string
     *     - "yyyy-MM-dd HH:mm:ss"
     */
    public static String getCurrentTime() {
        return LocalDateTime.now()
                .toString()
                .replace("T", " ")
                .replace("Z", "")
                .split("\\.")[0];
    }

    /**
     * Test if the string contains any of the strings in the array.
     * @param matchingArray the array of strings to match
     * @param testString the string to test
     * @return boolean
     * <p>Example: containsAny(new String[]{"a", "b", "c"}, "abc") -> true
     * <p>Example: containsAny(new String[]{"a", "b", "c"}, "def") -> false
     */
    public static boolean containsAny(String[] matchingArray, String testString) {
        for (String s : matchingArray) {
            if (testString.contains(s)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get a set of unique values from the Inner Map under conditions
     * @param property the set of values to get
     * @param filter the conditions to get the values
     * @return a set of values
     * <p>Example: An inner map with video extensions and audio codecs,
     * I want to get all the video extensions where the audio codec is "video only"
     * <p>Set<String> values = getUniqueValues("EXT", option -> option.get("ACODEC").equals("video only"));
     * <p>System.out.println(values);
     * <p>Output: [mp4, webm, mkv]
     */
    public static Set<String> extractUniqueValuesByPredicate(
            String property, Predicate<Map<String, String>> filter, Map<String, Map<String, String>> map)
    {
        Set<String> uniqueValues = new HashSet<>();
        for (Map<String, String> option : map.values()) {
            if (filter == null || filter.test(option)) {
                uniqueValues.add(option.get(property));
            }
        }
        return uniqueValues;
    }

    public static void printNestedMap(Map<String, Map<String, String>> map) {
        for (Map.Entry<String, Map<String, String>> entry : map.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
    }


    /**
     * Prints a formatted nested map. See example below.
     *
     * <p>Example:</p>
     * <p></p>
     * <p>Outer Map:</p>
     * <pre>
     * {
     *   "Key1": Inner Map
     *   {
     *     "SubKey1": "SubValue1",
     *     "SubKey2": "SubValue2",
     *     "SubKey3": "SubValue3"
     *   },
     *   "Key2": Inner Map
     *   {
     *     "SubKey1": "SubValue1",
     *     "SubKey2": "SubValue2"
     *   },
     *   "Key3": Inner Map
     *   {
     *     "SubKey1": "SubValue1",
     *     "SubKey2": "SubValue2",
     *     "SubKey3": "SubValue3",
     *     "SubKey4": "SubValue4"
     *   }
     * }
     * </pre>
     */
    public static void printNestedMapFormatted(Map<String, Map<String, String>> map) {
        System.out.println("Outer Map \n{");
        for (Map.Entry<String, Map<String, String>> outerEntry : map.entrySet()) {
            System.out.println("  \"" + outerEntry.getKey() + "\" : Inner Map\n        {");
            for (Map.Entry<String, String> innerEntry : outerEntry.getValue().entrySet()) {
                System.out.println("            \"" + innerEntry.getKey() + "\" : \"" + innerEntry.getValue() + "\",");
            }
            System.out.println("        },");
        }
        System.out.println("}");
    }


    // ----------------------------------------- TODO -----------------------------------------
    // Non-modular utility functions
}
