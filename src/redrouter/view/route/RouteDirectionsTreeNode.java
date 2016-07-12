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

import javax.swing.JComponent;
import javax.swing.JLabel;
import redrouter.route.RouteDirections;

/**
 *
 * @author Marco Willems
 */
public class RouteDirectionsTreeNode extends RouteEntryTreeNode {

    public RouteDirectionsTreeNode(RouteTree tree, RouteDirections routeDirections) {
        super(tree, routeDirections);
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
        text = routeEntry.toString();
        lbl = new JLabel();
        setLabelText(lbl, text, leftoverWidth);
//                lbl.setText("<html><body>" + wrappedText(text, 8, lbl.getFontMetrics(lbl.getFont()), leftoverWidth - 0) + "</body></html>");
        view.add(lbl);

        labelText = text;
        lblInfo = lbl;
    }

}
