// dro1dDev - created: 2025-02-18

package com.everdro1d.libs.swing.themes;

import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.UIManager;

public class EverDarkLaf extends FlatDarkLaf {
    public static final String NAME = "EverDarkLaf";

    public static boolean setup() {
        return setup( new EverDarkLaf() );
    }

    public static void installLafInfo() {
        installLafInfo( NAME, EverDarkLaf.class );
    }

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
