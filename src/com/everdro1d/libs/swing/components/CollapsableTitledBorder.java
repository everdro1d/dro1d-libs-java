/**************************************************************************************************
 * dro1dDev 2025.                                                                                 *
 **************************************************************************************************/

package com.everdro1d.libs.swing.components;

import com.everdro1d.libs.swing.SwingGUI;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

public class CollapsableTitledBorder extends TitledBorder {

    private static boolean showTabbedPaneSeparators;

    public CollapsableTitledBorder(
            JComponent panel, String titleText, boolean expandedDefault,
            boolean exclusive, int panelExpandedHeight, boolean showTabbedPaneSeparators
    ) {
        this(panel, titleText, expandedDefault, exclusive, panelExpandedHeight, 50, showTabbedPaneSeparators);
    }

    public CollapsableTitledBorder(
            JComponent panel, String titleText, boolean expandedDefault,
            boolean exclusive, int panelExpandedHeight, int panelCollapsedHeight, boolean showTabbedPaneSeparators
    ) {
        this(panel, titleText, expandedDefault, exclusive, panelExpandedHeight, panelCollapsedHeight, null, showTabbedPaneSeparators);
    }

    public CollapsableTitledBorder(
            JComponent panel, String titleText, boolean expandedDefault,
            boolean exclusive, int panelExpandedHeight,
            Consumer<JTabbedPane> tabbedPaneExpandFunc, boolean showTabbedPaneSeparators
    ) {
        this(panel, titleText, expandedDefault, exclusive, panelExpandedHeight, 50, tabbedPaneExpandFunc, showTabbedPaneSeparators);
    }

    public CollapsableTitledBorder(
            JComponent panel, String titleText, boolean expandedDefault,
            boolean exclusive, int panelExpandedHeight
    ) {
        this(panel, titleText, expandedDefault, exclusive, panelExpandedHeight, 50, false);
    }

    public CollapsableTitledBorder(
            JComponent panel, String titleText, boolean expandedDefault,
            boolean exclusive, int panelExpandedHeight, int panelCollapsedHeight
    ) {
        this(panel, titleText, expandedDefault, exclusive, panelExpandedHeight, panelCollapsedHeight, null, false);
    }

    public CollapsableTitledBorder(
            JComponent panel, String titleText, boolean expandedDefault,
            boolean exclusive, int panelExpandedHeight,
            Consumer<JTabbedPane> tabbedPaneExpandFunc
    ) {
        this(panel, titleText, expandedDefault, exclusive, panelExpandedHeight, 50, tabbedPaneExpandFunc, false);
    }

    public CollapsableTitledBorder(
            JComponent panel, String titleText, boolean expandedDefault,
            boolean exclusive, int panelExpandedHeight, int panelCollapsedHeight,
            Consumer<JTabbedPane> tabbedPaneExpandFunc, boolean showTabbedPaneSeparators
    ) {
        super(titleText);

        if (!(panel instanceof JPanel || panel instanceof JTabbedPane)) {
            return;
        }

        this.showTabbedPaneSeparators = showTabbedPaneSeparators;

        CollapsableTitledBorderMouseAdapter adapter = new CollapsableTitledBorderMouseAdapter(
                panel, titleText, expandedDefault, panelExpandedHeight, panelCollapsedHeight,
                this, tabbedPaneExpandFunc, exclusive
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
                return; // do not collapse the panel if it is exclusive and currently expanded
            }
            if (panelExpanded) collapsePanel(); else expandPanel();
        }

        private void expandPanel() {
            if (exclusive && this != currentlyExpanded && currentlyExpanded != null) {
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
            panel.setPreferredSize(new Dimension(panel.getPreferredSize().width,
                    panelExpanded ? panelExpandedHeight : panelCollapsedHeight));

            String s = panelExpanded ? "▼ " : "▲ ";
            border.setTitle(s + titleText);

            if (panel instanceof JTabbedPane tabbedPane) {
                for (int i = 0; i < tabbedPane.getTabCount(); i++) {
                    tabbedPane.setEnabledAt(i, panelExpanded);
                    tabbedPane.setTabComponentAt(i, panelExpanded ? null : new JLabel());
                }
                if (panelExpanded) tabbedPaneExpandFunc.accept(tabbedPane);

                if (showTabbedPaneSeparators) {
                    UIManager.put("TabbedPane.showTabSeparators", panelExpanded);
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
                    panel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                }
                return;
            }
            int newCursorType = e.getY() <= panel.getBorder().getBorderInsets(panel).top ?
                    Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR;

            if (cursorType != newCursorType) {
                panel.setCursor(Cursor.getPredefinedCursor(newCursorType));
            }
        }
    }
}
