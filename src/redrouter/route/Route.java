/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package redrouter.route;

import java.util.ArrayList;
import java.util.List;
import redrouter.data.Trainer;

/**
 *
 * @author marco
 */
public class Route {
    
    private final List<RouteEntry> theRoute;
    private final Trainer player;

    public Route() {
        theRoute = new ArrayList<>();
        player = new Trainer("Pallet Town", "Red", "The playable character", null);
    }
    
    public void addRouteEntry(RouteEntry entry) {
        theRoute.add(entry);
    }
    
    public RouteDirections addDirections(String summary) {
        RouteDirections r = new RouteDirections(summary);
        this.addRouteEntry(r);
        return r;
    }
    
    @Override
    public String toString() {
        String route = "";
        for (RouteEntry entry : theRoute) {
            route += entry + "\n\n";
        }
        return route;
    }
    
}
