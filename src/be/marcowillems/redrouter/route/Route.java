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
import java.io.File;
import be.marcowillems.redrouter.Settings;
import be.marcowillems.redrouter.data.Player;
import be.marcowillems.redrouter.data.RouterData;
import be.marcowillems.redrouter.observers.RouteObservable;
import be.marcowillems.redrouter.observers.RouteObserver;
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

    private Player startPlayer;

    public Route(RouterData rd, String title) {
        super(null, title);
        this.rd = rd;
        this.routeObservable = new RouteObservable();
        this.startPlayer = new Player("Red", "The playable character", null);
        this.setLocation(rd.getDefaultLocation());
        super.setRoute(this);
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
            setDataUpdated();
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
            if (dataUpdated) { // TODO only start updating from the first source!
                Player p = startPlayer;
                for (RouteEntry e : getEntryList()) {
                    p = e.apply(p);
                }
            }
            routeObservable.setChanged();
            routeObservable.notifyObservers(TREE_UPDATED);
            dataUpdated = false;
            infoUpdated = false;
        }
    }

    // TODO add a source!
    void setDataUpdated() {
        dataUpdated = true;
    }

    void setInfoUpdated() {
        infoUpdated = true;
    }

    // IO stuff
    // TODO
    public void load(File file) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    // TODO
    public void save(File file) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    // TODO
    public void printReadable(File file, PrintSettings printSettings) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
