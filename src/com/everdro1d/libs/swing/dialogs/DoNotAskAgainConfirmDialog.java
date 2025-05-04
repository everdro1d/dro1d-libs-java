/**************************************************************************************************
 * dro1dDev 2025.                                                                                 *
 **************************************************************************************************/

package com.everdro1d.libs.swing.dialogs;

import com.everdro1d.libs.locale.LocaleManager;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.TreeMap;
import java.util.prefs.Preferences;

public class DoNotAskAgainConfirmDialog extends JPanel {
    private LocaleManager localeManager;
    private final JCheckBox doNotAskAgainCheckBox;
        private String doNotAskAgainCheckBoxText = "Don't ask me again";

    private DoNotAskAgainConfirmDialog(Object message, LocaleManager localeManager) {
        setLayout(new BorderLayout());

        if (localeManager != null) {
            this.localeManager = localeManager;
            if (!localeManager.getClassesInLocaleMap().contains("Dialogs")
                    || !localeManager.getComponentsInClassMap("Dialogs").contains("DoNotAskAgainConfirmDialog")) {
                addComponentToClassInLocale();
            }
            useLocale();
        } else System.out.println("LocaleManager is null. DoNotAskAgainConfirmDialog will launch without localization.");


        if (message instanceof Component) {
            add((Component) message);
        } else if (message != null) {
            JLabel messageLabel = new JLabel("<html>" + message + "</html>");
            messageLabel.setFont(new Font(getFont().getFontName(), Font.PLAIN, 14));
            add(messageLabel);
        }

        doNotAskAgainCheckBox = new JCheckBox(doNotAskAgainCheckBoxText);
        JPanel checkBoxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        checkBoxPanel.add(doNotAskAgainCheckBox);
        add(checkBoxPanel, BorderLayout.SOUTH);
    }

    public boolean isDoNotAskAgainSelected() { return doNotAskAgainCheckBox.isSelected(); }

    /**
     * Shows a confirmation dialog with the selected options.
     * @param parentComponent Parent
     * @param message Message to show in the dialog <html> format
     * @param title Dialog title
     * @param optionType JOptionPane.[OPTIONS]
     * @param messageType JOptionPane.[MESSAGE_TYPE]
     * @param prefs user preferences to save do not ask again
     * @param prefsKey key for do not ask again
     * @return int selected option
     */
    public static int showConfirmDialog(
            Component parentComponent, Object message, String title,
            int optionType, int messageType, Preferences prefs, String prefsKey,
            LocaleManager localeManager
    ) {
        int result;

        if (prefs.getBoolean(prefsKey, false)) {
            return JOptionPane.YES_OPTION;
        }

        DoNotAskAgainConfirmDialog confirmDialog = new DoNotAskAgainConfirmDialog(message, localeManager);

        result = JOptionPane.showOptionDialog(
                parentComponent, confirmDialog, title, optionType,
                messageType, null, null, null
        );

        if (confirmDialog.isDoNotAskAgainSelected()) {
            prefs.putBoolean(prefsKey, true);
        }

        return result;
    }

    private void addComponentToClassInLocale() {
        Map<String, String> map = new TreeMap<>();
        map.put("doNotAskAgainCheckBoxText", doNotAskAgainCheckBoxText);

        if (!localeManager.getClassesInLocaleMap().contains("Dialogs")) {
            localeManager.addClassSpecificMap("Dialogs", new TreeMap<>());
        }

        localeManager.addComponentSpecificMap("Dialogs", "DoNotAskAgainConfirmDialog", map);
    }

    private void useLocale() {
        Map<String, String> varMap = localeManager.getComponentSpecificMap("Dialogs", "DoNotAskAgainConfirmDialog");
        doNotAskAgainCheckBoxText = varMap.getOrDefault("doNotAskAgainCheckBoxText", doNotAskAgainCheckBoxText);
    }
}
