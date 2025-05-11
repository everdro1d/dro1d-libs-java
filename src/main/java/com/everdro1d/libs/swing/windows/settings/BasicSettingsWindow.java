// dro1dDev - created: 2025-05-09

package com.everdro1d.libs.swing.windows.settings;

import com.everdro1d.libs.core.ApplicationCore;
import com.everdro1d.libs.locale.LocaleManager;
import com.everdro1d.libs.core.Utils;
import com.everdro1d.libs.io.Files;
import com.everdro1d.libs.swing.ImageUtils;
import com.everdro1d.libs.swing.SwingGUI;
import com.everdro1d.libs.swing.components.FlexWidthSeparator;
import com.everdro1d.libs.swing.components.HyperLinkButton;
import com.everdro1d.libs.swing.components.WindowDependentSeparator;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.prefs.Preferences;

import static com.everdro1d.libs.swing.windows.settings.SettingsWindowCommon.*;

public abstract class BasicSettingsWindow extends JFrame {
    // Variables ------------------------------------------------------------------------------------------------------|

    // Swing components - Follow indent hierarchy for organization -----------|
    public static JFrame settingsFrame;
        private JPanel mainPanel;
            private JPanel northPanel;
                private JPanel upperNorthPanel;
                    private JLabel titleLabel;
                private WindowDependentSeparator northPanelSeparator;
            private JPanel centerPanel;
                private JPanel localeSettingsPanel;
                    private JLabel localeSwitchLabel;
                    private JComboBox<String> localeSwitchComboBox;
                    private JButton openLocaleDirectoryButton;
                    private JButton openLocaleRepositoryButton;
            private JPanel southPanel;
                private JPanel upperSouthPanel;
                    private FlexWidthSeparator southPanelSeparator;
                    private HyperLinkButton hyperlinkButton;
                private JPanel lowerSouthPanel;
                    private JPanel leftLowerSouthPanel;
                        private JButton importSettingsButton;
                        private JButton exportSettingsButton;
                private JPanel rightLowerSouthPanel;
                    private JButton applySettingsButton;
                    private JButton cancelSettingsButton;
            private JPanel eastPanel;
            private JPanel westPanel;

    // UI Text
    private String titleText = "Settings";
    private String localeBorderTitleText = "Locale";
    private String localeSwitchLabelText = "Change Language (Restart Required):";
    private String openLocaleDirectoryToolTipText = "Open Locale Directory";
    private String openLocaleRepositoryToolTipText = "Open Locale Website";
    private String openHelpWebsiteToolTipText = "Open Help Website";
    private String generalBorderTitleText = "General";
    private String importText = "Import";
    private String importButtonToolTipText = "Import Settings";
    private String exportText = "Export";
    private String exportButtonToolTipText = "Export Settings";
    private String acceptText = "OK";
    private String cancelText = "Cancel";
    private String confirmCancelDialogMessageText = "You have unsaved changes. Are you sure you want to cancel?";
    private String confirmCancelDialogTitleText = "Unsaved Changes!";
    private String restartRequiredDialogMessageText = "Some changes require a restart to take effect. Would you like to exit now?";
    private String restartRequiredDialogTitleText = "Restart Required!";

    // End of Swing components -----------------------------------------------|
    private static LocaleManager localeManager;
    private Preferences prefs;
    private Map<String,String> originalSettingsMap = new HashMap<>();
    private Map<String,Boolean> restartRequiredSettingsMap = new HashMap<>();
    private boolean debug;
    private final int WINDOW_WIDTH = 500;
    private final int WINDOW_HEIGHT = 400;
    private final int BORDER_PADDING_WIDTH = 15;
    private final int BORDER_PADDING_HEIGHT = 35;
    private final String fontName;
    private final int fontSize;
    private String localeRepositoryURL;
    private String helpWebsiteURL;

    // End of variables -----------------------------------------------------------------------------------------------|

