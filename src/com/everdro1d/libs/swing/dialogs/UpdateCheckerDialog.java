/**************************************************************************************************
 * dro1dDev 2025.                                                                                 *
 **************************************************************************************************/

package com.everdro1d.libs.swing.dialogs;

import com.everdro1d.libs.core.ApplicationCore;
import com.everdro1d.libs.core.LocaleManager;
import com.everdro1d.libs.core.Utils;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.TreeMap;
import java.util.prefs.Preferences;

public class UpdateCheckerDialog {
    private LocaleManager localeManager;
    private static String[] updateCheckerDialogText = {
            "An update is available.","Would you like to update now?","Latest Version: v",
            "Installed Version: v","Update Available"
    };

    private UpdateCheckerDialog(LocaleManager localeManager) {
        if (localeManager != null) {
            this.localeManager = localeManager;
            // if the locale does not contain the class, add it and it's components
            if (!localeManager.getClassesInLocaleMap().contains("UpdateCheckerDialog")) {
                addClassToLocale();
            }
            useLocale();
        } else System.out.println("LocaleManager is null. UpdateCheckerDialog will launch without localization.");
    }

    /** TODO - update for localeManager
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
     * @see LocaleManager#LocaleManager(Class)
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

    private void addClassToLocale() {
        Map<String, Map<String, String>> map = new TreeMap<>();
        map.put("Main", new TreeMap<>());
        Map<String, String> mainMap = map.get("Main");
        for (int i = 0; i < updateCheckerDialogText.length; i++) {
            mainMap.put("updateCheckerDialogText"+i, updateCheckerDialogText[i]);
        }
        localeManager.addClassSpecificMap("UpdateCheckerDialog", map);
    }

    private void useLocale() {
        Map<String, String> varMap = localeManager.getAllVariablesWithinClassSpecificMap("UpdateCheckerDialog");
        for (int i = 0; i < updateCheckerDialogText.length; i++) {
            updateCheckerDialogText[i] = varMap.getOrDefault("updateCheckerDialogText"+i, updateCheckerDialogText[i]);
        }
    }

}
