/*
 * Copyright (C) 2017 Marco Willems
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

import be.marcowillems.redrouter.data.Battler;
import be.marcowillems.redrouter.data.Move;
import be.marcowillems.redrouter.util.BadgeBoosts;
import be.marcowillems.redrouter.util.BattleEntry;
import be.marcowillems.redrouter.util.Range;
import be.marcowillems.redrouter.util.Stages;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

/**
 * TODO: constructor with "battle settings"?
 *
 * @author Marco Willems
 */
public class BattlePanel extends JPanel {

    private JPanel pnlOpponent;
    private JPanel pnlParty;

    private final BattleEntry be;

    public BattlePanel(BattleEntry battleEntry) {
        super(new GridLayout(0, 2, 0, 0));
        super.setOpaque(false);
        this.be = battleEntry;
        initPanels();
        this.add(pnlOpponent);
        this.add(pnlParty);
    }

    private void initPanels() {
        pnlOpponent = getMovesCell(be.battlerOpp, be.battlerPl, false);
        pnlParty = getMovesCell(be.battlerPl, be.battlerOpp, true);
    }

    // TODO: some more cleanup (ranges)
    private JPanel getMovesCell(Battler attacker, Battler defender, boolean isPlayerAttacker) {
        JPanel pnlCell = new JPanel(new BorderLayout(2, 2));
        if (attacker != null && defender != null) {
            // Who is faster?
            int spdBBA = (isPlayerAttacker ? be.getBadgeBoosts().getSpd() : 0);
            int spdBBB = (!isPlayerAttacker ? be.getBadgeBoosts().getSpd() : 0);
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
            JButton btnBattlerInfo = RouteEntryTreeNode.makeBattlerInfoButton(attacker, isPlayerAttacker);
            if (maybeFaster) {
                btnBattlerInfo.setText(btnBattlerInfo.getText() + " (F)");
            } else if (isFaster) {
                btnBattlerInfo.setText(btnBattlerInfo.getText() + " F");
            }
            pnlCell.add(btnBattlerInfo, BorderLayout.NORTH);

            String text = "<html><body>";
            Map<Move, Move.DamageRange> ranges = isPlayerAttacker ? be.getPlayerRanges() : be.getOpponentRanges();
            for (Move m : ranges.keySet()) {
                text += m;
                Move.DamageRange dr = ranges.get(m);
                if (dr.getCritMax() != 0) {
                    text += ": " + dr;
                }
                text += "<br>";
            }
            text += "</body></html";
            JLabel lbl = new JLabel(text);
            pnlCell.add(lbl);
        }
        pnlCell.setOpaque(false);
        Border lineBorder = BorderFactory.createMatteBorder(1, 1, 2, 0, Color.black);
        Border emptyBorder = BorderFactory.createEmptyBorder(2, 2, 2, 2);
        pnlCell.setBorder(BorderFactory.createCompoundBorder(lineBorder, emptyBorder));

        return pnlCell;
    }

}
