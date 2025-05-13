// dro1dDev - created: 2024-03-21

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
 * The {@code TextFieldFileChooser} class provides a text field with a button that opens a file chooser dialog.
 * <p>
 * This class is designed to be used in Swing applications and supports localization through the {@link LocaleManager}.
 * </p>
 *
 * <p><strong>Features:</strong></p>
 * <ul>
 *   <li>Text field for displaying the selected file path.</li>
 *   <li>Button to open a file chooser dialog.</li>
 *   <li>Customizable dialog title and behavior.</li>
 * </ul>
 *
 * <p><strong>Example Usage:</strong></p>
 * <blockquote><pre>
 * TextFieldFileChooser fileChooser = new TextFieldFileChooser(localeManager);
 * fileChooser.setText("~/path/to/file.txt");
 * </pre></blockquote>
 * <p><strong>Or:</strong></p>
 * <blockquote><pre>
 * TextFieldFileChooser fileChooser = new TextFieldFileChooser(localeManager) {
 *     &#64;Override
 *     public void customActionOnApprove(File file) {
 *     // Custom action when a file is approved
 *     System.out.println("File selected: " + file.getAbsolutePath());
 *     }
 * }
 * </pre></blockquote>
 */
public class TextFieldFileChooser extends JComponent {

    // Variables --------------------------------------------------------------|
    private String dialogTitle = "Select path:";
    private final JTextField textField;
    private final JButton button;
    private FileChooser fileChooser;
    private LocaleManager localeManager;

    // End of variables -------------------------------------------------------|

    // Constructors -----------------------------------------------------------|

    /**
     * Constructor for creating a TextFieldFileChooser with default options.
     *
     * <p><strong>Default params set to:</strong></p>
     * <blockquote><pre>
     * selectFiles = true
     * selectDirectories = true
     * defaultPath = null
     * </pre></blockquote>
     *
     * @param localeManager the LocaleManager for localization. can be null
     */
    public TextFieldFileChooser(LocaleManager localeManager) {
        this(localeManager, true, true, null);
    }

    /**
     * Constructor for creating a TextFieldFileChooser with customizable options.
     *
     * <p><strong>Default params set to:</strong></p>
     * <blockquote><pre>
     * selectFiles = true
     * selectDirectories = true
     * </pre></blockquote>
     *
     * @param localeManager       the LocaleManager for localization. can be null
     * @param defaultPath         the default path to display in the text field
     */
    public TextFieldFileChooser(
            LocaleManager localeManager, String defaultPath
    ) {
        this(localeManager, true, true, defaultPath);
    }

    /**
     * Constructor for creating a TextFieldFileChooser with customizable options.
     *
     * <p><strong>Default params set to:</strong></p>
     * <blockquote><pre>
     * selectDirectories = false
     * defaultPath = null
     * </pre></blockquote>
     *
     * @param localeManager       the LocaleManager for localization. can be null
     * @param selectFiles         whether to allow file selection
     */
    public TextFieldFileChooser(
            LocaleManager localeManager, boolean selectFiles
    ) {
        this(localeManager, selectFiles, false, null);
    }

    /**
     * Constructor for creating a TextFieldFileChooser with customizable options.
     *
     * <p><strong>Default params set to:</strong></p>
     * <blockquote><pre>
     * selectDirectories = false
     * </pre></blockquote>
     *
     * @param localeManager       the LocaleManager for localization. can be null
     * @param selectFiles         whether to allow file selection
     * @param defaultPath         the default path to display in the text field
     */
    public TextFieldFileChooser(
            LocaleManager localeManager, boolean selectFiles, String defaultPath
    ) {
        this(localeManager, selectFiles, false, defaultPath);
    }

