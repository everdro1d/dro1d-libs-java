// dro1dDev - created: 2025-04-28

package com.everdro1d.libs.swing.windows.settings;

import com.everdro1d.libs.locale.LocaleManager;
import com.everdro1d.libs.io.Files;
import com.everdro1d.libs.swing.windows.FileChooser;

import javax.swing.*;
import java.io.*;
import java.nio.file.FileSystems;
import java.util.prefs.Preferences;

import static com.everdro1d.libs.core.ApplicationCore.getApplicationName;

/**
 * The {@code SettingsWindowCommon} class provides utility methods for importing and exporting user settings,
 * managing file paths for settings, and interacting with file choosers.
 * <p>
 * This class is non-instantiable and contains only static methods.
 * </p>
 *
 * <p><strong>Features:</strong></p>
 * <ul>
 *   <li>Import settings from an XML file and apply them using {@link Preferences}.</li>
 *   <li>Export current settings to an XML file using {@link Preferences}.</li>
 *   <li>Retrieve or set file paths for settings using a custom file chooser.</li>
 * </ul>
 *
 * <p><strong>Example Usage:</strong></p>
 * <blockquote><pre>
 * String filePath = SettingsWindowCommon.getFilePathUser(true, true, frame, localeManager, prefs);
 * SettingsWindowCommon.exportSettings(filePath, true, frame, prefs);
 * </pre></blockquote>
 *
 * <p><strong>Note:</strong> This class cannot be instantiated.</p>
 */
public class SettingsWindowCommon {

    // Private constructor to prevent instantiation.
    private SettingsWindowCommon() {
        throw new UnsupportedOperationException("SettingsWindowCommon class cannot be instantiated");
    }

    /**
     * Imports settings from an XML file and applies them using {@link Preferences}.
     *
     * @param filePath      the path to the XML file containing the settings
     * @param debug         whether to enable debug logging
     * @param settingsFrame the parent frame for displaying messages
     */
    public static void importSettings(
            String filePath, boolean debug,
            JFrame settingsFrame
    ) {
        if (filePath.isEmpty()) return;

        int success;
        try (InputStream isNode = new BufferedInputStream(new FileInputStream(filePath))) {
            Preferences.importPreferences(isNode);
            if (debug) System.out.println("Read settings from file.");
            success = 0;
        } catch (Exception ex) {
            success = 1;
            ex.printStackTrace(System.err);
        }

        if (success == 0) {
            if (debug)
                System.out.println("Successfully imported settings from .xml file. Showing message.");
            JOptionPane.showMessageDialog(settingsFrame,
                    "Successfully imported from:" + " \"" + filePath + "\"", "Success!",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    /**
     * Exports current settings to an XML file using {@link Preferences}.
     *
     * @param filePath      the path to save the XML file
     * @param debug         whether to enable debug logging
     * @param settingsFrame the parent frame for displaying messages
     * @param prefs         the {@link Preferences} node to export
     */
    public static void exportSettings(
            String filePath, boolean debug,
            JFrame settingsFrame, Preferences prefs
    ) {
        if (filePath.isEmpty()) return;

        int success;
        try (OutputStream osNode = new BufferedOutputStream(new FileOutputStream(filePath))) {
            prefs.exportNode(osNode);
            if (debug) System.out.println("Wrote settings to file.");
            success = 0;
        } catch (Exception ex) {
            success = 1;
            ex.printStackTrace(System.err);
        }

        if (success == 0) {
            if (debug)
                System.out.println("Successfully saved settings as .xml file. Showing message.");
            JOptionPane.showMessageDialog(settingsFrame,
                    "Successfully saved to:" + " \"" + filePath + "\"", "Success!",
                    JOptionPane.INFORMATION_MESSAGE
            );

            Files.openInFileManager(filePath);
        }
    }

    /**
     * Retrieves the file path for importing or exporting settings using a file chooser.
     *
     * @param export        whether the operation is an export
     * @param debug         whether to enable debug logging
     * @param settingsFrame the parent frame for the file chooser
     * @param localeManager the {@link LocaleManager} for localization
     * @param prefs         the {@link Preferences} node to store the file path
     * @return the selected file path, or an empty string if canceled
     */
    public static String getFilePathUser(
            boolean export, boolean debug,
            JFrame settingsFrame,
            LocaleManager localeManager,
            Preferences prefs
    ) {
        String settingsFilePath = openFileChooser(
                prefs.get("settingsFilePath", ""),
                export, settingsFrame, localeManager
        );
        if (settingsFilePath.contains("Cancel-")) return "";

        prefs.put("settingsFilePath", settingsFilePath);

        if (export) settingsFilePath = settingsFilePath + FileSystems.getDefault().getSeparator() + getApplicationName(settingsFrame.getClass()) + "_exported_settings.xml";

        if (debug) System.out.println("Settings file at: " + settingsFilePath);

        return settingsFilePath;
    }

    /**
     * Opens a file chooser dialog for selecting a file path.
     *
     * @param existingFilePath the existing file path to preselect
     * @param export           whether the operation is an export
     * @param settingsFrame    the parent frame for the file chooser
     * @param localeManager    the {@link LocaleManager} for localization
     * @return the selected file path, or a "Cancel-" prefixed string if canceled
     */
    private static String openFileChooser(
            String existingFilePath, boolean export,
            JFrame settingsFrame, LocaleManager localeManager
    ) {
        String output = System.getProperty("user.home");

        Boolean old = UIManager.getBoolean("FileChooser.readOnly");
        UIManager.put("FileChooser.readOnly", Boolean.TRUE);
        FileChooser fileChooser = new FileChooser(
                output,
                (export ? "Save To" : "Read From"),
                !export, true, true,
                "xml", true,
                (export ? (getApplicationName(settingsFrame.getClass()) + "_exported_settings.xml") : "*.xml"),
                localeManager
        );
        UIManager.put("FileChooser.readOnly", old);

        int returnValue = fileChooser.showOpenDialog(settingsFrame);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            output = fileChooser.getSelectedFile().getAbsolutePath();
        } else {
            output = "Cancel-" + existingFilePath;
        }

        return output;
    }
}
