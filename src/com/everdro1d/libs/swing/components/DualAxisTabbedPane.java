/**************************************************************************************************
 * dro1dDev 2025.                                                                                 *
 **************************************************************************************************/

package com.everdro1d.libs.swing.components;

import javax.swing.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class DualAxisTabbedPane extends JTabbedPane {
    // a tabbed pane on top containing a tabbed pane on the left vertically
    // based on the tab selected on the top tabbed pane, switch the set of left tabs
    // both the top and left tabbed panes don't wrap tabs and must be scrollable
    // if the tabs exceed the width or height, respectively.
    // | Outer Tab 1 | Outer Tab 2 | Outer Tab 3 |
    // | ----------- | ----------- | ----------- |
    // | Panel Tab 1 | A JPanel                  |
    // | Panel Tab 2 |                           |
    // | Panel Tab 3 |                           |
    private int tabLayoutPolicy;
    private int xTabPlacement;
    private int yTabPlacement;

    /** A tabbed pane with a row/column of tabs on each axis.
     * @param map       (tab name), ( (inner tab name), (JPanel to show in tab) )
     * @param wrapTabs  wrap or scroll tabs
     * @param xTop      show the x-axis tabs on the top or bottom
     * @param yLeft     show the y-axis tabs on the left or right
     * @see DualAxisTabbedPane#DualAxisTabbedPane(LinkedHashMap)
     * @see DualAxisTabbedPane#DualAxisTabbedPane(LinkedHashMap, boolean)
     * @see DualAxisTabbedPane#DualAxisTabbedPane(LinkedHashMap, boolean, boolean)
     **/
    public DualAxisTabbedPane(
            LinkedHashMap<String, LinkedHashMap<String, JPanel>> map,
            boolean wrapTabs,
            boolean xTop,
            boolean yLeft
    ) {

        this.tabLayoutPolicy = wrapTabs ? JTabbedPane.WRAP_TAB_LAYOUT : JTabbedPane.SCROLL_TAB_LAYOUT;

        this.xTabPlacement = xTop ? JTabbedPane.TOP : JTabbedPane.BOTTOM;

        this.yTabPlacement = yLeft ? JTabbedPane.LEFT : JTabbedPane.RIGHT;

        // x-axis tabs
        super.setTabLayoutPolicy(tabLayoutPolicy);
        super.setTabPlacement(xTabPlacement);

        // y-axis tabs per x-tab
        // <outer tab name>, <inner tab name>, <jpanel to show on inner tab>
        for (Map.Entry<String, LinkedHashMap<String, JPanel>> outerEntry : map.entrySet()) {
            JTabbedPane innerTabbedPane = new JTabbedPane(yTabPlacement);
            innerTabbedPane.setTabLayoutPolicy(tabLayoutPolicy);

            for (Map.Entry<String, JPanel> innerEntry : outerEntry.getValue().entrySet()) {
                innerTabbedPane.addTab(innerEntry.getKey(), innerEntry.getValue());
            }
            addTab(outerEntry.getKey(), innerTabbedPane);
        }
    }

    /**
     * A tabbed pane with a row/column of tabs on each axis. Defaults to scroll tabs, x-axis to Top, and y-axis to Left.
     * @param map      (tab name), ( (inner tab name), (JPanel to show in tab) )
     * @see DualAxisTabbedPane#DualAxisTabbedPane(LinkedHashMap, boolean)
     * @see DualAxisTabbedPane#DualAxisTabbedPane(LinkedHashMap, boolean, boolean)
     * @see DualAxisTabbedPane#DualAxisTabbedPane(LinkedHashMap, boolean, boolean, boolean)
     **/
    public DualAxisTabbedPane(
            LinkedHashMap<String, LinkedHashMap<String, JPanel>> map
    ) {
        this(map, false, true, true);
    }

    /**
     * A tabbed pane with a row/column of tabs on each axis. Defaults x-axis to Top, and y-axis to Left.
     * @param map      (tab name), ( (inner tab name), (JPanel to show in tab) )
     * @param wrapTabs wrap or scroll tabs
     * @see DualAxisTabbedPane#DualAxisTabbedPane(LinkedHashMap)
     * @see DualAxisTabbedPane#DualAxisTabbedPane(LinkedHashMap, boolean, boolean)
     * @see DualAxisTabbedPane#DualAxisTabbedPane(LinkedHashMap, boolean, boolean, boolean)
     **/
    public DualAxisTabbedPane(
            LinkedHashMap<String, LinkedHashMap<String, JPanel>> map,
            boolean wrapTabs
    ) {
        this(map, wrapTabs, true, true);
    }

    /**
     * A tabbed pane with a row/column of tabs on each axis. Defaults y-axis to Left.
     * @param map      (tab name), ( (inner tab name), (JPanel to show in tab) )
     * @param wrapTabs wrap or scroll tabs
     * @param xTop     show the x-axis tabs on the top or bottom
     * @see DualAxisTabbedPane#DualAxisTabbedPane(LinkedHashMap)
     * @see DualAxisTabbedPane#DualAxisTabbedPane(LinkedHashMap, boolean)
     * @see DualAxisTabbedPane#DualAxisTabbedPane(LinkedHashMap, boolean, boolean, boolean)
     **/
    public DualAxisTabbedPane(
            LinkedHashMap<String, LinkedHashMap<String, JPanel>> map,
            boolean wrapTabs,
            boolean xTop
    ) {
        this(map, wrapTabs, xTop, true);
    }

    /**
     * Returns the Placement of the tabs on the x-axis.
     *
     * @return {@code int} - one of JTabbedPane.TOP or JTabbedPane.BOTTOM
     */
    public int getXTabPlacement() {
        return this.xTabPlacement;
    }

    /**
     * Returns the Placement of the tabs on the y-axis.
     *
     * @return {@code int} - one of JTabbedPane.LEFT or JTabbedPane.RIGHT
     */
    public int getYTabPlacement() {
        return this.yTabPlacement;
    }
}