    /**
     * Constructor for creating a TextFieldFileChooser with customizable options.
     *
     * <p><strong>Default params set to:</strong></p>
     * <blockquote><pre>
     * defaultPath = null
     * </pre></blockquote>
     *
     * @param localeManager       the LocaleManager for localization. can be null
     * @param selectFiles         whether to allow file selection
     * @param selectDirectories   whether to allow directory selection
     */
    public TextFieldFileChooser(
            LocaleManager localeManager, boolean selectFiles,
            boolean selectDirectories
    ) {
        this(localeManager, selectFiles, selectDirectories, null);
    }

    /**
     * Constructor for creating a TextFieldFileChooser with customizable options.
     *
     * @param localeManager       the LocaleManager for localization. can be null
     * @param selectFiles         whether to allow file selection
     * @param selectDirectories   whether to allow directory selection
     * @param defaultPath         the default path to display in the text field
     */
    public TextFieldFileChooser(
            LocaleManager localeManager, boolean selectFiles,
            boolean selectDirectories, String defaultPath
    ) {
        if (localeManager != null) {
            this.localeManager = localeManager;

            // if the locale does not contain the
            // class or string defaults, add them.
            if (!localeManager.getClassesInLocaleMap().contains("FileChooser")
                    || !localeManager.getComponentsInClassMap("FileChooser")
                        .contains("TextFieldFileChooser")
            ) {
                addComponentToClassInLocale();
            }

            useLocale();

        } else System.out.println(
                "LocaleManager is null. "
                    + "TextFieldFileChooser will init without localization."
        );

        this.textField = new JTextField(defaultPath);
        this.button = new JButton("...");

        // Create a JPanel with a FlowLayout and add the button to it
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        buttonPanel.add(button);

        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        this.setLayout(layout);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        this.add(textField, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0;
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
                customActionOnApprove(fileChooser.getSelectedFile());
            }
        });
    }

    /**
     * Override this method to perform custom actions when a file is approved.
     * For example, you can open the file or perform some other action
     * @param file the file that was approved
     */
    public void customActionOnApprove(File file) {}

    private void addComponentToClassInLocale() {
        Map<String, String> fileChooserMap = new TreeMap<>();
        fileChooserMap.put("dialogTitle", dialogTitle);

        if (!localeManager.getClassesInLocaleMap().contains("FileChooser")) {
            localeManager.addClassSpecificMap("FileChooser", new TreeMap<>());
        }

        localeManager.addComponentSpecificMap(
                "FileChooser", "TextFieldFileChooser", fileChooserMap
        );
    }

    private void useLocale() {
        Map<String, String> varMap =
                localeManager.getComponentSpecificMap("FileChooser", "TextFieldFileChooser");

        dialogTitle = varMap.getOrDefault("dialogTitle", dialogTitle);
    }

    // Getters and Setters ----------------------------------------------------|

    /**
     * Gets the text field component.
     *
     * @return the {@link JTextField} used in this component
     */
    public JTextField getTextField() {
        return textField;
    }

    /**
     * Gets the button component.
     *
     * @return the {@link JButton} used in this component
     */
    public JButton getButton() {
        return button;
    }

    /**
     * Gets the file chooser instance.
     *
     * @return the {@link FileChooser} instance used in this component
     */
    public FileChooser getFileChooser() {
        return fileChooser;
    }

    /**
     * Gets the text currently displayed in the text field.
     *
     * @return the text in the text field
     */
    public String getText() {
        return textField.getText();
    }

    /**
     * Sets the text to be displayed in the text field.
     *
     * @param text the text to set in the text field
     */
    public void setText(String text) {
        textField.setText(text);
    }

    /**
     * Enables or disables the text field and button.
     *
     * @param enabled {@code true} to enable, {@code false} to disable
     */
    public void setEnabled(boolean enabled) {
        textField.setEnabled(enabled);
        button.setEnabled(enabled);
    }

    /**
     * Sets whether the text field is editable.
     *
     * @param editable {@code true} to make the text field editable, {@code false} otherwise
     */
    public void setEditable(boolean editable) {
        textField.setEditable(editable);
    }

    /**
     * Sets the tooltip text for both the text field and button.
     *
     * @param text the tooltip text to set
     */
    public void setToolTipText(String text) {
        textField.setToolTipText(text);
        button.setToolTipText(text);
    }

    /**
     * Sets the font for both the text field and button.
     *
     * @param font the {@link Font} to set
     */
    public void setFont(Font font) {
        textField.setFont(font);
        button.setFont(font);
    }

    /**
     * Gets the font used by the text field.
     *
     * @return the {@link Font} of the text field
     */
    public Font getFont() {
        return textField.getFont();
    }

    // Size Setters -----------------------------------------------------------|
    /**
     * Sets the size of the component, dividing the width between the text field
     * and button based on the specified percentage for the button width.
     *
     * @param size                  the total size of the component
     * @param percentageButtonWidth the percentage of the total width allocated to the button
     * @param setSizeFunc           a function to apply the size to the components
     */
    public void setSize(
            Dimension size, int percentageButtonWidth,
            BiConsumer<JComponent, Dimension> setSizeFunc
    ) {
        int w = size.width; // total width of component
        int h = size.height; // height of component

        int buttonWidth = Math.max( (w * percentageButtonWidth) / 100, 25 );

        int textFieldWidth = w - buttonWidth; // width of text field

        setSizeFunc.accept(textField, new Dimension(textFieldWidth, h));
        setSizeFunc.accept(button, new Dimension(buttonWidth, h));

    }

    /**
     * Sets the preferred size of the component.
     *
     * @param size the preferred size of the component
     */
    public void setSize(Dimension size) {
        setSize(size, 10, JComponent::setSize);
    }

    /**
     * Sets the preferred size of the component.
     *
     * @param preferredSize the preferred size of the component
     */
    public void setPreferredSize(Dimension preferredSize) {
        setSize(preferredSize, 10, JComponent::setPreferredSize);
    }

    /**
     * Sets the preferred size of the component, dividing the width between the text field and button
     * based on the specified percentage for the button width.
     *
     * @param preferredSize         the preferred size of the component
     * @param percentageButtonWidth the percentage of the total width allocated to the button
     */
    public void setPreferredSize(
            Dimension preferredSize, int percentageButtonWidth
    ) {
        setSize(
                preferredSize, percentageButtonWidth,
                JComponent::setPreferredSize
        );
    }

    /**
     * Sets the minimum size of the component, allocating 10% of the width to the button by default.
     *
     * @param minimumSize the minimum size of the component
     */
    public void setMinimumSize(Dimension minimumSize) {
        setSize(minimumSize, 10, JComponent::setMinimumSize);
    }

    /**
     * Sets the minimum size of the component, dividing the width between the text field and button
     * based on the specified percentage for the button width.
     *
     * @param minimumSize           the minimum size of the component
     * @param percentageButtonWidth the percentage of the total width allocated to the button
     */
    public void setMinimumSize(Dimension minimumSize, int percentageButtonWidth
    ) {
        setSize(minimumSize, percentageButtonWidth, JComponent::setMinimumSize);
    }

    /**
     * Sets the maximum size of the component, allocating 10% of the width to the button by default.
     *
     * @param maximumSize the maximum size of the component
     */
    public void setMaximumSize(Dimension maximumSize) {
        setSize(maximumSize, 10, JComponent::setMaximumSize);
    }

    /**
     * Sets the maximum size of the component, dividing the width between the text field and button
     * based on the specified percentage for the button width.
     *
     * @param maximumSize           the maximum size of the component
     * @param percentageButtonWidth the percentage of the total width allocated to the button
     */
    public void setMaximumSize(Dimension maximumSize, int percentageButtonWidth
    ) {
        setSize(maximumSize, percentageButtonWidth, JComponent::setMaximumSize);
    }

    // End of Size Setters ----------------------------------------------------|

    // End of Getters and Setters ---------------------------------------------|

}
