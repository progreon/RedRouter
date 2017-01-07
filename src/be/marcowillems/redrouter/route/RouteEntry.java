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
package be.marcowillems.redrouter.route;

import java.util.List;
import be.marcowillems.redrouter.data.Location;
import be.marcowillems.redrouter.data.Player;
import be.marcowillems.redrouter.data.SingleBattler;
import be.marcowillems.redrouter.io.Writable;
import be.marcowillems.redrouter.util.WildEncounters;
import java.util.ArrayList;

/**
 * TODO: keep pointer to next node for performance of refresh()?
 *
 * @author Marco Willems
 */
public abstract class RouteEntry extends Writable {

    private Route route = null;
    public final List<RouterMessage> messages = new ArrayList<>();

    public RouteEntryInfo info;
    private RouteSection parent = null;
    public final boolean isLeafType; // TODO: is this needed, or only to initialize children?
    protected final List<RouteEntry> children;

    private Location location; // TODO: move to player class?
    private WildEncounters wildEncounters;

    /**
     * Instance of the player when entering this entry.
     */
    private Player player = null;

    public RouteEntry(RouteEntryInfo info, boolean isLeafType) {
        this(info, isLeafType, null, null);
    }

    public RouteEntry(RouteEntryInfo info, boolean isLeafType, Location location) {
        this(info, isLeafType, null, location);
    }

    public RouteEntry(RouteEntryInfo info, boolean isLeafType, List<RouteEntry> children) {
        this(info, isLeafType, children, null);
    }

    public RouteEntry(RouteEntryInfo info, boolean isLeafType, List<RouteEntry> children, Location location) {
        this.info = info;
        this.isLeafType = isLeafType;
        this.children = (isLeafType ? null : (children == null ? new ArrayList<>() : children));
        this.location = location;
        this.wildEncounters = new WildEncounters(getLocation());
    }

    protected Route getRoute() {
        return this.route;
    }

    /**
     * Sets the route object this entry belongs to, and updates it for its
     * children.
     *
     * @param route
     */
    final void setRoute(Route route) {
        if (this.route != route) {
            this.route = route;
            if (hasChildren()) {
                for (RouteEntry child : this.children) {
                    child.setRoute(route);
                }
            }
            notifyDataUpdated();
            notifyRoute();
        }
    }

    protected void notifyRoute() {
        if (this.route != null) {
            route.notifyChanges();
        }
    }

    protected void notifyDataUpdated() {
        if (this.route != null) {
            this.route.setDataUpdated();
        }
    }

    protected void notifyInfoUpdated() {
        if (this.route != null) {
            this.route.setInfoUpdated();
        }
    }

    public void notifyWildEncountersUpdated() {
        notifyDataUpdated();
        notifyRoute();
    }

    // Info messages system
    private void clearMessages() {
        messages.clear();
    }

    protected void showMessage(RouterMessage.Type type, String message) {
        messages.add(new RouterMessage(this, type, message));
    }

    /**
     * Gets all of its entries, including itself as the first one.
     *
     * @return
     */
    protected List<RouteEntry> getEntryList() {
        List<RouteEntry> entryList = new ArrayList<>();
        entryList.add(this);
        if (hasChildren()) {
            for (RouteEntry child : getChildren()) {
                entryList.addAll(child.getEntryList());
            }
        }
        return entryList;
    }

    public List<RouteEntry> getChildren() {
        return this.children;
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
//            refreshData(null);
            notifyDataUpdated();
            notifyRoute();
        }
    }

    private void refreshLocationData() {
        this.wildEncounters = new WildEncounters(getLocation());
        if (this instanceof RouteEncounter) { // TODO how to avoid this?
            this.wildEncounters.setPreferences(((RouteEncounter) this).getPreferences());
        }
        RouteEntry next = getNext();
        while (next != null && next.location == null) {
            next.setLocation(null); // TODO Too much refreshData()? => location in player class!
            next = next.getNext();
        }
        notifyDataUpdated();
        notifyRoute();
    }

    public RouteSection getParentSection() {
        return this.parent;
    }

    // TODO don't do this here but in a move function?
    public void setParentSection(RouteSection parentSection) {
        if (this.parent != parentSection) {
            this.parent = parentSection;
            this.setRoute(parent.getRoute());
            refreshLocationData();
//            notifyDataUpdated();
//            notifyRoute();
        }
    }

    public Player getPlayer() {
        return this.player;
    }

    public WildEncounters getWildEncounters() {
        return this.wildEncounters;
    }

    protected Player apply(Player p) {
        clearMessages();
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

    @Override
    public abstract String toString();

}
