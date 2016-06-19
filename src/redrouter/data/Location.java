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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 *
 * @author Marco Willems
 */
public class Location {
    
    // Dynamic!?
//    public static String PALLET_TOWN = "Pallet Town";
//    public static String ROUTE_1 = "Route 1";
//    public static String VIRIDIAN_CITY = "Viridian City";
    // ...
    public static final Map<String, Location> locations = new HashMap<>();
    
    // TODO move to a world class?
    public final String name;
//    public final BufferedImage image;
    public final List<EncounterArea> encounterAreas;

    private Location(String name) {
        this.name = name;
        encounterAreas = new ArrayList<>();
    }
    
    public static Location newLocation(String name) {
        if (!locations.containsKey(toString(name).toUpperCase(Locale.ROOT))) {
            Location location = new Location(name);
            locations.put(toString(name).toUpperCase(Locale.ROOT), location);
            return location;
        } else {
            return null;
        }
    }
    
    public static Location getLocation(String name) {
        return locations.get(name.toUpperCase(Locale.ROOT));
    }
    
    private static String toString(String name) {
        return name;
    }

    @Override
    public String toString() {
        return toString(this.name);
    }
    
}
