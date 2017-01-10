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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.TreeMap;
import be.marcowillems.redrouter.data.EncounterArea;
import be.marcowillems.redrouter.data.Location;
import be.marcowillems.redrouter.data.Player;
import be.marcowillems.redrouter.data.SingleBattler;
import be.marcowillems.redrouter.util.PokemonCountPair;
import be.marcowillems.redrouter.util.PokemonLevelPair;

/**
 *
 * @author Marco Willems
 */
public final class WildEncounters {

    private final RouteEntry routeEntry;
    private Location location = null;
    private final Map<EncounterArea, Set<PokemonCountPair>> encounterCounts = new TreeMap<>(); // Encounter counts per area

    public WildEncounters(RouteEntry routeEntry) {
        this.routeEntry = routeEntry;
        reset();
    }

    final void apply(Player p) {
        Location old = location;
        updateLocation();
        if (old != location) {
            reset();
        }
        for (SingleBattler sb : getBattledBattlers()) {
            if (p.getFrontBattler() != null) {
                p.getFrontBattler().defeatBattler(sb);
            } else {
                routeEntry.showMessage(RouterMessage.Type.ERROR, "You can't fight encounters when you don't have a pokemon!");
            }
        }
    }

    final void reset() {
        updateLocation();
        encounterCounts.clear();
        if (location != null) {
            for (EncounterArea ea : location.getAllEncounterAreas()) {
                if (!encounterCounts.containsKey(ea)) {
                    encounterCounts.put(ea, new TreeSet<>());
                }
                for (PokemonLevelPair plp : ea.getUniqueSlots()) {
                    encounterCounts.get(ea).add(new PokemonCountPair(plp));
                }
            }
            if (routeEntry instanceof RouteEncounter) {
                Set<PokemonCountPair> preferences = ((RouteEncounter) routeEntry).getPreferences();
                if (preferences != null) {
                    for (PokemonCountPair preference : preferences) {
                        boolean done = false; // out of all the encounter areas, add it to the first who has this slot
                        for (Set<PokemonCountPair> pcps : encounterCounts.values()) {
                            for (PokemonCountPair pcp : pcps) {
                                if (pcp.plp.equals(preference.plp) && !done) {
                                    pcp.setCount(preference.getCount());
                                    done = true;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public List<SingleBattler> getBattledBattlers() {
        List<SingleBattler> bbs = new ArrayList<>();
        for (EncounterArea ea : encounterCounts.keySet()) {
            Set<PokemonCountPair> spcp = encounterCounts.get(ea);
            for (PokemonCountPair pcp : spcp) {
                for (int i = 0; i < pcp.getCount(); i++) {
                    bbs.add(new SingleBattler(routeEntry.route.rd, pcp.plp.pkmn, ea, pcp.plp.level));
                }
            }
        }
        return bbs;
    }

    public Set<PokemonCountPair> getEncounterCounts(EncounterArea encounterArea) {
        if (encounterArea == null) {
            return null;
        } else {
            return encounterCounts.get(encounterArea);
        }
    }

    public Location getLocation() {
        return this.location;
    }

    private void updateLocation() {
        location = routeEntry.getLocation();
        if (location == null && routeEntry.getPlayerBefore() != null) {
            location = routeEntry.getPlayerBefore().getCurrentLocation();
        }
    }
}
