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

/**
 *
 * @author Marco Willems
 */
public class RenderSettings {

    public final int availableWidth;
    public final boolean selected;
    public final boolean expanded;
    public final boolean leaf;
    public final int row;
    public final boolean hasFocus;

    public RenderSettings(int availableWidth, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        this.availableWidth = availableWidth;
        this.selected = selected;
        this.expanded = expanded;
        this.leaf = leaf;
        this.row = row;
        this.hasFocus = hasFocus;
    }

    public RenderSettings(RenderSettings rs, int newAvailableWidth) {
        this.availableWidth = newAvailableWidth;
        this.selected = rs.selected;
        this.expanded = rs.expanded;
        this.leaf = rs.leaf;
        this.row = rs.row;
        this.hasFocus = rs.hasFocus;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RenderSettings) {
            RenderSettings rs = (RenderSettings) obj;
            return availableWidth == rs.availableWidth
                    && selected == rs.selected
                    && expanded == rs.expanded
                    && leaf == rs.leaf
                    && row == rs.row
                    && hasFocus == rs.hasFocus;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + this.availableWidth;
        hash = 37 * hash + (this.selected ? 1 : 0);
        hash = 37 * hash + (this.expanded ? 1 : 0);
        hash = 37 * hash + (this.leaf ? 1 : 0);
        hash = 37 * hash + this.row;
        hash = 37 * hash + (this.hasFocus ? 1 : 0);
        return hash;
    }

}