    public BasicSettingsWindow(
            JFrame parent, String fontName, int fontSize, Preferences prefs,
            boolean debug, LocaleManager localeManager, JPanel settingsPanel,
            String localeRepositoryURL, String helpWebsiteURL
    ) {
        this.fontName = fontName;
        this.fontSize = fontSize;
        this.prefs = prefs;
        this.debug = debug;
        this.localeRepositoryURL = localeRepositoryURL;
        this.helpWebsiteURL = helpWebsiteURL;

        originalSettingsMap.clear();
        restartRequiredSettingsMap.clear();

        if (localeManager != null) {
            BasicSettingsWindow.localeManager = localeManager;

            originalSettingsMap.put("currentLocale", localeManager.getCurrentLocale());
            restartRequiredSettingsMap.put("currentLocale", true);

            // if the locale does not contain the class, add it and it's components
            if (!localeManager.getClassesInLocaleMap().contains("BasicSettingsWindow")
                    || !localeManager.getComponentsInClassMap("BasicSettingsWindow").contains("Defaults")
                    || !localeManager.getComponentsInClassMap("Dialogs").contains("BasicSettingsWindow")
                    || !localeManager.getComponentsInClassMap("Dialogs").contains("BasicSettingsWindow")
            ) {
                addComponentToClassInLocale();
            }
            useLocale();
        } else System.out.println("LocaleManager is null. BasicSettingsWindow will launch without localization.");

        initializeWindowProperties(parent);
        initializeGUIComponents(settingsPanel);

        themeChanged();

        settingsFrame.setVisible(true);

        SwingGUI.setHandCursorToClickableComponents(settingsFrame);

        applySettingsButton.requestFocusInWindow();

        originalSettingsMap.putAll(setOriginalSettingsMap());
        restartRequiredSettingsMap.putAll(setRestartRequiredSettingsMap());
    }

    private void addComponentToClassInLocale() {
        Map<String, String> map = new TreeMap<>();
        map.put("titleText", titleText);
        map.put("importText", importText);
        map.put("importButtonToolTipText", importButtonToolTipText);
        map.put("exportText", exportText);
        map.put("exportButtonToolTipText", exportButtonToolTipText);
        map.put("acceptText", acceptText);
        map.put("cancelText", cancelText);
        map.put("localeBorderTitleText", localeBorderTitleText);
        map.put("localeSwitchLabelText", localeSwitchLabelText);
        map.put("openLocaleDirectoryButtonText", openLocaleDirectoryToolTipText);
        map.put("openLocaleRepositoryTooltipText", openLocaleRepositoryToolTipText);
        map.put("openHelpWebsiteTooltipText", openHelpWebsiteToolTipText);
        map.put("generalBorderTitleText", generalBorderTitleText);

        if (!localeManager.getClassesInLocaleMap().contains("BasicSettingsWindow")) {
            localeManager.addClassSpecificMap("BasicSettingsWindow", new TreeMap<>());
        }

        localeManager.addComponentSpecificMap("BasicSettingsWindow", "Defaults", map);

        // ---

        Map<String,String> dialogMap = new TreeMap<>();

        dialogMap.put("confirmCancelDialogMessageText", confirmCancelDialogMessageText);
        dialogMap.put("confirmCancelDialogTitleText", confirmCancelDialogTitleText);
        dialogMap.put("restartRequiredDialogMessageText", restartRequiredDialogMessageText);
        dialogMap.put("restartRequiredDialogTitleText", restartRequiredDialogTitleText);

        if (!localeManager.getClassesInLocaleMap().contains("Dialogs")) {
            localeManager.addClassSpecificMap("Dialogs", new TreeMap<>());
        }

        localeManager.addComponentSpecificMap("Dialogs", "BasicSettingsWindow", dialogMap);
    }

