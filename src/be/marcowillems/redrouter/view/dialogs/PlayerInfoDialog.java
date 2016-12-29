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
package be.marcowillems.redrouter.view.dialogs;

import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import be.marcowillems.redrouter.data.Battler;
import be.marcowillems.redrouter.data.Player;

/**
 *
 * @author Marco Willems
 */
public class PlayerInfoDialog extends InfoDialog {

    private final Player player;

    private JPanel mainPanel = null;

    public PlayerInfoDialog(Player player) {
        this.player = player;
        initMainPanel();
    }

    private void initMainPanel() {
        mainPanel = new JPanel(new BorderLayout());
        String info = "<html><body>";
        if (player != null) {
            info += "<p style=\"font-size:16px\">" + player.name + "</p>";
            info += "Info: " + player.info + "<br>";
            info += "Money: " + player.getMoney() + "<br>";
            info += "<table border=1>";
            info += "<tr align=\"center\"><th>Team:</th></tr>";
            for (Battler b : player.team) {
                info += "<tr><td>" + b + "</td></tr>";
            }
            info += "</table>";
        } else {
            info += "<p style=\"font-size:16px\">Player Not Initialised</p>";
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
