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
package redrouter.route;

import java.util.List;
import redrouter.data.Protagonist;

/**
 *
 * @author Marco Willems
 */
public abstract class RouteEntry {

    public RouteEntryInfo info;
    public RouteSection parent;
    public final List<RouteEntry> children;

//    public RouteEntry() {
//    }
    public RouteEntry(RouteSection parentSection, RouteEntryInfo info) {
        this(parentSection, info, null);
    }

    public RouteEntry(RouteSection parentSection, RouteEntryInfo info, List<RouteEntry> children) {
        this.parent = parentSection;
        this.info = info;
        this.children = children;
    }

    public abstract Protagonist apply(Protagonist p);

    public boolean hasChildren() {
        return children != null && !children.isEmpty();
    }

    @Override
    public abstract String toString();

}
