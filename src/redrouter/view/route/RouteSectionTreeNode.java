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

import java.awt.Font;
import javax.swing.JLabel;
import redrouter.route.RouteEntry;
import redrouter.route.RouteSection;

/**
 *
 * @author Marco Willems
 */
public class RouteSectionTreeNode extends RouteEntryTreeNode {

    public RouteSectionTreeNode(RouteTree tree, RouteSection routeSection) {
        super(tree, routeSection, false);
    }

    @Override
    protected void doSizedRender(int availableWidth, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        String text = ((RouteSection) routeEntry).info.toString();
        JLabel lbl = new JLabel();
        Font font = lbl.getFont();
        float minFontSize = font.getSize2D();
        float maxFontSize = 18.0f;
        float level = 0.0f;
        RouteEntry entry = routeEntry;
        while (entry.getParentSection() != null) {
            level++;
            entry = entry.getParentSection();
        }
        float thisFontSize = Math.max(minFontSize, maxFontSize - level * 2);
        lbl.setFont(font.deriveFont(thisFontSize));
        setLabelText(lbl, text, availableWidth);
        view.add(lbl);
    }

}
