/**************************************************************************************************
 * dro1dDev 2025.                                                                                 *
 **************************************************************************************************/

package com.everdro1d.libs.swing.components;

import com.everdro1d.libs.locale.LocaleManager;
import com.everdro1d.libs.swing.windows.FileChooser;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.BiConsumer;

/**
 * A custom JComponent that combines a JTextField and a JButton to allow the user to select a file from the file system.
 * and then write the path to the JTextField. Also allows the user to manually enter the path into the JTextField and
 * open the FileChooser to that path.
 */
public class TextFieldFileChooser extends JComponent {

    // Variables ------------------------------------------------------------------------------------------------------|
    private String dialogTitle = "Select path:";
    private final JTextField textField;
    private final JButton button;
    private FileChooser fileChooser;
    private LocaleManager localeManager;

    // End of variables -----------------------------------------------------------------------------------------------|

    // Constructors ---------------------------------------------------------------------------------------------------|
    public TextFieldFileChooser(LocaleManager localeManager, String defaultPath) {
        this(localeManager, true, true, defaultPath);
    }

    public TextFieldFileChooser(LocaleManager localeManager, boolean selectFiles, String defaultPath) {
        this(localeManager, selectFiles, false, defaultPath);
    }

    public TextFieldFileChooser(LocaleManager localeManager) {
        this(localeManager, true, true, null);
    }

    public TextFieldFileChooser(LocaleManager localeManager, boolean selectFiles) {
        this(localeManager, selectFiles, false, null);
    }

    public TextFieldFileChooser(LocaleManager localeManager, boolean selectFiles, boolean selectDirectories) {
        this(localeManager, selectFiles, selectDirectories, null);
    }

    public TextFieldFileChooser(LocaleManager localeManager, boolean selectFiles, boolean selectDirectories, String defaultPath) {
        if (localeManager != null) {
            this.localeManager = localeManager;

            // if the locale does not contain the class or string defaults, add them
            if (!localeManager.getClassesInLocaleMap().contains("FileChooser")
                    || !localeManager.getComponentsInClassMap("FileChooser").contains("TextFieldFileChooser")
            ) {
                addComponentToClassInLocale();
            }

            useLocale();

        } else System.out.println("LocaleManager is null. TextFieldFileChooser will init without localization.");

        this.textField = new JTextField(defaultPath);
        this.button = new JButton("...");

        // Create a JPanel with a FlowLayout and add the button to it
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        buttonPanel.add(button);

        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        this.setLayout(layout);

        // Set constraints for the textField
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0; // This will make textField take up the remaining horizontal space
        gbc.weighty = 1.0; // This will make textField take up the remaining vertical space
        this.add(textField, gbc);

        // Set constraints for the buttonPanel
        gbc.gridx = 1;
        gbc.weightx = 0; // This will make buttonPanel only take up its preferred width
        this.add(buttonPanel, gbc);

        // set to fill parent container
        setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        button.addActionListener(e -> {
            this.fileChooser = new FileChooser(getText(), dialogTitle,
                    selectFiles, selectDirectories, false, null,
                    false, null, localeManager);

            fileChooser.setCurrentDirectory(new File(getText()));
            int returnValue = fileChooser.showOpenDialog(this.getRootPane());

            if (returnValue == JFileChooser.APPROVE_OPTION) {
                setText(fileChooser.getSelectedFile().getAbsolutePath());
            }
        });
    }

    private void addComponentToClassInLocale() {
        Map<String, String> fileChooserMap = new TreeMap<>();
        fileChooserMap.put("dialogTitle", dialogTitle);

        if (!localeManager.getClassesInLocaleMap().contains("FileChooser")) {
            localeManager.addClassSpecificMap("FileChooser", new TreeMap<>());
        }

        localeManager.addComponentSpecificMap("FileChooser", "TextFieldFileChooser", fileChooserMap);
    }

    private void useLocale() {
        Map<String, String> varMap =
                localeManager.getComponentSpecificMap("FileChooser", "TextFieldFileChooser");

        dialogTitle = varMap.getOrDefault("dialogTitle", dialogTitle);
    }

    // Getters and Setters --------------------------------------------------------------------------------------------|
    public JTextField getTextField() {
        return textField;
    }

    public JButton getButton() {
        return button;
    }

    public FileChooser getFileChooser() {
        return fileChooser;
    }

    public String getText() {
        return textField.getText();
    }

    public void setText(String text) {
        textField.setText(text);
    }

    public void setEnabled(boolean enabled) {
        textField.setEnabled(enabled);
        button.setEnabled(enabled);
    }

    public void setEditable(boolean editable) {
        textField.setEditable(editable);
    }

    public void setToolTipText(String text) {
        textField.setToolTipText(text);
        button.setToolTipText(text);
    }

    public void setFont(Font font) {
        textField.setFont(font);
        button.setFont(font);
    }
    public Font getFont() {
        return textField.getFont();
    }

    // Size Setters -------------------------------------------------------|
    public void setSize(Dimension size, int percentageButtonWidth, BiConsumer<JComponent, Dimension> setSizeFunc) {
        int w = size.width; // total width of component
        int h = size.height; // height of component

        int buttonWidth = Math.max( (w * percentageButtonWidth) / 100, 25 );

        int textFieldWidth = w - buttonWidth; // width of text field

        setSizeFunc.accept(textField, new Dimension(textFieldWidth, h));
        setSizeFunc.accept(button, new Dimension(buttonWidth, h));

    }

    public void setSize(Dimension size) {
        setSize(size, 10, JComponent::setSize);
    }

    public void setPreferredSize(Dimension preferredSize) {
        setSize(preferredSize, 10, JComponent::setPreferredSize);
    }

    public void setPreferredSize(Dimension preferredSize, int percentageButtonWidth) {
        setSize(preferredSize, percentageButtonWidth, JComponent::setPreferredSize);
    }

    public void setMinimumSize(Dimension minimumSize) {
        setSize(minimumSize, 10, JComponent::setMinimumSize);
    }

    public void setMinimumSize(Dimension minimumSize, int percentageButtonWidth) {
        setSize(minimumSize, percentageButtonWidth, JComponent::setMinimumSize);
    }

    public void setMaximumSize(Dimension maximumSize) {
        setSize(maximumSize, 10, JComponent::setMaximumSize);
    }

    public void setMaximumSize(Dimension maximumSize, int percentageButtonWidth) {
        setSize(maximumSize, percentageButtonWidth, JComponent::setMaximumSize);
    }

    // End of Size Setters ------------------------------------------------|

    // End of Getters and Setters -------------------------------------------------------------------------------------|

}
