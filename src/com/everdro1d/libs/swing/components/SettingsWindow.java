/**************************************************************************************************
 * Copyright (c) dro1dDev 2025.                                                                   *
 **************************************************************************************************/

package com.everdro1d.libs.swing.components;

import com.everdro1d.libs.core.ApplicationCore;
import com.everdro1d.libs.core.LocaleManager;
import com.everdro1d.libs.swing.SwingGUI;

import javax.swing.*;
import java.awt.*;
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
    private final int WINDOW_HEIGHT = 360;
    private final int EDGE_PADDING_WIDTH = 15;
    private final int BORDER_PADDING_HEIGHT = 35;
    private final String fontName;
    private final int fontSize;
    
    // End of variables -----------------------------------------------------------------------------------------------|

    /**
     * Overload Constructor with default font and null locale.
     * @param parent frame to latch onto if called from another window
     * @param prefs Preferences object for saving and loading user settings
     * @param debug whether to print debug information
     * @see SettingsWindow#SettingsWindow(JFrame, Preferences, boolean, LocaleManager)
     * @see SettingsWindow#SettingsWindow(JFrame, String, int, Preferences, boolean, LocaleManager)
     */
    public SettingsWindow(JFrame parent, Preferences prefs, boolean debug) {
        this(parent, "Tahoma", 16, prefs, debug, null);
    }

    /**
     * Overload Constructor with default font.
     * @param parent frame to latch onto if called from another window
     * @param prefs Preferences object for saving and loading user settings
     * @param debug whether to print debug information
     * @param localeManager LocaleManager object for handling locale changes
     * @see SettingsWindow#SettingsWindow(JFrame, Preferences, boolean)
     * @see SettingsWindow#SettingsWindow(JFrame, String, int, Preferences, boolean, LocaleManager)
     */
    public SettingsWindow(JFrame parent, Preferences prefs, boolean debug, LocaleManager localeManager) {
        this(parent, "Tahoma", 16, prefs, debug, localeManager);
    }

    /**
     * Create a debug console window.
     * @param parent frame to latch onto if called from another window
     * @param fontName the name of the font to use
     * @param fontSize the size of the font to use
     * @param prefs Preferences object for saving and loading user settings
     * @param debug whether to print debug information
     * @param localeManager LocaleManager object for handling locale changes
     * @see SettingsWindow#SettingsWindow(JFrame, Preferences, boolean)
     */
    public SettingsWindow(JFrame parent, String fontName, int fontSize, Preferences prefs, boolean debug, LocaleManager localeManager) {
        this.fontName = fontName;
        this.fontSize = fontSize;
        this.debug = debug;

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
                menuItemsTabbedPane = new DualAxisTabbedPane(null);// TODO: implement DualAxisTabbedPane first
            }
        }
    }

    private void resizeWindow(boolean maximized) {
        int i;
        if (!maximized) {
            i = 2;
            expandWindowButton.setIcon(iconShrink);
            if (debug) System.out.println("Maximized settings window.");
        } else {
            i = 1;
            expandWindowButton.setIcon(iconExpand);
            if (debug) System.out.println("Minimized settings window.");
        }
        settingsFrame.setSize(new Dimension(WINDOW_WIDTH * i, WINDOW_HEIGHT * i));

        leftNorthPanel.setPreferredSize(new Dimension(
                (WINDOW_WIDTH * i - (EDGE_PADDING_WIDTH * 2)) / 2, BORDER_PADDING_HEIGHT
        ));
        rightNorthPanel.setPreferredSize(new Dimension(
                (WINDOW_WIDTH * i - (EDGE_PADDING_WIDTH * 2)) / 2, BORDER_PADDING_HEIGHT
        ));

        leftSouthPanel.setPreferredSize(new Dimension(
                (WINDOW_WIDTH * i - (EDGE_PADDING_WIDTH * 2)) / 2 + 50, BORDER_PADDING_HEIGHT
        ));
        rightSouthPanel.setPreferredSize(new Dimension(
                (WINDOW_WIDTH * i - (EDGE_PADDING_WIDTH * 2)) / 2 - 50, BORDER_PADDING_HEIGHT
        ));

        SwingGUI.setLocationOnResize(settingsFrame, true);

        expandWindowButtonColorChange();

        this.maximized = !maximized;
    }

    public static void expandWindowButtonColorChange() {
        boolean darkMode = SwingGUI.isDarkModeActive();
        Color color = new Color(darkMode ? 0xbbbbbb : 0x000000);
        if (DebugConsoleWindow.debugFrame != null) {
            Icon tmp = expandWindowButton.getIcon();
            // set the icon to the colour
            Icon icon = SwingGUI.changeIconColor(tmp, color);
            expandWindowButton.setIcon(icon);
        }
    }
}
