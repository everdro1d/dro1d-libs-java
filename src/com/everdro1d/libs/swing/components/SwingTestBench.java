/**************************************************************************************************
 * Copyright (c) dro1dDev 2025.                                                                   *
 **************************************************************************************************/

package com.everdro1d.libs.swing.components;

import com.everdro1d.libs.swing.SwingGUI;
import javax.swing.*;
import java.util.LinkedHashMap;

public class SwingTestBench {
    private static boolean darkMode = true;

    public static void main(String[] args) {

        SwingGUI.setLookAndFeel(true, darkMode);
        SwingGUI.uiSetup(darkMode,"Arial", 18);
        System.out.println(SwingGUI.isDarkModeActive());

        LinkedHashMap<String, JPanel> settingsMap = new LinkedHashMap<>();
        settingsMap.put("General", new JPanel());
        settingsMap.put("Appearance", new JPanel());
        settingsMap.put("Advanced", new JPanel());

        SettingsWindow s = new SettingsWindow(null, null, true, darkMode, settingsMap);
    }
}
