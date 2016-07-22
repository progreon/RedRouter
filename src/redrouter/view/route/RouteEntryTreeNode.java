/*
 * Copyright (C) 2016 Marco Willems
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package redrouter.view.route;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import redrouter.route.RouteEntry;

/**
 *
 * @author Marco Willems
 */
public abstract class RouteEntryTreeNode extends DefaultMutableTreeNode {

    protected final RouteTree tree;
    protected RouteEntry routeEntry;

    protected JPanel view;
    private final Border border;
    private final int initAvailableWidth = 400;

    public RouteEntryTreeNode(RouteTree tree, RouteEntry routeEntry) {
        this.tree = tree;
        this.routeEntry = routeEntry;
        Border marginBorder = BorderFactory.createEmptyBorder(4, 4, 4, 4);
        Border lineBorder = BorderFactory.createLineBorder(tree.nodeBorderColor, 2, false);
        Border emptyBorder = BorderFactory.createLineBorder(tree.getBackground(), 2, false);
        Border outsideBorder = BorderFactory.createCompoundBorder(emptyBorder, lineBorder);
        border = BorderFactory.createCompoundBorder(outsideBorder, marginBorder);
        initRender_();
    }

    private int getBorderWidth() {
        return view.getInsets().left + view.getInsets().right;
    }

    private int getBorderHeight() {
        return view.getInsets().top + view.getInsets().bottom;
    }

    private void initRender_() {
        view = new JPanel(new BorderLayout());
        view.setBorder(border);

        initRender(initAvailableWidth - getBorderWidth());
        view.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() > 1) {
                    if (tree.isEditMode()) {

                    } else {
                        TreePath path = new TreePath(getPath());
                        if (tree.isCollapsed(path)) {
                            tree.expandPath(path);
                        } else {
                            tree.collapsePath(path);
                        }
                    }
                }
            }
        });

        view.setPreferredSize(new Dimension(initAvailableWidth, view.getPreferredSize().height));
    }

    protected abstract void initRender(int availableWidth);

    public JComponent getRender(int availableWidth, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        if (selected) {
            view.setBackground(tree.nodeSelectedColor);
        } else {
            view.setBackground(tree.nodeBackgroundColor);
        }
        doSizedRender(availableWidth - getBorderWidth() - 2, selected, expanded, leaf, row, hasFocus);
        int preferredHeight = 0;
        BorderLayout layout = (BorderLayout) view.getLayout();
        if (layout.getLayoutComponent(BorderLayout.NORTH) != null) {
            preferredHeight += layout.getLayoutComponent(BorderLayout.NORTH).getPreferredSize().height;
        }
        if (layout.getLayoutComponent(BorderLayout.NORTH) != null && layout.getLayoutComponent(BorderLayout.CENTER) != null) {
            preferredHeight += layout.getVgap();
        }
        if (layout.getLayoutComponent(BorderLayout.CENTER) != null) {
            preferredHeight += layout.getLayoutComponent(BorderLayout.CENTER).getPreferredSize().height;
        }
        if (layout.getLayoutComponent(BorderLayout.CENTER) != null && layout.getLayoutComponent(BorderLayout.SOUTH) != null) {
            preferredHeight += layout.getVgap();
        }
        if (layout.getLayoutComponent(BorderLayout.SOUTH) != null) {
            preferredHeight += layout.getLayoutComponent(BorderLayout.SOUTH).getPreferredSize().height;
        }

        view.setPreferredSize(new Dimension(availableWidth, preferredHeight + getBorderHeight()));
        return view;
    }

    protected abstract void doSizedRender(int availableWidth, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus);

    protected void setLabelText(JLabel label, String text, int labelWidth) {
        label.setText(wrappedHTMLText(text, 8, label.getFontMetrics(label.getFont()), labelWidth));
    }

    protected String wrappedHTMLText(String text, int tabSize, FontMetrics fm, int width) {
        String wrapped = "";
        String tabs = "";
        for (int n = 0; n < tabSize * 2; n++) {
            tabs += " ";
        }
        text = text.replaceAll("\t", tabs);

        String[] lines = text.split("\n");
        for (int j = 0; j < lines.length; j++) {
            String[] words = lines[j].split(" ");
            String line = "";
            for (int i = 0; i < words.length; i++) {
                if (fm.stringWidth(line + words[i]) >= width) {
//                        wrapped += line + "\n";
                    wrapped += line + "<br>";
                    line = "";
                }
                line += words[i] + " ";
            }
//                wrapped += line + "\n";
            wrapped += line + "<br>";
        }
        wrapped = wrapped.substring(0, wrapped.length() - 1);
        wrapped = wrapped.replaceAll(" ", "&nbsp;");
        wrapped = "<html><body>" + wrapped + "</body></html>";

        return wrapped;
    }

}
