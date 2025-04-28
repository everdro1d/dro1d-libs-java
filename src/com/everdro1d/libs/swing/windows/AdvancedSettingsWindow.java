/**************************************************************************************************
 * dro1dDev 2025.                                                                                 *
 **************************************************************************************************/

package com.everdro1d.libs.swing.windows;

import com.everdro1d.libs.core.ApplicationCore;
import com.everdro1d.libs.core.LocaleManager;
import com.everdro1d.libs.swing.SwingGUI;
import com.everdro1d.libs.swing.components.ResizeWindowButton;
import com.everdro1d.libs.swing.components.WindowDependentSeparator;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.prefs.Preferences;

import static com.everdro1d.libs.swing.windows.SettingsWindowCommon.*;

public abstract class AdvancedSettingsWindow extends JFrame {
    // Variables ------------------------------------------------------------------------------------------------------|

    // Swing components - Follow indent hierarchy for organization -----------|
    public static JFrame settingsFrame;
        private JPanel mainPanel;
            private JPanel northPanel;
                private JPanel upperNorthPanel;
                    private JPanel leftUpperNorthPanel;
                        private JLabel titleLabel;
                        private String titleText = "Settings";
                    private JPanel rightUpperNorthPanel;
                        public static JButton resizeWindowButton;
                    private WindowDependentSeparator northPanelSeparator;
            private JPanel centerPanel;
                private JTabbedPane menuItemsTabbedPane;
            private JPanel southPanel;
                private WindowDependentSeparator southPanelSeparator;
                private JPanel lowerSouthPanel;
                    private JPanel leftLowerSouthPanel;
                        private JButton importSettings;
                        private JButton exportSettings;
                    private JPanel rightLowerSouthPanel;
                        private JButton applySettings;
                        private JButton cancelSettings;


    // End of Swing components -----------------------------------------------|
    private static LocaleManager localeManager;
    private Preferences prefs;
    private boolean debug;
    private final int WINDOW_WIDTH = 600;
    private final int WINDOW_HEIGHT = 700;
    private final int BORDER_PADDING_WIDTH = 15;
    private final int BORDER_PADDING_HEIGHT = 35;
    private final String fontName;
    private final int fontSize;
    private final LinkedHashMap<String, JPanel> settingsTabPanelMap;

    // End of variables -----------------------------------------------------------------------------------------------|

    /**
     * Overload Constructor with default font and null locale.
     * @param parent frame to latch onto if called from another window
     * @param prefs Preferences object for saving and loading user settings
     * @param debug whether to print debug information
     * @see AdvancedSettingsWindow#AdvancedSettingsWindow(JFrame, Preferences, boolean, LocaleManager, LinkedHashMap)
     * @see AdvancedSettingsWindow#AdvancedSettingsWindow(JFrame, String, int, Preferences, boolean, LocaleManager, LinkedHashMap)
     */
    public AdvancedSettingsWindow(
            JFrame parent,
            Preferences prefs, boolean debug,
            LinkedHashMap<String, JPanel> settingsTabPanelMap
    ) {
        this(parent, "Tahoma", 16, prefs, debug, null, settingsTabPanelMap);
    }

    /**
     * Overload Constructor with default font.
     * @param parent frame to latch onto if called from another window
     * @param prefs Preferences object for saving and loading user settings
     * @param debug whether to print debug information
     * @param localeManager LocaleManager object for handling locale changes
     * @see AdvancedSettingsWindow#AdvancedSettingsWindow(JFrame, Preferences, boolean, LinkedHashMap)
     * @see AdvancedSettingsWindow#AdvancedSettingsWindow(JFrame, String, int, Preferences, boolean, LocaleManager, LinkedHashMap)
     */
    public AdvancedSettingsWindow(
            JFrame parent, Preferences prefs,
            boolean debug, LocaleManager localeManager,
            LinkedHashMap<String, JPanel> settingsTabPanelMap
    ) {
        this(parent, "Tahoma", 16, prefs, debug, localeManager, settingsTabPanelMap);
    }

