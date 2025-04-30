package com.everdro1d.libs.swing.components;

import javax.swing.*;
import java.awt.*;

public class FlexWidthSeparator extends JPanel {
    public FlexWidthSeparator() {
        setBackground(UIManager.getColor("Separator.foreground"));
        setPreferredSize(new Dimension(0,1));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        setMinimumSize(new Dimension(0, 1));
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
    }
}
