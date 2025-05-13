// dro1dDev - created: 2025-02-16

package com.everdro1d.libs.swing.dialogs;

import com.everdro1d.libs.core.ApplicationCore;
import com.everdro1d.libs.locale.LocaleManager;
import com.everdro1d.libs.core.Utils;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.TreeMap;
import java.util.prefs.Preferences;

/**
 * The {@code UpdateCheckerDialog} class provides functionality to check for application updates
 * and display a dialog prompting the user to update if a newer version is available.
 * <p>
 * This class integrates with GitHub release tags (formatted as "vX.Y.Z") to determine
 * the latest version and supports localization through the {@link LocaleManager}.
 * </p>
 *
 * <p><strong>Features:</strong></p>
 * <ul>
 *   <li>Checks for updates using the GitHub releases page.</li>
 *   <li>Displays a localized dialog with options to update or skip.</li>
 *   <li>Supports "Do not ask again" functionality using {@link Preferences}.</li>
 * </ul>
 *
 * <p><strong>Example Usage:</strong></p>
 * <blockquote><pre>
 * UpdateCheckerDialog.showUpdateCheckerDialog(
 *     "1.2.1", parentFrame, true,
 *     "https://github.com/user/repo/releases/latest/",
 *     "https://someurl.com/download", prefs, localeManager
 * );
 * </pre></blockquote>
 *
 * <p><strong>Note:</strong> This class is not intended to be instantiated directly.</p>
 */
public class UpdateCheckerDialog {
    private LocaleManager localeManager;
    private static String[] updateCheckerDialogText = {
            "An update is available.","Would you like to update now?","Latest Version: v",
            "Installed Version: v","Update Available"
    };

    private UpdateCheckerDialog(LocaleManager localeManager) {
        if (localeManager != null) {
            this.localeManager = localeManager;
            if (!localeManager.getClassesInLocaleMap().contains("Dialogs")
                    || !localeManager.getComponentsInClassMap("Dialogs").contains("UpdateCheckerDialog")) {
                addComponentToClassInLocale();
            }
            useLocale();
        } else System.out.println("LocaleManager is null. UpdateCheckerDialog will launch without localization.");
    }

    /**
     * Check for updates and display a dialog if an update is available.
     * @param currentVersion the current version of the application - "1.2.1"
     * @param parentFrame the parent frame of the dialog
     * @param printDebug whether to print debug information
     * @param githubURL the URL of the GitHub repository - "https://github.com/user/repo/releases/latest/"
     * @param downloadURL the URL of the download link - "https://someurl.com/download"
     * @param prefs the preferences object for saving do not ask again
     * @param localeManager locale manager obj for translations
     * @see ApplicationCore#getLatestVersion(String)
     * @see DoNotAskAgainConfirmDialog#showConfirmDialog(Component, Object, String, int, int, Preferences, String, LocaleManager)
     * @see LocaleManager#LocaleManager(Class, String)
     */
    public static void showUpdateCheckerDialog(
            String currentVersion, JFrame parentFrame, boolean printDebug,
            String githubURL, String downloadURL, Preferences prefs,
            LocaleManager localeManager
    ) {
        UpdateCheckerDialog updateCheckerDialog = new UpdateCheckerDialog(localeManager);

        String latestVersion = ApplicationCore.getLatestVersion(githubURL);
        if (latestVersion == null) {
            System.err.println("Failed to check for update. Latest Version returned null.");
            return;
        }

        if (latestVersion.equals(currentVersion)) {
            if (printDebug) System.out.println("Application up to date.");
            return;
        }

        if (printDebug) System.out.println("Application update available.");

        int dialogResult = DoNotAskAgainConfirmDialog.showConfirmDialog(parentFrame,
                updateCheckerDialogText[0]+"<br>"+updateCheckerDialogText[1]+"<br><br>"+updateCheckerDialogText[2]+latestVersion+
                        "<br>"+updateCheckerDialogText[3] +
                        currentVersion, updateCheckerDialogText[4],
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, prefs, "doNotAskAgainUpdateDialog",
                localeManager
        );

        if (dialogResult == JOptionPane.YES_OPTION) {
            Utils.openLink(downloadURL);
            System.exit(0);
        }
    }

    private void addComponentToClassInLocale() {
        Map<String, String> map = new TreeMap<>();
        for (int i = 0; i < updateCheckerDialogText.length; i++) {
            map.put("updateCheckerDialogText"+i, updateCheckerDialogText[i]);
        }

        if (!localeManager.getClassesInLocaleMap().contains("Dialogs")) {
            localeManager.addClassSpecificMap("Dialogs", new TreeMap<>());
        }

        localeManager.addComponentSpecificMap("Dialogs", "UpdateCheckerDialog", map);
    }

    private void useLocale() {
        Map<String, String> varMap = localeManager.getComponentSpecificMap("Dialogs", "UpdateCheckerDialog");
        for (int i = 0; i < updateCheckerDialogText.length; i++) {
            updateCheckerDialogText[i] = varMap.getOrDefault("updateCheckerDialogText"+i, updateCheckerDialogText[i]);
        }
    }

}
