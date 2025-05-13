// dro1dDev - created: 2025-02-18

package com.everdro1d.libs.swing.themes;

import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.UIManager;

/**
 * A custom Look and Feel based on FlatLaf for Java Swing applications.
 * <p>
 * The UI defaults are loaded from {@code EverDarkLaf.properties}
 * </p>
 * @see EverLightLaf
 */
public class EverDarkLaf extends FlatDarkLaf {
    public static final String NAME = "EverDarkLaf";

    /**
     * Sets the application look and feel to this LaF
     * using {@link UIManager#setLookAndFeel(javax.swing.LookAndFeel)}.
     * @return true if the look and feel was set successfully, false otherwise
     */
    public static boolean setup() {
        return setup( new EverDarkLaf() );
    }

    /**
     * Adds this look and feel to the set of available look and feels.
     * <p>
     * Useful if your application uses {@link UIManager#getInstalledLookAndFeels()}
     * to query available LaFs and display them to the user in a combobox.
     * </p>
     * @see #isLafInstalled()
     */
    public static void installLafInfo() {
        installLafInfo( NAME, EverDarkLaf.class );
    }

    /**
     * Returns true if this look and feel is installed.
     * <p>
     * Useful if your application uses {@link UIManager#getInstalledLookAndFeels()}
     * to query available LaFs and display them to the user in a combobox.
     * </p>
     * @return true if this look and feel is installed, false otherwise
     * @see #installLafInfo()
     */
    public static boolean isLafInstalled() {
        for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            if (info.getName().equals(NAME)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String getName() {
        return NAME;
    }
}
