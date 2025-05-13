// dro1dDev - created: 2024-03-21

package com.everdro1d.libs.swing.components;

import com.everdro1d.libs.swing.SwingGUI;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

/**
 * A collapsable titled border for JPanels and JTabbedPanes.<p>
 * If using a JTabbedPane and you want to get the expanded status, implement a method like below.
 * <blockquote><pre>
 *     private boolean isOSTabbedPaneExpanded() {
 *         return !(osTabbedPane.getTabComponentAt(0) instanceof JLabel);
 *     }
 * </pre></blockquote>
 */
public class CollapsableTitledBorder extends TitledBorder {

    private static boolean showTabbedPaneSeparators;

    /**
     * Creates a collapsable titled border for a JPanel or JTabbedPane.
     *
     * <p><strong>Default params set to:</strong></p>
     * <blockquote><pre>
     * panelCollapsedHeight = 50
     * </pre></blockquote>
     *
     * @param panel the panel to add the border to
     * @param titleText the text to display in the title
     * @param expandedDefault true if the panel should be expanded by default
     * @param exclusive true if only one panel can be expanded at a time
     * @param panelExpandedHeight the height of the panel when expanded
     * @param showTabbedPaneSeparators true if the tabbed pane separators should be shown when expanded
     */
    public CollapsableTitledBorder(
            JComponent panel, String titleText, boolean expandedDefault,
            boolean exclusive, int panelExpandedHeight, boolean showTabbedPaneSeparators
    ) {
        this(panel, titleText, expandedDefault, exclusive, panelExpandedHeight, 50, showTabbedPaneSeparators);
    }

    /**
     * Creates a collapsable titled border for a JPanel or JTabbedPane.
     *
     * <p><strong>Default params set to:</strong></p>
     * <blockquote><pre>
     * tabbedPaneExpandFunc = null
     * </pre></blockquote>
     *
     * @param panel the panel to add the border to
     * @param titleText the text to display in the title
     * @param expandedDefault true if the panel should be expanded by default
     * @param exclusive true if only one panel can be expanded at a time
     * @param panelExpandedHeight the height of the panel when expanded
     * @param panelCollapsedHeight the height of the panel when collapsed
     * @param showTabbedPaneSeparators true if the tabbed pane separators should be shown when expanded
     */
    public CollapsableTitledBorder(
            JComponent panel, String titleText, boolean expandedDefault,
            boolean exclusive, int panelExpandedHeight, int panelCollapsedHeight, boolean showTabbedPaneSeparators
    ) {
        this(panel, titleText, expandedDefault, exclusive, panelExpandedHeight, panelCollapsedHeight, null, showTabbedPaneSeparators);
    }


    /**
     * Creates a collapsable titled border for a JPanel or JTabbedPane.
     *
     * <p><strong>Default params set to:</strong></p>
     * <blockquote><pre>
     * panelCollapsedHeight = 50
     * </pre></blockquote>
     *
     * @param panel the panel to add the border to
     * @param titleText the text to display in the title
     * @param expandedDefault true if the panel should be expanded by default
     * @param exclusive true if only one panel can be expanded at a time
     * @param panelExpandedHeight the height of the panel when expanded
     * @param tabbedPaneExpandFunc a function to call when the tabbed pane is expanded
     * @param showTabbedPaneSeparators true if the tabbed pane separators should be shown when expanded
     */
    public CollapsableTitledBorder(
            JComponent panel, String titleText, boolean expandedDefault,
            boolean exclusive, int panelExpandedHeight,
            Consumer<JTabbedPane> tabbedPaneExpandFunc, boolean showTabbedPaneSeparators
    ) {
        this(panel, titleText, expandedDefault, exclusive, panelExpandedHeight, 50, tabbedPaneExpandFunc, showTabbedPaneSeparators);
    }

