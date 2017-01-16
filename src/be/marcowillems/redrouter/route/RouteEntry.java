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

import java.util.ArrayList;
import java.util.List;
import be.marcowillems.redrouter.data.Location;
import be.marcowillems.redrouter.data.Player;
import be.marcowillems.redrouter.io.Writable;

/**
 * TODO: keep pointer to next node for performance of refresh()?
 *
 * @author Marco Willems
 */
public abstract class RouteEntry extends Writable {

    public final Route route;
    /**
     * All the messages from this route entry (lets just put it here for now).
     */
    public final List<RouterMessage> messages = new ArrayList<>();

    public RouteEntryInfo info;
    private RouteSection parent = null;
    public final boolean isLeafType; // TODO: is this needed, or only to initialize children?
    protected final List<RouteEntry> children;
    private Location location; // null => same as previous entry

    public final WildEncounters wildEncounters;

    /**
     * Instance of the player when entering this entry.
     */
    private Player playerBefore = null;
    /**
     * Instance of the player when exiting this entry.
     */
    private Player playerAfter = null;

    public RouteEntry(Route route, RouteEntryInfo info, boolean isLeafType) {
        this(route, info, isLeafType, null, null);
    }

    public RouteEntry(Route route, RouteEntryInfo info, boolean isLeafType, Location location) {
        this(route, info, isLeafType, null, location);
    }

    public RouteEntry(Route route, RouteEntryInfo info, boolean isLeafType, List<RouteEntry> children) {
        this(route, info, isLeafType, children, null);
    }

    public RouteEntry(Route route, RouteEntryInfo info, boolean isLeafType, List<RouteEntry> children, Location location) {
        this.route = (this instanceof Route ? (Route) this : route);
        this.info = info;
        this.isLeafType = isLeafType;
        this.children = (isLeafType ? null : (children == null ? new ArrayList<>() : children));
        this.location = location;
        this.wildEncounters = new WildEncounters(this);
    }

    /**
     *
     * @param p the player before this entry
     * @return the player after this entry
     */
    protected Player apply(Player p) {
        Player newPlayer = null;
        clearMessages();
        this.playerBefore = p;

        if (this.playerBefore != null) {
            newPlayer = this.playerBefore.getDeepCopy();
            if (location != null) {
                newPlayer.setCurrentLocation(location);
            }
            this.wildEncounters.apply(newPlayer); // Defeat wild encounters
        } else {
            showMessage(RouterMessage.Type.ERROR, "There is no player set!");
        }

        setPlayerAfter(newPlayer);
        return newPlayer;
    }

    protected void setPlayerAfter(Player playerAfter) {
        this.playerAfter = playerAfter;
    }

    protected Route getRoute() {
        return this.route;
    }

    protected void notifyRoute() {
        if (this.route != null) {
            route.notifyChanges();
        }
    }

    protected final void notifyDataUpdated() {
        if (this.route != null) {
            this.route.setDataUpdated(this);
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
        return this.location;
    }

    public final void setLocation(Location location) {
        if (this.location != location) {
            this.location = location;
            this.wildEncounters.reset();
            notifyDataUpdated();
            notifyRoute();
        }
    }

    public RouteSection getParentSection() {
        return this.parent;
    }

    // TODO don't do this here but in a move function?
    public void setParentSection(RouteSection parentSection) {
        if (this.parent != parentSection) {
            this.parent = parentSection;
            this.playerBefore = null;
            this.wildEncounters.reset();
            notifyDataUpdated();
            notifyRoute();
        }
    }

    /**
     * Gets the player when entering this entry
     *
     * @return The player when entering this entry, be aware this might still be
     * null.
     */
    public Player getPlayerBefore() {
        return this.playerBefore;
    }

    /**
     * Gets the player when exiting this entry
     *
     * @return The player when exiting this entry, be aware this might still be
     * null.
     */
    public Player getPlayerAfter() {
        return this.playerAfter;
    }

//    public WildEncounters getWildEncounters() {
//        return this.wildEncounters;
//    }
    public boolean hasChildren() {
        return children != null && !children.isEmpty();
    }

    //// Info messages system ////
    private void clearMessages() {
        messages.clear();
    }

    protected void showMessage(RouterMessage.Type type, String message) {
        messages.add(new RouterMessage(this, type, message));
    }

    public RouterMessage.Type getMessagesType() {
        RouterMessage.Type type = RouterMessage.Type.INFO;
        for (RouterMessage rm : messages) {
            if (rm.type.priority > type.priority) {
                type = rm.type;
            }
        }
        return type;
    }

    @Override
    public abstract String toString();

//    private RouteEntry getPrevious() {
//        if (parent != null) {
//            int nIndex = parent.children.indexOf(this);
//            if (nIndex > 0) {
//                RouteEntry previous = parent.children.get(nIndex - 1);
//                while (previous.hasChildren()) {
//                    previous = previous.children.get(previous.children.size() - 1);
//                }
//                return previous;
//            } else {
//                return (RouteEntry) parent;
//            }
//        } else {
//            return null;
//        }
//    }
//
//    private RouteEntry getNext() {
//        if (hasChildren()) {
//            return children.get(0);
//        } else {
//            RouteEntry node = this;
//            RouteEntry pNode = node.parent;
//            while (pNode != null) {
//                int nIndex = pNode.children.indexOf(node);
//                if (nIndex < pNode.children.size() - 1) {
//                    return pNode.children.get(nIndex + 1);
//                } else {
//                    node = pNode;
//                    pNode = node.parent;
//                }
//            }
//        }
//        return null;
//    }
//
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
//    
//    private RouteEntry getFirstChild() {
//        if (!hasChildren()) {
//            return this;
//        } else {
//            return children.get(0).getFirstChild();
//        }
//    }
//
//    private RouteEntry getLastChild() {
//        if (!hasChildren()) {
//            return this;
//        } else {
//            return children.get(children.size() - 1).getLastChild();
//        }
//    }
}
