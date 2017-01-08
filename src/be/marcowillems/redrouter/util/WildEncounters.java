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
package be.marcowillems.redrouter.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import be.marcowillems.redrouter.data.EncounterArea;
import be.marcowillems.redrouter.data.Location;
import be.marcowillems.redrouter.data.RouterData;
import be.marcowillems.redrouter.data.SingleBattler;

/**
 *
 * @author Marco Willems
 */
public class WildEncounters {

    public final RouterData rd;
    public final Location loc;
    public final Map<EncounterArea, Set<PokemonCountPair>> encounterCounts; // Encounter counts per area

    public WildEncounters(RouterData rd, Location loc) {
        this.rd = rd;
        this.loc = loc;
        if (loc != null) {
            this.encounterCounts = new HashMap<>(loc.encounterAreas.size());
            for (EncounterArea ea : loc.encounterAreas) {
                Set<PokemonCountPair> areaCounts = new TreeSet<>();
                for (PokemonLevelPair plp : ea.getUniqueSlots()) {
                    areaCounts.add(new PokemonCountPair(plp, 0));
                }
                this.encounterCounts.put(ea, areaCounts);
            }
        } else {
            this.encounterCounts = null;
        }
    }

    public WildEncounters(RouterData rd, EncounterArea ea) {
        this.rd = rd;
        if (ea != null) {
            this.loc = ea.location;
            this.encounterCounts = new HashMap<>(1);
            Set<PokemonCountPair> areaCounts = new TreeSet<>();
            for (PokemonLevelPair plp : ea.getUniqueSlots()) {
                areaCounts.add(new PokemonCountPair(plp, 0));
            }
            this.encounterCounts.put(ea, areaCounts);
        } else {
            this.loc = null;
            this.encounterCounts = null;
        }
    }

    public List<SingleBattler> getBattledBattlers() {
        List<SingleBattler> bbs = new ArrayList<>();
        if (encounterCounts != null) {
            for (EncounterArea ea : encounterCounts.keySet()) {
                Set<PokemonCountPair> spcp = encounterCounts.get(ea);
                for (PokemonCountPair pcp : spcp) {
                    for (int i = 0; i < pcp.getCount(); i++) {
                        bbs.add(new SingleBattler(rd, pcp.plp.pkmn, ea, pcp.plp.level));
                    }
                }
            }
        }
        return bbs;
    }

    public void reset() {
        if (encounterCounts != null) {
            for (EncounterArea ea : encounterCounts.keySet()) {
                for (PokemonCountPair pcp : encounterCounts.get(ea)) {
                    pcp.setCount(0);
                }
            }
        }
    }

    public void setPreferences(Set<PokemonCountPair> preferences) {
        if (encounterCounts != null) {
            reset();
            if (preferences != null) {
                for (PokemonCountPair preference : preferences) {
                    boolean done = false;
                    for (EncounterArea ea : encounterCounts.keySet()) {
                        for (PokemonCountPair pcp : encounterCounts.get(ea)) {
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