    private void useLocale() {
        Map<String, String> varMap = localeManager.getComponentSpecificMap("BasicSettingsWindow", "Defaults");

        titleText = varMap.getOrDefault("titleText", titleText);
        localeBorderTitleText = varMap.getOrDefault("localeBorderTitleText", localeBorderTitleText);
        localeSwitchLabelText = varMap.getOrDefault("localeSwitchLabelText", localeSwitchLabelText);
        openLocaleDirectoryToolTipText = varMap.getOrDefault("openLocaleDirectoryButtonText", openLocaleDirectoryToolTipText);
        openLocaleRepositoryToolTipText = varMap.getOrDefault("openLocaleRepositoryButtonText", openLocaleRepositoryToolTipText);
        openHelpWebsiteToolTipText = varMap.getOrDefault("openHelpWebsiteTooltipText", openHelpWebsiteToolTipText);
        generalBorderTitleText = varMap.getOrDefault("generalBorderTitleText", generalBorderTitleText);
        importText = varMap.getOrDefault("importText", importText);
        importButtonToolTipText = varMap.getOrDefault("importButtonToolTipText", importButtonToolTipText);
        exportText = varMap.getOrDefault("exportText", exportText);
        exportButtonToolTipText = varMap.getOrDefault("exportButtonToolTipText", exportButtonToolTipText);
        acceptText = varMap.getOrDefault("acceptText", acceptText);
        cancelText = varMap.getOrDefault("cancelText", cancelText);

        Map<String, String> dialogMap = localeManager.getComponentSpecificMap("Dialogs", "BasicSettingsWindow");

        confirmCancelDialogMessageText = dialogMap.getOrDefault("confirmCancelDialogMessageText", confirmCancelDialogMessageText);
        confirmCancelDialogTitleText = dialogMap.getOrDefault("confirmCancelDialogTitleText", confirmCancelDialogTitleText);
        restartRequiredDialogMessageText = dialogMap.getOrDefault("restartRequiredDialogMessageText", restartRequiredDialogMessageText);
        restartRequiredDialogTitleText = dialogMap.getOrDefault("restartRequiredDialogTitleText", restartRequiredDialogTitleText);

    }

    private void initializeWindowProperties(JFrame parent) {
        settingsFrame = this;
        settingsFrame.setTitle(titleText);
        settingsFrame.setMinimumSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        settingsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        settingsFrame.setResizable(false);
        settingsFrame.setLocationRelativeTo(parent);
    }

    private void initializeGUIComponents(JPanel settingsPanel) {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        settingsFrame.add(mainPanel);
        {
            // Add components here
            northPanel = new JPanel();
            northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));
            northPanel.setPreferredSize(new Dimension(WINDOW_WIDTH, BORDER_PADDING_HEIGHT + 20));
            mainPanel.add(northPanel, BorderLayout.NORTH);
            int halfSizePanelWidth = (WINDOW_WIDTH - (BORDER_PADDING_WIDTH * 2)) / 2;
            {
                upperNorthPanel = new JPanel();
                upperNorthPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
                upperNorthPanel.setPreferredSize(new Dimension(WINDOW_WIDTH, BORDER_PADDING_HEIGHT + 5));
                northPanel.add(upperNorthPanel);
                {
                    upperNorthPanel.add(Box.createRigidArea(new Dimension(2, 0)));

                    titleLabel = new JLabel(titleText);
                    int mac = ApplicationCore.detectOS().equals("mac") ? 30 : 0;
                    titleLabel.setPreferredSize(
                            new Dimension((int) upperNorthPanel.getPreferredSize().getWidth() - mac, BORDER_PADDING_HEIGHT - 10)
                    );
                    titleLabel.setFont(new Font(fontName, Font.BOLD, fontSize + 4));
                    titleLabel.setHorizontalAlignment(SwingConstants.LEFT);
                    titleLabel.setVerticalAlignment(SwingConstants.BOTTOM);
                    upperNorthPanel.add(titleLabel);
                }

                northPanelSeparator =
                        new WindowDependentSeparator(settingsFrame, BORDER_PADDING_WIDTH, 1);
                northPanel.add(northPanelSeparator);

                northPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }

