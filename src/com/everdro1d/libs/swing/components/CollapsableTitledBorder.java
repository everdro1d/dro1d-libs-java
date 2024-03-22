package com.everdro1d.libs.swing.components;

import com.everdro1d.libs.swing.SwingGUI;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

public class CollapsableTitledBorder extends TitledBorder {

    public CollapsableTitledBorder(
            JComponent panel, String titleText, boolean expandedDefault,
            boolean exclusive, int panelExpandedHeight, int panelCollapsedHeight
    ) {
        this(panel, titleText, expandedDefault, exclusive, panelExpandedHeight, panelCollapsedHeight, null);
    }

    public CollapsableTitledBorder(
            JComponent panel, String titleText, boolean expandedDefault,
            boolean exclusive, int panelExpandedHeight, int panelCollapsedHeight,
            Consumer<JTabbedPane> tabbedPaneExpandFunc
    ) {
        super(titleText);

        if (!(panel instanceof JPanel || panel instanceof JTabbedPane)) {
            return;
        }

        CollapsableTitledBorderMouseAdapter adapter = new CollapsableTitledBorderMouseAdapter(
                panel, titleText, expandedDefault, panelExpandedHeight, panelCollapsedHeight,
                this, tabbedPaneExpandFunc, exclusive
        );
        panel.addMouseListener(adapter);

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

        private static CollapsableTitledBorderMouseAdapter currentlyExpanded = null;

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getY() > panel.getBorder().getBorderInsets(panel).top) {
                return;
            }
            togglePanelExpanded();
        }

        private void togglePanelExpanded() {
            if (panelExpanded) {
                if (exclusive && this == currentlyExpanded) {
                    // If the panel is exclusive, and it's the currently expanded panel, stop
                    return;
                }
                collapsePanel();
            } else {
                expandPanel();
            }
        }

        private void expandPanel() {
            if (exclusive && this != currentlyExpanded) {
                if (currentlyExpanded != null) {
                    currentlyExpanded.collapsePanel();
                }

                panelExpanded = true;
                currentlyExpanded = this;
            } else if (!exclusive) {
                panelExpanded = true;
            }
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

            } else {
                for (Component c : panel.getComponents()) {
                    c.setVisible(panelExpanded);
                }
            }

            panel.revalidate();
            panel.repaint();
            SwingGUI.setHandCursorToClickableComponents(panel);
        }
    }
}
