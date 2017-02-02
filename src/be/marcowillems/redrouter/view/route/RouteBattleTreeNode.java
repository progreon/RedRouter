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
package be.marcowillems.redrouter.view.route;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import be.marcowillems.redrouter.route.RouteBattle;
import be.marcowillems.redrouter.util.BattleEntry;
import java.util.List;
import javax.swing.JComponent;

/**
 *
 * @author Marco Willems
 */
public class RouteBattleTreeNode extends RouteEntryTreeNode {

    private final JLabel lblInfo = new JLabel();
    private String text = "";
    private JPanel pnlRender = new JPanel(new BorderLayout());
    private JPanel pnlOpponents;
    private int availableWidth = 0;

    public RouteBattleTreeNode(RouteTree tree, RouteBattle routeBattle) {
        super(tree, routeBattle, true);
        updateText();
        setLabelText(lblInfo, text, availableWidth);
        updatePnlRender(true);
    }

    @Override
    protected JComponent getSizedRenderComponent(RenderSettings rs) {
        boolean updateInfo = false;
        boolean updateData = false;
        if (this.availableWidth != rs.availableWidth) {
            this.availableWidth = rs.availableWidth;
            updateInfo = true;
        }
        if (tree.route.isInfoUpdated() || updateInfo) {
            updateText();
            setLabelText(lblInfo, text, availableWidth);
        }
        if (tree.route.isEntryDataUpdated(routeEntry)) {
            updateData = true;
        }
        if (updateInfo || updateData) {
            updatePnlRender(updateData);
        }

        return pnlRender;
    }

    private void updateText() {
        RouteBattle rb = (RouteBattle) routeEntry;
        text = rb.info.title + "\n";
        if (rb.info.description != null && !rb.info.description.equals("")) {
            text += "\t" + rb.info.description + "\n";
        }
        text += (rb.opponent.info == null ? "" : "\tInfo: " + rb.opponent.info + "\n");
    }

    private void updatePnlRender(boolean updateData) {
        pnlRender.removeAll();
        pnlRender.add(lblInfo, BorderLayout.CENTER);
        if (updateData) {
            updatePnlOpponents();
        }
        pnlRender.add(pnlOpponents, BorderLayout.SOUTH);
    }

    // TODO: stages, badge boosts
    private void updatePnlOpponents() {
        RouteBattle rb = (RouteBattle) routeEntry;
        pnlOpponents = new JPanel();
        pnlOpponents.setLayout(new BoxLayout(pnlOpponents, BoxLayout.Y_AXIS));
        List<List<BattleEntry>> battleEntries = rb.getBattleEntriesPerOpponent();
        for (int i = 0; i < battleEntries.size(); i++) {
            Color bg = new Color(215, 215, 215, 150);
            if (i % 2 == 1) {
                bg = new Color(165, 165, 165, 150);
            }
            JPanel pnlMoves = new JPanel(new GridLayout(0, 1));
            for (BattleEntry be : battleEntries.get(i)) {
                pnlMoves.add(new BattlePanel(be));
            }
            pnlMoves.setBackground(bg);
            pnlMoves.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 1, Color.black));
            pnlOpponents.add(pnlMoves);
        }

        pnlOpponents.setOpaque(false);
        pnlOpponents.setBorder(BorderFactory.createMatteBorder(1, 2, 1, 2, Color.black));
    }

}
