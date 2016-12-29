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
package redrouter.view.dialogs;

import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import redrouter.data.Battler;

/**
 *
 * @author Marco Willems
 */
public class BattlerInfoDialog extends InfoDialog {

    private final Battler battler;
    private final boolean isPlayerBattler;

    private JPanel mainPanel;

    public BattlerInfoDialog(Battler battler, boolean isPlayerBattler) {
        this.battler = battler;
        this.isPlayerBattler = isPlayerBattler;
        if (battler != null) {
            initMainPanel();
        } else {
            // TODO handle this in initMainPanel()
            this.mainPanel = new JPanel(new BorderLayout());
        }
    }

    private void initMainPanel() {
        mainPanel = new JPanel(new BorderLayout());
        String info = "<html><body>";
        info += "<p style=\"font-size:16px\">" + battler.toString() + "</p>";
        info += "Experience group: " + battler.getPokemon().expGroup.group + "<br>";
        if (!isPlayerBattler) {
            info += "Given experience: " + battler.getExp(1) + "<br>";
        } else {
            info += "Redbar: &lt;" + battler.getHP().multiplyBy(53).devideBy(256).add(1) + " HP<br>";
        }
        // TODO: check & move to Pokemon class
        info += "Critical hit ratio: " + (((battler.getPokemon().spd / 2) / 256.0) * 100.0) + "% ";
        info += "(high: " + Math.min(((battler.getPokemon().spd / 2) / 32.0) * 100.0, 100.0) + "%)<br>";
        info += "<table border=1>";
//        info += "<tr><th>Base HP</th><th>Base ATK</th><th>Base DEF</th><th>Base SPD</th><th>Base SPC</th></tr><tr>";
//        info += "<td align=\"center\">" + battler.getPokemon().hp + "</td>";
//        info += "<td align=\"center\">" + battler.getPokemon().atk + "</td>";
//        info += "<td align=\"center\">" + battler.getPokemon().def + "</td>";
//        info += "<td align=\"center\">" + battler.getPokemon().spd + "</td>";
//        info += "<td align=\"center\">" + battler.getPokemon().spc + "</td></tr>";
        info += "<tr align=\"center\"><th>HP</th><th>ATK</th><th>DEF</th><th>SPD</th><th>SPC</th></tr><tr>";
        info += "<td align=\"center\">" + battler.getHP() + "</td>";
        info += "<td align=\"center\">" + battler.getAtk() + "</td>";
        info += "<td align=\"center\">" + battler.getDef() + "</td>";
        info += "<td align=\"center\">" + battler.getSpd() + "</td>";
        info += "<td align=\"center\">" + battler.getSpc() + "</td></tr></table>";
        if (isPlayerBattler) {
            // TODO
            info += "Exp. to next level: TODO";
        }
        info += "</body></html>";

        JLabel lblInfo = new JLabel(info);
        mainPanel.add(lblInfo);
    }

    @Override
    protected JPanel getMainPanel() {
        return mainPanel;
    }

    @Override
    protected void refreshData() {
        initMainPanel();
    }

}
