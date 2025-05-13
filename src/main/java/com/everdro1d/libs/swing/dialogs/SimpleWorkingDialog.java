// dro1dDev - created: 2025-04-28

package com.everdro1d.libs.swing.dialogs;

import com.everdro1d.libs.locale.LocaleManager;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.TreeMap;

/**
 * The {@code SimpleWorkingDialog} class provides a customizable dialog panel
 * for displaying progress messages and an optional cancel button.
 * <p>
 * This class is designed to be extended for specific use cases and supports
 * localization through the {@link LocaleManager}.
 * </p>
 *
 * <p><strong>Features:</strong></p>
 * <ul>
 *   <li>Displays a progress bar with a customizable message.</li>
 *   <li>Supports localization for dialog components.</li>
 *   <li>Allows cancel behavior to be defined through the {@code onCancel} method.</li>
 * </ul>
 *
 * <p><strong>Example Usage:</strong></p>
 * <blockquote><pre>
 * SimpleWorkingDialog dialog = new SimpleWorkingDialog("Loading...", localeManager) {
 *     &#64;Override
 *     public void onCancel() {
 *         System.out.println("Operation canceled.");
 *     }
 * };
 * dialog.showDialog(parentFrame, true);
 * </pre></blockquote>
 *
 * <p><strong>Note:</strong> This class must implement the {@code onCancel} method.</p>
 */
public abstract class SimpleWorkingDialog extends JPanel {
    private LocaleManager localeManager;
    private static String titleText = "Working...";
    private static String progressText = "Please wait...";
    private static String cancelText = "Cancel";

    /**
     * Constructs a new {@code SimpleWorkingDialog} with the specified message and {@link LocaleManager}.
     *
     * @param message       the message to display in the dialog, can be a {@link Component} or {@link String}
     * @param localeManager the {@link LocaleManager} for localization, can be {@code null}
     */
    public SimpleWorkingDialog(Object message, LocaleManager localeManager) {
        if (localeManager != null) {
            this.localeManager = localeManager;
            // if the locale does not contain the class, add it and it's components
            if (!localeManager.getClassesInLocaleMap().contains("Dialogs")
                    || !localeManager.getComponentsInClassMap("Dialogs").contains("SimpleWorkingDialog")) {
                addComponentToClassInLocale();
            }
            useLocale();
        } else System.out.println("LocaleManager is null. SimpleWorkingDialog will launch without localization.");

        setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
        c.weighty = 0.0;
        c.insets = new Insets(10, 10, 0, 10);
        c.anchor = GridBagConstraints.NORTH;

        if (message instanceof Component) {
            add((Component) message, c);
        } else if (message != null) {
            JLabel messageLabel = new JLabel("<html>" + message + "</html>");
            messageLabel.setFont(new Font(getFont().getFontName(), Font.PLAIN, 14));
            add(messageLabel, c);
        }

        c.gridy++;
        c.weighty = 1.0;
        JProgressBar progressBar = new JProgressBar();
        progressBar.setPreferredSize(new Dimension(100, 15));
        progressBar.setIndeterminate(true);
        add(progressBar, c);
    }

    /**
     * Displays the dialog with the specified parent frame and cancelable option.
     *
     * @param parentFrame the parent frame for the dialog
     * @param cancelable  whether the dialog should include a cancel button
     */
    public void showDialog(JFrame parentFrame, boolean cancelable) {
        Object[] options = cancelable ? new Object[]{cancelText} : new Object[]{};
        JOptionPane pane = new JOptionPane(this, JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION, null, options, cancelText);
        JDialog d = pane.createDialog(parentFrame, titleText);
        d.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        d.setModal(true);
        d.setVisible(true);
        d.toFront();
        if (cancelable) {
            Object selectedValue = pane.getValue();
            if (cancelText.equals(selectedValue)) {
                onCancel();
                d.dispose();
            }
        }
    }

    /**
     * Called when the cancel button is pressed. Must be implemented.
     */
    public abstract void onCancel();

    /**
     * Disposes of the dialog.
     */
    public void dispose() {
        SwingUtilities.getWindowAncestor(this).dispose();
    }

    private void addComponentToClassInLocale() {
        Map<String, String> map = new TreeMap<>();
        map.put("titleText", titleText);
        map.put("progressText", progressText);
        map.put("cancelText", cancelText);

        if (!localeManager.getClassesInLocaleMap().contains("Dialogs")) {
            localeManager.addClassSpecificMap("Dialogs", new TreeMap<>());
        }

        localeManager.addComponentSpecificMap("Dialogs", "SimpleWorkingDialog", map);
    }

    private void useLocale() {
        Map<String, String> varMap = localeManager.getComponentSpecificMap("Dialogs", "SimpleWorkingDialog");
        titleText = varMap.getOrDefault("titleText", titleText);
        progressText = varMap.getOrDefault("progressText", progressText);
        cancelText = varMap.getOrDefault("cancelText", cancelText);
    }
}
