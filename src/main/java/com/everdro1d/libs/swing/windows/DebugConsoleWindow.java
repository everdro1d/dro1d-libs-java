// dro1dDev - created: 2024-03-06

package com.everdro1d.libs.swing.windows;

import com.everdro1d.libs.core.ApplicationCore;
import com.everdro1d.libs.locale.LocaleManager;
import com.everdro1d.libs.core.Utils;
import com.everdro1d.libs.io.Files;
import com.everdro1d.libs.io.TiedOutputStream;
import com.everdro1d.libs.swing.SwingGUI;
import com.everdro1d.libs.swing.components.ResizeWindowButton;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.*;
import java.nio.file.FileSystems;
import java.util.Map;
import java.util.TreeMap;
import java.util.prefs.Preferences;

public class DebugConsoleWindow extends JFrame {
    // Variables ------------------------------------------------------------------------------------------------------|

    // Swing components - Follow indent hierarchy for organization -----------|
    private static JFrame debugFrame;
        private JPanel mainPanel;
            private JPanel northPanel;
                private JPanel leftNorthPanel;
                    private JLabel titleLabel;
                        private String titleText = "Debug Console";

                private JPanel rightNorthPanel;
                    private JLabel numberOfLinesLabel;
                        private String numberOfLinesText = "Number of Lines:";
                    private static JButton resizeWindowButton;
            private JPanel centerPanel;
                private JScrollPane debugScrollPane;
                    private JTextArea debugTextArea;
            private JPanel southPanel;
                private JPanel leftSouthPanel;
                    private JButton clearButton;
                        private String clearButtonText = "Clear";
                    private JButton copyButton;
                        private String copyButtonText = "Copy";
                    private JButton saveButton;
                        private String saveButtonText = "Save as";
                        private String savedSuccessDialogMessage = "Saved debug console to file at:";
                        private String savedSuccessDialogTitle = "Success!";
                        private static String fileChooserTitle = "Save To";
                        private static String fileChooserCustomMessage = "Text File";
                private JPanel rightSouthPanel;
                    private JButton closeButton;
                        private String closeButtonText = "Close";
            private JPanel eastPanel;
            private JPanel westPanel;

    // End of Swing components -----------------------------------------------|
    private static LocaleManager localeManager;
    private boolean debug;
    private int numberOfLines = 0;
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
     * @see DebugConsoleWindow#DebugConsoleWindow(JFrame, Preferences, boolean, LocaleManager)
     * @see DebugConsoleWindow#DebugConsoleWindow(JFrame, String, int, Preferences, boolean, LocaleManager)
     */
    public DebugConsoleWindow(JFrame parent, Preferences prefs, boolean debug) {
        this(parent, "Tahoma", 16, prefs, debug, null);
    }

