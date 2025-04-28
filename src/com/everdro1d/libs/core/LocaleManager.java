/**************************************************************************************************
 * dro1dDev 2025.                                                                                 *
 **************************************************************************************************/

package com.everdro1d.libs.core;

import com.everdro1d.libs.io.Files;
import org.json.JSONObject;
import org.json.JSONWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

/**
 * Manages the locales for the application.
 * <p>
 * Mainly meant for swing gui applications, but can be used for other types that need text on the UI as well.
 */
public class LocaleManager {

    private final List<LocaleChangeListener> localeChangeListeners = new ArrayList<>();

    // Uses tree map to sort the locales in alphabetical order by their ISO 639 codes
    Map<String, String> validLocaleMap = new TreeMap<>();

    /** The map of the locale, with the outermost key being the Class Name of the UI component, and the second key being the
     * name of the component, and the third key being the descriptive variable and the value being the text to display.
     * <p>
     * Example:
     * <pre>
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
     * </pre>
     */
    Map<String, Map<String, Map<String, String>>> LocaleMap = new TreeMap<>();

    // The default path to the locales directory
    Path localeDirPath;

    private String currentLocale = "eng";
    private boolean localeMapUpdated = false;

    public LocaleManager(Class<?> mainClazz) {
        localeDirPath = Path.of(Path.of(Files.getJarPath(mainClazz)).getParent() + "/locale");
        initValidLocalesMap();
    }

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

    private void addValidLocales() {
        // Additions
        validLocaleMap.put("cmn", "Mandarin");
    }

    /**
     * Loads a locale from a file. Defaults to English if the file does not exist.
     * Expects "locale_[id]". If only "[id]", then prepends "locale_"
     *
     * @param localeFileName The name of the file or locale to load the locale from
     *                       **without the file extension** ex: "locale_eng" or "eng".
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
     * Saves a locale file from a localeMap
     * Expects "locale_[id]". If only "[id]", then prepends "locale_"
     *
     * @param localeFileName The name of the file or locale to load the locale from
     *                       **without the file extension** ex: "locale_eng" or "eng".
     * @param localeMap the locale map
     * @param overwrite whether to overwrite any existing file with the same name
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
     * Validates a locale code
     *
     * @param locale The locale code to validate
     * @return True if the locale code is valid, false otherwise
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
     * Checks if the locale file exists, also creates the "/locales/" directory in the jar path if it doesn't exist yet.
     *
     * @param fileName The file name of the locale ex: locale_eng
     * @return True if the locale file exists, false otherwise
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
     * Prints all valid locale codes with names to the console
     */
    public void printValidLocales() {
        getValidLocaleMap().forEach((k, v) -> {
            if (!k.isEmpty() && !v.isEmpty()) {
                System.out.println(k + " : " + v);
            } else System.err.println("Invalid key or value, skipping...");
        });
    }

    /**
     * Returns an array of ISO 639 codes for all available locales
     *
     * @return An array of 3-letter ISO codes
     */
    public String[] getISOCodes() {
        Locale[] locales = Locale.getAvailableLocales();
        String[] codes = new String[locales.length];
        for (int i = 0; i < locales.length; i++) {
            codes[i] = locales[i].getISO3Language();
        }
        return codes;
    }

    private void localeMapUpdated() {
        if (!localeMapUpdated) {
            Runtime.getRuntime().addShutdownHook(
                    new Thread(() -> saveLocaleToFile("locale_" + getCurrentLocale(), LocaleMap, true)));
            localeMapUpdated = true;
        }
    }

    /**
     *
     * ex: localeManager.reloadLocaleInProgram(prefs.get("currentLocale", "eng"));
     * @param newLocale
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
     * Returns a map of all available locales
     *
     * @return A map of ISO 639 codes and their respective language names
     */
    public Map<String, String> getValidLocaleMap() {
        return validLocaleMap;
    }

    /**
     * Gets the map of the locale, with the outermost key being the Class Name of the UI component, and the second key being the
     * name of the component, and the third key being the descriptive variable and the value being the text to display.
     *
     * @return Map
     */
    public Map<String, Map<String, Map<String, String>>> getLocaleMap() {
        return LocaleMap;
    }