            centerPanel = new JPanel();
            centerPanel.setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.weightx = 1;
            gbc.weighty = 0;
            gbc.fill = GridBagConstraints.BOTH;
            mainPanel.add(centerPanel, BorderLayout.CENTER);
            {
                if (localeManager != null) {
                    localeSettingsPanel = new JPanel();
                    localeSettingsPanel.setLayout(new GridBagLayout());
                    GridBagConstraints gbcAuto = new GridBagConstraints();
                    gbcAuto.gridx = 0;
                    gbcAuto.gridy = 0;
                    gbcAuto.weightx = 0;
                    gbcAuto.weighty = 0;
                    gbcAuto.fill = GridBagConstraints.HORIZONTAL;
                    gbcAuto.anchor = GridBagConstraints.CENTER;
                    gbcAuto.insets = new Insets(4, 4, 4, 4);
                    localeSettingsPanel.setBorder(BorderFactory.createTitledBorder(
                            settingsPanel.getBorder(), localeBorderTitleText, TitledBorder.LEADING,
                            TitledBorder.TOP, new Font(fontName, Font.PLAIN, fontSize - 2)
                    ));
                    centerPanel.add(localeSettingsPanel, gbc);
                    {
                        localeSwitchLabel = new JLabel(localeSwitchLabelText);
                        localeSwitchLabel.setFont(new Font(fontName, Font.PLAIN, fontSize));
                        localeSettingsPanel.add(localeSwitchLabel, gbcAuto);

                        gbcAuto.gridx++;
                        gbcAuto.weightx = 1;
                        localeSwitchComboBox = new JComboBox<>();
                        localeSwitchComboBox.setFont(new Font(fontName, Font.PLAIN, fontSize));
                        localeSettingsPanel.add(localeSwitchComboBox, gbcAuto);

                        populateLocaleComboBox(localeSwitchComboBox, localeManager);

                        localeSwitchComboBox.setSelectedItem(localeManager.getCurrentLocaleName());

                        localeSwitchComboBox.addActionListener(e -> {
                            String selectedLocale = Utils.reverseKeyFromValueInMap(
                                    localeSwitchComboBox.getSelectedItem().toString(),
                                    localeManager.getAvailableLocales()
                            );
                            if (selectedLocale != null && !selectedLocale.isBlank()) {
                                prefs.put("currentLocale", selectedLocale);
                            }
                        });

                        gbcAuto.gridx = 3;
                        gbcAuto.fill = GridBagConstraints.NONE;
                        gbcAuto.weightx = 0;
                        openLocaleDirectoryButton = new JButton();
                        openLocaleDirectoryButton.setPreferredSize(new Dimension(25, 25));
                        openLocaleDirectoryButton.setIcon(ImageUtils.getApplicationIcon("main/resources/images/icons/folder.png", this.getClass()));
                        openLocaleDirectoryButton.setToolTipText(openLocaleDirectoryToolTipText);
                        localeSettingsPanel.add(openLocaleDirectoryButton, gbcAuto);
                        openLocaleDirectoryButton.addActionListener(e -> {
                            Path localePath = localeManager.getLocaleDirectoryPath();
                            if (localePath != null) {
                                Files.openInFileManager(localePath.toString());
                            } else {
                                System.out.println("Locale path is null.");
                            }
                        });

                        gbcAuto.gridx = 4;
                        gbcAuto.fill = GridBagConstraints.NONE;
                        gbcAuto.weightx = 0;
                        openLocaleRepositoryButton = new JButton();
                        openLocaleRepositoryButton.setPreferredSize(new Dimension(25, 25));
                        openLocaleRepositoryButton.setIcon(ImageUtils.getApplicationIcon("main/resources/images/icons/open-external.png", this.getClass()));
                        openLocaleRepositoryButton.setToolTipText(openLocaleRepositoryToolTipText);
                        localeSettingsPanel.add(openLocaleRepositoryButton, gbcAuto);
                        openLocaleRepositoryButton.addActionListener(e -> {
                            Utils.openLink(localeRepositoryURL);
                        });
                    }
                    gbc.gridy++;
                }
                gbc.weighty = 1;
                settingsPanel.setBorder(BorderFactory.createTitledBorder(
                        settingsPanel.getBorder(), generalBorderTitleText, TitledBorder.LEADING,
                        TitledBorder.TOP, new Font(fontName, Font.PLAIN, fontSize - 2)
                        )
                );
                centerPanel.add(settingsPanel, gbc);
            }

