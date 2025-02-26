/**************************************************************************************************
 * Copyright (c) dro1dDev 2025.                                                                   *
 **************************************************************************************************/

package com.everdro1d.libs.swing.components;

import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import java.awt.*;

public class WindowDependentSeparator extends JPanel {

    /**
     * Creates a separator dependent on a % of the frame width.
     *
     * @param frame       to base width off of
     * @param percentOfWidth what % of the total should the separator fill
     * @param height      how many px tall should the bar be
     */
    public WindowDependentSeparator(JFrame frame, float percentOfWidth, int height) {
        this(frame, false, percentOfWidth, 0, height);
    }

    /**
     * Creates a separator dependent on (frameWidth - (amtSubtract * 2)).
     *
     * @param frame       to base width off of
     * @param amtSubtract number of px to subtract from width
     * @param height      how many px tall should the bar be
     */
    public WindowDependentSeparator(JFrame frame, int amtSubtract, int height) {
        this(frame, true, 0.0F, amtSubtract, height);
    }

    /**
     * Creates a separator dependent on a % of the frame width.
     * If subtract is true, the width of the separator will instead be width - %width,
     * OR if %=0.0 then (width - (amtSubtract * 2)).
     * @param frame          to base width off of
     * @param subtract       do you want to subtract from width instead?
     * @param percentOfWidth what % of the total should the separator fill
     * @param amtSubtract    number of px to subtract from width
     * @param height         how many px tall should the bar be
     */
    private WindowDependentSeparator(JFrame frame, boolean subtract, float percentOfWidth, int amtSubtract, int height) {
        updateSize(frame, subtract, percentOfWidth, amtSubtract, height);
        putClientProperty(FlatClientProperties.STYLE, "arc: @arc");
    }

    /**
     * Updates an existing separator dependent on a % of the frame width.
     * Uses current height.
     * @param frame          to base width off of
     * @param percentOfWidth what % of the total should the separator fill
     */
    public void updateWidth(JFrame frame, float percentOfWidth) {
        updateSize(frame, false, percentOfWidth, 0, getHeight());

    }

    /**
     * Updates an existing separator dependent on a % of the frame width.
     * @param frame          to base width off of
     * @param percentOfWidth what % of the total should the separator fill
     * @param height         how many px tall should the bar be
     */
    public void updateSize(JFrame frame, float percentOfWidth, int height) {
        updateSize(frame, false, percentOfWidth, 0, height);
    }

    /**
     * Updates an existing separator dependent on (frameWidth - (amtSubtract * 2)).
     * Uses current height.
     * @param frame       to base width off of
     * @param amtSubtract number of px to subtract from width
     */
    public void updateWidth(JFrame frame, int amtSubtract) {
        updateSize(frame, true, 0.0F, amtSubtract, getHeight());
    }

    /**
     * Updates an existing separator dependent on (frameWidth - (amtSubtract * 2)).
     * @param frame       to base width off of
     * @param amtSubtract number of px to subtract from width
     * @param height      how many px tall should the bar be
     */
    public void updateSize(JFrame frame, int amtSubtract, int height) {
        updateSize(frame, true, 0.0F, amtSubtract, height);
    }

    /**
     * Updates an existing separator dependent on a % of the frame width.
     * If subtract is true, the width of the separator will instead be width - %width,
     * OR if %=0.0 then (width - (amtSubtract * 2)).
     * @param frame          to base width off of
     * @param subtract       do you want to subtract from width instead?
     * @param percentOfWidth what % of the total should the separator fill
     * @param amtSubtract    number of px to subtract from width
     * @param height         how many px tall should the bar be
     */
    private void updateSize(JFrame frame, boolean subtract, float percentOfWidth, int amtSubtract, int height) {
        int width;
        if (subtract) {
            width = (percentOfWidth != 0.0F)
                    ? frame.getWidth() - Math.round(frame.getWidth() * (percentOfWidth))
                    : frame.getWidth() - (amtSubtract * 2);
        } else width = Math.round(frame.getWidth() * percentOfWidth);

        Dimension nd = new Dimension(width, height);
        this.setMinimumSize(nd);
        this.setPreferredSize(nd);
        this.setMaximumSize(nd);
        this.setSize(nd);
    }

    @Override
    public void updateUI() {
        super.updateUI();
        setBackground(UIManager.getColor("Separator.foreground"));
    }
}
