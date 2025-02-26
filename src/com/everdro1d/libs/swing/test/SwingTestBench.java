/**************************************************************************************************
 * Copyright (c) dro1dDev 2025.                                                                   *
 **************************************************************************************************/

package com.everdro1d.libs.swing.test;

import com.everdro1d.libs.swing.SwingGUI;
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

            LinkedHashMap<String, JPanel> settingsMap = new LinkedHashMap<>();
            settingsMap.put("General", new JPanel());
            settingsMap.put("Appearance", new JPanel());
            settingsMap.put("Advanced", new JPanel());

            p.put("ababa", "wow");
            p.put("sntoehu", "teuho");

            SettingsWindow s = new SettingsWindow(null, p, true, settingsMap) {
                public void applySettings() {
                    SwingGUI.switchLightOrDarkMode(!darkMode, frameArr);
                    darkMode = !darkMode;
                }
            };

            frameArr[0] = s;
        });
    }
}
