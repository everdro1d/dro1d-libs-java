// dro1dDev - created: 2024-03-03

package com.everdro1d.libs.core;

import com.everdro1d.libs.io.SyncPipe;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.function.Predicate;

/**
 * A utility class providing various helper methods for common operations.
 * <p>This class includes methods for string manipulation, date and time formatting,
 * clipboard operations, map processing, command execution, and more.</p>
 * <p>All methods are static and can be accessed directly without instantiating the class.</p>
 * <p><strong>Note:</strong> This class is not meant to be instantiated.</p>
 *
 * <h2>Key Features:</h2>
 * <ul>
 *     <li>String operations like replacing characters and checking substrings.</li>
 *     <li>Date and time formatting with file-system-safe options.</li>
 *     <li>Clipboard operations for copying text.</li>
 *     <li>Map utilities for extracting values, printing, and reversing key-value pairs.</li>
 *     <li>Command execution in the system shell.</li>
 *     <li>Operating system detection and configuration directory retrieval.</li>
 * </ul>
 */
public final class Utils {

    // Private constructor to prevent instantiation.
    private Utils() {
        throw new UnsupportedOperationException("Utils class cannot be instantiated");
    }

    /**
     * Opens the specified URL in the default web browser.
     * @param url the URL to open
     */
    public static void openLink(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    /**
     * Copies the specified string to the system clipboard.
     * @param copyString the string to copy to the clipboard
     */
    public static void copyToClipboard(String copyString) {
        try {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(copyString), null);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    /**
     * Returns the current date and/or time as a formatted string.
     * @param includeDate whether to include the date in the format {@code yyyy-MM-dd}
     * @param includeTime whether to include the time in the format {@code HH:mm:ss}
     * @param includeMillis whether to include milliseconds in the format {@code .SSS}
     * @return the current date and/or time as a string
     */
    public static String getCurrentTime(boolean includeDate, boolean includeTime, boolean includeMillis) {
        String now = LocalDateTime.now().toString();
        String date = now.split("T")[0];
        String time = now.split("T")[1].split("\\.")[0];
        String millis = now.split("\\.")[1];

        String currentTime = "";
        if (includeDate) currentTime += date + (includeTime ? " " : "");
        if (includeTime) currentTime += time;
        if (includeMillis) currentTime += "." + millis;

        return currentTime;
    }

    /**
     * Returns a file-system-safe version of the current date and/or time.
     * <p>Replaces invalid file system characters with underscores.</p>
     * @param includeDate whether to include the date in the format {@code yyyy-MM-dd}
     * @param includeTime whether to include the time in the format {@code HH:mm:ss}
     * @param includeMillis whether to include milliseconds in the format {@code .SSS}
     * @see #getCurrentTime(boolean, boolean, boolean)
     * @return a sanitized string representation of the current date and/or time
     */
    public static String getSanitizedCurrentTime(boolean includeDate, boolean includeTime, boolean includeMillis) {
        return getCurrentTime(includeDate, includeTime, includeMillis)
                .replaceAll("[ :]", "_");
    }

    /**
     * Checks if the specified string contains any of the substrings in the given array.
     * @param matchingArray the array of substrings to check for
     * @param testString the string to test
     * @return {@code true} if the test string contains any of the substrings, {@code false} otherwise
     * <p><strong>Example:</strong></p>
     * <p>{@code stringContainsAny(new String[]{"a", "b", "ab", "c"}, "abc")} -> {@code true}</p>
     * <p>{@code stringContainsAny(new String[]{"a", "b", "c"}, "def")} -> {@code false}</p>
     */
    public static boolean stringContainsAny(String[] matchingArray, String testString) {
        for (String s : matchingArray) {
            if (testString.contains(s)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the given 2D array contains the specified test string.
     * @param matchingArray the 2D array to check in
     * @param testString the string to test
     * @return {@code true} if the array contains the test string, {@code false} otherwise
     */
    public static boolean arrayContains(String[][] matchingArray, String testString) {
        return arrayContains(matchingArray, testString, false);
    }

    /**
     * Checks if the given 2D array contains the specified test string.
     * @param matchingArray the 2D array to check in
     * @param testString the string to test
     * @param treatAsKey whether to treat the test string as a key (2D array pretending to be a map)
     * @return {@code true} if the array contains the test string, {@code false} otherwise
     */
    public static boolean arrayContains(String[][] matchingArray, String testString, boolean treatAsKey) {
        if (treatAsKey) {
            for (String[] entry : matchingArray) {
                if (entry[0].equals(testString)) {
                    return true;
                }
            }
            return false;
        }

        for (String[] entry : matchingArray) {
            for (String s : entry) {
                if (s.equals(testString)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Replaces the character at the specified index in the given string with the provided replacement string.
     * @param string the original string
     * @param i the index of the character to replace
     * @param s the replacement string
     * @return a new string with the character at the specified index replaced
     */
    public static String replaceCharAt(String string, int i, String s) {
        return string.substring(0, i) + s + string.substring(i + s.length());
    }

    /**
     * Extracts a set of unique values from a nested map based on a specified property and filter condition.
     * @param property the key whose values are to be extracted
     * @param filter the condition to filter the values (can be {@code null} for no filtering)
     * @param map the nested map to process
     * @return a set of unique values matching the specified property and filter
     * <p><strong>Example:</strong></p>
     * <blockquote><pre>
     * // Input nested map:
     * Map&lt;String, Map&lt;String, String&gt;&gt; map = Map.of(
     *     "18", Map.of("EXT", "mp4", "ACODEC", "mp4a.40.2"),
     *     "22", Map.of("EXT", "webm", "ACODEC", "video only"),
     *     "37", Map.of("EXT", "mkv", "ACODEC", "video only")
     * );
     *
     * // Extract unique extensions where ACODEC equals "video only":
     * Set&lt;String&gt; values = extractUniqueValuesByPredicate(
     *     "EXT",
     *     option -> "video only".equals(option.get("ACODEC")),
     *     map
     * );
     *
     * // Output:
     * System.out.println(values); // [webm, mkv]
     * </pre></blockquote>
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

    /**
     * Recursively prints a nested map in a JSON-like formatted structure. Allows for infinite nesting.
     * @param map the nested map to print
     * <p><strong>Example:</strong></p>
     * <blockquote><pre>
     * {
     *   "Key1": {
     *     "Value1": {
     *       "SubKey1-1": "SubValue1-1",
     *       "SubKey1-2": "SubValue1-2",
     *       "SubKey1-3": "SubValue1-3"
     *      }
     *   },
     *   ...
     * }
     * </pre></blockquote>
     */
    public static void printNestedMapFormatted(Object map) {
        printNestedMapFormatted(map, 0);
    }

    /**
     * Recursive helper for {@link #printNestedMapFormatted(Object)}
     */
    private static void printNestedMapFormatted(Object map, int indentLeaveAsZero) {
        String indentString = new String(new char[indentLeaveAsZero]).replace("\0", "  ");

        // Use a separate indentation for nested content
        String nestedIndent = indentString + "  ";

        if (map instanceof Map) {
            Map<String, ?> mapObj = (Map<String, ?>) map;
            boolean isFirstEntry = true;
            System.out.print("{");
            for (Map.Entry<String, ?> entry : mapObj.entrySet()) {
                if (!isFirstEntry) {
                    System.out.print(",");
                }
                isFirstEntry = false;
                System.out.print("\n" + nestedIndent + "\"" + entry.getKey() + "\" : ");
                printNestedMapFormatted(entry.getValue(), indentLeaveAsZero + 1);
            }
            // Check if it's the last entry and only print newline then
            if (!isFirstEntry) {
                System.out.println();
            }
            System.out.print(indentString + "}");
        } else {
            System.out.print("\"" + map + "\"");
        }
    }

    /**
     * Prints each element of the provided list to the console in a newline.
     * <p>
     * This method iterates through the list and prints each element on a
     * new line for better readability.
     * </p>
     *
     * @param list the list of elements to print
     * <p><strong>Example:</strong></p>
     * <blockquote><pre>
     * List&lt;String&gt; items = List.of("Item1", "Item2", "Item3");
     * Utils.printlnList(items);
     * // Output:
     * // Item1
     * // Item2
     * // Item3
     * </pre></blockquote>
     */
    public static void printlnList(List<?> list) {
        for (Object o : list) {
            System.out.println(o);
        }
    }

    /**
     * Executes a command in the system shell.
     * @param cmd          the command to execute as a list of strings
     * @param pipeToSysOut whether to pipe the command's output to {@code System.out}
     */
    public static void runCommand(List<String> cmd, boolean pipeToSysOut) {
        runCommand(cmd, null, false, pipeToSysOut);
    }


    /**
     * Executes a command in the system shell.
     * @param cmd          the command to execute as a list of strings
     * @param pwd          the working directory to execute the command in
     * @param pipeToSysOut whether to pipe the command's output to {@code System.out}
     */
    public static void runCommand(List<String> cmd, String pwd, boolean pipeToSysOut) {
        runCommand(cmd, pwd, false, pipeToSysOut);
    }

    /**
     * Executes a command in the system shell.
     * @param cmd          the command to run
     * @param pwd          the working directory to execute the command in
     * @param debug        whether to print debug information to {@code System.out}
     * @param pipeToSysOut whether to pipe the command's output to {@code System.out}
     */
    public static void runCommand(List<String> cmd, String pwd, boolean debug, boolean pipeToSysOut) {
        ProcessBuilder pb = new ProcessBuilder(cmd);
        if (pwd != null && new File(pwd).exists()) {
            pb.directory(new File(pwd));
        }
        Process p;
        try {
            p = pb.start();
            new Thread(new SyncPipe(p.getErrorStream(), System.err)).start();
            if (pipeToSysOut) try (Scanner scanner = new Scanner(p.getInputStream())) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    if (!line.isEmpty()) {
                        if (debug) System.out.println(line);
                    }
                }
            }
            p.waitFor();
            if (debug) System.out.println(p.exitValue());
        } catch (Exception e) {
            if (debug) e.printStackTrace(System.err);
        }
    }

    /**
     * Reverses the key associated with the given value in the provided map.
     * @param value the value to search for in the map
     * @param map the map to search
     * @return the key associated with the given value, or {@code null} if not found
     */
    public static <T> String reverseKeyFromValueInMap(T value, Map<String, T> map) {
        if (!map.containsValue(value)) {
            System.err.println("Given Map does not contain such a value. Please check again.");
            return null;
        }

        for (Map.Entry<String, T> entry : map.entrySet()) {
            if (Objects.equals(entry.getValue(), value)) {
                return entry.getKey();
            }
        }

        System.err.println("The value exists in the map, but no matching key-value pair is found during the iteration.\n"
                + "Check if key=null, or if map is modified during check.");
        return null;
    }

    /**
     * Reverses all keys associated with the given value in the provided map.
     * @param value the value to search for in the map
     * @param map the map to search
     * @return an array of keys associated with the given value, or null if not found
     */
    public static <T> String[] reverseKeysFromValueInMap(T value, Map<String, T> map) {
        if (!map.containsValue(value)) {
            System.err.println("Given Map does not contain such a value. Please check again.");
            return null;
        }

        List<String> keys = new ArrayList<>();

        for (Map.Entry<String, T> entry : map.entrySet()) {
            if (Objects.equals(entry.getValue(), value)) {
                keys.add(entry.getKey());
            }
        }

        if (!keys.isEmpty()) return keys.toArray(new String[0]);

        System.err.println("The value exists in the map, but no matching key-value pair is found during the iteration.\n"
                + "Check if key=null, or if map is modified during check.");
        return null;
    }

    /**
     * Retrieves the user's configuration directory based on the operating system.
     * @return the path to the user's configuration directory
     */
    public static String getUserConfigDirectory() {
        String userHome = System.getProperty("user.home");
        String os = System.getProperty("os.name").toLowerCase();
        String configDir;
        if (os.contains("win")) {
            configDir = userHome + "\\AppData\\Local\\";
        } else if (os.contains("mac")) {
            configDir = userHome + "/Library/Application Support/";
        } else {
            configDir = userHome + "/.config/";
        }
        return configDir;
    }
}
