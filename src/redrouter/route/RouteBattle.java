/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package redrouter.route;

import redrouter.data.Trainer;

/**
 *
 * @author marco
 */
public class RouteBattle extends RouteEntry {
    
    public final Trainer opponent;

    public RouteBattle(Trainer opponent) {
        this.opponent = opponent;
    }

    @Override
    public String toString() {
        String s = opponent.toString();
        if (info != null) {
            s += "\n\n\t" + info;
        }
        return s;
    }
    
}
