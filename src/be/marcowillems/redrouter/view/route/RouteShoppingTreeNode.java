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
import be.marcowillems.redrouter.route.RouteShopping;
import javax.swing.JComponent;

/**
 *
 * @author Marco Willems
 */
public class RouteShoppingTreeNode extends RouteEntryTreeNode {

    private final JLabel lblInfo = new JLabel();
    private String text = "";
    private int availableWidth = 0;

    public RouteShoppingTreeNode(RouteTree tree, RouteShopping routeOr) {
        super(tree, routeOr, true);
        this.text = routeEntry.toString();
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
            text = routeEntry.toString();
            update = true;
        }
        if (update) {
            setLabelText(lblInfo, text, availableWidth);
        }
        return lblInfo;
    }

}
