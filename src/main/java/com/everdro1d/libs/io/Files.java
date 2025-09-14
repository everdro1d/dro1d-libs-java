// dro1dDev - created: 2024-03-03

package com.everdro1d.libs.io;

import com.everdro1d.libs.core.ApplicationCore;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The <strong>Files</strong> class provides utility methods for file and directory operations.
 *
 * <p>This class includes methods for retrieving JAR file paths, checking file usage,
 * deleting files, retrieving file names, opening files in the file manager,
 * validating file paths, and loading or saving key-value maps to files.</p>
 *
 * <p><strong>Key Features:</strong></p>
 * <ul>
 *   <li>Retrieve the absolute path or directory of a JAR file.</li>
 *   <li>Check if a file is currently in use by attempting a write lock.</li>
 *   <li>Delete files with optional debug output.</li>
 *   <li>Retrieve all file names or matching file names in a directory.</li>
 *   <li>Open files or directories in the system's default file manager.</li>
 *   <li>Validate file paths for different operating systems.</li>
 *   <li>Load and save key-value pairs from/to files in `key=value` format.</li>
 * </ul>
 * 
 * <p><strong>Note:</strong> This class cannot be instantiated as it is designed to be a utility class.</p>
 */
public final class Files {

    // Private constructor to prevent instantiation.
    private Files() {
        throw new UnsupportedOperationException("Files class cannot be instantiated");
    }

    /**
     * Retrieves the absolute path of the JAR file containing the specified class.
     *
     * @param clazz the class whose JAR file path is to be determined
     * @return the absolute path of the JAR file as a string, or {@code null} if the path cannot be determined
     */
    public static String getJarPath(Class<?> clazz) {
        String jarPath = null;
        try {
            jarPath = Paths.get(clazz.getProtectionDomain().getCodeSource().getLocation().toURI()).toString();
        } catch (URISyntaxException e) {
            e.printStackTrace(System.err);
        }
        return jarPath;
    }

    /**
     * Retrieves the directory containing the JAR file of the specified class.
     *
     * @param clazz the class whose JAR file directory is to be determined
     * @return the directory path of the JAR file as a string, or {@code null} if the path cannot be determined
     */
    public static String getJarDirectory(Class<?> clazz) {
        String jarPath = getJarPath(clazz);
        if (jarPath != null) {
            return new File(jarPath).getParent();
        }
        return null;
    }

    /**
     * Checks if a file is currently in use by attempting to acquire a write lock on it.
     *
     * @param filePath the path of the file to check
     * @return {@code true} if the file is in use or an error occurs, {@code false} otherwise
     */
    public static boolean isFileInUse(Path filePath) {
        try (FileChannel channel = FileChannel.open(filePath, StandardOpenOption.WRITE);
             FileLock lock = channel.tryLock()) {
            // If the lock is null, then the file is already locked
            return lock == null;
        } catch (IOException e) {
            // An exception occurred, which means the file is likely in use
            return true;
        }
    }

    /**
     * Deletes a file at the specified path.
     *
     * @param path the path of the file to delete
     */
    public static void deleteFile(String path) {
        deleteFile(path, false);
    }

    /**
     * Deletes a file at the specified path.
     *
     * @param path  the path of the file to delete
     * @param debug if {@code true}, prints debug information to System.out about the deletion process
     */
    public static void deleteFile(String path, boolean debug) {
        File fileToDelete = new File(path);
        String name = fileToDelete.getName();
        if (fileToDelete.exists()) {
            if (fileToDelete.delete()) {
                if (debug) System.out.println("Deleted file: " + name);
            } else {
                System.err.println("Failed to delete file: " + name);
            }
        }
    }

    /**
     * Delete a directory & all files within.
     * @param path path of the directory to delete
     */
    public static void deleteDirectory(String path) {
        deleteDirectory(path, false);
    }

    /**
     * Delete a directory & all files within.
     * @param path path of the directory to delete
     * @param debug whether to print debug information
     */
    public static void deleteDirectory(String path, boolean debug) {
        Path recipePath = Path.of(path);

        try (Stream<Path> paths = java.nio.file.Files.walk(recipePath)) {
            paths.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
        } catch (IOException e) {
            if (debug) System.err.println("[deleteDirectory]: Could not delete directory: " + path);
            return;
        }

        if (debug) System.out.println("[deleteDirectory]: Directory deleted: " + path);
    }

