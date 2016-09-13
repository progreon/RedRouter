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
import java.awt.Color;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import redrouter.data.Battler;
import redrouter.data.Move;
import redrouter.data.Move.DamageRange;
import redrouter.route.RouteBattle;

/**
 *
 * @author Marco Willems
 */
public class RouteBattleTreeNode extends RouteEntryTreeNode {

    public RouteBattleTreeNode(RouteTree tree, RouteBattle routeBattle) {
        super(tree, routeBattle);
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
    private JPanel getMovesCell(Battler attacker, Battler defender, boolean isPlayerAttacker) {
        JPanel pnlCell = new JPanel(new BorderLayout(2, 2));
        if (attacker != null && defender != null) {
            pnlCell.add(makeBattlerInfoButton(attacker, isPlayerAttacker), BorderLayout.NORTH);

            String text = "<html><body>";
            for (Move m : attacker.getMoveset()) {
                text += m;
                DamageRange dr = m.getDamageRange(attacker, defender);
                if (dr.critMax != 0) {
                    text += ": " + m.getDamageRange(attacker, defender);
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

    @Override
    protected void doSizedRender(int availableWidth, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        JLabel lbl = new JLabel();
        RouteBattle rb = (RouteBattle) routeEntry;
        String text = rb.info.toString() + "\n";
        text += (rb.opponent.info == null ? "" : "\tInfo: " + rb.opponent.info + "\n");
        setLabelText(lbl, text, availableWidth);
        view.add(lbl);

        // TODO: stages, badge boosts
        JPanel pnlOpponents = new JPanel();
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
        view.add(pnlOpponents, BorderLayout.SOUTH);

        view.add(makePlayerInfoButton(), BorderLayout.EAST);
    }

}
