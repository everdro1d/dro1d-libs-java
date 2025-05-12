// dro1dDev - created: 2025-05-09

package com.everdro1d.libs.swing;

import com.everdro1d.libs.locale.LocaleManager;
import com.everdro1d.libs.swing.components.TextFieldFileChooser;
import com.everdro1d.libs.swing.windows.settings.BasicSettingsWindow;


import javax.swing.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;

public class SwingTestBench {
    private static boolean darkMode = true;
    private static Preferences p = Preferences.userNodeForPackage(SwingTestBench.class);
    private static JFrame[] frameArr = new JFrame[2];

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SwingGUI.setupLookAndFeel(true, true, false);
            SwingGUI.uiSetup(SwingGUI.getDefaultFontNameForOS(), 18);
            System.out.println("DarkMode?" + SwingGUI.isDarkModeActive());

            LocaleManager localeManager = new LocaleManager(SwingTestBench.class, "dro1dDev");
            localeManager.loadLocaleFromFile(p.get("currentLocale","eng"));

            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            JLabel label = new JLabel("Beep");
            panel.add(label);
            TextFieldFileChooser textField = new TextFieldFileChooser(localeManager) {
                @Override
                public void customActionOnApprove(File file) {
                    System.out.println("File selected: " + file.getAbsolutePath());
                    setText(file.getParent());
                    System.out.println("TextField text: " + getText());
                }
            };
            panel.add(textField);

            JButton button = new JButton("Click me");
            button.addActionListener(e -> {
                System.out.println("Button clicked!");
                System.out.println("DarkMode?" + SwingGUI.isDarkModeActive());
                darkMode = !darkMode;
                SwingUtilities.invokeLater(() -> {
                    SwingGUI.switchLightOrDarkMode(darkMode, getFrameArr());
                });
            });
            panel.add(button);

            BasicSettingsWindow s = new BasicSettingsWindow(
                    null, SwingGUI.getDefaultFontNameForOS(), 16, p,
                    true, localeManager, panel,
                    "https://github.com/everdro1d/dro1d-libs-java",
                    "https://everdro1d.github.io/posts/dro1d-libs-java/"
            ) {
                public void applySettings() {
                    System.out.println("beep boop");
                    System.err.print(1);
                    JOptionPane.showMessageDialog(null, "Wassap??????");
                }

                public Map<String, String> setOriginalSettingsMap() {
                    return new HashMap<>();
                }

                @Override
                public Map<String, Boolean> setRestartRequiredSettingsMap() {
                    return new HashMap<>();
                }
            };

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

            //DebugConsoleWindow d = new DebugConsoleWindow(s,p,true);

            frameArr[0] = s;
            //frameArr[1] = d;

            SwingGUI.switchLightOrDarkMode(darkMode, frameArr);
        });
    }

    private static JFrame[] getFrameArr() {
        return frameArr;
    }
}