    // Modifies the LocaleMap -----------------------------------------------------------------------------------------|
    /**
     * Sets the map of the locale, with the outermost key being the Class Name of the UI component, and the second key being the
     * name of the component, and the third key being the descriptive variable and the value being the text to display.
     *
     * @param LocaleMap the map of the locale
     */
    public void setLocaleMap(Map<String, Map<String, Map<String, String>>> LocaleMap) {
        this.LocaleMap = LocaleMap;
        localeMapUpdated();
    }

    public List<String> getClassesInLocaleMap() {
        return Arrays.stream(LocaleMap.keySet().toArray(new String[0])).toList();
    }

    public List<String> getComponentsInClassMap(String className) {
        if (!LocaleMap.containsKey(className)) {
            System.err.println("Class not found in locale map: " + className);
            return new ArrayList<>();
        }

        return Arrays.stream(LocaleMap.get(className).keySet().toArray(new String[0])).toList();
    }

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
     * Gets the map of the locale for a specific class
     *
     * @param className The class name of the inner map to get
     * @return Map
     */
    public Map<String, Map<String, String>> getClassSpecificMap(String className) {
        if (!LocaleMap.containsKey(className)) {
            System.err.println("Class not found in locale map: " + className);
            return new HashMap<>();
        }

        return LocaleMap.get(className);
    }

    /**
     * Gets all variables within a class specific map
     *
     * @param className The class name to get
     * @return Map
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
     * Adds a map to the locale for a specific class
     *
     * @param className The class name to set
     * @param map       the map of the locale
     */
    public void addClassSpecificMap(String className, Map<String, Map<String, String>> map) {
        LocaleMap.put(className, map);
        localeMapUpdated();
    }

    /**
     * Removes a map to the locale for a specific class
     *
     * @param className The class name to remove
     */
    public void removeClassSpecificMap(String className) {
        LocaleMap.remove(className);
        localeMapUpdated();
    }

    /**
     * Gets the map of the locale for a specific component
     *
     * @param className     The class name of the component
     * @param componentName The name of the component
     * @return Map
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
     * Adds a map to the locale for a specific component
     *
     * @param className     The class name of the component
     * @param componentName The name of the component
     * @param map           the map of the locale
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
     * Removes a map to the locale for a specific  component
     *
     * @param className     The class name of the component
     * @param componentName The name of the component to remove
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
     * Gets the text of the locale for a specific  variable
     *
     * @param className     The class name of the  component
     * @param componentName The name of the  component
     * @param variableName  The name of the  variable
     * @return String
     */
    public String getVariableSpecificMap(String className, String componentName, String variableName) {
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
     * Adds a map to the locale for a specific  variable
     *
     * @param className     The class name of the  component
     * @param componentName The name of the  component
     * @param variableName  The name of the  variable
     * @param text          the text of the locale
     */
    public void addVariableSpecificMap(String className, String componentName, String variableName, String text) {
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
     * Removes a map to the locale for a specific  variable
     *
     * @param className     The class name of the component
     * @param componentName The name of the component
     * @param variableName  The name of the variable to remove
     */
    public void removeVariableSpecificMap(String className, String componentName, String variableName) {
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
     * Gets the path to the locales directory
     *
     * @return Path
     */
    public Path getLocaleDirPath() {
        return localeDirPath;
    }

    /**
     * Sets the path to the locales directory
     *
     * @param localeDirPath the path to the locales directory
     */
    public void setLocaleDirPath(Path localeDirPath) {
        this.localeDirPath = localeDirPath;
    }

    public String getCurrentLocale() {
        return currentLocale;
    }

    public String getCurrentLocaleName() {
        return validLocaleMap.get(currentLocale);
    }

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

    // LocaleChangeListener methods -------------------------------------------------------------------------------|

    /**
     * Adds a listener to the list of listeners
     * @param listener The listener to add
     */
    public void addLocaleChangeListener(LocaleChangeListener listener) {
        localeChangeListeners.add(listener);
    }

    /**
     * Removes a listener from the list of listeners
     * @param listener The listener to remove
     */
    public void removeLocaleChangeListener(LocaleChangeListener listener) {
        localeChangeListeners.remove(listener);
    }

    /**
     * Notifies all listeners of a locale change
     */
    public void notifyLocaleChange() {
        for (LocaleChangeListener listener : localeChangeListeners) {
            listener.onLocaleChange();
        }
    }


}
