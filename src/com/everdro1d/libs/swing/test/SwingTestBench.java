/**************************************************************************************************
 * dro1dDev 2025.                                                                                 *
 **************************************************************************************************/

package com.everdro1d.libs.swing.test;

import com.everdro1d.libs.core.LocaleManager;
import com.everdro1d.libs.swing.SwingGUI;
import com.everdro1d.libs.swing.components.TextFieldFileChooser;
import com.everdro1d.libs.swing.windows.SettingsWindow;

import javax.swing.*;
import java.util.LinkedHashMap;
import java.util.prefs.Preferences;

public class SwingTestBench {
    private static boolean darkMode = true;
    private static Preferences p = Preferences.userNodeForPackage(SwingTestBench.class);
    private static JFrame[] frameArr = new JFrame[1];

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SwingGUI.setupLookAndFeel(true, darkMode);
            SwingGUI.uiSetup("Tahoma", 18);
            System.out.println(SwingGUI.isDarkModeActive());

            LocaleManager localeManager = new LocaleManager(SwingTestBench.class);
            localeManager.loadLocaleFromFile("eng");

            LinkedHashMap<String, JPanel> settingsMap = new LinkedHashMap<>();

            JPanel settingsPanel = new JPanel();
            TextFieldFileChooser textFieldFileChooser = new TextFieldFileChooser(localeManager, "SettingsPanel_TextField");
            settingsPanel.add(textFieldFileChooser);

            settingsMap.put("General", settingsPanel);
            settingsMap.put("Appearance", new JPanel());
            settingsMap.put("Advanced", new JPanel());

            p.put("ababa", "wow");
            p.put("sntoehu", "teuho");

            SettingsWindow s = new SettingsWindow(null, p, true, localeManager, settingsMap) {
                public void applySettings() {
                    SwingGUI.switchLightOrDarkMode(!darkMode, frameArr);
                    darkMode = !darkMode;
                }
            };

            frameArr[0] = s;
        });
    }
}
