// dro1dDev - created: 2025-02-18

package com.everdro1d.libs.swing.themes;

import com.formdev.flatlaf.FlatLightLaf;

public class EverLightLaf extends FlatLightLaf {
    public static final String NAME = "EverLightLaf";

    public static boolean setup() {
        return setup( new EverLightLaf() );
    }

    public static void installLafInfo() {
        installLafInfo( NAME, EverLightLaf.class );
    }

    @Override
    public String getName() {
        return NAME;
    }
}
