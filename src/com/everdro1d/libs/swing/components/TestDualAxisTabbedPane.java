/**************************************************************************************************
 * Copyright (c) dro1dDev 2025.                                                                   *
 **************************************************************************************************/

package com.everdro1d.libs.swing.components;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class TestDualAxisTabbedPane {
    public static void main(String[] args) {
        Map<String, Map<String, JPanel>> map = new HashMap<>();
        for (int i = 1; i <= 10; i++) {
            Map<String, JPanel> innerMap = new HashMap<>();
            for (int j = 1; j <= 40; j++) {
                innerMap.put("Panel Tab " + j, new JPanel());
            }
            map.put("Outer Tab " + i, innerMap);
        }

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new DualAxisTabbedPane(map));
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
