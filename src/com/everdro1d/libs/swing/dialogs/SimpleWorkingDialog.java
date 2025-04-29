package com.everdro1d.libs.swing.dialogs;

import com.everdro1d.libs.core.LocaleManager;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.TreeMap;

public abstract class SimpleWorkingDialog extends JPanel {
    private LocaleManager localeManager;
    private static String titleText = "Working...";
    private static String progressText = "Please wait...";
    private static String cancelText = "Cancel";

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

    public abstract void onCancel();

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
