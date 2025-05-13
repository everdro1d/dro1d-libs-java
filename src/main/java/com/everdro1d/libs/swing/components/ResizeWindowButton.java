// dro1dDev - created: 2025-05-09

package com.everdro1d.libs.swing.components;

import com.everdro1d.libs.swing.ImageUtils;
import com.everdro1d.libs.swing.SwingGUI;

import javax.swing.*;
import java.awt.*;

/**
 * Abstract class for a button that resizes a window when clicked.
 * <p>
 * This class provides functionality to create a button that can resize a JFrame
 * from a specified width and height, with customizable multipliers for
 * resizing. It also allows for custom actions to be performed after resizing.
 * </p>
 *
 * <p><strong>Example Usage:</strong></p>
 * <blockquote><pre>
 * JFrame frame = new JFrame();
 * ResizeWindowButton resizeButton = new ResizeWindowButton(frame, 800, 600, 1.5f, 1.5f, true) {
 *   &#64;Override
 *   public void customResizeActions() {
 *    // Custom actions to perform after resizing
 *    System.out.println("Window resized to: " + getNewWidth() + "x" + getNewHeight());
 *    }
 * };
 *
 * </pre></blockquote>
 *
 * @see JButton
 */
public abstract class ResizeWindowButton extends JButton {
    private final int baseWidth, baseHeight;
    private float xMult, yMult;
    private int newWidth, newHeight;

    private final Icon iconExpand, iconShrink;

    private final JFrame frame;

    private final boolean debug;
    private boolean maximized = false;

    /**
     * Constructor for {@link ResizeWindowButton}.
     *
     * @param frame      the JFrame to resize
     * @param baseWidth  the base width of the window
     * @param baseHeight the base height of the window
     * @param xMult      the multiplier for width resizing
     * @param yMult      the multiplier for height resizing
     * @param debug      whether to enable debug output
     */
    public ResizeWindowButton(
            JFrame frame,
            int baseWidth,
            int baseHeight,
            float xMult,
            float yMult,
            boolean debug
    ) {
        this.frame = frame;
        this.baseWidth = baseWidth;
        this.baseHeight = baseHeight;
        this.xMult = xMult;
        this.yMult = yMult;
        this.debug = debug;

        this.setMargin(new Insets(2, 2, 2, 2));

        ImageIcon iconE = (ImageIcon) ImageUtils.getApplicationIcon("main/resources/images/size/expand.png",
                this.getClass());
        ImageIcon iconS = (ImageIcon) ImageUtils.getApplicationIcon("main/resources/images/size/shrink.png",
                this.getClass());
        iconShrink = new ImageIcon(iconS.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
        iconExpand = new ImageIcon(iconE.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));

        this.setIcon(iconExpand);
        resizeWindowButtonColorChange();

        this.addActionListener(e -> this.resizeWindow());

    }

    /**
     * Resize the window based on the current state (maximized or minimized).
     * <p>
     * This method toggles the window size between the base size and the
     * multiplied size, and performs custom actions after resizing.
     * </p>
     */
    private void resizeWindow() {
        if (maximized) {
            newWidth = this.baseWidth;
            newHeight = this.baseHeight;
            this.setIcon(iconExpand);
                if (debug) System.out.println("Minimized "+frame.getTitle()+" window.");
        } else {
            newWidth = Math.round(this.baseWidth * xMult);
            newHeight = Math.round(this.baseHeight * yMult);
            this.setIcon(iconShrink);
                if (debug) System.out.println("Maximized "+frame.getTitle()+" window.");
        }

        frame.setSize(new Dimension(newWidth, newHeight));

        customResizeActions();

        SwingGUI.setLocationOnResize(frame, true);

        resizeWindowButtonColorChange();

        this.maximized = !maximized;
    }

    /**
     * Custom actions to perform after resizing the window.
     * <p>
     * This method can be overridden to provide custom behavior after the
     * window has been resized.
     * </p>
     */
    public abstract void customResizeActions();

    @Override
    public void updateUI() {
        super.updateUI();
        resizeWindowButtonColorChange();
    }

    /**
     * Overload method for default color based on root pane foreground color.
     * @see #resizeWindowButtonColorChange(Color)
     */
    public void resizeWindowButtonColorChange() {
        Color c = UIManager.getColor("RootPane.foreground");
        if (c == null) c = Color.BLACK;
        resizeWindowButtonColorChange(c);
    }

    /**
     * Change the color of the resize icon based on param
     * @param color new icon color
     */
    public void resizeWindowButtonColorChange(Color color) {
        if (this.frame == null) {
            return;
        }
        Icon tmp = this.getIcon();
        // set the icon to the color
        Icon icon = ImageUtils.changeIconColor(tmp, color);
        this.setIcon(icon);
    }

    // getters
    /**
     * Retrieve the base width (width before resize).
     * @return the base width of the window
     */
    public int      getBaseWidth()      { return this.baseWidth;    }

    /**
     * Retrieve the base height (height before resize).
     * @return the base height of the window
     */
    public int      getBaseHeight()     { return this.baseHeight;   }

    /**
     * Retrieve the new width (width after resize).
     * @return the new width of the window
     */
    public int      getNewWidth()       { return this.newWidth;     }

    /**
     * Retrieve the new height (height after resize).
     * @return the new height of the window
     */
    public int      getNewHeight()      { return this.newHeight;    }

    /**
     * Retrieve the multiplier for width resizing.
     * @return the multiplier for width resizing
     */
    public float    getXMultiplier()    { return this.xMult;        }

    /**
     * Retrieve the multiplier for height resizing.
     * @return the multiplier for height resizing
     */
    public float    getYMultiplier()    { return yMult;             }

    /**
     * Retrieve whether the window is maximized (using this button).
     * @return true if the window is maximized, false otherwise
     */
    public boolean  isMaximized()       { return maximized;         }

    // setters
    /**
     * Set the width multiplier for resizing.
     * @param newXMult the new width multiplier
     */
    public void setXMultiplier(float newXMult) {
        this.xMult = newXMult;
    }

    /**
     * Set the height multiplier for resizing.
     * @param newYMult the new height multiplier
     */
    public void setYMultiplier(float newYMult) {
        this.yMult = newYMult;
    }
}
