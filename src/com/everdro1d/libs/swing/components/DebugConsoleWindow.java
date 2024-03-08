package com.everdro1d.libs.swing.components;

import com.everdro1d.libs.core.ApplicationCore;
import com.everdro1d.libs.core.Utils;
import com.everdro1d.libs.io.Files;
import com.everdro1d.libs.io.TiedOutputStream;
import com.everdro1d.libs.swing.SwingGUI;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.FileSystems;
import java.util.prefs.Preferences;

public class DebugConsoleWindow extends JFrame {
    // Variables ------------------------------------------------------------------------------------------------------|

    // Swing components - Follow tab hierarchy for organization -----------|
    public static JFrame debugFrame;
        private JPanel mainPanel;
            private JPanel northPanel;
                private JPanel leftNorthPanel;
                    private JLabel titleLabel;
                private JPanel rightNorthPanel;
                    private JLabel numberOfLinesLabel;
                    private JButton expandWindowButton;
                        private Icon iconExpand;
                        private Icon iconShrink;
            private JPanel centerPanel;
                private JScrollPane debugScrollPane;
                    private JTextArea debugTextArea;
            private JPanel southPanel;
                private JPanel leftSouthPanel;
                    private JButton copyButton;
                    private JButton clearButton;
                    private JButton saveButton;
                private JPanel rightSouthPanel;
                    private JButton closeButton;
            private JPanel eastPanel;
            private JPanel westPanel;

    // End of Swing components --------------------------------------------|
    private boolean debug = false;
    private boolean maximized;
    private int numberOfLines = 0;
    private final int WINDOW_WIDTH = 600;
    private final int WINDOW_HEIGHT = 360;
    private final int EDGE_PADDING_WIDTH = 15;
    private final int BORDER_PADDING_HEIGHT = 35;
    private final String fontName;
    private final int fontSize;

    // End of variables -----------------------------------------------------------------------------------------------|

    /**
     * Overload Constructor with default font.
     * @param parent frame to latch onto if called from another window
     * @param debug whether to print debug information
     * @see DebugConsoleWindow#DebugConsoleWindow(JFrame, String, int, Preferences, boolean)
     */
    public DebugConsoleWindow(JFrame parent, Preferences prefs, boolean debug) {
        this(parent, "Tahoma", 16, prefs, debug);
    }

    /**
     * Create a debug console window.
     * @param parent frame to latch onto if called from another window
     * @param fontName the name of the font to use
     * @param fontSize the size of the font to use
     * @param debug whether to print debug information
     * @see DebugConsoleWindow#DebugConsoleWindow(JFrame, Preferences, boolean)
     */
    public DebugConsoleWindow(JFrame parent, String fontName, int fontSize, Preferences prefs, boolean debug) {
        this.fontName = fontName;
        this.fontSize = fontSize;
        this.debug = debug;

        initializeWindowProperties(parent);
        initializeGUIComponents(prefs);

        debugFrame.setVisible(true);

        SwingGUI.setHandCursorToClickableComponents(debugFrame);

        System.out.println("Debug Console initialized.");
    }
    private void initializeWindowProperties(JFrame parent) {
        debugFrame = this;
        debugFrame.setTitle("Debug Console");
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

                    titleLabel = new JLabel("Debug Console");
                    int mac = ApplicationCore.detectOS(debug).equals("macOS") ? 30 : 0;
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

                    expandWindowButton = new JButton();
                    expandWindowButton.setBorderPainted(false);
                    expandWindowButton.setContentAreaFilled(false);
                    expandWindowButton.setFocusPainted(false);
                    expandWindowButton.setMargin(new Insets(0,0,15,0));

                    ImageIcon iconE = (ImageIcon) SwingGUI.getApplicationIcon("com/everdro1d/libs/swing/resources/images/debugconsolewindow/expand.png",
                            this.getClass(), debug);
                    ImageIcon iconS = (ImageIcon) SwingGUI.getApplicationIcon("com/everdro1d/libs/swing/resources/images/debugconsolewindow/shrink.png",
                            this.getClass(), debug);
                    iconShrink = new ImageIcon(iconS.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
                    iconExpand = new ImageIcon(iconE.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));

                    expandWindowButton.setIcon(iconExpand);

                    expandWindowButton.addActionListener(e -> resizeWindow(maximized));
                    rightNorthPanel.add(expandWindowButton);
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
                TiedOutputStream.tieOutputStreams(tiedOutputStream);

                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    TiedOutputStream.resetOutputStreams(tiedOutputStream);
                }));

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
                    clearButton = new JButton("Clear");
                    clearButton.setFont(new Font(fontName, Font.PLAIN, fontSize));
                    clearButton.addActionListener(e -> {
                        debugTextArea.setText("");
                        updateNumberOfLines(0);
                        if (debug) System.out.println("Cleared debug console.");
                    });
                    leftSouthPanel.add(clearButton);

                    copyButton = new JButton("Copy");
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

                    saveButton = new JButton("Save as \".txt\"");
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
                                    "Saved log to: \"" + debugSaveAsFilePath + "\"", "Success!",
                                    JOptionPane.INFORMATION_MESSAGE
                            );

                            Files.openInFileManager(debugSaveAsFilePath, debug);
                        }

                    });
                    leftSouthPanel.add(saveButton);
                }

                rightSouthPanel = new JPanel();
                rightSouthPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
                rightSouthPanel.setPreferredSize(new Dimension(halfSizePanelWidth - 50, BORDER_PADDING_HEIGHT));
                southPanel.add(rightSouthPanel);
                {
                    closeButton = new JButton("Close");
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

    private void resizeWindow(boolean maximized) {
        int i;
        if (!maximized) {
            i = 2;
            expandWindowButton.setIcon(iconShrink);
            if (debug) System.out.println("Maximized debug console.");
        } else {
            i = 1;
            expandWindowButton.setIcon(iconExpand);
            if (debug) System.out.println("Minimized debug console.");
        }
        debugFrame.setSize(new Dimension(WINDOW_WIDTH * i, WINDOW_HEIGHT * i));

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

        SwingGUI.setLocationOnResize(debugFrame, true);

        this.maximized = !maximized;
    }

    private String getLogFileName() {
        String dateTime = Utils.getCurrentTime(true, true, false)
                .replace(" ", "_")
                .replaceAll(":","!");
        return FileSystems.getDefault().getSeparator() + "DEBUG_LOG_[" + dateTime + "].txt";
    }

    private void updateNumberOfLines(int numberOfLines) {
        this.numberOfLines = numberOfLines;
        numberOfLinesLabel.setText("Number of Lines: " + numberOfLines);

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
                output, "Save To", false, "Text File - *.txt");

        int returnValue = fileChooser.showOpenDialog(debugFrame);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            output = fileChooser.getSelectedFile().getAbsolutePath();
        } else {
            output = "Cancel-" + existingFilePath;
        }

        return output;
    }
}
