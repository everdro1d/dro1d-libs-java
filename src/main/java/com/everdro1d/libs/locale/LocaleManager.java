// dro1dDev - created: 2024-03-13

package com.everdro1d.libs.locale;

import com.everdro1d.libs.core.ApplicationCore;
import com.everdro1d.libs.io.Files;
import org.json.JSONObject;
import org.json.JSONWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

/**
 * The {@code LocaleManager} class is responsible for managing application locales, primarily for Swing GUI applications.
 * It provides functionality to load, save, and manipulate locale data, enabling dynamic text updates for UI components.
 *
 * <p><strong>Key Features:</strong></p>
 * <ul>
 *   <li>Manages locale files stored in JSON format.</li>
 *   <li>Supports ISO 639-3 language codes for locale identification.</li>
 *   <li>Allows dynamic reloading of locales and notifies listeners of locale changes.</li>
 *   <li>Provides methods to add, remove, and retrieve locale-specific text for UI components.</li>
 *   <li>Ensures locale data is saved on application shutdown if updated.</li>
 * </ul>
 *
 * <p><strong>Usage Examples:</strong></p>
 * <blockquote><pre>
 * // Initialize the LocaleManager
 * LocaleManager localeManager = new LocaleManager(Main.class, "ImADeveloper");
 *
 * // Load a locale file
 * localeManager.loadLocaleFromFile("locale_eng");
 *
 * // Retrieve text for a specific UI component
 * String buttonText = localeManager.getVariableSpecificMap("MainWindow", "button1", "text");
 *
 * // Save updated locale data
 * localeManager.saveLocaleToFile("locale_eng", localeManager.getLocaleMap(), true);
 * </pre></blockquote>
 *
 * <p><strong>Note:</strong> This class is designed to work with applications that have text based UI components, GUI or CLI.</p>
 * @see #LocaleMap
 */
public class LocaleManager {

    private final List<LocaleChangeListener> localeChangeListeners = new ArrayList<>();

    // Uses tree map to sort the locales in alphabetical order by their ISO 639 codes
    Map<String, String> validLocaleMap = new TreeMap<>();

    /**
     * The map of the locale, with the outermost key being the Class Name of the UI component,
     * the second key being the name of the component, and the third key being the descriptive
     * variable and the value being the text to display.
     * <p><strong>Example:</strong></p>
     * <blockquote><pre>
     * {
     *   "MainWindow": {
     *     "titleLabel": {
     *       "text": "My App"
     *     },
     *     "button1": {
     *       "text": "Click me!"
     *     },
     *     "JDialog1": {
     *       "titleText": "Dialog 1",
     *       "messageText": "Dialog 1"
     *     }
     *   },
     *   "DebugConsoleWindow": {
     *     "titleLabel": {
     *       "text": "Debug Console"
     *     },
     *     "closeButton": {
     *       "text": "Close"
     *     }
     *   }
     * }
     * </pre></blockquote>
     */
    Map<String, Map<String, Map<String, String>>> LocaleMap = new TreeMap<>();

    // The default path to the locales directory
    Path localeDirPath;

    private String currentLocale = "eng";
    private boolean localeMapUpdated = false;

    /**
     * Initializes the LocaleManager with the specified class and developer directory name.
     * Sets up the locale directory path and initializes the valid locales map.
     *
     * @param clazz         the class associated with the application
     * @param developerName the developer's name for directory organization
     */
    public LocaleManager(Class<?> clazz, String developerName) {
        localeDirPath = Path.of(
                ApplicationCore.getApplicationConfigDirectory(clazz, developerName) + File.separator + "locale"
        );
        initValidLocalesMap();
    }

    /**
     * Initializes the map of valid locales using ISO 639-3 codes.
     * Sorts the codes alphabetically and fixes or adds specific locale names.
     */
    private void initValidLocalesMap() {
        String[] codes = getISOCodes();
        Arrays.sort(codes);
        for (String code : codes) {
            if (code.isBlank()) continue;
            String name = Locale.of(code).getDisplayLanguage();
            validLocaleMap.put(code, name);
        }
        fixValidLocaleNames();
        addValidLocales();
    }

