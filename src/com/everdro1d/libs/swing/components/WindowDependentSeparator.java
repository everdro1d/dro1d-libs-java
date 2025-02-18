/**************************************************************************************************
 * Copyright (c) dro1dDev 2025.                                                                   *
 **************************************************************************************************/

package com.everdro1d.libs.swing.components;

import javax.swing.*;
import java.awt.*;

public class WindowDependentSeparator extends JSeparator {

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
        updateWidth(frame, subtract, percentOfWidth, amtSubtract, height);
    }

    /**
     * Updates an existing separator dependent on a % of the frame width.
     *
     * @param frame          to base width off of
     * @param percentOfWidth what % of the total should the separator fill
     * @param height         how many px tall should the bar be
     */
    public void updateWidth(JFrame frame, float percentOfWidth, int height) {
        updateWidth(frame, false, percentOfWidth, 0, height);
    }

    /**
     * Updates an existing separator dependent on (frameWidth - (amtSubtract * 2)).
     * @param frame       to base width off of
     * @param amtSubtract number of px to subtract from width
     * @param height      how many px tall should the bar be
     */
    public void updateWidth(JFrame frame, int amtSubtract, int height) {
        updateWidth(frame, true, 0.0F, amtSubtract, height);
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
    private void updateWidth(JFrame frame, boolean subtract, float percentOfWidth, int amtSubtract, int height) {
        int width;
        if (subtract) {
            width = (percentOfWidth != 0.0F)
                    ? frame.getWidth() - Math.round(frame.getWidth() * (percentOfWidth))
                    : frame.getWidth() - (amtSubtract * 2);
        } else width = Math.round(frame.getWidth() * percentOfWidth);

        this.setMinimumSize(new Dimension(width, height));
        this.setPreferredSize(new Dimension(width, height));
        this.setMaximumSize(new Dimension(width, height));
    }
}
