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
public class RouteEntryInfo {
    
    private String info;

    public RouteEntryInfo(String info) {
        this.info = info;
    }
    
    @Override
    public String toString() {
        return info;
    }
    
}
