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

import java.util.HashMap;
import redrouter.data.Player;
import redrouter.io.PrintSettings;

/**
 * TODO
 *
 * @author Marco Willems
 */
public class RouteOr extends RouteEntry {

    private final HashMap<String, RouteSection> subRoutes; // name (ID) -> subroute
    private String selectedSubRouteName;

    public RouteOr(RouteSection parentSection, RouteEntryInfo info) {
        super(parentSection, info);
        this.subRoutes = new HashMap<>();
        this.selectedSubRouteName = null;
    }

    public boolean addSubRoute(String subRouteName, RouteSection subRoute) {
        boolean contains = subRoutes.containsKey(subRouteName);
        if (!contains) {
            subRoutes.put(subRouteName, subRoute);
        }
        return !contains;
    }

    public RouteSection getSelectedSubRoute() {
        return subRoutes.get(selectedSubRouteName);
    }

    public String getSelectedSubRouteName() {
        return this.selectedSubRouteName;
    }

    public HashMap<String, RouteSection> getSubRoutes() {
        return this.subRoutes;
    }

    public boolean selectSubRoute(String subRouteName) {
        boolean available = subRoutes.containsKey(subRouteName);
        if (available) {
            selectedSubRouteName = subRouteName;
        }
        return available;
    }

    @Override
    protected Player apply(Player p) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String toString() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String writeToString(int depth, PrintSettings ps) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
