// dro1dDev - created: 2025-05-10

package com.everdro1d.libs.swing.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * A custom {@link JTextField} component that includes a 'default text' that appears
 * when the field is empty and unfocused. The default text is styled
 * with a desaturated/lighter color to differentiate it from user input.
 *
 * <p>This component automatically handles focus changes and user input to
 * toggle between the default text and the actual input. It also provides
 * methods to set and retrieve the default text.</p>
 *
 * <p><strong>Example usage:</strong></p>
 * <blockquote><pre>
 * LabeledTextField textField = new LabeledTextField("Enter your name");
 * </pre></blockquote>
 */
public class LabeledTextField extends JTextField {
    private String defaultText;
    private Color defaultForeground;
    private boolean isDefaultTyped = false;
    private float opacity;

    /**
     * Constructs a {@link LabeledTextField} with the specified default text.
     *
     * @param defaultText the default text to display when the field is empty and unfocused
     */
    public LabeledTextField(String defaultText) {
        this(defaultText, 0.65f);
    }

    /**
     * Constructs a {@link LabeledTextField} with the specified default text.
     *
     * @param defaultText the default text to display when the field is empty and unfocused
     * @param opacity the opacity of the default text color (0.0f to 1.0f)
     */
    public LabeledTextField(String defaultText, float opacity) {
        super();
        this.defaultText = defaultText;
        this.defaultForeground = this.getForeground();
        this.opacity = opacity;

        this.setText(defaultText, true);
        this.setForeground(getDesaturatedForeground(), true);

        // change color on focus change
        this.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                updateDefaultText(true);
            }

            @Override
            public void focusLost(FocusEvent e) {
                updateDefaultText(false);
            }
        });

        // check for case where user typed the default text
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                isDefaultTyped = getText().trim().equals(defaultText);
            }
        });
    }

    /**
     * Sets the default text to be displayed in the text field when it is empty and unfocused.
     *
     * @param defaultText the default text to display
     */
    public void setDefaultText(String defaultText) {
        this.defaultText = defaultText;
    }

    /**
     * Retrieves the default text displayed in the text field when it is empty and unfocused.
     *
     * @return the default text
     */
    public String getDefaultText() {
        return defaultText;
    }

    /**
     * Sets the current opacity of the default text color.
     *
     * @return the current opacity value (0.0f to 1.0f)
     */
    public void setOpacity(float opacity) {
        if (opacity < 0.0f || opacity > 1.0f) {
            throw new IllegalArgumentException("Opacity must be between 0.0f and 1.0f");
        }
        this.opacity = opacity;
        this.setForeground(getDesaturatedForeground(), true);
    }

    /**
     * Retrieves the current opacity of the default text color.
     *
     * @return the opacity value (0.0f to 1.0f)
     */
    public float getOpacity() {
        return opacity;
    }

    // update color based on whether we have focus, also
    // set the text to "" if focus gained and defaultText.
    private void updateDefaultText(boolean focusGained) {
        if (
                this.getText() == null
                || this.getText().isBlank()
                || (this.getText().trim().equals(defaultText) && !isDefaultTyped)
        ) {
            this.setText(focusGained ? "" : defaultText, true);
            this.setForeground(
                    focusGained ? defaultForeground : getDesaturatedForeground(),
                    true
            );
        } else {
            this.setText(this.getText(), true);
            this.setForeground(defaultForeground, true);
        }
    }

    // normal foreground color and change the opacity to 65% (default)
    private Color getDesaturatedForeground() {
        Color c = this.defaultForeground;
        c = c == null ? this.getBackground() : c;

        float[] cArr = c.getRGBComponents(null);
        float alpha = cArr[3] * opacity;

        return new Color(cArr[0], cArr[1], cArr[2], alpha);
    }

    // overrides ---|

    @Override
    public void setForeground(Color foreground) {
        setForeground(foreground, false);
    }

    @Override
    public void setText(String text) {
        setText(text, false);
    }

    // end overrides ---|

    private void setForeground(Color fg, boolean internal) {
        super.setForeground(fg);
        if (!internal) {
            this.defaultForeground = fg;
            updateDefaultText(false);
        }
    }

    private void setText(String text, boolean internal) {
        super.setText(text);
        if (!internal) isDefaultTyped = getText().trim().equals(defaultText);
    }
}
