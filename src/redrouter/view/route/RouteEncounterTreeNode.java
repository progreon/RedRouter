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
import java.awt.FlowLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import redrouter.route.RouteEncounter;
import redrouter.util.PokemonCountPair;
import redrouter.view.dialogs.editroute.EditDialog;
import redrouter.view.dialogs.editroute.RouteEncounterEdit;

/**
 * TODO link with wild encounters
 *
 * @author Marco Willems
 */
public class RouteEncounterTreeNode extends RouteEntryTreeNode {

    public RouteEncounterTreeNode(RouteTree tree, RouteEncounter routeEncounter) {
        super(tree, routeEncounter, true);
    }

    @Override
    protected void doSizedRender(int availableWidth, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        RouteEncounter re = (RouteEncounter) routeEntry;
        String text = re + "\n";
        text += "Defeated preferred pokemon: ";
        int count = 0;
        for (PokemonCountPair pcp : re.getPreferences()) {
            if (pcp.getCount() > 0) {
                text += pcp.getCount() + "x " + pcp.plp + ", ";
                count++;
            }
        }
        if (count > 0) {
            text = text.substring(0, text.length() - 2);
        }
        JLabel lbl = new JLabel();
        setLabelText(lbl, text, availableWidth);
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        southPanel.setOpaque(false);
        southPanel.add(lbl);
        view.add(southPanel, BorderLayout.SOUTH);
    }

    @Override
    protected EditDialog getEditDialog() {
        RouteEncounter re = (RouteEncounter) routeEntry;
        return new RouteEncounterEdit(re, re.getPreferences());
    }

}
