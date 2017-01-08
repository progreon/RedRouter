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
package be.marcowillems.redrouter.route;

import java.util.Set;
import java.util.TreeSet;
import be.marcowillems.redrouter.data.EncounterArea;
import be.marcowillems.redrouter.io.PrintSettings;
import be.marcowillems.redrouter.util.IntPair;
import be.marcowillems.redrouter.util.PokemonCountPair;

/**
 * TODO: allow for location wide preferences
 *
 * @author Marco Willems
 */
public class RouteEncounter extends RouteEntry {

    private Set<PokemonCountPair> preferences;
    private final EncounterArea area;

    // Use this for DSUM later, maybe?
    private int[] slots;

    public RouteEncounter(Route route, RouteEntryInfo info, EncounterArea area, Set<PokemonCountPair> preferences) {
        this(route, info, area);
        updatePreferences(preferences); // TODO: different? this invokes notifyDataUpdated() etc
    }

    public RouteEncounter(Route route, RouteEntryInfo info, EncounterArea area, IntPair[] slotPreferences) {
        this(route, info, area);
        for (IntPair ip : slotPreferences) {
            if (ip.int1 > 0 && ip.int1 < area.slots.length) {
                this.preferences.add(new PokemonCountPair(area.slots[ip.int1], ip.int2));
            }
        }
        updatePreferences(this.preferences); // TODO: different? this invokes notifyDataUpdated() etc
    }

    private RouteEncounter(Route route, RouteEntryInfo info, EncounterArea area) {
        super(route, info, true, area.location);
        if (info == null || (info.title == null && info.description == null)) {
            this.info = new RouteEntryInfo(area + ": get experience");
        }
        this.area = area;
        this.preferences = new TreeSet<>();
    }

    public EncounterArea getArea() {
        return this.area;
    }

    public Set<PokemonCountPair> getPreferences() {
        return this.preferences;
    }

    public final void updatePreferences(Set<PokemonCountPair> preferences) {
        if (preferences != this.preferences) {
            this.preferences = new TreeSet<>();
            if (preferences != null) {
                for (PokemonCountPair pip : preferences) {
                    if (area.contains(pip.plp)) {
                        this.preferences.add(pip);
                    }
                }
            }
        }
        super.getWildEncounters().setPreferences(this.preferences);
        super.notifyDataUpdated();
        super.notifyRoute();
    }

    @Override
    public String toString() {
        String str = info + "\nPreferences: ";

        for (PokemonCountPair pcp : preferences) {
            str += pcp.plp + ", ";
        }
        if (preferences.size() > 0) {
            str = str.substring(0, str.length() - 2);
        }
        return str;
    }

    @Override
    public String writeToString(int depth, PrintSettings ps) {
        if (ps == null) {
            ps = new PrintSettings();
        }
        String str = "E: " + this.area.toString() + " ::";
        for (PokemonCountPair pcp : preferences) {
            str += " " + area.getSlots(pcp.plp)[0] + ":" + pcp.getCount();
        }
        // TODO description
        str = lineToDepth(str, depth);
        return str;
    }

}
