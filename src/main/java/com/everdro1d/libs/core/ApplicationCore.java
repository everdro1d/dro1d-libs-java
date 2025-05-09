// dro1dDev - created: 2024-03-03

package com.everdro1d.libs.core;

import com.everdro1d.libs.commands.*;

import java.io.*;
import java.net.*;
import java.util.prefs.Preferences;

import static com.everdro1d.libs.core.Utils.getUserConfigDirectory;
import static com.everdro1d.libs.io.Files.getJarPath;

/**
 * The {@code ApplicationCore} class provides core utility methods for managing
 * and configuring applications. It includes functionality for processing
 * command-line arguments, detecting the operating system, retrieving application
 * metadata, and managing configuration files.
 * <p>
 * This class is designed to be a central hub for application-level operations,
 * ensuring consistency and reusability across the application.
 * </p>
 * <h2>Key Features</h2>
 * <ul>
 *     <li>Processes CLI arguments using {@link CommandManager}.</li>
 *     <li>Detects the operating system type.</li>
 *     <li>Retrieves the latest application version based on GitHub release tags.</li>
 *     <li>Determines the application name based on the JAR file or package structure.</li>
 *     <li>Manages application configuration directories and files.</li>
 * </ul>
 * <h2>Usage</h2>
 * <p>
 * This class is intended to be used as a utility class with static methods,
 * and therefore cannot be instantiated. For an example usage, please refer to the
 * <a href="https://github.com/everdro1d/SwingGUIApplicationTemplate">SwingGUIApplicationTemplate</a>.
 * </p>
 */
public final class ApplicationCore {

    // Private constructor to prevent instantiation.
    private ApplicationCore() {
        throw new UnsupportedOperationException("ApplicationCore class cannot be instantiated");
    }

    /**
     * Processes and executes CLI arguments using the provided {@link CommandManager}.
     * @param args the array of CLI arguments to process
     * @param commandManager CommandManager instance used to execute commands
     * @see CommandManager
     * @see CommandInterface
     */
    public static void checkCLIArgs(String[] args, CommandManager commandManager) {
        for (String arg : args) {
            commandManager.executeCommand(arg);
        }
    }

    /**
     * Detects the operating system and returns its general type.
     * @return a string representing the OS type:
     * <ul>
     *     <li>"windows" for Windows-based systems</li>
     *     <li>"mac" for MacOS</li>
     *     <li>"unix" for Unix/Linux-based systems</li>
     *     <li>"unknown" if the OS cannot be determined</li>
     * </ul>
     */
    public static String detectOS() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("win")
                        ? "windows" : os.contains("mac") ? "mac"
                        : os.contains("nix") || os.contains("nux") ? "unix"
                        : "unknown";
    }

    /**
     * Retrieves the latest version of the application from the GitHub releases page using latest tag redirect function.
     * <p>Uses version tags in the format "vX.Y.Z".</p>
     * @param githubURL the URL of the GitHub latest releases page (/releases/latest/)
     * @return the latest version as a string (ex: "1.2.1"), or {@code null} if no valid version is found
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

    /**
     * Determines the application name based on the JAR file or package structure.
     * <p>If running as a JAR, the name is derived from the JAR file name. Otherwise, it uses the package name 2 levels after {@code "com"} (ex: {@code "com.everdro1d.libs"} becomes {@code "libs"}).</p>
     * @param clazz the {@code main()} class of the application
     * @return the name of the application, (ex: {@code "dro1d-libs-java"} or {@code "libs"}), or {@code "UnknownApplication"} if it cannot be determined.
     */
    public static String getApplicationName(Class<?> clazz) {
        String jarPath = getJarPath(clazz);
        if (jarPath != null) {
            String jarFileName = new File(jarPath).getName();
            String[] parts = jarFileName.split("\\.");
            if (parts.length > 0) {
                String appName = parts[0].replaceAll("\\s+", "-");
                return appName;
            }
        }

        // backup for when the application is not a jar
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

    /**
     * Retrieves the configuration directory for the application within the developer's config folder.
     * @param clazz the {@code main()} class of the application
     * @param developerName the name of the developer or vendor (ex: {@code "dro1dDev"})
     * @return the path to the application's configuration directory
     */
    public static String getApplicationConfigDirectory(Class<?> clazz, String developerName) {
        return getUserConfigDirectory() + File.separator + developerName + File.separator + getApplicationName(clazz);
    }

    /**
     * Saves a {@link Preferences} node as an XML file in the application's configuration directory.
     * <p>Use this method alongside {@link #loadConfigFile(Class, String)} to maintain persistent application settings.</p>
     * @param clazz the {@code main()} class of the application
     * @param developerName the name of the developer or vendor (ex: {@code "dro1dDev"})
     * @param prefs the {@link Preferences} node to save
     * @see #getApplicationConfigDirectory(Class, String)
     */
    public static void saveConfigFile(Class<?> clazz, String developerName, Preferences prefs) {
        File configFile = new File(getApplicationConfigDirectory(clazz, developerName), "config.xml");

        if (configFile.getAbsolutePath().isEmpty()) {
            return;
        }

        if (!configFile.getParentFile().exists() && !configFile.getParentFile().mkdirs()) {
            System.err.println("Failed to create config directory: " + configFile.getParentFile().getAbsolutePath());
            return;
        }

        if (!configFile.exists()) {
            try {
                if (!configFile.createNewFile()) {
                    System.err.println("Failed to create config file: " + configFile.getAbsolutePath());
                    return;
                }
            } catch (IOException ex) {
                ex.printStackTrace(System.err);
                return;
            }
        }

        try (OutputStream osNode = new BufferedOutputStream(new FileOutputStream(configFile))) {
            prefs.exportNode(osNode);
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
    }

    /**
     * Loads a {@link Preferences} node from an XML file in the application's configuration directory.
     * <p>Use this method alongside {@link #saveConfigFile(Class, String, Preferences)} to restore application settings.</p>
     * @param clazz the {@code main()} class of the application
     * @param developerName the name of the developer or vendor (ex: {@code "dro1dDev"})
     * @see #getApplicationConfigDirectory(Class, String)
     */
    public static void loadConfigFile(Class<?> clazz, String developerName) {
        File configFile = new File(getApplicationConfigDirectory(clazz, developerName), "config.xml");

        if (!configFile.exists()) {
            System.err.println("Config file not found: " + configFile.getAbsolutePath());
            return;
        }

        try (InputStream isNode = new BufferedInputStream(new FileInputStream(configFile))) {
            Preferences.importPreferences(isNode);
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
    }
}