    /**
     * Creates a collapsable titled border for a JPanel or JTabbedPane.
     *
     * <p><strong>Default params set to:</strong></p>
     * <blockquote><pre>
     * panelCollapsedHeight = 50
     * tabbedPaneExpandFunc = null
     * showTabbedPaneSeparators = false
     * </pre></blockquote>
     *
     * @param panel the panel to add the border to
     * @param titleText the text to display in the title
     * @param expandedDefault true if the panel should be expanded by default
     * @param exclusive true if only one panel can be expanded at a time
     * @param panelExpandedHeight the height of the panel when expanded
     */
    public CollapsableTitledBorder(
            JComponent panel, String titleText, boolean expandedDefault,
            boolean exclusive, int panelExpandedHeight
    ) {
        this(panel, titleText, expandedDefault, exclusive, panelExpandedHeight, 50, null, false);
    }

    /**
     * Creates a collapsable titled border for a JPanel or JTabbedPane.
     *
     * <p><strong>Default params set to:</strong></p>
     * <blockquote><pre>
     * tabbedPaneExpandFunc = null
     * showTabbedPaneSeparators = false
     * </pre></blockquote>
     *
     * @param panel the panel to add the border to
     * @param titleText the text to display in the title
     * @param expandedDefault true if the panel should be expanded by default
     * @param exclusive true if only one panel can be expanded at a time
     * @param panelExpandedHeight the height of the panel when expanded
     * @param panelCollapsedHeight the height of the panel when collapsed
     */
    public CollapsableTitledBorder(
            JComponent panel, String titleText, boolean expandedDefault,
            boolean exclusive, int panelExpandedHeight, int panelCollapsedHeight
    ) {
        this(panel, titleText, expandedDefault, exclusive, panelExpandedHeight, panelCollapsedHeight, null, false);
    }

    /**
     * Creates a collapsable titled border for a JPanel or JTabbedPane.
     *
     * <p><strong>Default params set to:</strong></p>
     * <blockquote><pre>
     * panelCollapsedHeight = 50
     * showTabbedPaneSeparators = false
     * </pre></blockquote>
     *
     * @param panel the panel to add the border to
     * @param titleText the text to display in the title
     * @param expandedDefault true if the panel should be expanded by default
     * @param exclusive true if only one panel can be expanded at a time
     * @param panelExpandedHeight the height of the panel when expanded
     * @param tabbedPaneExpandFunc a function to call when the tabbed pane is expanded
     */
    public CollapsableTitledBorder(
            JComponent panel, String titleText, boolean expandedDefault,
            boolean exclusive, int panelExpandedHeight,
            Consumer<JTabbedPane> tabbedPaneExpandFunc
    ) {
        this(panel, titleText, expandedDefault, exclusive, panelExpandedHeight, 50, tabbedPaneExpandFunc, false);
    }

    /**
     * Creates a collapsable titled border for a JPanel or JTabbedPane.
     *
     * @param panel the panel to add the border to
     * @param titleText the text to display in the title
     * @param expandedDefault true if the panel should be expanded by default
     * @param exclusive true if only one panel can be expanded at a time
     * @param panelExpandedHeight the height of the panel when expanded
     * @param panelCollapsedHeight the height of the panel when collapsed
     * @param tabbedPaneExpandFunc a function to call when the tabbed pane is expanded
     * @param showTabbedPaneSeparators true if the tabbed pane separators should be shown when expanded
     */
    public CollapsableTitledBorder(
            JComponent panel, String titleText, boolean expandedDefault,
            boolean exclusive, int panelExpandedHeight, int panelCollapsedHeight,
            Consumer<JTabbedPane> tabbedPaneExpandFunc, boolean showTabbedPaneSeparators
    ) {
        super(titleText);

        if (!(panel instanceof JPanel || panel instanceof JTabbedPane)) {
            return;
        }

        CollapsableTitledBorder.showTabbedPaneSeparators = showTabbedPaneSeparators;

        CollapsableTitledBorderMouseAdapter adapter =
                new CollapsableTitledBorderMouseAdapter(
                    panel, titleText, expandedDefault, panelExpandedHeight,
                    panelCollapsedHeight, this, tabbedPaneExpandFunc, exclusive
                );
        panel.addMouseListener(adapter);
        panel.addMouseMotionListener(adapter);

        if (expandedDefault) {
            adapter.expandPanel();
        } else {
            adapter.collapsePanel();
        }

    }

