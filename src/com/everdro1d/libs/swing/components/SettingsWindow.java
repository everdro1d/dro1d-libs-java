/**************************************************************************************************
 * Copyright (c) dro1dDev 2025.                                                                   *
 **************************************************************************************************/

package com.everdro1d.libs.swing.components;

import com.everdro1d.libs.core.ApplicationCore;
import com.everdro1d.libs.core.LocaleManager;
import com.everdro1d.libs.swing.SwingGUI;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.prefs.Preferences;

public class SettingsWindow extends JFrame {
    // Variables ------------------------------------------------------------------------------------------------------|

    // Swing components - Follow indent hierarchy for organization -----------|
    public static JFrame settingsFrame;
        private JPanel mainPanel;
            private JPanel northPanel;
                private JPanel leftNorthPanel;
                    private JLabel titleLabel;
                    private String titleText = "Settings";
                private JPanel rightNorthPanel;
                    public static JButton expandWindowButton;
                        private Icon iconExpand;
                        private Icon iconShrink;
            private JPanel centerPanel;
                private JTabbedPane menuItemsTabbedPane;
            private JPanel southPanel;
                private JPanel leftSouthPanel;
                    private JButton importSettings;
                    private JButton exportSettings;
                private JPanel rightSouthPanel;
                    private JButton saveSettings;
                    private JButton cancelSettings;

