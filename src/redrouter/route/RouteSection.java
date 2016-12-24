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
package redrouter.route;

import java.util.ArrayList;
import java.util.List;
import redrouter.data.EncounterArea;
import redrouter.data.SingleBattler;
import redrouter.data.Trainer;

/**
 * A route section contains a list of route entries
 *
 * @author Marco Willems
 */
public class RouteSection extends RouteEntry {

    public RouteSection(RouteSection parentSection, RouteEntryInfo info, List<RouteEntry> children) {
        super(parentSection, info, children == null ? new ArrayList<>() : children);
    }

    public RouteSection(RouteSection parentSection, RouteEntryInfo info) {
        this(parentSection, info, null);
    }

    public RouteSection(RouteSection parentSection, String title) {
        this(parentSection, new RouteEntryInfo(title), null);
    }

    public RouteSection(RouteSection parentSection, String title, String description) {
        this(parentSection, new RouteEntryInfo(title, description), null);
    }

    public RouteEntry addEntry(RouteEntry entry) {
        super.children.add(entry);
        entry.parent = this;
        return entry;
    }

    public RouteSection addSection(RouteSection section) {
        addEntry(section);
        return section;
    }

    public RouteBattle addNewBattle(RouteEntryInfo info, Trainer opponent) {
        RouteBattle r = new RouteBattle(this, info, opponent);
        addEntry(r);
        return r;
    }

    public RouteBattle addNewBattle(RouteEntryInfo info, Trainer opponent, int[][] competingPartyMon) {
        RouteBattle r = new RouteBattle(this, info, opponent, competingPartyMon);
        addEntry(r);
        return r;
    }

    public RouteDirections addNewDirections(String description) {
        RouteDirections r = new RouteDirections(this, description);
        addEntry(r);
        return r;
    }

    public RouteDirections addNewDirections(RouteEntryInfo info) {
        RouteDirections r = new RouteDirections(this, info);
        addEntry(r);
        return r;
    }

    public RouteEncounter addNewEncounter(String description, EncounterArea area, List<SingleBattler> choices, int preference) {
        RouteEncounter r = new RouteEncounter(this, new RouteEntryInfo(null, description), area, choices, preference);
        addEntry(r);
        return r;
    }

    public RouteEncounter addNewEncounter(String description, EncounterArea area, int[] choices, int preference) {
        RouteEncounter r = new RouteEncounter(this, new RouteEntryInfo(null, description), area, choices, preference);
        addEntry(r);
        return r;
    }

    public RouteOr addNewOr(RouteEntryInfo info) {
        RouteOr r = new RouteOr(this, info);
        addEntry(r);
        return r;
    }

    public RouteSection addNewSection(String title, String description) {
        RouteSection r = new RouteSection(this, title, description);
        addEntry(r);
        return r;
    }

    public RouteSection addNewSection(RouteEntryInfo info) {
        RouteSection r = new RouteSection(this, info);
        addEntry(r);
        return r;
    }

    @Override
    public String toString() {
        String str = "--" + info.title + "--";
        if (info.description != null && !info.description.isEmpty()) {
            str += "\n  " + info.description;
        }

        for (RouteEntry re : super.children) {
            str += "\n";
            str += re.toString();
        }

        return str;
    }

}