    private static class CollapsableTitledBorderMouseAdapter extends MouseAdapter {
        private boolean panelExpanded;
        private int panelExpandedHeight, panelCollapsedHeight;
        private String titleText;
        private JComponent panel;
        private TitledBorder border;
        private Consumer<JTabbedPane> tabbedPaneExpandFunc;
        private boolean exclusive;

        public CollapsableTitledBorderMouseAdapter(
                JComponent panel , String titleText, boolean expandedDefault,
                int panelExpandedHeight, int panelCollapsedHeight, TitledBorder border,
                Consumer<JTabbedPane> tabbedPaneExpandFunc, boolean exclusive
        ) {
            this.panelExpanded = expandedDefault;
            this.panelExpandedHeight = panelExpandedHeight;
            this.panelCollapsedHeight = panelCollapsedHeight;
            this.titleText = titleText;
            this.panel = panel;
            this.border = border;
            this.tabbedPaneExpandFunc = tabbedPaneExpandFunc;
            this.exclusive = exclusive;
        }

        @Override
        public void mouseMoved(MouseEvent e) { // change cursor to hand when hovering over title
            updateMouseCursor(e);
        }

        private static CollapsableTitledBorderMouseAdapter currentlyExpanded = null;

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getY() > panel.getBorder().getBorderInsets(panel).top) {
                return;
            }
            togglePanelExpanded();
            updateMouseCursor(e);
        }

        private void togglePanelExpanded() {
            if (exclusive && panelExpanded && this == currentlyExpanded) {
                // do not collapse the panel if it is
                // exclusive and currently expanded
                return;
            }
            if (panelExpanded) collapsePanel(); else expandPanel();
        }

        private void expandPanel() {
            if (exclusive
                && this != currentlyExpanded
                && currentlyExpanded != null
            ) {
                currentlyExpanded.collapsePanel();
            }
            panelExpanded = true;
            currentlyExpanded = exclusive ? this : null;
            updatePanel();
        }

        private void collapsePanel() {
            panelExpanded = false;
            updatePanel();
        }

        private void updatePanel() {
            panel.setPreferredSize(new Dimension(
                    panel.getPreferredSize().width,
                    panelExpanded ? panelExpandedHeight : panelCollapsedHeight
            ));

            String s = panelExpanded ? "▼ " : "▲ ";
            border.setTitle(s + titleText);

            if (panel instanceof JTabbedPane tabbedPane) {
                for (int i = 0; i < tabbedPane.getTabCount(); i++) {
                    tabbedPane.setEnabledAt(i, panelExpanded);
                    tabbedPane.setTabComponentAt(
                            i, panelExpanded ? null : new JLabel()
                    );
                }
                if (panelExpanded) tabbedPaneExpandFunc.accept(tabbedPane);

                // tabbed pane cannot be hidden as the border is on the tabbed pane
                if (showTabbedPaneSeparators) {
                    UIManager.put(
                            "TabbedPane.showTabSeparators", panelExpanded
                    );
                    tabbedPane.updateUI();
                }

            } else {
                for (Component c : panel.getComponents()) {
                    c.setVisible(panelExpanded);
                }
            }

            panel.revalidate();
            panel.repaint();
            SwingGUI.setHandCursorToClickableComponents(panel);
        }

        private void updateMouseCursor(MouseEvent e) {
            int cursorType = panel.getCursor().getType();
            if (exclusive && this == currentlyExpanded) {
                if (cursorType == Cursor.HAND_CURSOR) {
                    panel.setCursor(
                            Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)
                    );
                }
                return;
            }
            int newCursorType =
                    e.getY() <= panel.getBorder().getBorderInsets(panel).top
                            ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR;

            if (cursorType != newCursorType) {
                panel.setCursor(Cursor.getPredefinedCursor(newCursorType));
            }
        }
    }
}