    /**
     * Fixes specific locale names in the valid locale map to ensure proper display.
     */
    private void fixValidLocaleNames() {
        // Fixes
        validLocaleMap.put("ces", "Czech");
        validLocaleMap.put("deu", "German");
        validLocaleMap.put("cym", "Welsh");
        validLocaleMap.put("ell", "Greek");
        validLocaleMap.put("eus", "Basque");
        validLocaleMap.put("fas", "Persian");
        validLocaleMap.put("fra", "French");
        validLocaleMap.put("hye", "Armenian");
        validLocaleMap.put("kat", "Georgian");
        validLocaleMap.put("mkd", "Macedonian");
        validLocaleMap.put("mri", "Māori");
        validLocaleMap.put("msa", "Malay");
        validLocaleMap.put("mya", "Burmese");
        validLocaleMap.put("nld", "Dutch");
        validLocaleMap.put("ron", "Romanian");
        validLocaleMap.put("slk", "Slovak");
        validLocaleMap.put("sqi", "Albanian");
        validLocaleMap.put("zho", "Chinese");
    }

    /**
     * Adds additional valid locales to the valid locale map.
     */
    private void addValidLocales() {
        // Additions
        // Ex: validLocaleMap.put("cmn", "Mandarin");
    }

    /**
     * Loads a locale from a file. Defaults to English if the file does not exist or is invalid.
     * The file name should follow the format "locale_[id]" or "[id]" (e.g., "locale_eng" or "eng").
     *
     * @param localeFileName the name of the locale file (without the file extension)
     */
    public void loadLocaleFromFile(String localeFileName) {
        String fileName = localeFileName.startsWith("locale_")
            ? localeFileName : "locale_" + localeFileName;

        String[] code = fileName.split("_");
        if (code.length == 1 || code[1] == null || code[1].isBlank()) {
            System.err.println("Locale changed but missing, defaulting to English.");
            fileName = "locale_eng";
        }

        if (!isLocaleCodeValid(fileName.split("_")[1].toLowerCase())) {
            System.err.println("Invalid locale");
            return;
        }

        boolean exists = checkForLocaleFile(fileName);
        if (!exists) {
            System.err.println("Locale file does not exist, defaulting to English.");
            fileName = "locale_eng";
            currentLocale = "eng";
            if (!checkForLocaleFile(fileName)) {
                System.err.println("Default locale file does not exist, stopping load...");
                return;
            }
        }

        Path filePath = localeDirPath.resolve(fileName + ".json");
        try {
            String fileContent = java.nio.file.Files.readString(filePath).trim();

            // locale exists? for real this time ;)
            if (fileContent.isEmpty()) {
                System.err.println("Locale file is empty, treating as if no locale file was found.");
                return;
            }

            // validate json file
            if (!fileContent.startsWith("{") || !fileContent.endsWith("}")) {
                System.err.println("Locale file is not JSON formatted. It must start with '{' and end with '}'.");
                return;
            }

            // parse json
            JSONObject root = new JSONObject(fileContent);

            for (String className : root.keySet()) {
                JSONObject uiComponents = root.getJSONObject(className);
                Map<String, Map<String, String>> uiComponentMap = new TreeMap<>();

                for (String uiComponent : uiComponents.keySet()) {
                    JSONObject uiVariables = uiComponents.getJSONObject(uiComponent);
                    Map<String, String> uiVariableMap = new TreeMap<>();

                    for (String uiVariable : uiVariables.keySet()) {
                        String text = uiVariables.getString(uiVariable);
                        uiVariableMap.put(uiVariable, text);
                    }

                    uiComponentMap.put(uiComponent, uiVariableMap);
                }

                LocaleMap.put(className, uiComponentMap);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading locale file: " + filePath, e);
        }
    }

    /**
     * Saves the provided locale map to a file. The file name should follow the format "locale_[id]" or "[id]".
     *
     * @param localeFileName the name of the locale file (without the file extension)
     * @param localeMap      the locale map to save
     * @param overwrite      whether to overwrite an existing file with the same name
     */
    public void saveLocaleToFile(String localeFileName, Map<String, Map<String, Map<String, String>>> localeMap, boolean overwrite) {
        String fileName = localeFileName.startsWith("locale_")
                ? localeFileName : "locale_" + localeFileName;

        if (!isLocaleCodeValid(fileName.split("_")[1].toLowerCase())) {
            System.err.println("Invalid locale");
            return;
        }

        boolean exists = checkForLocaleFile(fileName);
        if (exists && !overwrite) {
            System.err.println("Locale file already exists. Overwrite is disabled, stopping...");
            return;
        }

        Path filePath = Path.of(localeDirPath + "/" + fileName + ".json");
        if (java.nio.file.Files.exists(filePath)) {
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

        try (FileWriter writer = new FileWriter(filePath.toString())) {
            JSONWriter wr = new JSONWriter(writer);
            wr.object();
            for (Map.Entry<String, Map<String, Map<String, String>>> entry : localeMap.entrySet()) {
                wr.key(entry.getKey()); // Class name
                wr.object();

                for (Map.Entry<String, Map<String, String>> entry2 : entry.getValue().entrySet()) {
                    wr.key(entry2.getKey()); // Component name
                    wr.object();

                    for (Map.Entry<String, String> entry3 : entry2.getValue().entrySet()) {
                        wr.key(entry3.getKey()); // Variable name
                        wr.value(entry3.getValue()); // String value
                    }
                    wr.endObject();
                }
                wr.endObject();
            }
            wr.endObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Validates whether the provided locale code exists in the valid locale map.
     *
     * @param locale the locale code to validate
     * @return true if the locale code is valid, false otherwise
     */
    public boolean isLocaleCodeValid(String locale) {
        boolean valid = false;
        String[] codes = validLocaleMap.keySet().toArray(new String[0]);
        for (String code : codes) {
            if (code.equals(locale)) {
                valid = true;
                currentLocale = locale;
                break;
            }
        }
        return valid;
    }

    /**
     * Checks if the specified locale file exists. Creates the "/locales/" directory in application config if it does not exist.
     *
     * @param fileName the name of the locale file (ex: locale_eng)
     * @return true if the locale file exists, false otherwise
     */
    public boolean checkForLocaleFile(String fileName) {
        boolean exists = false;
        if (!java.nio.file.Files.exists(localeDirPath)) {
            try {
                java.nio.file.Files.createDirectories(localeDirPath);

            } catch (IOException e) {
                if (e.getMessage().contains("File not found")) {
                    System.err.println("The file was not found. Please check the path and try again.");
                }
                throw new RuntimeException(e);
            }
        }

        Path filePath = Path.of(localeDirPath + "/" + fileName + ".json");
        if (java.nio.file.Files.exists(filePath)) {
            exists = true;
        }

        return exists;
    }

    /**
     * Prints all valid locale codes and their corresponding language names to the console.
     */
    public void printValidLocales() {
        getValidLocaleMap().forEach((k, v) -> {
            if (!k.isEmpty() && !v.isEmpty()) {
                System.out.println(k + " : " + v);
            } else System.err.println("Invalid key or value, skipping...");
        });
    }

    /**
     * Retrieves an array of ISO 639-3 codes for all available locales.
     *
     * @return an array of 3-letter ISO 639 codes
     */
    public String[] getISOCodes() {
        Locale[] locales = Locale.getAvailableLocales();
        String[] codes = new String[locales.length];
        for (int i = 0; i < locales.length; i++) {
            codes[i] = locales[i].getISO3Language();
        }
        return codes;
    }

    /**
     * Ensures that the locale map is saved on application shutdown if it has been updated.
     */
    private void localeMapUpdated() {
        if (!localeMapUpdated) {
            Runtime.getRuntime().addShutdownHook(
                    new Thread(() -> saveLocaleToFile("locale_" + getCurrentLocale(), LocaleMap, true)));
            localeMapUpdated = true;
        }
    }

    /**
     * Reloads the locale in the program by loading the specified locale file and notifying all active {@link LocaleChangeListener}.
     * <p><strong>Example Usage:</strong></p>
     * <blockquote><pre>
     * localeManager.reloadLocaleInProgram(prefs.get("currentLocale", "eng"));
     * </pre></blockquote>
     * @param newLocale the new locale code to load
     */
    public void reloadLocaleInProgram(String newLocale) {
        if (!isLocaleCodeValid(newLocale)) {
            System.err.println("Invalid locale");
            return;
        }
        loadLocaleFromFile(newLocale);
        notifyLocaleChange();
    }

    // Getters and Setters --------------------------------------------------------------------------------------------|

    /**
     * Retrieves a map of all valid locales with their ISO 639 codes and language names.
     *
     * @return a map of valid locale ISO 639-3 codes and their respective language names.
     */
    public Map<String, String> getValidLocaleMap() {
        return validLocaleMap;
    }

    /**
     * Retrieves the entire locale map, which contains UI component text mappings.
     * <p>Outermost key being the Class Name of the UI component, and the inner key being the
     * name of the component, and the third key being the descriptive variable with the value
     * being the text to display.</p>
     *
     * @return the locale map
     */
    public Map<String, Map<String, Map<String, String>>> getLocaleMap() {
        return LocaleMap;
    }

    /**
     * Sets the locale map and marks it as updated.
     * <p>Outermost key being the Class Name of the UI component, and the inner key being the
     * name of the component, and the third key being the descriptive variable with the value
     * being the text to display.</p>
     *
     * @param LocaleMap the new locale map to set
     */
    public void setLocaleMap(Map<String, Map<String, Map<String, String>>> LocaleMap) {
        this.LocaleMap = LocaleMap;
        localeMapUpdated();
    }

    /**
     * Retrieves a list of all class names in the locale map.
     *
     * @return a list of class names
     */
    public List<String> getClassesInLocaleMap() {
        return Arrays.stream(LocaleMap.keySet().toArray(new String[0])).toList();
    }

    /**
     * Retrieves a list of all component names for a specific class in the locale map.
     *
     * @param className the class name to search for
     * @return a list of component names, or an empty list if the class is not found
     */
    public List<String> getComponentsInClassMap(String className) {
        if (!LocaleMap.containsKey(className)) {
            System.err.println("Class not found in locale map: " + className);
            return new ArrayList<>();
        }

        return Arrays.stream(LocaleMap.get(className).keySet().toArray(new String[0])).toList();
    }

    /**
     * Retrieves a list of all variable names for a specific component in the locale map.
     *
     * @param className     the class name of the component
     * @param componentName the name of the component
     * @return a list of variable names, or an empty list if the class or component is not found
     */
    public List<String> getVariablesInComponentMap(String className, String componentName) {
        if (!LocaleMap.containsKey(className)) {
            System.err.println("Class not found in locale map: " + className);
            return new ArrayList<>();
        }
        if (!LocaleMap.get(className).containsKey(componentName)) {
            System.err.println("Component not found in locale map under this class: " + componentName);
            return new ArrayList<>();
        }

        return Arrays.stream(LocaleMap.get(className).get(componentName).keySet().toArray(new String[0])).toList();
    }

    /**
     * Retrieves the locale map for a specific class.
     *
     * @param className the class name to retrieve
     * @return the locale map for the specified class, or an empty map if the class is not found
     */
    public Map<String, Map<String, String>> getClassSpecificMap(String className) {
        if (!LocaleMap.containsKey(className)) {
            System.err.println("Class not found in locale map: " + className);
            return new HashMap<>();
        }

        return LocaleMap.get(className);
    }

    /**
     * Retrieves all variables within a specific class's locale map.
     *
     * @param className the class name to retrieve
     * @return a map of all variables and their text values, or an empty map if the class is not found
     */
    public Map<String, String> getAllVariablesWithinClassSpecificMap(String className) {
        if (!LocaleMap.containsKey(className)) {
            System.err.println("Class not found in locale map: " + className);
            return new HashMap<>();
        }

        Map<String, String> allVariables = new TreeMap<>();
        for (Map.Entry<String, Map<String, String>> entry : LocaleMap.get(className).entrySet()) {
            allVariables.putAll(entry.getValue());
        }
        return allVariables;
    }

    /**
     * Adds a locale map for a specific class.
     *
     * @param className the class name to add
     * @param map       the locale map to add
     */
    public void addClassSpecificMap(String className, Map<String, Map<String, String>> map) {
        LocaleMap.put(className, map);
        localeMapUpdated();
    }

    /**
     * Removes the locale map for a specific class.
     *
     * @param className the class name to remove
     */
    public void removeClassSpecificMap(String className) {
        LocaleMap.remove(className);
        localeMapUpdated();
    }

    /**
     * Retrieves the locale map for a specific component.
     *
     * @param className     the class name of the component
     * @param componentName the name of the component
     * @return the locale map for the specified component, or an empty map if not found
     */
    public Map<String, String> getComponentSpecificMap(String className, String componentName) {
        if (!LocaleMap.containsKey(className)) {
            System.err.println("Class not found in locale map: " + className);
            return new HashMap<>();
        }
        if (!LocaleMap.get(className).containsKey(componentName)) {
            System.err.println("Component not found in locale map under this class: " + componentName);
            return new HashMap<>();
        }

        return LocaleMap.get(className).get(componentName);
    }

    /**
     * Adds a locale map for a specific component.
     *
     * @param className     the class name of the component
     * @param componentName the name of the component
     * @param map           the locale map to add
     */
    public void addComponentSpecificMap(String className, String componentName, Map<String, String> map) {
        if (!LocaleMap.containsKey(className)) {
            System.err.println("Class not found in locale map: " + className);
            return;
        }

        LocaleMap.get(className).put(componentName, map);
        localeMapUpdated();
    }

    /**
     * Removes the locale map for a specific component.
     *
     * @param className     the class name of the component
     * @param componentName the name of the component to remove
     */
    public void removeComponentSpecificMap(String className, String componentName) {
        if (!LocaleMap.containsKey(className)) {
            System.err.println("Class not found in locale map: " + className);
            return;
        }

        LocaleMap.get(className).remove(componentName);
        localeMapUpdated();
    }

    /**
     * Retrieves the text for a specific variable in the locale map.
     *
     * @param className     the class name of the component
     * @param componentName the name of the component
     * @param variableName  the name of the variable
     * @return the text for the specified variable, or null if not found
     */
    public String getVariableInComponent(String className, String componentName, String variableName) {
        if (!LocaleMap.containsKey(className)) {
            System.err.println("Class not found in locale map: " + className);
            return null;
        }
        if (!LocaleMap.get(className).containsKey(componentName)) {
            System.err.println("Component not found in locale map under this class: " + componentName);
            return null;
        }
        if (!LocaleMap.get(className).get(componentName).containsKey(variableName)) {
            System.err.println("Variable not found in locale map under this component: " + variableName);
            return null;
        }

        return LocaleMap.get(className).get(componentName).get(variableName);
    }

    /**
     * Adds a text value for a specific variable in the locale map.
     *
     * @param className     the class name of the component
     * @param componentName the name of the component
     * @param variableName  the name of the variable
     * @param text          the text value to add
     */
    public void addVariableToComponent(String className, String componentName, String variableName, String text) {
        if (!LocaleMap.containsKey(className)) {
            System.err.println("Class not found in locale map: " + className);
            return;
        }
        if (!LocaleMap.get(className).containsKey(componentName)) {
            System.err.println("Component not found in locale map under this class: " + componentName);
            return;
        }

        LocaleMap.get(className).get(componentName).put(variableName, text);
        localeMapUpdated();
    }

    /**
     * Removes a text value for a specific variable in the locale map.
     *
     * @param className     the class name of the component
     * @param componentName the name of the component
     * @param variableName  the name of the variable to remove
     */
    public void removeVariableInComponent(String className, String componentName, String variableName) {
        if (!LocaleMap.containsKey(className)) {
            System.err.println("Class not found in locale map: " + className);
            return;
        }
        if (!LocaleMap.get(className).containsKey(componentName)) {
            System.err.println("Component not found in locale map under this class: " + componentName);
            return;
        }

        LocaleMap.get(className).get(componentName).remove(variableName);
        localeMapUpdated();
    }

    /**
     * Retrieves the path to the locale directory.
     *
     * @return the path to the locale directory
     */
    public Path getLocaleDirectoryPath() {
        return localeDirPath;
    }

    /**
     * Sets the path to the locale directory.
     *
     * @param localeDirPath the new path to the locale directory
     */
    public void setLocaleDirectoryPath(Path localeDirPath) {
        this.localeDirPath = localeDirPath;
    }

    /**
     * Retrieves the current locale code.
     *
     * @return the current locale code
     */
    public String getCurrentLocale() {
        return currentLocale;
    }

    /**
     * Retrieves the name of the current locale.
     *
     * @return the name of the current locale
     */
    public String getCurrentLocaleName() {
        return validLocaleMap.get(currentLocale);
    }

    /**
     * Retrieves a map of available locales that have corresponding files in the locale directory.
     *
     * @return a map of available locale codes and their respective language names
     */
    public Map<String,String> getAvailableLocales() {
        Map<String,String> availableLocales = new HashMap<>();
        // match valid locales with locales that exist in the locale directory
        for (String code : validLocaleMap.keySet()) {
            String fileName = "locale_" + code;
            if (checkForLocaleFile(fileName)) {
                availableLocales.put(code, validLocaleMap.get(code));
            }
        }
        return availableLocales;
    }

    // LocaleChangeListener Methods -----------------------------------------------------------------------------------|

    /**
     * Adds a listener to be notified of locale changes.
     *
     * @param listener the listener to add
     */
    public void addLocaleChangeListener(LocaleChangeListener listener) {
        localeChangeListeners.add(listener);
    }

    /**
     * Removes a listener from being notified of locale changes.
     *
     * @param listener the listener to remove
     */
    public void removeLocaleChangeListener(LocaleChangeListener listener) {
        localeChangeListeners.remove(listener);
    }

    /**
     * Notifies all registered listeners of a locale change.
     */
    public void notifyLocaleChange() {
        for (LocaleChangeListener listener : localeChangeListeners) {
            listener.onLocaleChange();
        }
    }

}