    /**
     * Create a settings window.
     * @param parent frame to latch onto if called from another window
     * @param fontName the name of the font to use
     * @param fontSize the size of the font to use
     * @param prefs Preferences object for saving and loading user settings
     * @param debug whether to print debug information
     * @param localeManager LocaleManager object for handling locale changes
     * @param settingsTabPanelMap a map of tabs and their contents
     * @see AdvancedSettingsWindow#AdvancedSettingsWindow(JFrame, Preferences, boolean, LinkedHashMap)
     */
    public AdvancedSettingsWindow(
            JFrame parent, String fontName,
            int fontSize, Preferences prefs,
            boolean debug, LocaleManager localeManager,
            LinkedHashMap<String, JPanel> settingsTabPanelMap
    ) {
        this.fontName = fontName;
        this.fontSize = fontSize;
        this.prefs = prefs;
        this.debug = debug;
        this.settingsTabPanelMap = settingsTabPanelMap;

        if (localeManager != null) {
            AdvancedSettingsWindow.localeManager = localeManager;
            // if the locale does not contain the class, add it and it's components
            if (!localeManager.getClassesInLocaleMap().contains("AdvancedSettingsWindow")) {
                addClassToLocale();
            }
            useLocale();
        } else System.out.println("LocaleManager is null. AdvancedSettingsWindow will launch without localization.");

        initializeWindowProperties(parent);
        initializeGUIComponents();

        settingsFrame.setVisible(true);

        SwingGUI.setHandCursorToClickableComponents(settingsFrame);

        applySettings.requestFocusInWindow();
    }

    private void addClassToLocale() {
        Map<String, Map<String, String>> map = new TreeMap<>();
        map.put("Main", new TreeMap<>());
        Map<String, String> mainMap = map.get("Main");
        mainMap.put("titleText", titleText);

        localeManager.addClassSpecificMap("AdvancedSettingsWindow", map);
    }

    private void useLocale() {
        Map<String, String> varMap = localeManager.getAllVariablesWithinClassSpecificMap("AdvancedSettingsWindow");

        titleText = varMap.getOrDefault("titleText", titleText);
    }

    private void initializeWindowProperties(JFrame parent) {
        settingsFrame = this;
        settingsFrame.setTitle(titleText);
        settingsFrame.setMinimumSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        settingsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        settingsFrame.setResizable(false);
        settingsFrame.setLocationRelativeTo(parent);
    }

    private void initializeGUIComponents() {
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
                    leftUpperNorthPanel = new JPanel();
                    leftUpperNorthPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
                    leftUpperNorthPanel.setAlignmentY(TOP_ALIGNMENT);
                    leftUpperNorthPanel.setPreferredSize(new Dimension(halfSizePanelWidth, BORDER_PADDING_HEIGHT));
                    upperNorthPanel.add(leftUpperNorthPanel);
                    {
                        leftUpperNorthPanel.add(Box.createRigidArea(new Dimension(2, 0)));

                        titleLabel = new JLabel(titleText);
                        int mac = ApplicationCore.detectOS().equals("macOS") ? 30 : 0;
                        titleLabel.setPreferredSize(
                                new Dimension((int) leftUpperNorthPanel.getPreferredSize().getWidth() - mac, BORDER_PADDING_HEIGHT - 10)
                        );
                        titleLabel.setFont(new Font(fontName, Font.BOLD, fontSize + 4));
                        titleLabel.setVerticalAlignment(SwingConstants.BOTTOM);
                        leftUpperNorthPanel.add(titleLabel);
                    }

                    rightUpperNorthPanel = new JPanel();
                    rightUpperNorthPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
                    rightUpperNorthPanel.setPreferredSize(new Dimension(halfSizePanelWidth, BORDER_PADDING_HEIGHT));
                    upperNorthPanel.add(rightUpperNorthPanel);
                    {
                        resizeWindowButton = new ResizeWindowButton(
                                settingsFrame,
                                rightUpperNorthPanel,
                                WINDOW_WIDTH,
                                WINDOW_HEIGHT,
                                1.4f,
                                1.2f,
                                debug
                        ) {
                            @Override
                            public void customResizeActions() {
                                leftUpperNorthPanel.setPreferredSize(new Dimension(
                                        (getNewWidth() - (BORDER_PADDING_WIDTH * 2)) / 2, BORDER_PADDING_HEIGHT)
                                );
                                rightUpperNorthPanel.setPreferredSize(new Dimension(
                                        (getNewWidth() - (BORDER_PADDING_WIDTH * 2)) / 2, BORDER_PADDING_HEIGHT)
                                );

                                leftLowerSouthPanel.setPreferredSize(new Dimension(
                                        (getNewWidth() - (BORDER_PADDING_WIDTH * 2)) / 2 + 50, BORDER_PADDING_HEIGHT)
                                );
                                rightLowerSouthPanel.setPreferredSize(new Dimension(
                                        (getNewWidth() - (BORDER_PADDING_WIDTH * 2)) / 2 - 50, BORDER_PADDING_HEIGHT)
                                );

                                northPanelSeparator.updateWidth(settingsFrame,BORDER_PADDING_WIDTH);
                                southPanelSeparator.updateWidth(settingsFrame,BORDER_PADDING_WIDTH);
                            }
                        };
                        rightUpperNorthPanel.add(resizeWindowButton);
                    }
                }

