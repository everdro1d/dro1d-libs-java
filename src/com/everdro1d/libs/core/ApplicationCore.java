package com.everdro1d.libs.core;

import com.everdro1d.libs.interfaces.CLICommands;

import javax.swing.*;
import java.net.*;
import java.util.prefs.Preferences;

public final class ApplicationCore {
    private ApplicationCore() {}


    // ----------------------------------------- TODO -----------------------------------------
    // Modular functions for the application core


    /**
     * Implement CLI arguments through CLICommands interface
     * @param args passed from main
     */
    public static void checkCLIArgs(String[] args) {
        for (String arg : args) {
            CLICommands command = CLICommands.CLI_COMMANDS_MAP.get(arg);
            if (command != null) {
                command.execute();
            } else {
                System.err.println(
                        "Unknown argument: [" + arg + "] Skipping." +
                                "\n    Use -help to list valid arguments."
                );
            }
        }
    }

    /**
     * Detects the OS as String.
     * @param debug whether to print debug information
     * @return the detected OS as a String
     *      - "Windows"
     *      - "macOS"
     *      - "Unix"
     *      - "Unknown"
     *
     * @see #checkOSCompatibility(boolean)
     */
    public static String detectOS(boolean debug) {
        String os = System.getProperty("os.name").toLowerCase();
        String detectedOS = os.contains("win") ? "Windows" : os.contains("mac") ? "macOS" : os.contains("nix") || os.contains("nux") ? "Unix" : "Unknown";
        if (debug) System.out.println(detectedOS + " detected.");
        return detectedOS;
    }

    /**
     * Get the latest version of the application from the GitHub latest releases page using redirect.
     * <p>Uses version tags in the format "v1.2.1"
     * @param githubURL the URL of the GitHub releases page
     * @param debug whether to print debug information
     * @return the latest version # as a String
     * <p>Example Output: - "1.2.1"
     * @see com.everdro1d.libs.swing.SwingGUI#updateCheckerDialog(String, JFrame, boolean, String, String, Preferences)
     */
    public static String getLatestVersion(String githubURL, boolean debug) {
        try {
            URL url = new URI(githubURL).toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setInstanceFollowRedirects(false);
            connection.connect();
            String location = connection.getHeaderField("Location");
            connection.disconnect();
            if (location != null) {
                String latestVersion = location.split("/v")[1];
                if (debug) System.out.println("Latest version: " + latestVersion);
                return latestVersion;
            }
        } catch (Exception e) {
            if (debug) e.printStackTrace(System.err);
        }
        return null;
    }


    // ----------------------------------------- TODO -----------------------------------------
    // Non-modular functions for the application core
    // Expected to be copied and pasted into the main application


    /**
     * Detects the OS to determine compat with application and dependencies.
     * @param debug whether to print debug information
     * @see #detectOS(boolean)
     * @see #executeOSSpecificCode(String, boolean)
     */
    public static void checkOSCompatibility(boolean debug) {
        String detectedOS = detectOS(debug);
        executeOSSpecificCode(detectedOS, debug);
    }

    /**
     * Execute OS specific code.
     * @param detectedOS the detected OS
     * @param debug whether to print debug information
     * @see #checkOSCompatibility(boolean)
     * @see #detectOS(boolean)
     */
    public static void executeOSSpecificCode(String detectedOS, boolean debug) {
        switch (detectedOS) {
            case "Windows" -> {
                // beep boop
            }
            case "macOS" -> {
                // beep boop the second coming
            }
            case "Unix" -> {
                // beep boop pengwin
            }
            default -> {
                System.out.println("Unknown OS detected. Exiting.");
                System.exit(1);
            }
        }
    }

}
