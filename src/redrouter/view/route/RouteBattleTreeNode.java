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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import redrouter.data.Battler;
import redrouter.data.Move;
import redrouter.data.Move.DamageRange;
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

    private JPanel getBattlerCell(Battler b, boolean isOpponent) {
        JPanel pnlCell = new JPanel(new BorderLayout());

        String text = "<html><body>";
        text += b.toString() + "<br>Health: " + b.getHP() + " hp<br>";
        if (isOpponent) {
            text += "Gives " + b.getExp(1) + " xp<br>";
        }
        text += "Crit: " + (((b.getPokemon().spd / 2) / 256.0) * 100.0) + "%";
        text += "</body></html";
        JLabel lbl = new JLabel(text);
        pnlCell.add(lbl);
        pnlCell.setOpaque(false);
        pnlCell.setBorder(BorderFactory.createLineBorder(Color.black, 1));

        return pnlCell;
    }

    private JPanel getMovesCell(Battler attacker, Battler defender) {
        JPanel pnlCell = new JPanel(new BorderLayout());

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
        pnlCell.setOpaque(false);
        pnlCell.setBorder(BorderFactory.createLineBorder(Color.black, 1));

        return pnlCell;
    }

    @Override
    protected void initRender(int availableWidth) {
        String text;
        JLabel lbl = new JLabel();
        RouteBattle rb = (RouteBattle) routeEntry;
        text = rb.info.toString() + "\n";
        text += (rb.opponent.info == null ? "" : "\tInfo: " + rb.opponent.info + "\n");
        setLabelText(lbl, text, availableWidth);
        view.add(lbl);

        Battler myBat;
        if (routeEntry.getPlayer() != null && !routeEntry.getPlayer().team.isEmpty()) {
            myBat = routeEntry.getPlayer().team.get(0);
        } else {
//            b = Battler.DUMMY;
            myBat = new SingleBattler(tree.route.rd.getPokemon("NidoranM"), null, 5);
        }

        JPanel pnlMoves = new JPanel(new GridLayout(0, 4));
        for (Battler b : rb.opponent.team) {
            pnlMoves.add(getBattlerCell(b, true));
            pnlMoves.add(getMovesCell(b, myBat));
            pnlMoves.add(getMovesCell(myBat, b));
            pnlMoves.add(getBattlerCell(myBat, false));
        }
        pnlMoves.setOpaque(false);
        view.add(pnlMoves, BorderLayout.SOUTH);

        labelText = text;
        lblInfo = lbl;

        JButton btnBattlerInfo = makeBattlerInfoButton(myBat);
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
