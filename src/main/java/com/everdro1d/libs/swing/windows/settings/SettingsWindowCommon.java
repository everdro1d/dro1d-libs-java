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

public class SettingsWindowCommon {
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
