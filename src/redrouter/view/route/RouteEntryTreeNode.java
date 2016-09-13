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
import java.util.Observable;
import java.util.Observer;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import redrouter.data.Battler;
import redrouter.route.RouteEntry;

/**
 *
 * @author Marco Willems
 */
public abstract class RouteEntryTreeNode extends DefaultMutableTreeNode implements Observer { // TODO: custom Observer class

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

    private void initRender_() {
        view = new JPanel(new BorderLayout());
        view.setBorder(border);

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

    public JComponent getRender(int availableWidth, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        view.removeAll();

        // TODO: temp
        JButton btnPlayerInfo = makePlayerInfoButton();
        view.add(btnPlayerInfo, BorderLayout.EAST);

        doSizedRender(availableWidth - getBorderWidth() - 2, selected, expanded, leaf, row, hasFocus);

        if (selected) {
            view.setBackground(tree.nodeSelectedColor);
        } else {
            view.setBackground(tree.nodeBackgroundColor);
        }
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

    private int getBorderWidth() {
        return view.getInsets().left + view.getInsets().right;
    }

    private int getBorderHeight() {
        return view.getInsets().top + view.getInsets().bottom;
    }

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

    public JButton makeBattlerInfoButton(Battler b, boolean isPlayerBattler) {
        JButton btn = new JButton(b.toString());
        btn.addMouseListener(new MouseAdapter() {
            BattlerInfoDialog bif = null;

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    if (bif != null) {
                        bif.dispose();
                    }
                    bif = new BattlerInfoDialog(b, isPlayerBattler, e.getLocationOnScreen());
                    bif.setVisible(true);
                    //                    tree.requestFocus();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (bif != null) {
                    bif.dispose();
                    bif = null;
                }
            }
        });
        return btn;
    }

    public JButton makePlayerInfoButton() {
        JButton btn = new JButton("Player");
        btn.addMouseListener(new MouseAdapter() {
            PlayerInfoDialog pif = null;

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    if (pif != null) {
                        pif.dispose();
                    }
                    pif = new PlayerInfoDialog(routeEntry.getPlayer(), e.getLocationOnScreen());
                    pif.setVisible(true);
                    //                    tree.requestFocus();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (pif != null) {
                    pif.dispose();
                    pif = null;
                }
            }
        });
        return btn;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o == this.routeEntry) {
            if (arg instanceof String && ((String) arg).equals("Tree updated")) { // TODO
                this.tree.validate();
            }
        }
    }

}
