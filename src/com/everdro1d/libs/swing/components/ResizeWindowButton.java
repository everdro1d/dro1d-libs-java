/**************************************************************************************************
 * dro1dDev 2025.                                                                                 *
 **************************************************************************************************/

package com.everdro1d.libs.swing.components;

import com.everdro1d.libs.swing.SwingGUI;

import javax.swing.*;
import java.awt.*;

public abstract class ResizeWindowButton extends JButton {
    private int baseWidth, baseHeight;
    private float xMult, yMult;
    private int newWidth, newHeight;

    private Icon iconExpand, iconShrink;

    private JFrame frame;

    private boolean debug = false;
    private boolean maximized = false;

    public ResizeWindowButton(
            JFrame frame,
            JPanel parentPanel,
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

        this.setBackground(parentPanel.getBackground());
        this.setMargin(new Insets(2, 2, 2, 2));

        ImageIcon iconE = (ImageIcon) SwingGUI.getApplicationIcon("com/everdro1d/libs/swing/resources/images/size/expand.png",
                this.getClass());
        ImageIcon iconS = (ImageIcon) SwingGUI.getApplicationIcon("com/everdro1d/libs/swing/resources/images/size/shrink.png",
                this.getClass());
        iconShrink = new ImageIcon(iconS.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
        iconExpand = new ImageIcon(iconE.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));

        this.setIcon(iconExpand);
        resizeWindowButtonColorChange();

        this.addActionListener(e -> this.resizeWindow());

    }

    public void resizeWindow() {
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

    public abstract void customResizeActions();

    @Override
    public void updateUI() {
        super.updateUI();
        resizeWindowButtonColorChange();
    }

    /**
     * Overload method for default color based on rootpane foreground color
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
        // set the icon to the colour
        Icon icon = SwingGUI.changeIconColor(tmp, color);
        this.setIcon(icon);
    }

    // getters
    public int      getBaseWidth()      { return this.baseWidth;    }
    public int      getBaseHeight()     { return this.baseHeight;   }
    public int      getNewWidth()       { return this.newWidth;     }
    public int      getNewHeight()      { return this.newHeight;    }
    public float    getXMultiplier()    { return this.xMult;        }
    public float    getYMultiplier()    { return yMult;             }
    public boolean  isMaximized()       { return maximized;         }

    public void setXMultiplier(float newXMult) {
        this.xMult = newXMult;
    }
    public void setYMultiplier(float newYMult) {
        this.yMult = newYMult;
    }
}