                northPanelSeparator =
                        new WindowDependentSeparator(settingsFrame, BORDER_PADDING_WIDTH, 1);
                northPanel.add(northPanelSeparator);

                northPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }

            centerPanel = new JPanel();
            centerPanel.setLayout(new BorderLayout());
            mainPanel.add(centerPanel, BorderLayout.CENTER);
            {
                menuItemsTabbedPane = new JTabbedPane(JTabbedPane.LEFT);
                menuItemsTabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
                menuItemsTabbedPane.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT - BORDER_PADDING_HEIGHT * 2));
                centerPanel.add(menuItemsTabbedPane, BorderLayout.CENTER);
                {
                    for (Map.Entry<String, JPanel> entry : settingsTabPanelMap.entrySet()) {
                        menuItemsTabbedPane.addTab(entry.getKey(), entry.getValue());
                    }
                }
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
                    leftLowerSouthPanel.setPreferredSize(new Dimension(halfSizePanelWidth + 50, BORDER_PADDING_HEIGHT));
                    lowerSouthPanel.add(leftLowerSouthPanel);
                    {
                        importSettings = new JButton("Import");
                        importSettings.setFont(new Font(fontName, Font.PLAIN, fontSize));
                        leftLowerSouthPanel.add(importSettings);
                        importSettings.addActionListener(e -> {
                            String filePath = getFilePathUser(
                                    false, debug, settingsFrame, localeManager, prefs
                            );
                            importSettings(filePath, debug, settingsFrame);
                        });

                        exportSettings = new JButton("Export");
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
                    rightLowerSouthPanel.setPreferredSize(new Dimension(halfSizePanelWidth - 50, BORDER_PADDING_HEIGHT));
                    lowerSouthPanel.add(rightLowerSouthPanel);
                    {
                        applySettings = new JButton("OK");
                        applySettings.setFont(new Font(fontName, Font.PLAIN, fontSize));
                        rightLowerSouthPanel.add(applySettings);
                        applySettings.addActionListener(e -> {
                            // save settings and close
                            applySettings();
                            //settingsFrame.dispose();
                        });

                        cancelSettings = new JButton("Cancel");
                        cancelSettings.setFont(new Font(fontName, Font.PLAIN, fontSize));
                        rightLowerSouthPanel.add(cancelSettings);
                        cancelSettings.addActionListener(e -> {
                            // cancel settings and close
                            // if settingsChanged then add confirm dialog beforehand
                            settingsFrame.dispose();
                        });
                    }
                }
            }
        }

        JRootPane rootPane = SwingUtilities.getRootPane(applySettings);
        rootPane.setDefaultButton(applySettings);
    }

    /**
     * Implement your actual save mechanism here.
     */
    public abstract void applySettings();
}
