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
package be.marcowillems.redrouter.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 *
 * @author Marco Willems
 */
public class Trainer {

    public final Location location;
    public final String name;
    public final String info;
    public final List<Battler> team;
    public Item item; // Item given after the fight

    public Trainer(Location location, String name, String info, List<Battler> team) {
        this.location = location;
        this.name = name;
        this.info = info;
        if (team == null) {
            this.team = new ArrayList<>();
        } else {
            this.team = team;
        }
    }

    // TODO: TEMP
    public static String getIndexString(String name) {
        return name.toUpperCase(Locale.ROOT);
    }

    public String getIndexString() {
        return getIndexString(name);
    }

    @Override
    public String toString() {
        String trainer = name;
        if (location != null) {
            trainer += " (" + location.name + ")";
        }
//        String trainer = name + "\n";
//        if (location != null) {
//            trainer += "Location: " + location.name + "\n";
//        }
//        if (info != null) {
//            trainer += "Info: " + info + "\n";
//        }
//        trainer += "Team: ";
//        for (Battler b : team) {
//            trainer += "\t" + b.toString();
//        }
        return trainer;
    }

}
