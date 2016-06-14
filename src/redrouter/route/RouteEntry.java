/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package redrouter.route;

/**
 *
 * @author marco
 */
public abstract class RouteEntry {
    
    public RouteEntryInfo info;

    public RouteEntry() {
    }

    public RouteEntry(RouteEntryInfo info) {
        this.info = info;
    }

    @Override
    public abstract String toString();
    
}