    /**
     * Overload Constructor with default font.
     * @param parent frame to latch onto if called from another window
     * @param prefs Preferences object for saving and loading user settings
     * @param debug whether to print debug information
     * @param localeManager LocaleManager object for handling locale changes
     * @see DebugConsoleWindow#DebugConsoleWindow(JFrame, Preferences, boolean)
     * @see DebugConsoleWindow#DebugConsoleWindow(JFrame, String, int, Preferences, boolean, LocaleManager)
     */
    public DebugConsoleWindow(JFrame parent, Preferences prefs, boolean debug, LocaleManager localeManager) {
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
     * @see DebugConsoleWindow#DebugConsoleWindow(JFrame, Preferences, boolean)
     */
    public DebugConsoleWindow(JFrame parent, String fontName, int fontSize, Preferences prefs, boolean debug, LocaleManager localeManager) {
        this.fontName = fontName;
        this.fontSize = fontSize;
        this.debug = debug;

        if (localeManager != null) {
            DebugConsoleWindow.localeManager = localeManager;
            // if the locale does not contain the class, add it and it's components
            if (!localeManager.getClassesInLocaleMap().contains("DebugConsoleWindow")) {
                addClassToLocale();
            }
            if (!localeManager.getClassesInLocaleMap().contains("FileChooser")
                    || !localeManager.getComponentsInClassMap("FileChooser").contains("DebugConsoleWindow")) {
                addFileChooserComponentToLocale();
            }
            useLocale();
        } else System.out.println("LocaleManager is null. DebugConsoleWindow will launch without localization.");

        initializeWindowProperties(parent);
        initializeGUIComponents(prefs);

        debugFrame.setVisible(true);

        SwingGUI.setHandCursorToClickableComponents(debugFrame);
    }

    private void addClassToLocale() {
        Map<String, Map<String, String>> map = new TreeMap<>();
        map.put("Main", new TreeMap<>());
        Map<String, String> mainMap = map.get("Main");
        mainMap.put("titleText", titleText);
        mainMap.put("numberOfLinesText", numberOfLinesText);
        mainMap.put("clearButtonText", clearButtonText);
        mainMap.put("copyButtonText", copyButtonText);
        mainMap.put("saveButtonText", saveButtonText);
        mainMap.put("closeButtonText", closeButtonText);

        localeManager.addClassSpecificMap("DebugConsoleWindow", map);
    }

    private void addFileChooserComponentToLocale() {
        Map<String, String> fileChooserMap = new TreeMap<>();
        fileChooserMap.put("savedSuccessDialogMessage", savedSuccessDialogMessage);
        fileChooserMap.put("savedSuccessDialogTitle", savedSuccessDialogTitle);
        fileChooserMap.put("fileChooserTitle", fileChooserTitle);
        fileChooserMap.put("fileChooserCustomMessage", fileChooserCustomMessage);

        if (!localeManager.getClassesInLocaleMap().contains("FileChooser")) {
            localeManager.addClassSpecificMap("FileChooser", new TreeMap<>());
        }

        localeManager.addComponentSpecificMap("FileChooser", "DebugConsoleWindow", fileChooserMap);
    }

    private void useLocale() {
        Map<String, String> varMap = localeManager.getAllVariablesWithinClassSpecificMap("DebugConsoleWindow");

        titleText = varMap.getOrDefault("titleText", titleText);
        numberOfLinesText = varMap.getOrDefault("numberOfLinesText", numberOfLinesText);
        clearButtonText = varMap.getOrDefault("clearButtonText", clearButtonText);
        copyButtonText = varMap.getOrDefault("copyButtonText", copyButtonText);
        saveButtonText = varMap.getOrDefault("saveButtonText", saveButtonText);
        closeButtonText = varMap.getOrDefault("closeButtonText", closeButtonText);

        Map<String, String> fileChooserMap = localeManager.getComponentSpecificMap("FileChooser", "DebugConsoleWindow");
        savedSuccessDialogMessage = fileChooserMap.getOrDefault("savedSuccessDialogMessage", savedSuccessDialogMessage);
        savedSuccessDialogTitle = fileChooserMap.getOrDefault("savedSuccessDialogTitle", savedSuccessDialogTitle);
        fileChooserTitle = fileChooserMap.getOrDefault("fileChooserTitle", fileChooserTitle);
        fileChooserCustomMessage = fileChooserMap.getOrDefault("fileChooserCustomMessage", fileChooserCustomMessage);
    }

    private void initializeWindowProperties(JFrame parent) {
        debugFrame = this;
        debugFrame.setTitle(titleText);
        debugFrame.setMinimumSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        debugFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        debugFrame.setResizable(false);
        debugFrame.setLocationRelativeTo(parent);
    }

    private void initializeGUIComponents(Preferences prefs) {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        debugFrame.add(mainPanel);
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
                    int mac = ApplicationCore.detectOS().equals("mac") ? 30 : 0;
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
                    numberOfLinesLabel = new JLabel();
                    updateNumberOfLines(numberOfLines);
                    numberOfLinesLabel.setPreferredSize(
                            new Dimension((int) numberOfLinesLabel.getPreferredSize().getWidth() * 2, BORDER_PADDING_HEIGHT - 10)
                    );
                    numberOfLinesLabel.setFont(new Font(fontName, Font.PLAIN, fontSize - 2));
                    numberOfLinesLabel.setVerticalAlignment(SwingConstants.TOP);
                    rightNorthPanel.add(numberOfLinesLabel);

                    resizeWindowButton = new ResizeWindowButton(
                            debugFrame,
                            WINDOW_WIDTH,
                            WINDOW_HEIGHT,
                            2f,
                            2f,
                            debug
                    ) {
                        @Override
                        public void customResizeActions() {
                            leftNorthPanel.setPreferredSize(new Dimension(
                                    (getNewWidth() - (EDGE_PADDING_WIDTH * 2)) / 2, BORDER_PADDING_HEIGHT
                            ));
                            rightNorthPanel.setPreferredSize(new Dimension(
                                    (getNewWidth() - (EDGE_PADDING_WIDTH * 2)) / 2, BORDER_PADDING_HEIGHT
                            ));

                            leftSouthPanel.setPreferredSize(new Dimension(
                                    (getNewWidth() - (EDGE_PADDING_WIDTH * 2)) / 2 + 50, BORDER_PADDING_HEIGHT
                            ));
                            rightSouthPanel.setPreferredSize(new Dimension(
                                    (getNewWidth() - (EDGE_PADDING_WIDTH * 2)) / 2 - 50, BORDER_PADDING_HEIGHT
                            ));

                            debugTextArea.setCaretPosition(debugTextArea.getDocument().getLength());
                        }
                    };
                    rightNorthPanel.add(resizeWindowButton);
                }
            }

