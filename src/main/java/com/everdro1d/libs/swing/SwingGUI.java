// dro1dDev - created: 2024-03-03

package com.everdro1d.libs.swing;

import com.everdro1d.libs.swing.themes.EverDarkLaf;
import com.everdro1d.libs.swing.themes.EverLightLaf;
import com.formdev.flatlaf.FlatLaf;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.*;
import java.io.InputStream;
import java.util.ArrayList;

public class SwingGUI {

    private SwingGUI() {}

    /**
     * Set the look and feel of the application.
     * @param useFlatLaf whether to use FlatLaf (defaults to system look and feel)
     * @param allowDarkMode whether to enable dark mode (FlatLaf only)
     */
    public static void setupLookAndFeel(boolean useFlatLaf, boolean allowDarkMode) {
        if (useFlatLaf) {
            FlatLaf.registerCustomDefaultsSource("com.everdro1d.libs.swing.themes");
            EverLightLaf.setup();
            if (allowDarkMode) {
                EverDarkLaf.setup();
            }

            return;
        }

        // fallback from flatlaf
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace(System.err);
            System.err.println("Could not set look and feel of application.");
        }
    }

    /**
     * Used to enable dark mode for the running application.
     * <p>
     * FlatLaf is used to set the look and feel of the application
     *
     * @param isDarkModeEnabled whether to enable dark mode
     * @param frames            array of JFrames to update
     */
    public static void switchLightOrDarkMode(boolean isDarkModeEnabled, JFrame[] frames) {
        switchLightOrDarkMode(isDarkModeEnabled, frames, false);
    }

    /**
     * Used to enable dark mode for the running application.
     * <p>
     * FlatLaf is used to set the look and feel of the application
     * @param isDarkModeEnabled whether to enable dark mode
     * @param frames array of JFrames to update
     * @param contrastTitleBars whether to use contrasting titleBar colors
     */
    public static void switchLightOrDarkMode(boolean isDarkModeEnabled, JFrame[] frames, boolean contrastTitleBars) {
        try {
            if (!UIManager.getLookAndFeel().getName().toLowerCase().contains("ever")) {
                throw new Exception("Not using a supported LaF, cannot proceed.");
            }

            LookAndFeel laf = isDarkModeEnabled ? new EverDarkLaf() : new EverLightLaf();
            UIManager.setLookAndFeel( laf );

            if (contrastTitleBars) {
                for (JFrame frame : frames) {
                    if (frame != null) { // because for some reason the title bar color doesn't change with the L&F
                        frame.getRootPane().putClientProperty("JRootPane.titleBarBackground", UIManager.getColor("TitlePane.background"));
                        frame.getRootPane().putClientProperty("JRootPane.titleBarForeground", UIManager.getColor("TitlePane.foreground"));
                    }
                }
            }

        } catch (Exception ex) {
            System.err.println("Could not set EverDarkLaf as application look and feel. Using Fallback.");
            JOptionPane.showMessageDialog(frames[0], "Dark mode does not exist. The appearance of this dialog represents an error.\n" +
                    "Contact the dev and ask them to check where \"switchLightOrDarkMode\" is called.", "ERROR", JOptionPane.ERROR_MESSAGE);
        } finally {
            for (JFrame frame : frames) if (frame != null) SwingUtilities.updateComponentTreeUI(frame);
        }
    }

    /**
     * Set the default UI settings for the application.
     * @param fontName the name of the font to use
     */
    public static void uiSetup(String fontName, int fontSize) {
        Font font = new Font(fontName, Font.PLAIN, fontSize);

        // OptionPane
        UIManager.put("OptionPane.minimumSize",new Dimension(300, 100));
        UIManager.put("OptionPane.messageFont",font);
        UIManager.put("OptionPane.buttonFont",font);

        // TabbedPane
        UIManager.put("TabbedPane.font",font);

        // FileChooser
        UIManager.put("FileChooser.noPlacesBar",Boolean.TRUE);

    }

    /**
     * Gets the window's position on the current monitor.
     * @param frame the frame to get the position of
     * @return int[] = {x, y, activeMonitor}
     */
    public static int[] getFramePositionOnScreen(JFrame frame) {
        int[] framePosition = new int[]{0, 0, 0};
        Point frameLocation = frame.getLocationOnScreen();

        // get the monitor that frame is on
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gs = ge.getScreenDevices();

        for (int i = 0; i < gs.length; i++) {
            GraphicsDevice gd = gs[i];
            GraphicsConfiguration[] gc = gd.getConfigurations();
            Rectangle screenBounds = gc[0].getBounds();
            int screenWidth = screenBounds.width;
            int screenHeight = screenBounds.height;

            framePosition[0] = frameLocation.x;
            framePosition[1] = frameLocation.y;

            if (frameLocation.x >= screenBounds.x && frameLocation.x <= screenBounds.x + screenWidth) {
                if (frameLocation.y >= screenBounds.y && frameLocation.y <= screenBounds.y + screenHeight) {
                    framePosition[2] = i;
                    break;
                }
            }
        }

        return framePosition;
    }

    /**
     * Set the position of the frame on the screen.
     * @param frame the frame to set the position of
     * @param framePosX the x position of the frame
     * @param framePosY the y position of the frame
     * @param activeMonitor the active monitor
     */
    public static void setFramePosition(JFrame frame, int framePosX, int framePosY, int activeMonitor) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gs = ge.getScreenDevices();

        if (activeMonitor > -1 && activeMonitor < gs.length) {
            GraphicsDevice gd = gs[activeMonitor];
            GraphicsConfiguration[] gc = gd.getConfigurations();
            Rectangle screenBounds = gc[0].getBounds();
            Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(gc[0]);
            int screenWidth = screenBounds.width;
            int screenHeight = screenBounds.height;

            // check if the window is on the screen and account for taskbar
            if (framePosY > screenBounds.height - frame.getHeight() - screenInsets.bottom) {
                framePosY = Math.min( Math.max(0, framePosY),
                        screenBounds.height - frame.getHeight() - screenInsets.bottom
                );
            }

            if (framePosX == 0 && framePosY == 0) {
                //center on screen 0
                framePosX = screenBounds.x + (screenWidth - frame.getWidth()) / 2;
                framePosY = screenBounds.y + (screenHeight - frame.getHeight()) / 2;
            }

            if (framePosX >= screenBounds.x && framePosX <= screenBounds.x + screenWidth) {
                if (framePosY >= screenBounds.y && framePosY <= screenBounds.y + screenHeight) {

                    frame.setLocation(framePosX, framePosY);
                }
            }
        }
    }

    /**
     * Set the location of the frame on the screen when it is resized.
     * @param frame the frame to set the location of
     * @param keepOnActiveMonitor whether to adjust the frame to fit fully on the active monitor
     */
    public static void setLocationOnResize(JFrame frame, boolean keepOnActiveMonitor) {
        int[] framePosition = getFramePositionOnScreen(frame);
        if (keepOnActiveMonitor) {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice[] gs = ge.getScreenDevices();
            if (framePosition[2] > -1 && framePosition[2] < gs.length) {
                GraphicsDevice gd = gs[framePosition[2]];
                GraphicsConfiguration[] gc = gd.getConfigurations();
                Rectangle screenBounds = gc[0].getBounds();
                Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(gc[0]);
                int screenWidth = screenBounds.width;
                int screenHeight = screenBounds.height;

                // Adjust the frame's position to fit within the monitor's dimensions
                if (framePosition[0] + frame.getWidth() > screenBounds.x + screenWidth) {
                    framePosition[0] = screenBounds.x + screenWidth - frame.getWidth();
                }
                if (framePosition[1] + frame.getHeight() > screenBounds.y + screenHeight - screenInsets.bottom) {
                    framePosition[1] = screenBounds.y + screenHeight - frame.getHeight() - screenInsets.bottom;
                }
            }
        }
        setFramePosition(frame, framePosition[0], framePosition[1], framePosition[2]);
    }

    /**
     * Get the size of the window. Used for debugging.
     * @param frame the frame to get the size of
     */
    public static void getWindowSize(JFrame frame) {
        System.out.println("Width: " + frame.getWidth() + "px Height: " + frame.getHeight() + "px");
    }

    /**
     * Set the icon of the frame.
     * @param frame the frame to set the icon of
     * @param internalPath path to the image file within resources
     * @param clazz the class to trace from
     */
    public static void setFrameIcon(JFrame frame, String internalPath, Class<?> clazz) {
        Icon icon = getApplicationIcon(internalPath, clazz);
        if (icon != null) {
            frame.setIconImage(((ImageIcon) icon).getImage());
        }
    }

    /**
     * Retrieve an image as an Icon from the application's resources.
     * @param internalPath path to the image file within resources
     * @param clazz the class to trace from
     * @return the Icon
     */
    public static Icon getApplicationIcon(String internalPath, Class<?> clazz) {
        Icon icon = null;
        try (InputStream iconStream = clazz.getClassLoader().getResourceAsStream(internalPath)) {
            if (iconStream != null) {
                icon = new ImageIcon(ImageIO.read(iconStream));
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        if (icon == null) {
            System.err.println("[ERROR] Could not find icon file at: " + internalPath);
        }
        return icon;
    }

    /**
     * Simulate a key event after requesting focus on a component.
     * @param component the component to request focus on
     * @see #simulateKeyEvent(JComponent, int, char, int, int)
     */
    public static void requestFocusAndSimulateKeyEvent(JComponent component) {
        component.requestFocusInWindow();
        simulateKeyEvent(component);
    }

    /**
     * Simulates an ENTER keyReleased event on a component.
     * @param component the component to simulate the event on
     * @see #simulateKeyEvent(JComponent, int, char, int, int)
     */
    public static void simulateKeyEvent(JComponent component) {
        simulateKeyEvent(component, KeyEvent.VK_ENTER, '\n', 0, KeyEvent.KEY_RELEASED);
    }

    /**
     * Simulates a keyEvent event on a component.
     * @param component the component to simulate the event on
     * @param keyCode ex: KeyEvent.VK_ENTER for ENTER
     * @param keyChar ex: '\n' for ENTER
     * @param modifiers KeyEvent.CTRL_MASK, KeyEvent.SHIFT_MASK, KeyEvent.ALT_MASK, KeyEvent.META_MASK
     * @param event KeyEvent.KEY_PRESSED, KeyEvent.KEY_RELEASED, or KeyEvent.KEY_TYPED
     * @see #simulateKeyEvent(JComponent)
     */
    public static void simulateKeyEvent(JComponent component, int keyCode, char keyChar, int modifiers, int event) {
        KeyEvent keyEvent = new KeyEvent(component, event, System.currentTimeMillis(), modifiers, keyCode, keyChar);
        component.dispatchEvent(keyEvent);
    }

    /**
     * Simulates a mouse event on a component.
     * @param component the component to simulate the event on
     * @param x the x position of the mouse
     * @param y the y position of the mouse
     * @param event MouseEvent.MOUSE_[PRESSED, RELEASED, CLICKED, ENTERED, EXITED]
     */
    public static void simulateMouseEvent(JComponent component, int x, int y, int event) {
        MouseEvent mouseEvent = new MouseEvent(component, event, System.currentTimeMillis(), 0, x, y, 1, false);
        component.dispatchEvent(mouseEvent);
    }

    /**
     * Creates a string combo box with the specified parameters.
     * @param strArr the array of items for the combo box
     * @param selectedIndex default selected index
     * @param fontName the name of the font to use
     * @param fontSize the size of the font to use
     * @return the combo box
     */
    public static JComboBox<String> stringComboBoxConstructor(String[] strArr, int selectedIndex, String fontName, int fontSize) {
        JComboBox<String> comboBox = new JComboBox<>(strArr);
        setupComboBox(new JComboBox<>(strArr), selectedIndex, fontName, fontSize);
        return comboBox;
    }

    public static void setupComboBox(JComboBox<String> comboBox, int selectedIndex, String fontName, int fontSize) {
        comboBox.setFont(new Font(fontName, Font.PLAIN, fontSize));
        comboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        comboBox.setSelectedIndex(selectedIndex);
    }

    /** OVERLOAD <p>
     * Update the width of the combobox depending on the width of the longest string within its array.
     * Padding defaults to 40.
     * @param strArr the array of items for the combo box
     * @param comboBox the combo box to update
     * @see #updateComboBox(String[], JComboBox, int)
     */
    public static void updateComboBox(String[] strArr, JComboBox<String> comboBox) {
        updateComboBox(strArr, comboBox, 40);
    }

    /**
     * Update the width of the combobox depending on the width of the longest string within its array.
     * @param strArr the array of items for the combo box
     * @param comboBox the combo box to update
     * @param padding the padding to add to the width
     */
    public static void updateComboBox(String[] strArr, JComboBox<String> comboBox, int padding) {
        comboBox.setModel(new DefaultComboBoxModel<>(strArr));

        // Determine the highest width of all items
        int maxWidth = 0;
        FontMetrics fontMetrics = comboBox.getFontMetrics(comboBox.getFont());

        for (int i = 0; i < comboBox.getItemCount(); i++) {
            int textWidth = fontMetrics.stringWidth(comboBox.getItemAt(i));
            maxWidth = Math.max(maxWidth, textWidth);
        }

        // Add some padding to the maxWidth to accommodate borders
        int adjustedMaxWidth = maxWidth + padding;

        // Set the width of the JComboBox to the maximum width
        Dimension dimension = new Dimension(adjustedMaxWidth, comboBox.getPreferredSize().height);
        comboBox.setMinimumSize(dimension);
        comboBox.setPreferredSize(dimension);
        comboBox.setMaximumSize(dimension);
    }

    /**
     * Set the cursor to the hand cursor for clickable components.
     * @param container the container of the components (top level JFrame etc.)
     */
    public static void setHandCursorToClickableComponents(Container container) {
        for (Component component : container.getComponents()) {
            if (component instanceof AbstractButton || component instanceof JComboBox) {
                component.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            } else if (component instanceof JTextField) {
                component.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
            } else if (component instanceof Container) {
                setHandCursorToClickableComponents((Container) component);
            }
        }
    }

    /**
     * Sets the progress bar in percentage format "1-100%", while ensuring non-jumpy values (only increases not decreases).
     * @param i current progress percentage
     * @param progressBar progressBar to change
     * <p><strong>Example Use Case:</strong></p>
     * Download ~X mb out of 100 mb where the estimated progress fluctuates.
     */
    public static void setProgressPercent(int i, JProgressBar progressBar) {
        if (i < 0 || i > 100) {
            if (progressBar.isIndeterminate()) return;
            progressBar.setIndeterminate(true);
            progressBar.setStringPainted(false);
            return;
        }
        int ip = progressBar.getValue();
        if (i > ip || ip == 100) {
            progressBar.setIndeterminate(false);
            progressBar.setValue(i);
            progressBar.setStringPainted(true);
            progressBar.setString(i + "%");
        }
    }

    public static ImageIcon changeIconColor(Icon icon, Color color) {
        // Create a new image that can be edited
        BufferedImage bufferedImage = new BufferedImage(
                icon.getIconWidth(),
                icon.getIconHeight(),
                BufferedImage.TYPE_INT_ARGB
        );
        Graphics2D g2d = bufferedImage.createGraphics();
        // Draw the icon on the new image
        icon.paintIcon(null, g2d, 0, 0);
        g2d.dispose();

        // Create a filter that changes the color of the image
        ImageFilter filter = new RGBImageFilter() {
            public int filterRGB(int x, int y, int rgb) {
                // If the pixel is transparent, preserve its original RGB values
                if ((rgb & 0xFF000000) == 0) {
                    return rgb;
                }
                // If the pixel is not transparent, replace its RGB values with the desired color
                else {
                    // Preserve the alpha value
                    int alpha = rgb & 0xFF000000;
                    // Replace the RGB values with the desired color
                    return alpha | (color.getRGB() & 0x00FFFFFF);
                }
            }
        };

        // Apply the filter
        ImageProducer producer = new FilteredImageSource(bufferedImage.getSource(), filter);
        Image newImage = Toolkit.getDefaultToolkit().createImage(producer);

        return new ImageIcon(newImage);
    }

    public static boolean isDarkModeActive() {
        return UIManager.getLookAndFeel().getName().toLowerCase().contains("dark") && FlatLaf.isLafDark();
    }

    public static boolean canHoldText(JComponent component) {
        return component instanceof JTextComponent || component instanceof JLabel;
    }

    public static void updateFrameColors(JFrame frame, boolean isDarkModeEnabled) {
        frame.getContentPane().setBackground(UIManager.getColor("RootPane.background"));
        frame.setBackground(UIManager.getColor("RootPane.background"));

        ArrayList<JComponent> frameComponents = getAllComponents(frame);

        for (JComponent component : frameComponents) {
            if (canHoldText(component)) {
                component.setForeground(UIManager.getColor("RootPane.foreground"));
            }
        }
        switchLightOrDarkMode(isDarkModeEnabled,new JFrame[]{frame});
    }

    public static ArrayList<JComponent> getAllComponents(JFrame frame) {
        return getAllComponents(frame.getContentPane());
    }

    public static ArrayList<JComponent> getAllComponents(Container container) {
        ArrayList<JComponent> components = new ArrayList<>();
        for (Component c : container.getComponents()) {
            if (c instanceof JComponent) { // Only include JComponents
                components.add((JComponent) c);
            }
            if (c instanceof Container) {  // If it’s a container, check its children
                components.addAll(getAllComponents((Container) c));
            }
        }

        return components;
    }

    }
