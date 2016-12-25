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
import java.util.Observable;
import redrouter.data.Player;
import redrouter.io.Writable;

/**
 * TODO: keep pointer to next node for performance of refresh()?
 *
 * @author Marco Willems
 */
public abstract class RouteEntry extends Observable implements Writable { // TODO: custom Observable class

    public RouteEntryInfo info;
    public RouteSection parent;
    public final List<RouteEntry> children;

    protected Player player = null; // instance of the player when entering this entry

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

    public final void refreshData(Player newPlayer) {
        if (newPlayer == null) {
            newPlayer = this.player;
        }
        if (newPlayer == null) {
            RouteEntry previous = getPrevious();
            if (previous != null) {
                previous.refreshData(null);
            }
            if (previous != null && previous.player != null) {
                newPlayer = previous.apply(previous.player);
            }
        }
        Player appliedPlayer = apply(newPlayer);
        RouteEntry next = getNext(); // TODO: not optimal to use getNext!
//        if (next != null && !appliedPlayer.equals(next.player)) { // TODO: ?
        if (next != null) { // only notify observers when the whole tree is updated!
            next.refreshData(appliedPlayer);
        } else {
            notifyObservers("Tree updated"); // TODO (RouteEntryTreeNode:165)
        }
    }

    protected Player apply(Player p) {
        this.player = p;
        return this.player;
    }

    public boolean hasChildren() {
        return children != null && !children.isEmpty();
    }

    private RouteEntry getPrevious() {
        if (parent != null) {
            int nIndex = parent.children.indexOf(this);
            if (nIndex > 0) {
                RouteEntry previous = parent.children.get(nIndex - 1);
                while (previous.hasChildren()) {
                    previous = previous.children.get(previous.children.size() - 1);
                }
                return previous;
            } else {
                return (RouteEntry) parent;
            }
        } else {
            return null;
        }
    }

    private RouteEntry getNext() {
        if (hasChildren()) {
            return children.get(0);
        } else {
            RouteEntry node = this;
            RouteEntry pNode = node.parent;
            while (pNode != null) {
                int nIndex = pNode.children.indexOf(node);
                if (nIndex < pNode.children.size() - 1) {
                    return pNode.children.get(nIndex + 1);
                } else {
                    node = pNode;
                    pNode = node.parent;
                }
            }
        }
        return null;
    }

//    private RouteEntry getNextSibling() {
//        if (parent != null) {
//            int thisIndex = parent.children.indexOf(this);
//            if (thisIndex < parent.children.size() - 1) {
//                return parent.children.get(thisIndex + 1);
//            } else {
//                return ((RouteEntry) parent).getNextSibling();
//            }
//        } else {
//            return null;
//        }
//    }
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

    public boolean hasNext() {
        return getNext() != null;
    }

    protected static String lineToDepth(String s, int depth) {
        if (s != null) {
            String newS = "";
            for (int i = 0; i < depth; i++) {
                newS += "\t";
            }
            newS += s;
            return newS;
        } else {
            return null;
        }
    }

    @Override
    public abstract String toString();

}
