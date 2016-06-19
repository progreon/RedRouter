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
package redrouter.data;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 *
 * @author Marco Willems
 */
public class EncounterArea {

    private final Location location;
    private final String subArea;
//    private final 

    public static final Map<String, EncounterArea> areas = new HashMap<>();

    private EncounterArea(Location location, String subArea) {
        this.location = location;
        this.subArea = subArea;
    }

    public static EncounterArea newEncounterArea(Location location, String subArea) {
        if (!areas.containsKey(toString(location, subArea).toUpperCase(Locale.ROOT))) {
            EncounterArea area = new EncounterArea(location, subArea);
            areas.put(area.toString().toUpperCase(Locale.ROOT), area);
            location.encounterAreas.add(area);
            return area;
        } else {
            return null;
        }
    }

    public static EncounterArea getEncounterArea(Location location, String subArea) {
        return areas.get(toString(location, subArea).toUpperCase(Locale.ROOT));
    }

    private static String toString(Location location, String subArea) {
        String str = location.name;
        if (subArea != null && !subArea.equals("")) {
            str += " (" + subArea + ")";
        }
        return str;
    }

    @Override
    public String toString() {
        return toString(this.location, this.subArea);
    }
    
    private class Slot {
        
        final int level;
        final Pokemon pkmn;

        public Slot(int level, Pokemon pkmn) {
            this.level = level;
            this.pkmn = pkmn;
        }
        
        public Slot(String slotString) {
            // "L4 PIKACHU"
            // TODO
            this.level = 4;
            this.pkmn = RouteFactory.getPokemonByName(Pokemon.Pkmn.PIKACHU);
        }
        
    }

}