            southPanel = new JPanel();
            southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));
            southPanel.setPreferredSize(new Dimension(WINDOW_WIDTH, BORDER_PADDING_HEIGHT + 20));
            mainPanel.add(southPanel, BorderLayout.SOUTH);
            {
                upperSouthPanel = new JPanel(new GridBagLayout());
                upperSouthPanel.setBorder(BorderFactory.createEmptyBorder(0,BORDER_PADDING_WIDTH,0,BORDER_PADDING_WIDTH));
                upperSouthPanel.setPreferredSize(
                        new Dimension(
                                WINDOW_WIDTH - (BORDER_PADDING_WIDTH * 2),
                                upperSouthPanel.getPreferredSize().height
                        ));
                GridBagConstraints gbcSeparator = new GridBagConstraints();
                gbcSeparator.gridx = 0;
                gbcSeparator.gridy = 0;
                gbcSeparator.weightx = 0;
                gbcSeparator.fill = GridBagConstraints.HORIZONTAL;
                {
                    gbcSeparator.weightx = 1;
                    gbcSeparator.weighty = 0;
                    southPanelSeparator = new FlexWidthSeparator();
                    upperSouthPanel.add(southPanelSeparator, gbcSeparator);

                    gbcSeparator.gridx++;
                    gbcSeparator.weightx = 0;
                    gbcSeparator.weighty = 1;
                    hyperlinkButton = new HyperLinkButton(
                            helpWebsiteURL, openHelpWebsiteToolTipText,
                            SwingConstants.LEFT, fontName, (fontSize - 5)
                    );
                    upperSouthPanel.add(hyperlinkButton, gbcSeparator);
                }
                southPanel.add(upperSouthPanel);

                lowerSouthPanel = new JPanel();
                lowerSouthPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
                lowerSouthPanel.setPreferredSize(new Dimension(WINDOW_WIDTH, BORDER_PADDING_HEIGHT + 5));
                southPanel.add(lowerSouthPanel);
                {
                    leftLowerSouthPanel = new JPanel();
                    leftLowerSouthPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
                    leftLowerSouthPanel.setPreferredSize(new Dimension(halfSizePanelWidth, BORDER_PADDING_HEIGHT));
                    lowerSouthPanel.add(leftLowerSouthPanel);
                    {
                        importSettingsButton = new JButton(importText);
                        importSettingsButton.setFont(new Font(fontName, Font.PLAIN, fontSize));
                        importSettingsButton.setToolTipText(importButtonToolTipText);
                        leftLowerSouthPanel.add(importSettingsButton);
                        importSettingsButton.addActionListener(e -> {
                            String filePath = getFilePathUser(
                                    false, debug, settingsFrame, localeManager, prefs
                            );
                            importSettings(filePath, debug, settingsFrame);
                        });

                        exportSettingsButton = new JButton(exportText);
                        exportSettingsButton.setFont(new Font(fontName, Font.PLAIN, fontSize));
                        exportSettingsButton.setToolTipText(exportButtonToolTipText);
                        leftLowerSouthPanel.add(exportSettingsButton);
                        exportSettingsButton.addActionListener(e -> {
                            String filePath = getFilePathUser(
                                    true, debug, settingsFrame, localeManager, prefs
                            );
                            exportSettings(filePath, debug, settingsFrame, prefs);
                        });
                    }

                    rightLowerSouthPanel = new JPanel();
                    rightLowerSouthPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
                    rightLowerSouthPanel.setPreferredSize(new Dimension(halfSizePanelWidth, BORDER_PADDING_HEIGHT));
                    lowerSouthPanel.add(rightLowerSouthPanel);
                    {
                        applySettingsButton = new JButton(acceptText);
                        applySettingsButton.setFont(new Font(fontName, Font.PLAIN, fontSize));
                        rightLowerSouthPanel.add(applySettingsButton);
                        applySettingsButton.addActionListener(e -> {
                            if (!settingsChanged()) {
                                this.dispose();
                                return;
                            }

                            if (localeManager != null) {
                                boolean hasLocaleChanged = !prefs.get("currentLocale", "").equals(originalSettingsMap.get("currentLocale"));
                                if (hasLocaleChanged) {
                                    localeManager.reloadLocaleInProgram(prefs.get("currentLocale", "eng"));
                                }
                            }

                            applySettings();

                            if (isRestartRequired()) {
                                promptRestart();
                            }

                            this.dispose();
                        });

                        cancelSettingsButton = new JButton(cancelText);
                        cancelSettingsButton.setFont(new Font(fontName, Font.PLAIN, fontSize));
                        rightLowerSouthPanel.add(cancelSettingsButton);
                        cancelSettingsButton.addActionListener(e -> {
                            if (settingsChanged()) {
                                int confirm = JOptionPane.showConfirmDialog(
                                    settingsFrame,
                                    confirmCancelDialogMessageText,
                                    confirmCancelDialogTitleText,
                                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE
                                );
                                if (confirm != JOptionPane.YES_OPTION) {
                                    return;
                                }

                                // reset settings in prefs
                                resetSettings();
                            }

                            this.dispose();
                        });
                    }
                }
            }

            eastPanel = new JPanel();
            eastPanel.setPreferredSize(new Dimension(BORDER_PADDING_WIDTH, WINDOW_HEIGHT));
            mainPanel.add(eastPanel, BorderLayout.EAST);

            westPanel = new JPanel();
            westPanel.setPreferredSize(new Dimension(BORDER_PADDING_WIDTH, WINDOW_HEIGHT));
            mainPanel.add(westPanel, BorderLayout.WEST);
        }

        JRootPane rootPane = SwingUtilities.getRootPane(applySettingsButton);
        rootPane.setDefaultButton(applySettingsButton);
    }

    private void populateLocaleComboBox(JComboBox<String> localeComboBox, LocaleManager localeManager) {
        for (String localeName : localeManager.getAvailableLocales().values()) {
            localeComboBox.addItem(localeName);
        }
    }

    private void themeChanged() {
        Icon folderIcon = ImageUtils.changeIconColor(
                openLocaleDirectoryButton.getIcon(),
                UIManager.getColor("RootPane.foreground")
        );
        Icon repoIcon = ImageUtils.changeIconColor(
                openLocaleRepositoryButton.getIcon(),
                UIManager.getColor("RootPane.foreground")
        );
        openLocaleDirectoryButton.setIcon(folderIcon);
        openLocaleRepositoryButton.setIcon(repoIcon);
    }

    private boolean settingsChanged() {
        for (String key : originalSettingsMap.keySet()) {
            String currentValue = prefs.get(key, "");
            String originalValue = String.valueOf(originalSettingsMap.get(key));
            if (currentValue != null && !currentValue.equals(originalValue)) {
                return true;
            }
        }
        return false;
    }

    private void resetSettings() {
        for (String key : originalSettingsMap.keySet()) {
            String originalValue = originalSettingsMap.get(key);
            if (originalValue != null) {
                prefs.put(key, originalValue);
            }
        }
    }

    private boolean isRestartRequired() {
        for (String key : restartRequiredSettingsMap.keySet()) {
            boolean requiresRestart = restartRequiredSettingsMap.get(key);
            boolean settingChanged = !prefs.get(key, "").equals(originalSettingsMap.get(key));
            if (requiresRestart && settingChanged) {
                return true;
            }
        }

        return false;
    }

    private void promptRestart() {
        int restart = JOptionPane.showConfirmDialog(
            this,
                restartRequiredDialogMessageText,
                restartRequiredDialogTitleText,
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (restart == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    /**
     * Implement your actual save mechanism here.
     */
    public abstract void applySettings();

    /**
     * Add current settings for GeneralSettingsPanel here ex:<p>
     *     <h2>NOTE! Name these keys the same as your prefs keys!</h2><p>
     * <blockquote><pre>
     *     Map<String,String> originalSettings = new HashMap<>();
     *         originalSettings.put("currentLocale",
     *             localeManager.getCurrentLocale())
     *         );
     *     return originalSettings;
     * </pre></blockquote>
     */
    public abstract Map<String,String> setOriginalSettingsMap();

    /**
     * Add current settings for GeneralSettingsPanel here ex:<p>
     *     <h2>NOTE! Name these keys the same as your prefs keys!</h2><p>
     * <blockquote><pre>
     *     Map<String,String> restartRequiredSettings = new HashMap<>();
     *         restartRequiredSettings.put("currentLocale", true));
     *     return restartRequiredSettings;
     * </pre></blockquote>
     */
    public abstract Map<String,Boolean> setRestartRequiredSettingsMap();

    public Map<String,String> getOriginalSettingsMap() {
        return originalSettingsMap;
    }

    public Map<String,Boolean> getRestartRequiredSettingsMap() {
        return restartRequiredSettingsMap;
    }
}
