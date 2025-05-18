// dro1dDev - created: 2024-03-03

package com.everdro1d.libs.core;

import com.everdro1d.libs.commands.*;

import java.io.*;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.prefs.Preferences;

import static com.everdro1d.libs.core.Utils.getUserConfigDirectory;
import static com.everdro1d.libs.core.Utils.validateVersion;
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
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (!arg.startsWith("-")) continue;

            CommandInterface cmd = commandManager.getCommand(arg);

            if (cmd == null || cmd.getExpectedArguments() == 0) {
                commandManager.executeCommand(arg);

            } else {
                int j = 0; // actual number of arguments passed
                          // (executeCommand() handles too few/many/invalid args)

                // max length is total args length - current index - 1
                String[] commandArgs = new String[args.length - i - 1];

                // treat args starting with "-" as a new command
                for (int k = ( i + 1 ); ( k < args.length && !args[k].startsWith("-") ); k++) {
                    commandArgs[j++] = args[k];
                }

                // trims the array of args to the actual number of args passed before continuing
                commandManager.executeCommand(arg, Arrays.copyOf(commandArgs, j));
            }
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
     * Retrieves the latest version of the application from the remote's
     * releases page using releases/latest tag redirect function.
     *
     * <p>Uses version tags in the format "X.Y.Z".</p>
     *
     * <p>Defaults tag prefix to "v" without a suffix, (vX.Y.Z).</p>
     *
     * <p><strong>Example URL:</strong></p>
     * <p>{@code https://gitlab.example.com/namespace/project/-/releases/permalink/latest}</p>
     * <p>{@code https://github.com/user/project/releases/latest}</p>
     *
     * @param remoteURL the URL of the latest releases page
     * @return the latest version as a string (ex: "1.2.1"), or {@code null} if no valid version is found
     *
     * @see com.everdro1d.libs.swing.dialogs.UpdateCheckerDialog UpdateCheckerDialog
     * @see #getLatestVersion(String, String)
     * @see #getLatestVersion(String, String, String)
     */
    public static String getLatestVersion(String remoteURL) {
        return getLatestVersion(remoteURL, "v", "");
    }

    /**
     * Retrieves the latest version of the application from the remote's
     * releases page using releases/latest tag redirect function.
     *
     * <p>Uses version tags in the format "X.Y.Z".</p>
     *
     * <p>Defaults to tag prefix without a suffix, (&lt;your-prefix&gt;X.Y.Z).</p>
     *
     * <p><strong>Example URL:</strong></p>
     * <p>{@code https://gitlab.example.com/namespace/project/-/releases/permalink/latest}</p>
     * <p>{@code https://github.com/user/project/releases/latest}</p>
     *
     * @param remoteURL the URL of the latest releases page
     * @return the latest version as a string (ex: "1.2.1"), or {@code null} if no valid version is found
     *
     * @see com.everdro1d.libs.swing.dialogs.UpdateCheckerDialog UpdateCheckerDialog
     * @see #getLatestVersion(String)
     * @see #getLatestVersion(String, String, String)
     */
    public static String getLatestVersion(String remoteURL, String prefix) {
        return getLatestVersion(remoteURL, prefix, "");
    }

    /**
     * Retrieves the latest version of the application from the remote's
     * releases page using releases/latest tag redirect function.
     *
     * <p>Uses version tags in the format "X.Y.Z".</p>
     *
     * <p>Defaults to tag prefix without a suffix, (&lt;your-prefix&gt;X.Y.Z&lt;your-suffix&gt;).</p>
     *
     * <p><strong>Example URL:</strong></p>
     * <p>{@code https://gitlab.example.com/namespace/project/-/releases/permalink/latest}</p>
     * <p>{@code https://github.com/user/project/releases/latest}</p>
     *
     * @param remoteURL the URL of the latest releases page
     * @return the latest version as a string (ex: "1.2.1"), or {@code null} if no valid version is found
     *
     * @see com.everdro1d.libs.swing.dialogs.UpdateCheckerDialog UpdateCheckerDialog
     * @see #getLatestVersion(String)
     * @see #getLatestVersion(String, String)
     */
    public static String getLatestVersion(String remoteURL, String customPrefix, String customSuffix) {
        String location = null;

        try ( HttpClient client = HttpClient.newHttpClient() ) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(remoteURL))
                    .method("HEAD", HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());

            location = response.headers().firstValue("Location").orElse(null);

        } catch (Exception e) {
            e.printStackTrace(System.err);
        }

        if (location == null) {
            System.err.println("Error: No redirect location found in response.");
            return null;
        }

        String version = location.split(customPrefix)[1];

        if (customSuffix != null && !customSuffix.isBlank()) {
            version = version.split(customSuffix)[0];
        }

        if (validateVersion(version)) {
            return version;
        }

        System.err.println("Error: Invalid version tag in URL: " + location);
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
