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
import java.awt.Point;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import redrouter.data.Battler;

/**
 *
 * @author Marco Willems
 */
public class BattlerInfoDialog extends JDialog {

    public BattlerInfoDialog(Battler battler, Point mouseLocation) {
//        super()
        JPanel panel = new JPanel(new BorderLayout());
        String info = "<html><body>";
        info += "<p style=\"font-size:16px\">" + battler.toString() + "</p>";
        info += "<table><tr><th>HP</th><th>ATK</th><th>DEF</th><th>SPD</th><th>SPC</th></tr><tr>";
        info += "<td>" + battler.getHPStatIfDV(8) + "</td>";
        info += "<td>" + battler.getAtkStatIfDV(9) + "</td>";
        info += "<td>" + battler.getDefStatIfDV(8) + "</td>";
        info += "<td>" + battler.getSpdStatIfDV(8) + "</td>";
        info += "<td>" + battler.getSpcStatIfDV(8) + "</td></tr></table>";
        info += "</body></html>";
        JLabel lblInfo = new JLabel(info);
        panel.add(lblInfo);
        this.setContentPane(panel);
        this.setUndecorated(true);
        this.pack();
        this.setModal(false);
        this.setLocation(new Point(mouseLocation.x, mouseLocation.y - this.getHeight()));
        this.setFocusable(false);
    }

}
