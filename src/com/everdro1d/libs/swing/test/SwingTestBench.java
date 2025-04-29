/**************************************************************************************************
 * dro1dDev 2025.                                                                                 *
 **************************************************************************************************/

package com.everdro1d.libs.swing.test;

import com.everdro1d.libs.core.LocaleManager;
import com.everdro1d.libs.swing.SwingGUI;
import com.everdro1d.libs.swing.dialogs.SimpleWorkingDialog;
import com.everdro1d.libs.swing.windows.settings.BasicSettingsWindow;


import javax.swing.*;
import java.util.prefs.Preferences;

public class SwingTestBench {
    private static boolean darkMode = false;
    private static Preferences p = Preferences.userNodeForPackage(SwingTestBench.class);
    private static JFrame[] frameArr = new JFrame[2];

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SwingGUI.setupLookAndFeel(true, darkMode);
            SwingGUI.uiSetup("Tahoma", 18);
            System.out.println("DarkMode?" + SwingGUI.isDarkModeActive());

            LocaleManager localeManager = new LocaleManager(SwingTestBench.class);
            localeManager.loadLocaleFromFile(p.get("currentLocale","eng"));

            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            JLabel label = new JLabel("Beep");
            panel.add(label);

            BasicSettingsWindow s = new BasicSettingsWindow(
                    null, "Tahoma", 16, p,
                    true, localeManager, panel
            ) {
                public void applySettings() {
                    localeManager.reloadLocaleInProgram(p.get("currentLocale", "eng"));
                    SwingGUI.switchLightOrDarkMode(!darkMode, frameArr);
                    darkMode = !darkMode;
                }
            };

            SimpleWorkingDialog diag = new SimpleWorkingDialog(
                    "Please Wait...", localeManager
            ) {
                @Override
                public void onCancel() {}
            };
            diag.showDialog(s, false);

//            LinkedHashMap<String, JPanel> settingsMap = new LinkedHashMap<>();
//
//            JPanel settingsPanel = new JPanel();
//            TextFieldFileChooser textFieldFileChooser = new TextFieldFileChooser(localeManager, "SettingsPanel_TextField");
//            settingsPanel.add(textFieldFileChooser);
//
//            settingsMap.put("General", settingsPanel);
//            settingsMap.put("Appearance", new JPanel());
//            settingsMap.put("Advanced", new JPanel());
//
//            p.put("ababa", "wow");
//            p.put("sntoehu", "teuho");
//
//            AdvancedSettingsWindow s = new AdvancedSettingsWindow(null, p, true, localeManager, settingsMap) {
//                public void applySettings() {
//                    SwingGUI.switchLightOrDarkMode(!darkMode, frameArr);
//                    darkMode = !darkMode;
//                }
//            };

//            DebugConsoleWindow d = new DebugConsoleWindow(s,p,true);

            frameArr[0] = s;
//            frameArr[1] = d;
        });
    }
}
