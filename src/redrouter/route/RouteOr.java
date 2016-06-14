/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package redrouter.route;

import java.util.HashMap;

/**
 *
 * @author marco
 */
public class RouteOr extends RouteEntry {
    
    private final Route parentRoute;
    private final HashMap<String, Route> subRoutes; // name -> subroute
    private String selectedSubRouteName;

    public RouteOr(Route parentRoute, RouteEntryInfo info) {
        super(info);
        this.parentRoute = parentRoute;
        this.subRoutes = new HashMap<>();
        this.selectedSubRouteName = null;
    }
    
    public boolean addSubRoute(String subRouteName, Route subRoute) {
        boolean contains = subRoutes.containsKey(subRouteName);
        if (!contains) {
            subRoutes.put(subRouteName, subRoute);
        }
        return !contains;
    }
    
    public Route getSelectedSubRoute() {
        return subRoutes.get(selectedSubRouteName);
    }
    
    public String getSelectedSubRouteName() {
        return this.selectedSubRouteName;
    }
    
    public HashMap<String, Route> getSubRoutes() {
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
    public String toString() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
