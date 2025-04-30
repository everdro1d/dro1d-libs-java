/**************************************************************************************************
 * dro1dDev 2025.                                                                                 *
 **************************************************************************************************/

package com.everdro1d.libs.core;

import com.everdro1d.libs.commands.*;

import java.io.File;
import java.net.*;

import static com.everdro1d.libs.core.Utils.getUserConfigDirectory;

public final class ApplicationCore {
    private ApplicationCore() {}

    /**
     * Implement CLI arguments through CommandInterface
     * @param args passed from main
     */
    public static void checkCLIArgs(String[] args, CommandManager commandManager) {
        for (String arg : args) {
            commandManager.executeCommand(arg);
        }
    }

    /**
     * Detects the OS as String.
     * @return the detected OS as a String
     *      - "Windows"
     *      - "macOS"
     *      - "Unix"
     *      - "Unknown"
     */
    public static String detectOS() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("win")
                        ? "Windows" : os.contains("mac") ? "macOS"
                        : os.contains("nix") || os.contains("nux") ? "Unix"
                        : "Unknown";
    }

    /**
     * Get the latest version of the application from the GitHub latest releases page using redirect.
     * <p>Uses version tags in the format "v1.2.1"
     * @param githubURL the URL of the GitHub releases page
     * @return the latest version # as a String
     * <p>Example Output: - "1.2.1"
     * @see com.everdro1d.libs.swing.dialogs.UpdateCheckerDialog
     */
    public static String getLatestVersion(String githubURL) {
        try {
            URL url = new URI(githubURL).toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setInstanceFollowRedirects(false);
            connection.connect();
            String location = connection.getHeaderField("Location");
            connection.disconnect();
            if (location != null) {
                String[] s = location.split("/v");
                if (s.length > 1) return s[1];
                System.err.println("Error: No valid version tag found in URL: " + location);
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return null;
    }

    public static String getApplicationName() {
        String mainClassName = System.getProperty("sun.java.command");
        if (mainClassName != null) {
            String[] parts = mainClassName.split("\\.");
            int comIndex = -1;
            for (int i = 0; i < parts.length; i++) {
                if (parts[i].equals("com")) {
                    comIndex = i;
                    break;
                }
            }
            if (comIndex != -1 && comIndex + 2 < parts.length) {
                return parts[comIndex + 2];
            }
        }
        return "UnknownApplication";
    }

    public static String getApplicationConfigDirectory() {
        return getUserConfigDirectory() + File.separator + "dro1dDev" + File.separator + getApplicationName();
    }
}
