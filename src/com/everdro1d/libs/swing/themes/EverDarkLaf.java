// dro1dDev - created: 2025-02-18

package com.everdro1d.libs.swing.themes;

import com.formdev.flatlaf.FlatDarkLaf;

public class EverDarkLaf extends FlatDarkLaf {
    public static final String NAME = "EverDarkLaf";

    public static boolean setup() {
        return setup( new EverDarkLaf() );
    }

    public static void installLafInfo() {
        installLafInfo( NAME, EverDarkLaf.class );
    }

    @Override
    public String getName() {
        return NAME;
    }
}