    // End of Swing components -----------------------------------------------|
    private static LocaleManager localeManager;
    private boolean debug;
    private boolean maximized;
    private final int WINDOW_WIDTH = 600;
    private final int WINDOW_HEIGHT = 800;
    private final int EDGE_PADDING_WIDTH = 15;
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
     * @see SettingsWindow#SettingsWindow(JFrame, Preferences, boolean, LocaleManager, LinkedHashMap)
     * @see SettingsWindow#SettingsWindow(JFrame, String, int, Preferences, boolean, LocaleManager, LinkedHashMap)
     */
    public SettingsWindow(
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
     * @see SettingsWindow#SettingsWindow(JFrame, Preferences, boolean, LinkedHashMap)
     * @see SettingsWindow#SettingsWindow(JFrame, String, int, Preferences, boolean, LocaleManager, LinkedHashMap)
     */
    public SettingsWindow(
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
     * @see SettingsWindow#SettingsWindow(JFrame, Preferences, boolean, LinkedHashMap)
     */
    public SettingsWindow(
            JFrame parent, String fontName,
            int fontSize, Preferences prefs,
            boolean debug, LocaleManager localeManager,
            LinkedHashMap<String, JPanel> settingsTabPanelMap
    ) {
        this.fontName = fontName;
        this.fontSize = fontSize;
        this.debug = debug;
        this.settingsTabPanelMap = settingsTabPanelMap;

        if (localeManager != null) {
            SettingsWindow.localeManager = localeManager;
            // if the locale does not contain the class, add it and it's components
            if (!localeManager.getClassesInLocaleMap().contains("SettingsWindow")) {
                addClassToLocale();
            }
            useLocale();
        } else System.out.println("LocaleManager is null. SettingsWindow will launch without localization.");

        initializeWindowProperties(parent);
        initializeGUIComponents(prefs);

        settingsFrame.setVisible(true);

        SwingGUI.setHandCursorToClickableComponents(settingsFrame);
    }

    private void addClassToLocale() {
        Map<String, Map<String, String>> map = new TreeMap<>();
        map.put("Main", new TreeMap<>());
        Map<String, String> mainMap = map.get("Main");
        mainMap.put("titleText", titleText);

        localeManager.addClassSpecificMap("SettingsWindow", map);
    }

    private void useLocale() {
        Map<String, String> varMap = localeManager.getAllVariablesWithinClassSpecificMap("SettingsWindow");

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

    private void initializeGUIComponents(Preferences prefs) {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        settingsFrame.add(mainPanel);
        {
            // Add components here
            northPanel = new JPanel();
            northPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
            northPanel.setPreferredSize(new Dimension(WINDOW_WIDTH, BORDER_PADDING_HEIGHT + 5));
            mainPanel.add(northPanel, BorderLayout.NORTH);
            int halfSizePanelWidth = (WINDOW_WIDTH - (EDGE_PADDING_WIDTH * 2)) / 2;
            {

                leftNorthPanel = new JPanel();
                leftNorthPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
                leftNorthPanel.setAlignmentY(TOP_ALIGNMENT);
                leftNorthPanel.setPreferredSize(new Dimension(halfSizePanelWidth, BORDER_PADDING_HEIGHT));
                northPanel.add(leftNorthPanel);
                {
                    leftNorthPanel.add(Box.createRigidArea(new Dimension(2, 0)));

                    titleLabel = new JLabel(titleText);
                    int mac = ApplicationCore.detectOS().equals("macOS") ? 30 : 0;
                    titleLabel.setPreferredSize(
                            new Dimension((int) titleLabel.getPreferredSize().getWidth() * 2 - mac, BORDER_PADDING_HEIGHT - 10)
                    );
                    titleLabel.setFont(new Font(fontName, Font.BOLD, fontSize + 4));
                    titleLabel.setVerticalAlignment(SwingConstants.BOTTOM);
                    leftNorthPanel.add(titleLabel);
                }

                rightNorthPanel = new JPanel();
                rightNorthPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
                rightNorthPanel.setPreferredSize(new Dimension(halfSizePanelWidth, BORDER_PADDING_HEIGHT));
                northPanel.add(rightNorthPanel);
                {
                    expandWindowButton = new JButton();
                    expandWindowButton.setBorderPainted(false);
                    expandWindowButton.setContentAreaFilled(false);
                    expandWindowButton.setFocusPainted(false);
                    expandWindowButton.setMargin(new Insets(0,0,15,0));

                    ImageIcon iconE = (ImageIcon) SwingGUI.getApplicationIcon("com/everdro1d/libs/swing/resources/images/size/expand.png",
                            this.getClass());
                    ImageIcon iconS = (ImageIcon) SwingGUI.getApplicationIcon("com/everdro1d/libs/swing/resources/images/size/shrink.png",
                            this.getClass());
                    iconShrink = new ImageIcon(iconS.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
                    iconExpand = new ImageIcon(iconE.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));

                    expandWindowButton.setIcon(iconExpand);
                    expandWindowButtonColorChange();

                    expandWindowButton.addActionListener(e -> resizeWindow(maximized));
                    rightNorthPanel.add(expandWindowButton);
                }
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
            southPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
            southPanel.setPreferredSize(new Dimension(WINDOW_WIDTH, BORDER_PADDING_HEIGHT + 5));
            mainPanel.add(southPanel, BorderLayout.SOUTH);
            {
                leftSouthPanel = new JPanel();
                leftSouthPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
                leftSouthPanel.setPreferredSize(new Dimension(halfSizePanelWidth + 50, BORDER_PADDING_HEIGHT));
                southPanel.add(leftSouthPanel);
                {
                    importSettings = new JButton("Import");
                    leftSouthPanel.add(importSettings);
                    importSettings.addActionListener(e -> {
                        // import settings
                    });

                    exportSettings = new JButton("Export");
                    leftSouthPanel.add(exportSettings);
                    exportSettings.addActionListener(e -> {
                        // export settings
                    });
                }

                rightSouthPanel = new JPanel();
                rightSouthPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
                rightSouthPanel.setPreferredSize(new Dimension(halfSizePanelWidth - 50, BORDER_PADDING_HEIGHT));
                southPanel.add(rightSouthPanel);
                {
                    saveSettings = new JButton("Save");
                    rightSouthPanel.add(saveSettings);
                    saveSettings.addActionListener(e -> {
                        // save settings and close
                    });

                    cancelSettings = new JButton("Cancel");
                    rightSouthPanel.add(cancelSettings);
                    cancelSettings.addActionListener(e -> {
                        // cancel settings and close
                    });
                }
            }
        }
    }

    private void resizeWindow(boolean maximized) {
        float i;
        if (!maximized) {
            i = 1.4f;
            expandWindowButton.setIcon(iconShrink);
            if (debug) System.out.println("Maximized settings window.");
        } else {
            i = 1.0f;
            expandWindowButton.setIcon(iconExpand);
            if (debug) System.out.println("Minimized settings window.");
        }
        int newWidth = Math.round(WINDOW_WIDTH * i);
        int newHeight = Math.round(WINDOW_HEIGHT * (i - 0.2f));

        settingsFrame.setSize(new Dimension(newWidth, newHeight));

        leftNorthPanel.setPreferredSize(new Dimension(
                (newWidth - (EDGE_PADDING_WIDTH * 2)) / 2, BORDER_PADDING_HEIGHT)
        );
        rightNorthPanel.setPreferredSize(new Dimension(
                (newWidth - (EDGE_PADDING_WIDTH * 2)) / 2, BORDER_PADDING_HEIGHT)
        );

        leftSouthPanel.setPreferredSize(new Dimension(
                (newWidth - (EDGE_PADDING_WIDTH * 2)) / 2 + 50, BORDER_PADDING_HEIGHT)
        );
        rightSouthPanel.setPreferredSize(new Dimension(
                (newWidth - (EDGE_PADDING_WIDTH * 2)) / 2 - 50, BORDER_PADDING_HEIGHT)
        );

        SwingGUI.setLocationOnResize(settingsFrame, true);

        expandWindowButtonColorChange();

        this.maximized = !maximized;
    }

    // overload for default color based on darkmode
    public static void expandWindowButtonColorChange() {
        boolean darkMode = SwingGUI.isDarkModeActive();
        Color color = new Color(darkMode ? 0xbbbbbb : 0x000000);
        expandWindowButtonColorChange(color);
    }

    public static void expandWindowButtonColorChange(Color color) {
        if (SettingsWindow.settingsFrame == null) {
            return;
        }
        Icon tmp = expandWindowButton.getIcon();
        // set the icon to the colour
        Icon icon = SwingGUI.changeIconColor(tmp, color);
        expandWindowButton.setIcon(icon);
    }
}
