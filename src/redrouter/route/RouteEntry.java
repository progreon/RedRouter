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
import java.util.Stack;
import redrouter.data.Location;
import redrouter.data.Player;
import redrouter.data.SingleBattler;
import redrouter.io.Writable;
import redrouter.util.WildEncounters;

/**
 * TODO: keep pointer to next node for performance of refresh()?
 *
 * @author Marco Willems
 */
public abstract class RouteEntry extends Observable implements Writable { // TODO: custom Observable class

    public final static String TREE_UPDATED = "Tree updated";

    public RouteEntryInfo info;
    private RouteSection parent;
    public final List<RouteEntry> children;

    private Location location; // TODO: move to player class?
    private WildEncounters wildEncounters;

    protected Player player = null; // instance of the player when entering this entry

    public RouteEntry(RouteEntryInfo info) {
        this(info, null, null);
    }

    public RouteEntry(RouteEntryInfo info, Location location) {
        this(info, null, location);
    }

    public RouteEntry(RouteEntryInfo info, List<RouteEntry> children) {
        this(info, children, null);
    }

    public RouteEntry(RouteEntryInfo info, List<RouteEntry> children, Location location) {
        this.parent = null;
        this.info = info;
        this.children = children;
        this.location = location;
        this.wildEncounters = new WildEncounters(getLocation());
//        refreshData(null);
    }

    public final Location getLocation() {
        if (this.location == null) {
            RouteEntry prev = getPrevious();
            if (prev != null) {
                return prev.getLocation();
            } else {
                return null;
            }
        } else {
            return this.location;
        }
    }

    public final void setLocation(Location location) {
        if (this.location != location || location == null) {
            this.location = location;
            this.wildEncounters = new WildEncounters(getLocation());
            refreshData(null);
        }
    }

    private void refreshLocationData() {
        this.wildEncounters = new WildEncounters(getLocation());
        RouteEntry next = getNext();
        while (next != null && next.location == null) {
            next.setLocation(null); // TODO Too much refreshData()? => location in player class!
            next = next.getNext();
        }
        refreshData(null);
    }

    public RouteSection getParentSection() {
        return this.parent;
    }

    // TODO don't do this here but in a move function?
    public void setParentSection(RouteSection parentSection) {
        if (this.parent != parentSection) {
            this.parent = parentSection;
            refreshLocationData();
        }
    }

    public Player getPlayer() {
        return this.player;
    }

    public WildEncounters getWildEncounters() {
        return this.wildEncounters;
    }

    // TODO fix this!! (part B)
    public final void refreshData(Player newPlayer) {
        if (newPlayer == null) {
            newPlayer = this.player;
        }
        // First lets go up to find a player object to work with
        if (newPlayer == null) {
            Stack<RouteEntry> sPrev = new Stack<>();
            RouteEntry prev = getPrevious();
            while (prev != null && prev.player == null) {
                sPrev.push(prev);
                prev = prev.getPrevious();
            }
            if (prev != null) {
                newPlayer = prev.player;
                newPlayer = prev.apply(newPlayer);
                while (!sPrev.isEmpty()) {
                    prev = sPrev.pop();
                    newPlayer = prev.apply(newPlayer);
                }
            } // else return
        }
        // Then apply this entry to the player if we found it
        if (newPlayer != null) {
            Player appliedPlayer = apply(newPlayer);
            // Then propagate the applied player down
            RouteEntry next = getNext(); // TODO: not optimal to use getNext!
//            if (next != null && !appliedPlayer.equals(next.player)) { // TODO: ? (+ location check?)
            if (next != null) { // only notify observers when the whole tree is updated!
                next.refreshData(appliedPlayer); // TODO remove (tail) recursion (list all nexts and apply one by one)
            } else {
                super.setChanged();
                notifyObservers(TREE_UPDATED);
            }
        }
    }

    protected Player apply(Player p) {
        this.player = p;
        // Applying wild encounters
        if (player != null && this.wildEncounters != null && player.getFrontBattler() != null) {
            Player newPlayer = this.player.getDeepCopy();
            for (SingleBattler sb : this.wildEncounters.getBattledBattlers()) {
                newPlayer.getFrontBattler().defeatBattler(sb);
            }
            return newPlayer;
        } else {
            return this.player;
        }
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