    /**
     * Retrieves a set of file names in a directory that contain a specific substring.
     *
     * @param inputDirectory the directory to search for files
     * @param contains       the substring to match in file names
     * @return a set of matching file names, or {@code null} if no matches are found
     */
    public static Set<String> getMatchingFiles(String inputDirectory, String contains) {
        Set<String> allFiles = getAllFilesInDirectory(inputDirectory);

        Set<String> wantedFiles = new HashSet<>();
        for (String file : allFiles) {
            if (file.contains(contains)) {
                wantedFiles.add(file);
            }
        }

        if (wantedFiles.isEmpty()) {
            return null;
        }

        return wantedFiles;
    }

    /**
     * Retrieves a set of all file names in a specified directory.
     *
     * @param inputDirectory the directory to retrieve file names from
     * @return a set of file names in the directory
     */
    public static Set<String> getAllFilesInDirectory(String inputDirectory) {
        return Stream.of(Objects.requireNonNull(new File(inputDirectory).listFiles()))
                .filter(file -> !file.isDirectory())
                .map(File::getName)
                .collect(Collectors.toSet());
    }

    /**
     * Opens a directory in the default file manager and selects the specified file.
     *
     * @param path the path to the file or directory to open
     */
    public static void openInFileManager(String path) {
        if (!Desktop.isDesktopSupported() || !Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
            System.err.println("Desktop not supported. Cannot open in file manager.");
            return;
        } else if (!validateFilePath(path)) {
            System.err.println("Invalid file path. Cannot open in file manager.");
            return;
        }

        String directory;
        if (!new File(path).isDirectory()) {
            String fileDiv = FileSystems.getDefault().getSeparator();
            if (fileDiv.equals("\\")) fileDiv = "\\\\";
            String fileName = path.split(fileDiv)[path.split(fileDiv).length - 1];
            directory = path.replace(fileName, "");
        } else {
            directory = path;
        }


        try {
            if (!new File(path).exists()) {
                System.err.println("File or Directory does not exist. Cannot open in file manager.");
                return;
            } else if (directory.equals(path)) {
                Desktop.getDesktop().open(new File(directory)); // Open the directory
                System.err.println("Path is not a file. Cannot select in file manager.");
                return;
            }

            // Open the directory and select the file
            String os = ApplicationCore.detectOS();
            switch (os) {
                case "windows" -> new ProcessBuilder("explorer.exe", "/select,", path).start();
                case "mac" -> new ProcessBuilder("open", "-R", path).start();
                case "unix" -> new ProcessBuilder("xdg-open", directory).start();
                default -> System.err.println("Unsupported OS: " + os + ". Cannot select in file manager.");
            }
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }

    /**
     * Validates whether a given file path is valid and exists.
     *
     * @param path the file path to validate
     * @return {@code true} if the path is valid and exists, {@code false} otherwise
     */
    public static boolean validateFilePath(String path) {
        if (path == null || path.isEmpty()) return false;

        String os = ApplicationCore.detectOS();
        if (os.equals("windows")) {
            if (!(path.contains(":") && path.contains("\\"))) return false;
        } else if (os.equals("mac") || os.equals("unix")) {
            if (!path.contains("/") || path.contains(":")) return false;
        } else {
            System.err.println("Unsupported OS: " + os + ". Cannot check file path. Assuming true.");
        }

        return new File(path).exists() || new File(path).isDirectory();
    }

    /**
     * Loads a map from a file where each line represents a key-value pair in the format `key=value`.
     * <p>
     * If the file does not exist or is empty, an error message is printed to {@code System.err}, and {@code null} is returned.
     * </p>
     *
     * @param filePath the full path of the file to load the map from
     * @return a {@code Map<String, String>} containing the key-value pairs from the file, or {@code null} if the file does not exist or is empty
     * @throws RuntimeException if an error occurs while reading the file
     * <p><strong>Example:</strong></p>
     * <blockquote><pre>
     * // File content:
     * // key1=value1
     * // key2=value2
     *
     * Map&lt;String, String&gt; map = Files.loadMapFromFile("/path/to/example.txt");
     * System.out.println(map); // {key1=value1, key2=value2}
     * </pre></blockquote>
     */
    public static Map<String,String> loadMapFromFile(String filePath) {
        return loadMapFromFile(Path.of(filePath));
    }

    /**
     * Loads a map from a file where each line represents a key-value pair in the format `key=value`.
     * <p>
     * If the file does not exist or is empty, an error message is printed to {@code System.err}, and {@code null} is returned.
     * </p>
     *
     * @param filePath the full path of the file to load the map from
     * @return a {@code Map<String, String>} containing the key-value pairs from the file, or {@code null} if the file does not exist or is empty
     * @throws RuntimeException if an error occurs while reading the file
     * <p><strong>Example:</strong></p>
     * <blockquote><pre>
     * // File content:
     * // key1=value1
     * // key2=value2
     *
     * Map&lt;String, String&gt; map = Files.loadMapFromFile("/path/to/example.txt");
     * System.out.println(map); // {key1=value1, key2=value2}
     * </pre></blockquote>
     */
    public static Map<String,String> loadMapFromFile(Path filePath) {
        if (!java.nio.file.Files.exists(filePath)) {
            System.err.println("File does not exist: " + filePath);
            return null;
        }

        try {
            String fileContent = java.nio.file.Files.readString(filePath).trim();

            // anything exists in file?
            if (fileContent.isEmpty()) {
                System.err.println("File is empty. Stopping load...");
                return null;
            }

            Map<String,String> map = new HashMap<>();

            // parse content
            String[] lines = fileContent.split("\n");

            for (String line : lines) {
                String key = line.trim().split("=")[0];
                String value = line.trim().split("=").length > 1 ? line.trim().split("=")[1] : "";
                map.put(key, value);
            }
            return map;
        } catch (IOException e) {
            throw new RuntimeException("Error reading file: " + filePath, e);
        }
    }

    /**
     * Saves a map to a file in the format `key=value` with each entry on a new line.
     * <p>
     * If the file already exists and overwrite is disabled, the method stops and prints an error message to {@code System.err}.
     * If overwrite is enabled, the existing file is deleted and replaced with the new content.
     * </p>
     *
     * @param filePath      the full filepath where the file will be saved (some/dir/filename.txt)
     * @param map       the {@code Map<String, String>} to save
     * @param overwrite whether to overwrite the file if it already exists
     * @throws RuntimeException if an error occurs while writing to the file
     * <p><strong>Example:</strong></p>
     * <blockquote><pre>
     * Map&lt;String, String&gt; map = new HashMap&lt;&gt;();
     * map.put("key1", "value1");
     * map.put("key2", "value2");
     *
     * Files.saveMapToFile("/path/to/directory/file.txt", map, true);
     * // File content:
     * // key1=value1
     * // key2=value2
     * </pre></blockquote>
     *
     * @see #saveMapToFile(String, String, Map, boolean)
     */
    public static void saveMapToFile(Path filePath, Map<String,String> map, boolean overwrite) {
        if (java.nio.file.Files.exists(filePath)) {
            if (!overwrite) {
                System.err.println("File with that name already exists. Overwrite is disabled, stopping...");
                return;
            }

            try {
                if (!Files.isFileInUse(filePath)) {
                    java.nio.file.Files.delete(filePath);
                } else {
                    throw new IOException("The file is in use");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            java.nio.file.Files.createFile(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // sort the map naturally first so the file is not all over the place
        List<Map.Entry<String, String>> sortedEntries = new ArrayList<>(map.entrySet());
        sortedEntries.sort(Map.Entry.comparingByKey());
        map = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : sortedEntries) {
            map.put(entry.getKey(), entry.getValue());
        }

        try (FileWriter wr = new FileWriter(filePath.toString())) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                wr.write(entry.getKey() + "=" + entry.getValue() + System.lineSeparator());
            }
            wr.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Saves a map to a file in the format `key=value` with each entry on a new line.
     * <p>
     * If the file already exists and overwrite is disabled, the method stops and prints an error message to {@code System.err}.
     * If overwrite is enabled, the existing file is deleted and replaced with the new content.
     * </p>
     *
     * @param path      the directory path where the file will be saved
     * @param fileName  the name of the file (without extension) to save the map to
     * @param map       the {@code Map<String, String>} to save
     * @param overwrite whether to overwrite the file if it already exists
     * @throws RuntimeException if an error occurs while writing to the file
     * <p><strong>Example:</strong></p>
     * <blockquote><pre>
     * Map&lt;String, String&gt; map = new HashMap&lt;&gt;();
     * map.put("key1", "value1");
     * map.put("key2", "value2");
     *
     * Files.saveMapToFile("/path/to/directory", "example", map, true);
     * // File content:
     * // key1=value1
     * // key2=value2
     * </pre></blockquote>
     *
     * @see #saveMapToFile(Path, Map, boolean)
     */
    public static void saveMapToFile(String path, String fileName, Map<String, String> map, boolean overwrite) {
        Path filePath = Path.of(path + File.separator + fileName + ".txt");
        saveMapToFile(filePath, map, overwrite);
    }
}
