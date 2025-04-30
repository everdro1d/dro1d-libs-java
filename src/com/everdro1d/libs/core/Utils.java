/**************************************************************************************************
 * dro1dDev 2025.                                                                                 *
 **************************************************************************************************/

package com.everdro1d.libs.core;

import com.everdro1d.libs.io.SyncPipe;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;

public class Utils {
    private Utils() {}

    /**
     * Open a link in the default browser.
     * @param url the link to open
     */
    public static void openLink(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    public static void copyToClipboard(String copyString) {
        try {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(copyString), null);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    /**
     * Get the current time as a string.
     * @param includeDate whether to include the date - yyyy-MM-dd
     * @param includeTime whether to include the time - HH:mm:ss
     * @param includeMillis whether to include milliseconds - .SSS
     * @return the current time as a string
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
     *
     * @see #getCurrentTime(boolean, boolean, boolean)
     * @return file system safe output of related method
     */
    public static String getSanitizedCurrentTime(boolean includeDate, boolean includeTime, boolean includeMillis) {
        return getCurrentTime(includeDate, includeTime, includeMillis)
                .replaceAll("[ :]", "_");
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

    public static String replaceCharAt(String string, int i, String s) {
        return string.substring(0, i) + s + string.substring(i + s.length());
    }

    /**
     * Get a set of unique values from the Inner Map under conditions
     * @param property the set of values to get
     * @param filter the conditions to get the values
     * @return a set of values
     * <p>Example: An inner map with video extensions and audio codecs,
     * I want to get all the video extensions where the audio codec is "video only"
     * <p>Set<String> values = extractUniqueValuesByPredicate("EXT", option -> option.get("ACODEC").equals("video only"));
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

    /**
     * Prints a json formatted nested map (allows for infinite nesting). See example below.
     *
     * <p>Example:</p>
     * <p></p>
     * <pre>
     * {
     *   "Key1": {
     *     "SubKey1-1": {
     *       "SubKey1-2": "SubValue1-2",
     *       "SubKey2-2": "SubValue2-2",
     *       "SubKey3-2": "SubValue3-2"
     *      }
     *   },
     *   "Key2": {
     *     "SubKey1-1": {
     *       "SubKey1-2": "SubValue1-2",
     *       "SubKey2-2": "SubValue2-2",
     *       "SubKey3-2": "SubValue3-2"
     *     }
     *   },
     *   "Key3": {
     *     "SubKey1": "SubValue1",
     *     "SubKey2": "SubValue2",
     *     "SubKey3": "SubValue3",
     *     "SubKey4": "SubValue4"
     *   }
     * }
     * </pre>
     */
    public static void printNestedMapFormatted(Object map, int indentLeaveAsZero) {
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
     * Run a command in the system shell.
     * @param cmd          the command to run
     * @param pipeToSysOut whether to pipe the output to System.out
     */
    public static void runCommand(ArrayList<String> cmd, boolean pipeToSysOut) {
        runCommand(cmd, null, false, pipeToSysOut);
    }


    /**
     * Run a command in the system shell.
     * @param cmd          the command to run
     * @param pwd          the working directory
     * @param pipeToSysOut whether to pipe the output to System.out
     */
    public static void runCommand(ArrayList<String> cmd, String pwd, boolean pipeToSysOut) {
        runCommand(cmd, pwd, false, pipeToSysOut);
    }

    /**
     * Run a command in the system shell.
     * @param cmd          the command to run
     * @param pwd          the working directory
     * @param debug        whether to print debug information
     * @param pipeToSysOut whether to pipe the output to System.out
     */
    public static void runCommand(ArrayList<String> cmd, String pwd, boolean debug, boolean pipeToSysOut) {
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

    public static String reverseKeyFromValueInMap(String value, Map<String, String> map) {
        if (!map.containsValue(value)) {
            System.err.println("Given Map does not contain such a value. Please check again.");
            return null;
        }
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (Objects.equals(entry.getValue(), value)) {
                return entry.getKey();
            }
        }
        System.err.println("The value exists in the map, but no matching key-value pair is found during the iteration.\n"
                + "Check if key=null, or if map is modified during check.");
        return null;
    }

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
