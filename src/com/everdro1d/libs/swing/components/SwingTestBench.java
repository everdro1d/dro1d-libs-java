/**************************************************************************************************
 * Copyright (c) dro1dDev 2025.                                                                   *
 **************************************************************************************************/

package com.everdro1d.libs.swing.components;

import com.everdro1d.libs.swing.SwingGUI;
import javax.swing.*;
import java.util.LinkedHashMap;
import java.util.prefs.Preferences;

public class SwingTestBench {
    private static boolean darkMode = true;
    private static Preferences p = Preferences.userNodeForPackage(SwingTestBench.class);
    private static JFrame[] frameArr = new JFrame[2];

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SwingGUI.setupLookAndFeel(true, darkMode);
            SwingGUI.uiSetup("Tahoma", 18);
            System.out.println(SwingGUI.isDarkModeActive());

            LinkedHashMap<String, JPanel> settingsMap = new LinkedHashMap<>();
            //TODO actually do something with the JPanels
            settingsMap.put("General", new JPanel());
            settingsMap.put("Appearance", new JPanel());
            settingsMap.put("Advanced", new JPanel());

            p.put("ababa", "wow");
            p.put("sntoehu", "teuho");

            SettingsWindow s = new SettingsWindow(null, p, true, settingsMap) {
                public void applySettings() {
                    System.out.println("Oops");
                    SwingGUI.switchLightOrDarkMode(!darkMode, frameArr);
                    darkMode = !darkMode; //TODO super dang simple dark mode switch
                }
            };

            DebugConsoleWindow d = new DebugConsoleWindow(s,p,true);

            frameArr[0] = s;
            frameArr[1] = d;
        });
    }
}