            centerPanel = new JPanel();
            centerPanel.setLayout(new BorderLayout());
            mainPanel.add(centerPanel, BorderLayout.CENTER);
            {
                // Add components here
                debugTextArea = new JTextArea();
                debugTextArea.setFont(new Font("Courier New", Font.PLAIN, fontSize));
                debugTextArea.setEditable(false);
                debugTextArea.setLineWrap(true);
                debugTextArea.setCaretColor(debugTextArea.getBackground());

                TiedOutputStream tiedOutputStream = getTiedOutputStream();
                tiedOutputStream.tieOutputStreams(true);

                this.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                        tiedOutputStream.resetOutputStreams();
                    }
                });

                debugScrollPane = new JScrollPane(debugTextArea);
                centerPanel.add(debugScrollPane, BorderLayout.CENTER);
            }

            southPanel = new JPanel();
            southPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
            mainPanel.add(southPanel, BorderLayout.SOUTH);
            {
                leftSouthPanel = new JPanel();
                leftSouthPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
                leftSouthPanel.setPreferredSize(new Dimension(halfSizePanelWidth + 50, BORDER_PADDING_HEIGHT));
                southPanel.add(leftSouthPanel);
                {
                    clearButton = new JButton(clearButtonText);
                    clearButton.setFont(new Font(fontName, Font.PLAIN, fontSize));
                    clearButton.addActionListener(e -> {
                        debugTextArea.setText("");
                        updateNumberOfLines(0);
                        if (debug) System.out.println("Cleared debug console.");
                    });
                    leftSouthPanel.add(clearButton);

                    copyButton = new JButton(copyButtonText);
                    copyButton.setFont(new Font(fontName, Font.PLAIN, fontSize));
                    copyButton.addActionListener(e -> {
                        try {
                            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
                                    new StringSelection(debugTextArea.getText()), null
                            );
                            if (debug)
                                System.out.println("Copied debug console to clipboard.");
                        } catch (Exception ex) {
                            if (debug) ex.printStackTrace(System.err);
                            System.err.println("Error copying to clipboard.");
                        }
                    });
                    leftSouthPanel.add(copyButton);

                    saveButton = new JButton(saveButtonText + " \".txt\"");
                    saveButton.setFont(new Font(fontName, Font.PLAIN, fontSize));
                    saveButton.addActionListener(e -> {
                        String debugSaveAsFilePath = openFileChooser(
                                prefs.get("debugSaveAsFilePath", "")
                        );
                        if (debugSaveAsFilePath.contains("Cancel-")) return;

                        prefs.put("debugSaveAsFilePath", debugSaveAsFilePath);

                        debugSaveAsFilePath = debugSaveAsFilePath + getLogFileName();

                        if (debug) System.out.println("Saved debug console to file at: " + debugSaveAsFilePath);

                        int success = -1;
                        try {
                            String saveText = debugTextArea.getText();
                            FileWriter writer = new FileWriter(debugSaveAsFilePath);
                            writer.write(saveText);
                            if (debug) System.out.println("Wrote debug console to file.");
                            success = 0;
                            writer.close();
                            if (debug) System.out.println("Closed FileWriter.");
                        } catch (IOException ex) {
                            success = 1;
                            ex.printStackTrace(System.err);
                        }

                        if (success == 0) {
                            if (debug)
                                System.out.println("Successfully saved debug console as .txt file. Showing message.");
                            JOptionPane.showMessageDialog(debugFrame,
                                    savedSuccessDialogMessage + " \"" + debugSaveAsFilePath + "\"", savedSuccessDialogTitle,
                                    JOptionPane.INFORMATION_MESSAGE
                            );

                            Files.openInFileManager(debugSaveAsFilePath);
                        }

                    });
                    leftSouthPanel.add(saveButton);
                }

                rightSouthPanel = new JPanel();
                rightSouthPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
                rightSouthPanel.setPreferredSize(new Dimension(halfSizePanelWidth - 50, BORDER_PADDING_HEIGHT));
                southPanel.add(rightSouthPanel);
                {
                    closeButton = new JButton(closeButtonText);
                    closeButton.setFont(new Font(fontName, Font.PLAIN, fontSize));
                    closeButton.addActionListener(e -> {
                        if (debug) System.out.println("Closed debug console.");
                        debugFrame.dispose();
                    });
                    rightSouthPanel.add(closeButton);
                }
            }

            eastPanel = new JPanel();
            mainPanel.add(eastPanel, BorderLayout.EAST);
            {
                eastPanel.setPreferredSize(new Dimension(EDGE_PADDING_WIDTH + 10, 10));
            }

            westPanel = new JPanel();
            mainPanel.add(westPanel, BorderLayout.WEST);
            {
                westPanel.setPreferredSize(new Dimension(EDGE_PADDING_WIDTH + 10, 10));
            }
        }
    }

    private String getLogFileName() {
        String dateTime = Utils.getSanitizedCurrentTime(true, true, false);
        return FileSystems.getDefault().getSeparator() + "DEBUG_LOG_" + dateTime + ".txt";
    }

    private void updateNumberOfLines(int numberOfLines) {
        this.numberOfLines = numberOfLines;
        numberOfLinesLabel.setText(numberOfLinesText + " " + numberOfLines);

        FontMetrics metrics = numberOfLinesLabel.getFontMetrics(numberOfLinesLabel.getFont());
        int textWidth = metrics.stringWidth(numberOfLinesLabel.getText());
        numberOfLinesLabel.setPreferredSize(new Dimension(
                textWidth + 10, numberOfLinesLabel.getHeight()
        ));
    }

    private TiedOutputStream getTiedOutputStream() {
        PrintStream debugPrintStream = new PrintStream(new OutputStream() {
            boolean newLine = true;
            @Override
            public void write(int b) {
                if (newLine) {
                    debugTextArea.append("[" + Utils.getCurrentTime(false, true, false) + "]: ");
                    newLine = false;
                }

                debugTextArea.append(String.valueOf((char)b));

                if ((char)b == '\n') {
                    newLine = true;
                }

                debugTextArea.setCaretPosition(debugTextArea.getDocument().getLength());
                updateNumberOfLines(debugTextArea.getLineCount());
            }
        });

        return new TiedOutputStream(debugPrintStream);
    }

    private static String openFileChooser(
            String existingFilePath) {
        String output = System.getProperty("user.home");

        FileChooser fileChooser = new FileChooser(
                output, fileChooserTitle, false, fileChooserCustomMessage+ " - *.txt", localeManager);

        int returnValue = fileChooser.showOpenDialog(debugFrame);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            output = fileChooser.getSelectedFile().getAbsolutePath();
        } else {
            output = "Cancel-" + existingFilePath;
        }

        return output;
    }
}
