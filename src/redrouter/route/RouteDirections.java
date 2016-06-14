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
public class RouteDirections extends RouteEntry {
    
    public final String summary;

    public RouteDirections(String summary) {
        this.summary = summary;
    }

    @Override
    public String toString() {
        String s = summary;
        if (info != null) {
            s += "\n\t" + info;
        }
        return s;
    }
    
}
