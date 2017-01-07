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

import javax.swing.JLabel;
import be.marcowillems.redrouter.route.RouteEncounter;
import be.marcowillems.redrouter.util.PokemonCountPair;
import be.marcowillems.redrouter.view.dialogs.editroute.EditDialog;
import be.marcowillems.redrouter.view.dialogs.editroute.RouteEncounterEdit;
import javax.swing.JComponent;

/**
 * TODO link with wild encounters
 *
 * @author Marco Willems
 */
public class RouteEncounterTreeNode extends RouteEntryTreeNode {

    private final JLabel lblInfo = new JLabel();
    private String text = "";
    private int availableWidth = 0;

    public RouteEncounterTreeNode(RouteTree tree, RouteEncounter routeEncounter) {
        super(tree, routeEncounter, true);
        updateText();
        setLabelText(lblInfo, text, availableWidth);
    }

    @Override
    protected JComponent getSizedRenderComponent(RenderSettings rs) {
        boolean update = false;
        if (this.availableWidth != rs.availableWidth) {
            this.availableWidth = rs.availableWidth;
            update = true;
        }
        if (tree.route.isEntryDataUpdated(routeEntry)) {
            updateText();
            update = true;
        }
        if (update) {
            setLabelText(lblInfo, text, availableWidth);
        }
        return lblInfo;
    }

    private void updateText() {
//        RouteEncounter re = (RouteEncounter) routeEntry;
//        text = re + "\n";
//        text += "Defeated preferred pokemon: ";
//        int count = 0;
//        for (PokemonCountPair pcp : re.getPreferences()) {
//            if (pcp.getCount() > 0) {
//                text += pcp.getCount() + "x " + pcp.plp + ", ";
//                count++;
//            }
//        }
//        if (count > 0) {
//            text = text.substring(0, text.length() - 2);
//        }
        text = "";
        if (routeEntry.info.title != null) {
            text = routeEntry.info.title;
            if (routeEntry.info.description != null) {
                text += "\n\t";
            }
        }
        if (routeEntry.info.description != null) {
            text += routeEntry.info.description;
        }
    }

    @Override
    protected EditDialog getEditDialog() {
        RouteEncounter re = (RouteEncounter) routeEntry;
        return new RouteEncounterEdit(re, re.getPreferences());
    }

}
