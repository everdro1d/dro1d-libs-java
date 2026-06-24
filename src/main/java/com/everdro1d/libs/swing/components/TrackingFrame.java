package com.everdro1d.libs.swing.components;

import com.everdro1d.libs.swing.SwingGUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.prefs.Preferences;

/**
 * JFrame implementation that automatically tracks location on screen.
 * <br>Extend this class and use <code>super(prefs, "whateverPrefix");</code> to apply.
 * <br>
 * <br><b>NOTE: Not compatible with setLocationRelativeTo().</b>
 * @see TrackingFrame#TrackingFrame(Preferences, String)
 */
public abstract class TrackingFrame extends JFrame {

    private final JFrame frame;
    private final Preferences prefs;
    private final String prefixKey;

    private int[] windowPosition = {0, 0, 0};
    private Dimension windowSize = new Dimension();

    /**
     * JFrame implementation that automatically tracks location on screen.
     * <br>Extend this class and use <code>super(prefs, "whateverPrefix");</code> to apply.
     * <br>
     * <br><b>NOTE: Not compatible with setLocationRelativeTo().</b>
     * @param prefs preferences object to save position information to.
     * @param prefixKey prefix to attach on the preferences keys.<br>(Recommended: class name)
     */
    public TrackingFrame(Preferences prefs, String prefixKey) {
        this.frame = this;
        this.prefs = prefs;
        this.prefixKey = prefixKey;

        // INIT POS ---
        loadWindowPosition();
        setWindowPosition();

        // READ ADJUSTMENTS ---
        this.addComponentListener(new ComponentAdapter() {
            public void componentMoved(ComponentEvent e) {
                if (isShowing()) {
                    windowPosition = SwingGUI.getFramePositionOnScreen(frame);
                }
            }

            public void componentResized(ComponentEvent e) {
                windowSize = frame.getSize();
            }
        });

        // SAVE ADJUSTED POS ---
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                saveWindowPosition();
            }
        });

    }

    private void loadWindowPosition() {
        windowPosition[0] = prefs.getInt(prefixKey + "FramePosX", 0);
        windowPosition[1] = prefs.getInt(prefixKey + "FramePosY", 0);
        windowPosition[2] = prefs.getInt(prefixKey + "ActiveMonitor", 0);

        windowSize.width  = prefs.getInt(prefixKey + "WindowWidth", frame.getMinimumSize().width);
        windowSize.height = prefs.getInt(prefixKey + "WindowHeight", frame.getMinimumSize().height);
    }

    private void setWindowPosition() {
        SwingGUI.setFramePosition(
                frame,
                windowPosition[0],
                windowPosition[1],
                windowPosition[2]
        );

        frame.setSize(
                windowSize.width,
                windowSize.height
        );
    }

    private void saveWindowPosition() {
        prefs.putInt(prefixKey + "FramePosX",     windowPosition[0]);
        prefs.putInt(prefixKey + "FramePosY",     windowPosition[1]);
        prefs.putInt(prefixKey + "ActiveMonitor", windowPosition[2]);

        prefs.putInt(prefixKey + "WindowWidth",   windowSize.width);
        prefs.putInt(prefixKey + "WindowHeight",  windowSize.height);
    }
}
