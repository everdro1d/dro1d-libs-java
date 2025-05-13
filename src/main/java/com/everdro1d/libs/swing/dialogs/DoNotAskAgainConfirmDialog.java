// dro1dDev - created: 2024-03-03

package com.everdro1d.libs.swing.dialogs;

import com.everdro1d.libs.locale.LocaleManager;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.TreeMap;
import java.util.prefs.Preferences;

/**
 * A confirmation dialog with a "Do not ask again" checkbox.
 * <p>
 * This class extends JPanel and provides a static method to show a confirmation dialog with a message,
 * title, options, and a "Do not ask again" checkbox. The state of the checkbox is saved in user preferences.
 * </p>
 *
 * @see <a href="https://github.com/everdro1d/YouTubeVideoDownloader/blob/master/src/main/com/everdro1d/ytvd/ui/HistoryWindow.java#L619">DoNotAskAgainConfirmDialog Example Implementation</a>
 */
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

    /**
     * Returns the state of the "Do not ask again" checkbox.
     * @return true if the checkbox is selected, false otherwise
     */
    public boolean isDoNotAskAgainSelected() { return doNotAskAgainCheckBox.isSelected(); }

    /**
     * Shows a confirmation dialog with the selected options.
     * @param parentComponent Parent
     * @param message Message to show in the dialog &lt;html&gt; format
     * @param title Dialog title
     * @param optionType JOptionPane.[OPTIONS]
     * @param messageType JOptionPane.[MESSAGE_TYPE]
     * @param prefs user preferences to save do not ask again
     * @param prefsKey key for do not ask again
     * @param localeManager LocaleManager for localization
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
