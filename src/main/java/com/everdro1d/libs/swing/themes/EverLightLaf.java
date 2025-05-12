// dro1dDev - created: 2025-02-18

package com.everdro1d.libs.swing.themes;

import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.UIManager;

public class EverLightLaf extends FlatLightLaf {
    public static final String NAME = "EverLightLaf";

    public static boolean setup() {
        return setup( new EverLightLaf() );
    }

    public static void installLafInfo() {
        installLafInfo( NAME, EverLightLaf.class );
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
