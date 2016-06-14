/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package redrouter.data;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author marco
 */
public class Trainer {

    public final String location;
    public final String name;
    public final String info;
    public final List<Battler> team;

    public Trainer(String location, String name, String info, List<Battler> team) {
        this.location = location;
        this.name = name;
        this.info = info;
        if (team == null) {
            this.team = new ArrayList<>();
        } else {
            this.team = team;
        }
    }

    @Override
    public String toString() {
        String trainer = name + "\nLocation: " + location + "\nInfo: " + (info == null ? "" : info) + "\nTeam: ";
        for (Battler b : team) {
            trainer += "\t" + b.toString();
        }
        return trainer;
    }

}
