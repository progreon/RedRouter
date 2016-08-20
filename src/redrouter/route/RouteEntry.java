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
import redrouter.data.Player;

/**
 *
 * @author Marco Willems
 */
public abstract class RouteEntry {

    public RouteEntryInfo info;
    public RouteSection parent;
    public final List<RouteEntry> children;

    protected Player player = null; // current instance of the player

    public RouteEntry(RouteSection parentSection, RouteEntryInfo info) {
        this(parentSection, info, null);
    }

    public RouteEntry(RouteSection parentSection, RouteEntryInfo info, List<RouteEntry> children) {
        this.parent = parentSection;
        this.info = info;
        this.children = children;
//        refreshData(null);
    }

    public Player getPlayer() {
        return this.player;
    }

    public final void refreshData(Player previousPlayer) {
        RouteEntry previous = getPrevious();
        if (previousPlayer == null && previous != null) {
            previousPlayer = previous.player;
        }
        if (previousPlayer == null) {
            previousPlayer = new Player(null, "The player", "", null);
        }
        apply(previousPlayer);
        RouteEntry next = getNext();
        if (next != null) {
            getNext().refreshData(player); // TODO: not optimal to use getNext!
        }
    }

    protected abstract void apply(Player previousPlayer);

    public boolean hasChildren() {
        return children != null && !children.isEmpty();
    }

    private RouteEntry getPrevious() {
        if (parent != null) {
            int thisIndex = parent.children.indexOf(this);
            if (thisIndex > 0) {
                return parent.children.get(thisIndex - 1).getLastChild();
            } else { // This is the first child
                return (RouteEntry) parent;
            }
        } else { // This is the root
            return null;
        }
    }

    private RouteEntry getNext() {
        if (hasChildren()) {
            return children.get(0);
        } else if (parent != null) {
            int thisIndex = parent.children.indexOf(this);
            if (thisIndex < parent.children.size() - 1) {
                return parent.children.get(thisIndex + 1).getFirstChild();
            } else { // This is the last child
                return ((RouteEntry) parent).getNext();
            }
        } else { // This is the root
            return null;
        }
    }

    private RouteEntry getFirstChild() {
        if (!hasChildren()) {
            return this;
        } else {
            return children.get(0).getFirstChild();
        }
    }

    private RouteEntry getLastChild() {
        if (!hasChildren()) {
            return this;
        } else {
            return children.get(children.size() - 1).getLastChild();
        }
    }

    @Override
    public abstract String toString();

}
