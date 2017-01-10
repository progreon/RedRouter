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

import java.awt.image.BufferedImage;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import be.marcowillems.redrouter.io.ParserException;

/**
 * Overworld location
 *
 * @author Marco Willems
 */
public class Location implements Comparable<Location> {

    private final RouterData rd;

    public final String name;
    public final BufferedImage image;
    final Set<Location> subLocations = new TreeSet();
    final Map<String, EncounterArea> encounterAreas = new TreeMap(); // TODO: private?

    public Location(RouterData rd, String name) {
        this(rd, name, null);
    }

    public Location(RouterData rd, String name, BufferedImage image) {
        this.rd = rd;
        this.name = name;
        this.image = image;
    }

    public Location(RouterData rd, String locationString, String file, int line) throws ParserException {
        this.rd = rd;
        String[] s = locationString.split("#");
        this.name = s[0];
        this.image = null;
    }

    public Set<EncounterArea> getEncounterAreas() {
        Set<EncounterArea> allAreas = new TreeSet<>();
        allAreas.addAll(encounterAreas.values());
        return allAreas;
    }

    public Set<EncounterArea> getAllEncounterAreas() {
        Set<EncounterArea> allAreas = new TreeSet<>();
        allAreas.addAll(encounterAreas.values());
        for (Location l : subLocations) {
            allAreas.addAll(l.getAllEncounterAreas());
        }
        return allAreas;
    }

    boolean addSubLocation(Location location) {
        return subLocations.add(location);
    }

    /**
     * Including this location
     *
     * @return
     */
    public Set<Location> getLocations() {
        Set<Location> allLocations = new TreeSet<>();
        allLocations.add(this);
        for (Location l : subLocations) {
            allLocations.addAll(l.getLocations());
        }
        return allLocations;
    }

    public String getIndexString() {
        return getIndexString(name);
    }

    public static String getIndexString(String name) {
        return name.toUpperCase(Locale.ROOT);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int compareTo(Location o) {
        return getIndexString().compareTo(o.getIndexString());
    }

}
