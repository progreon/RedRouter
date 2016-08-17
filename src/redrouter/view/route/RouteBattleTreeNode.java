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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JLabel;
import redrouter.data.Battler;
import redrouter.data.Move;
import redrouter.data.SingleBattler;
import redrouter.route.RouteBattle;

/**
 *
 * @author Marco Willems
 */
public class RouteBattleTreeNode extends RouteEntryTreeNode {

    private JLabel lblInfo;
    private String labelText;

    public RouteBattleTreeNode(RouteTree tree, RouteBattle routeBattle) {
        super(tree, routeBattle);
    }

    @Override
    protected void doSizedRender(int availableWidth, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        setLabelText(lblInfo, labelText, availableWidth);
    }

    @Override
    protected void initRender(int availableWidth) {
        String text;
        JLabel lbl;
        RouteBattle rb = (RouteBattle) routeEntry;
        text = rb.info.toString() + "\n";
        text += (rb.opponent.info == null ? "" : "\tInfo: " + rb.opponent.info + "\n");

        lbl = new JLabel();
        setLabelText(lbl, text, availableWidth);
        view.add(lbl);
        JLabel lblTeam = new JLabel();
        String teamTable = "<html><body><table border=\"1\">";
        teamTable += "<tr><th>Party Pkmn</th><th>Moveset</th></tr>";
        for (Battler b : rb.opponent.team) {
            teamTable += "<tr>";
            teamTable += "<td>" + b.toString() + " (" + b.getHP() + "hp)" + "</td>";
            teamTable += "<td>";
            for (Move m : b.getMoveset()) {
                teamTable += m.toString() + "<br>";
            }
            teamTable += "</td>";
            teamTable += "</tr>";
        }
        teamTable += "</table></body></html>";
        lblTeam.setText(teamTable);
        view.add(lblTeam, BorderLayout.SOUTH);

        labelText = text;
        lblInfo = lbl;

        Battler b;
        if (routeEntry.getPlayer() != null && !routeEntry.getPlayer().team.isEmpty()) {
            b = routeEntry.getPlayer().team.get(0);
        } else {
//            b = Battler.DUMMY;
            b = new SingleBattler(tree.route.rd.getPokemon("NidoranM"), null, 5);
        }
        JButton btnBattlerInfo = makeBattlerInfoButton(b);

        view.add(btnBattlerInfo, BorderLayout.EAST);
    }

    public JButton makeBattlerInfoButton(Battler b) {
        JButton btn = new JButton("B");
        btn.addMouseListener(new MouseAdapter() {
            BattlerInfoDialog bif = null;

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    if (bif != null) {
                        bif.dispose();
                    }
                    bif = new BattlerInfoDialog(b, e.getLocationOnScreen());
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

}
