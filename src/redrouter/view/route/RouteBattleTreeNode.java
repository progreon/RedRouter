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
import javax.swing.JComponent;
import javax.swing.JLabel;
import redrouter.data.Battler;
import redrouter.data.Move;
import redrouter.route.RouteBattle;

/**
 *
 * @author Marco Willems
 */
public class RouteBattleTreeNode extends RouteEntryTreeNode {

    public RouteBattleTreeNode(RouteTree tree, RouteBattle routeBattle) {
        super(tree, routeBattle);
    }

    @Override
    protected JComponent getSizedRender(int availableWidth, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        return view;
    }

    @Override
    protected void initRender() {
        int leftoverWidth = initAvailableWidth - getBorderWidth();

        String text;
        JLabel lbl;
        RouteBattle rb = (RouteBattle) routeEntry;
        text = rb.info.toString() + "\n";
        text += (rb.opponent.info == null ? "" : "\tInfo: " + rb.opponent.info + "\n");
        text += "Team:\n";
//        for (Battler b : rb.opponent.team) {
//            text += "\t" + b.toString();
//        }
        lbl = new JLabel();
        setLabelText(lbl, text, leftoverWidth);
//                lbl.setText("<html><body>" + wrappedText(text, 8, lbl.getFontMetrics(lbl.getFont()), leftoverWidth - 0) + "</body></html>");
        view.add(lbl);
        JLabel lblTeam = new JLabel();
        String teamTable = "<html><body><table border=\"1\">";
        for (Battler b : rb.opponent.team) {
            teamTable += "<tr>";
            teamTable += "<th>" + b.toString() + "</th>";
            teamTable += "<th>";
            for (Move m : b.moveset) {
                teamTable += m.toString() + "<br>";
            }
            teamTable += "</th>";
            teamTable += "</tr>";
        }
        teamTable += "</table></body></html>";
        lblTeam.setText(teamTable);
        view.add(lblTeam, BorderLayout.SOUTH);

        labelText = text;
        lblInfo = lbl;
    }

}
