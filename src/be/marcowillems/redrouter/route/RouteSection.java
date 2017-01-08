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

import java.util.List;
import java.util.Set;
import be.marcowillems.redrouter.data.EncounterArea;
import be.marcowillems.redrouter.data.Trainer;
import be.marcowillems.redrouter.io.PrintSettings;
import be.marcowillems.redrouter.util.IntPair;
import be.marcowillems.redrouter.util.PokemonCountPair;

/**
 * A route section contains a list of route entries
 *
 * @author Marco Willems
 */
public class RouteSection extends RouteEntry {

    public RouteSection(Route route, String title) {
        this(route, title, null);
    }

    public RouteSection(Route route, String title, List<RouteEntry> children) {
        super(route, new RouteEntryInfo(title), false, children);
    }

    public RouteEntry addEntry(RouteEntry entry) {
        super.children.add(entry);
        entry.setParentSection(this);
        super.notifyDataUpdated();
        super.notifyRoute();
        return entry;
    }

    public RouteSection addSection(RouteSection section) {
        addEntry(section);
        return section;
    }

    public RouteBattle addNewBattle(RouteEntryInfo info, Trainer opponent) {
        RouteBattle r = new RouteBattle(route, info, opponent);
        addEntry(r);
        return r;
    }

    public RouteBattle addNewBattle(RouteEntryInfo info, Trainer opponent, int[][] competingPartyMon) {
        RouteBattle r = new RouteBattle(route, info, opponent, competingPartyMon);
        addEntry(r);
        return r;
    }

    public RouteDirections addNewDirections(String description) {
        RouteDirections r = new RouteDirections(route, description);
        addEntry(r);
        return r;
    }

    public RouteEncounter addNewEncounter(String description, EncounterArea area, IntPair[] slotPreferences) {
        RouteEncounter r = new RouteEncounter(route, new RouteEntryInfo(null, description), area, slotPreferences);
        addEntry(r);
        return r;
    }

    public RouteEncounter addNewEncounter(String description, EncounterArea area, Set<PokemonCountPair> preferences) {
        RouteEncounter r = new RouteEncounter(route, new RouteEntryInfo(null, description), area, preferences);
        addEntry(r);
        return r;
    }

    public RouteOr addNewOr(RouteEntryInfo info) {
        RouteOr r = new RouteOr(route, info);
        addEntry(r);
        return r;
    }

    public RouteSection addNewSection(String title) {
        RouteSection r = new RouteSection(route, title);
        addEntry(r);
        return r;
    }

    @Override
    public String toString() {
        String str = "--" + info.title + "--";

        for (RouteEntry re : super.children) {
            str += "\n";
            str += re.toString();
        }

        return str;
    }

    @Override
    public String writeToString(int depth, PrintSettings ps) {
        if (ps == null) {
            ps = new PrintSettings();
        }
        String str = lineToDepth("S: " + info, depth);
        for (RouteEntry child : children) {
            String childStr = child.writeToString(depth + 1, ps);
            if (childStr != null) {
                str += "\n" + child.writeToString(depth + 1, ps);
            }
        }
        return str;
    }

}
