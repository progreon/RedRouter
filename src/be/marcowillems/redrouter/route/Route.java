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

import be.marcowillems.redrouter.io.PrintSettings;
import be.marcowillems.redrouter.Settings;
import be.marcowillems.redrouter.data.Player;
import be.marcowillems.redrouter.data.RouterData;
import be.marcowillems.redrouter.observers.RouteObservable;
import be.marcowillems.redrouter.observers.RouteObserver;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/**
 *
 * @author Marco Willems
 */
public class Route extends RouteSection {

    public final RouterData rd;

    public final static String TREE_UPDATED = "Tree updated";

    private final RouteObservable routeObservable;
    private boolean canRefresh = true;
    private boolean dataUpdated = false;
    private boolean infoUpdated = false;
    private final List<RouteEntry> updatedEntries = new ArrayList<>();

    private Player startPlayer;
    private List<RouteEntry> entryList;

    public Route(RouterData rd, String title) {
        super(null, title);
        this.rd = rd;
        this.routeObservable = new RouteObservable();
        this.startPlayer = new Player("Red", "The playable character", null, rd.getDefaultStartLocation());
        this.entryList = new ArrayList<>();
        this.setLocation(rd.getDefaultStartLocation());
    }

    public boolean isThisObservable(Observable o) {
        return o == routeObservable;
    }

    public void addObserver(RouteObserver observer) {
        routeObservable.addObserver(observer);
    }

    public final void setPlayer(Player p) {
        if (this.startPlayer != p) {
            this.startPlayer = p;
            setDataUpdated(this);
            notifyChanges();
        }
    }

    public void disableRefresh() {
        canRefresh = false;
    }

    public void enableRefresh() {
        canRefresh = true;
        notifyChanges();
    }

    public void notifyChanges() {
        if (canRefresh && (dataUpdated || infoUpdated)) {
            updateEntryList();
            if (dataUpdated) { // TODO only start updating from the first source!
                boolean toUpdate = false;
                Player p = startPlayer;
                for (RouteEntry e : this.entryList) {
                    if (!toUpdate && updatedEntries.contains(e)) {
                        toUpdate = true;
                    }
                    if (toUpdate) {
                        p = e.apply(p);
                    } else {
                        p = e.getPlayerAfter();
                    }
                }
                updatedEntries.clear();
            }
            routeObservable.setChanged();
            routeObservable.notifyObservers(TREE_UPDATED);
            dataUpdated = false;
            infoUpdated = false;
        }
    }

    // TODO add a source!
    void setDataUpdated(RouteEntry entry) {
        updatedEntries.add(entry);
        dataUpdated = true;
    }

    public boolean isDataUpdated() {
        return dataUpdated;
    }

    // TODO add a source!
    void setInfoUpdated() {
        infoUpdated = true;
    }

    public boolean isInfoUpdated() {
        return infoUpdated;
    }

    public boolean isRouteUpdated() {
        return dataUpdated || infoUpdated;
    }

    public boolean isEntryDataUpdated(RouteEntry entry) {
        // TODO
        return isRouteUpdated();
    }

    private void updateEntryList() {
        if (canRefresh) {
            this.entryList = getEntryList();
        }
    }

    public void resetEncounters() {
        updateEntryList();
        for (RouteEntry re : entryList) {
            re.wildEncounters.reset();
        }
    }

    public List<RouterMessage> getAllMessages() {
        updateEntryList();
        List<RouterMessage> allMessages = new ArrayList<>();
        for (RouteEntry re : entryList) {
            allMessages.addAll(re.messages);
        }
        return allMessages;
    }

    @Override
    public String toString() {
        String route = "";
        for (RouteEntry entry : super.children) {
            route += entry + "\n\n";
        }
        return route;
    }

    @Override
    public String writeToString(int depth, PrintSettings ps) {
        if (ps == null) {
            ps = new PrintSettings();
        }
        String str = "Route: ";
        switch (rd.settings.game) {
            case Settings.GAME_BLUE:
                str += "B";
                break;
            case Settings.GAME_RED:
                str += "R";
                break;
            case Settings.GAME_YELLOW:
                str += "Y";
                break;
            default:
                throw new RuntimeException("Error while writing route to file: invalid game \"" + rd.settings.game + "\"");
        }
        str += " :: " + info;

        for (RouteEntry child : children) {
            str += "\n" + child.writeToString(depth + 1, ps);
        }

        return str;
    }

}
