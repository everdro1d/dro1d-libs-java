package com.everdro1d.libs.swing.windows.settings;

import com.everdro1d.libs.core.ApplicationCore;
import com.everdro1d.libs.core.LocaleManager;
import com.everdro1d.libs.core.Utils;
import com.everdro1d.libs.swing.SwingGUI;
import com.everdro1d.libs.swing.components.WindowDependentSeparator;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
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
                private JPanel autoSettingsPanel;
                    private JLabel localeSwitchLabel;
                    private JComboBox<String> localeSwitchComboBox;
            private JPanel southPanel;
                private WindowDependentSeparator southPanelSeparator;
                private JPanel lowerSouthPanel;
                    private JPanel leftLowerSouthPanel;
                        private JButton importSettings;
                        private JButton exportSettings;
                private JPanel rightLowerSouthPanel;
                    private JButton applySettings;
                    private JButton cancelSettings;
            private JPanel eastPanel;
            private JPanel westPanel;

    // UI Text
    private String titleText = "Settings";
    private String localeBorderTitleText = "Locale";
    private String localeSwitchLabelText = "Select Locale (Restart Required):";
    private String generalBorderTitleText = "General";
    private String importText = "Import";
    private String exportText = "Export";
    private String acceptText = "OK";
    private String cancelText = "Cancel";

    // End of Swing components -----------------------------------------------|
    private static LocaleManager localeManager;
    private Preferences prefs;
    private boolean debug;
    private final int WINDOW_WIDTH = 500;
    private final int WINDOW_HEIGHT = 400;
    private final int BORDER_PADDING_WIDTH = 15;
    private final int BORDER_PADDING_HEIGHT = 35;
    private final String fontName;
    private final int fontSize;
    private String selectedLocale = "";

    // End of variables -----------------------------------------------------------------------------------------------|

    public BasicSettingsWindow(
            JFrame parent, String fontName, int fontSize, Preferences prefs,
            boolean debug, LocaleManager localeManager, JPanel settingsPanel
    ) {
        this.fontName = fontName;
        this.fontSize = fontSize;
        this.prefs = prefs;
        this.debug = debug;

        if (localeManager != null) {
            BasicSettingsWindow.localeManager = localeManager;
            // if the locale does not contain the class, add it and it's components
            if (!localeManager.getClassesInLocaleMap().contains("BasicSettingsWindow")) {
                addClassToLocale();
            }
            useLocale();
        } else System.out.println("LocaleManager is null. BasicSettingsWindow will launch without localization.");

        initializeWindowProperties(parent);
        initializeGUIComponents(settingsPanel);

        settingsFrame.setVisible(true);

        SwingGUI.setHandCursorToClickableComponents(settingsFrame);

        applySettings.requestFocusInWindow();
    }

    private void addClassToLocale() {
        Map<String, Map<String, String>> map = new TreeMap<>();
        map.put("Main", new TreeMap<>());
        Map<String, String> mainMap = map.get("Main");
        mainMap.put("titleText", titleText);
        mainMap.put("localeBorderTitleText", localeBorderTitleText);
        mainMap.put("localeSwitchLabelText", localeSwitchLabelText);
        mainMap.put("generalBorderTitleText", generalBorderTitleText);
        mainMap.put("importText", importText);
        mainMap.put("exportText", exportText);
        mainMap.put("acceptText", acceptText);
        mainMap.put("cancelText", cancelText);

        localeManager.addClassSpecificMap("BasicSettingsWindow", map);
    }

    private void useLocale() {
        Map<String, String> varMap = localeManager.getAllVariablesWithinClassSpecificMap("BasicSettingsWindow");

        titleText = varMap.getOrDefault("titleText", titleText);
        localeBorderTitleText = varMap.getOrDefault("localeBorderTitleText", localeBorderTitleText);
        localeSwitchLabelText = varMap.getOrDefault("localeSwitchLabelText", localeSwitchLabelText);
        generalBorderTitleText = varMap.getOrDefault("generalBorderTitleText", generalBorderTitleText);
        importText = varMap.getOrDefault("importText", importText);
        exportText = varMap.getOrDefault("exportText", exportText);
        acceptText = varMap.getOrDefault("acceptText", acceptText);
        cancelText = varMap.getOrDefault("cancelText", cancelText);
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
                    int mac = ApplicationCore.detectOS().equals("macOS") ? 30 : 0;
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
                    autoSettingsPanel = new JPanel();
                    autoSettingsPanel.setLayout(new GridBagLayout());
                    GridBagConstraints gbcAuto = new GridBagConstraints();
                    gbcAuto.gridx = 0;
                    gbcAuto.gridy = 0;
                    gbcAuto.weightx = 0;
                    gbcAuto.weighty = 0;
                    gbcAuto.fill = GridBagConstraints.HORIZONTAL;
                    gbcAuto.anchor = GridBagConstraints.CENTER;
                    gbcAuto.insets = new Insets(4, 4, 4, 4);
                    autoSettingsPanel.setBorder(BorderFactory.createTitledBorder(
                            settingsPanel.getBorder(), localeBorderTitleText, TitledBorder.LEADING,
                            TitledBorder.TOP, new Font(fontName, Font.PLAIN, 14)
                    ));
                    centerPanel.add(autoSettingsPanel, gbc);
                    {
                        localeSwitchLabel = new JLabel(localeSwitchLabelText);
                        autoSettingsPanel.add(localeSwitchLabel, gbcAuto);

                        gbcAuto.gridx++;
                        gbcAuto.weightx = 1;
                        localeSwitchComboBox = new JComboBox<>();
                        localeSwitchComboBox.setFont(new Font(fontName, Font.PLAIN, fontSize));
                        autoSettingsPanel.add(localeSwitchComboBox, gbcAuto);

                        populateLocaleComboBox(localeSwitchComboBox, localeManager);

                        localeSwitchComboBox.setSelectedItem(localeManager.getCurrentLocaleName());

                        localeSwitchComboBox.addActionListener(e -> {
                            String selectedLocale = Utils.reverseKeyFromValueInMap(
                                    localeSwitchComboBox.getSelectedItem().toString(),
                                    localeManager.getAvailableLocales()
                            );
                            if (selectedLocale != null) {
                                this.selectedLocale = selectedLocale;
                            }
                        });
                    }
                    gbc.gridy++;
                }
                gbc.weighty = 1;
                settingsPanel.setBorder(BorderFactory.createTitledBorder(
                        settingsPanel.getBorder(), generalBorderTitleText, TitledBorder.LEADING,
                        TitledBorder.TOP, new Font(fontName, Font.PLAIN, 14)
                        )
                );
                centerPanel.add(settingsPanel, gbc);
            }

            southPanel = new JPanel();
            southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));
            southPanel.setPreferredSize(new Dimension(WINDOW_WIDTH, BORDER_PADDING_HEIGHT + 20));
            mainPanel.add(southPanel, BorderLayout.SOUTH);
            {
                southPanel.add(Box.createRigidArea(new Dimension(0, 10)));

                southPanelSeparator =
                        new WindowDependentSeparator(settingsFrame, BORDER_PADDING_WIDTH, 1);
                southPanel.add(southPanelSeparator);

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
                        importSettings = new JButton(importText);
                        importSettings.setFont(new Font(fontName, Font.PLAIN, fontSize));
                        leftLowerSouthPanel.add(importSettings);
                        importSettings.addActionListener(e -> {
                            String filePath = getFilePathUser(
                                    false, debug, settingsFrame, localeManager, prefs
                            );
                            importSettings(filePath, debug, settingsFrame);
                        });

                        exportSettings = new JButton(exportText);
                        exportSettings.setFont(new Font(fontName, Font.PLAIN, fontSize));
                        leftLowerSouthPanel.add(exportSettings);
                        exportSettings.addActionListener(e -> {
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
                        applySettings = new JButton(acceptText);
                        applySettings.setFont(new Font(fontName, Font.PLAIN, fontSize));
                        rightLowerSouthPanel.add(applySettings);
                        applySettings.addActionListener(e -> {
                            // save settings and close
                            if (!selectedLocale.isBlank()) {
                                prefs.put("currentLocale", selectedLocale);
                            }
                            applySettings();
                            // TODO: if locale changed prompt restart
                            this.dispose();
                        });

                        cancelSettings = new JButton(cancelText);
                        cancelSettings.setFont(new Font(fontName, Font.PLAIN, fontSize));
                        rightLowerSouthPanel.add(cancelSettings);
                        cancelSettings.addActionListener(e -> {
                            // cancel changes and close
                            // TODO: if settingsChanged then add confirm dialog beforehand
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

        JRootPane rootPane = SwingUtilities.getRootPane(applySettings);
        rootPane.setDefaultButton(applySettings);
    }

    private void populateLocaleComboBox(JComboBox<String> localeComboBox, LocaleManager localeManager) {
        for (String localeName : localeManager.getAvailableLocales().values()) {
            localeComboBox.addItem(localeName);
        }
    }

    /**
     * Implement your actual save mechanism here.
     */
    public abstract void applySettings();
}
