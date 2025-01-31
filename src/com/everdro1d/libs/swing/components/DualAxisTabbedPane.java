/**************************************************************************************************
 * Copyright (c) dro1dDev 2025.                                                                   *
 **************************************************************************************************/

package com.everdro1d.libs.swing.components;

import javax.swing.*;
import java.util.Map;

public class DualAxisTabbedPane extends JTabbedPane {
    // a tabbed pane on top containing a tabbed pane on the left vertically
    // based on the tab selected on the top tabbed pane, switch the left tabs
    // both the top and left tabbed panes don't wrap tabs and must be scrollable if the tabs exceed the width or height, respectively
    // | Outer Tab 1 | Outer Tab 2 | Outer Tab 3 |
    // | Panel Tab 1 | A JPanel                  |
    // | Panel Tab 2 |                           |
    // | Panel Tab 3 |                           |
// TODO: only allow one row of tabs and add scrollbars if tabs exceed the width
    public DualAxisTabbedPane(Map<String, Map<String, JPanel> > map) {
        for (Map.Entry<String, Map<String, JPanel>> outerEntry : map.entrySet()) {
            JTabbedPane innerTabbedPane = new JTabbedPane(JTabbedPane.LEFT);
            for (Map.Entry<String, JPanel> innerEntry : outerEntry.getValue().entrySet()) {
                innerTabbedPane.addTab(innerEntry.getKey(), innerEntry.getValue());
            }
            addTab(outerEntry.getKey(), innerTabbedPane);
        }
    }
}