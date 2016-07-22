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

import javax.swing.JLabel;
import redrouter.route.RouteShopping;

/**
 *
 * @author Marco Willems
 */
public class RouteShoppingTreeNode extends RouteEntryTreeNode {

    protected JLabel lblInfo;
    protected String labelText;

    public RouteShoppingTreeNode(RouteTree tree, RouteShopping routeOr) {
        super(tree, routeOr);
    }

    @Override
    protected void initRender(int availableWidth) {
        String text;
        JLabel lbl;
        text = routeEntry.toString();
        lbl = new JLabel();
        setLabelText(lbl, text, availableWidth);
        view.add(lbl);

        labelText = text;
        lblInfo = lbl;
    }

    @Override
    protected void doSizedRender(int availableWidth, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        setLabelText(lblInfo, labelText, availableWidth);
    }

}
