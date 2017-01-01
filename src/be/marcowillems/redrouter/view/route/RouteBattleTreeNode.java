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
import javax.swing.border.Border;
import be.marcowillems.redrouter.data.Battler;
import be.marcowillems.redrouter.data.Move;
import be.marcowillems.redrouter.data.Move.DamageRange;
import be.marcowillems.redrouter.data.Player;
import be.marcowillems.redrouter.route.RouteBattle;
import be.marcowillems.redrouter.util.Range;
import javax.swing.JButton;
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
        super(tree, routeBattle, false, true);
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
        text = rb.info.toString() + "\n";
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
        for (int i = 0; i < rb.opponent.team.size(); i++) {
            Color bg = new Color(215, 215, 215, 150);
            if (i % 2 == 1) {
                bg = new Color(165, 165, 165, 150);
            }
            JPanel pnlMoves = new JPanel(new GridLayout(0, 2));
            Battler opp = rb.opponent.team.get(i);
            for (int j = 0; j < rb.entries[i].length; j++) {
                Battler myBat = null;
                if (rb.getPlayersBeforeEvery()[i] != null) {
                    myBat = rb.getPlayersBeforeEvery()[i].team.get(rb.entries[i][j].partyIndex);
//                } else {
//                    myBat = new SingleBattler(tree.route.rd.getPokemon("Nidoking"), null, 25);
                }

//                pnlMoves.add(getBattlerCell(opp, true));
                pnlMoves.add(getMovesCell(opp, myBat, false));
                pnlMoves.add(getMovesCell(myBat, opp, true));
//                pnlMoves.add(getBattlerCell(myBat, false));
            }
//            pnlMoves.setOpaque(false);
            pnlMoves.setBackground(bg);
//            pnlMoves.setBorder(BorderFactory.createLineBorder(Color.black, 1));
            pnlMoves.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 1, Color.black));
            pnlOpponents.add(pnlMoves);
        }
        pnlOpponents.setOpaque(false);
//        pnlOpponents.setBorder(BorderFactory.createLineBorder(Color.black, 1));
        pnlOpponents.setBorder(BorderFactory.createMatteBorder(1, 2, 1, 2, Color.black));
    }

    private JPanel getMovesCell(Battler attacker, Battler defender, boolean isPlayerAttacker) {
        JPanel pnlCell = new JPanel(new BorderLayout(2, 2));
        if (attacker != null && defender != null) {
            // Who is faster?
            int spdBBA = (isPlayerAttacker ? (routeEntry.getPlayer().spdBadge ? 1 : 0) : 0);
            int spdBBB = (!isPlayerAttacker ? (routeEntry.getPlayer().spdBadge ? 1 : 0) : 0);
            Range rSpdA = attacker.getSpd(spdBBA, 0);
            Range rSpdB = defender.getSpd(spdBBB, 0);
            boolean isFaster = false;
            boolean maybeFaster = true;
            if (rSpdA.getMin() > rSpdB.getMax()) {
                isFaster = true;
                maybeFaster = false;
            } else if (rSpdB.getMin() > rSpdA.getMax()) {
                maybeFaster = false;
            }

            // Info button
            JButton btnBattlerInfo = makeBattlerInfoButton(attacker, isPlayerAttacker);
            if (maybeFaster) {
                btnBattlerInfo.setText(btnBattlerInfo.getText() + " (F)");
            } else if (isFaster) {
                btnBattlerInfo.setText(btnBattlerInfo.getText() + " F");
            }
            pnlCell.add(btnBattlerInfo, BorderLayout.NORTH);

            String text = "<html><body>";
            for (Move m : attacker.getMoveset()) {
                text += m;
                Player playerA = (isPlayerAttacker ? routeEntry.getPlayer() : null);
                Player playerB = (!isPlayerAttacker ? routeEntry.getPlayer() : null);
                DamageRange dr = m.getDamageRange(playerA, playerB, attacker, defender);
                if (dr.critMax != 0) {
                    text += ": " + dr;
                }
                text += "<br>";
            }
            text += "</body></html";
            JLabel lbl = new JLabel(text);
            pnlCell.add(lbl);
        }
        pnlCell.setOpaque(false);
//        Border lineBorder = BorderFactory.createLineBorder(Color.black, 1);
        Border lineBorder = BorderFactory.createMatteBorder(1, 1, 2, 0, Color.black);
        Border emptyBorder = BorderFactory.createEmptyBorder(2, 2, 2, 2);
        pnlCell.setBorder(BorderFactory.createCompoundBorder(lineBorder, emptyBorder));

        return pnlCell;
    }

//    private JPanel getBattlerCell(Battler b, boolean isOpponent) {
//        JPanel pnlCell = new JPanel(new BorderLayout(2, 2));
////        pnlCell.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
//
//        String text = "<html><body>";
//        text += b.toString() + "<br>Health: " + b.getHP() + " hp<br>";
//        if (isOpponent) {
//            text += "Gives " + b.getExp(1) + " xp<br>";
//        }
//        text += "Crit: " + (((b.getPokemon().spd / 2) / 256.0) * 100.0) + "%";
//        text += "</body></html";
//        JLabel lbl = new JLabel(text);
//        pnlCell.add(lbl);
//        pnlCell.setOpaque(false);
//        Border lineBorder = BorderFactory.createLineBorder(Color.black, 1);
//        Border emptyBorder = BorderFactory.createEmptyBorder(2, 2, 2, 2);
//        pnlCell.setBorder(BorderFactory.createCompoundBorder(lineBorder, emptyBorder));
//
//        return pnlCell;
//    }
}
