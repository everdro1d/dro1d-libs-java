// dro1dDev - created: 2025-04-29

package com.everdro1d.libs.swing.components;

import com.everdro1d.libs.core.Utils;

import javax.swing.*;
import java.awt.*;

/**
 * A custom Swing button that looks like a hyperlink.
 * <p>
 * The button displays a URL as underlined text and opens it in
 * the default browser when clicked.
 * It supports customizable tool tip text, horizontal padding,
 * font name, and font size.
 * </p>
 *
 * <p><strong>Example usage:</strong></p>
 * <blockquote><pre>
 * HyperLinkButton linkButton = new HyperLinkButton(
 *     "https://everdro1d.github.io",
 *     "Click to visit everdro1d's website",
 *     SwingConstants.LEFT,
 *     "Tahoma",
 *     12
 * );
 * </pre></blockquote>
 */
public class HyperLinkButton extends JButton {
    /**
     * Creates a new HyperLinkButton with the specified URL and tool tip text.
     *
     * @param url the url to open and show
     * @param toolTipText tool tip text to show
     * @param horizontalPadding use SwingConstants.LEFT or SwingConstants.RIGHT
     * @param fontName name of font ex: "Tahoma"
     * @param fontSize font size
     */
    public HyperLinkButton(String url, String toolTipText, int horizontalPadding, String fontName, int fontSize) {
        setText("<html><u>" + url + "</u></html>");
        setToolTipText(toolTipText);
        setForeground(UIManager.getColor("Component.accentColor"));
        setBorder(BorderFactory.createEmptyBorder(
                0,
                (horizontalPadding == SwingConstants.LEFT) ? 4 : 0,
                0,
                (horizontalPadding == SwingConstants.RIGHT) ? 4 : 0
        ));
        setFont(new Font(fontName, Font.PLAIN, fontSize));
        setBorderPainted(false);
        setContentAreaFilled(false);
        setFocusPainted(false);

        addActionListener(e -> Utils.openLink(url));

    }

    // override updateUI to set the foreground color
    @Override
    public void updateUI() {
        super.updateUI();
        setForeground(UIManager.getColor("Component.accentColor"));
    }
}
