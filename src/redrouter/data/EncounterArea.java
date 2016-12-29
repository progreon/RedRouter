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
import java.util.List;
import java.util.Locale;
import redrouter.util.PokemonLevelPair;

/**
 * TODO fishing
 *
 * @author Marco Willems
 */
public class EncounterArea {

    private final RouterData rd;

    public final Location location;
    public final String subArea;
    public final int encounterRate;
    public final PokemonLevelPair[] slots;

    public EncounterArea(RouterData rd, Location location, String subArea, int encounterRate, PokemonLevelPair[] slots) {
        this.rd = rd;
        this.location = location;
        this.subArea = subArea;
        this.encounterRate = encounterRate;
        this.slots = slots;
    }

    public EncounterArea(RouterData rd, String areaString, String file, int line) throws ParserException {
        this.rd = rd;
        // MT.MOON#1F#10#L8 ZUBAT#L7 ZUBAT#L9 ZUBAT#L8 GEODUDE#L6 ZUBAT#L10 ZUBAT#L10 GEODUDE#L8 PARAS#L11 ZUBAT#L8 CLEFAIRY
        String[] s = areaString.split("#");
        if (s.length == 13) {
            this.location = rd.getLocation(s[0]);
            if (this.location == null) {
                throw new ParserException(file, line, "Could not find the specified location!");
            }
            this.subArea = s[1];
            try {
                this.encounterRate = Integer.parseInt(s[2]);
                this.slots = new PokemonLevelPair[10];
                for (int i = 0; i < 10; i++) {
                    this.slots[i] = parseSlot(s[i + 3], file, line);
                }
            } catch (NumberFormatException ex) {
                throw new ParserException(file, line, "Could not parse the encounter rate!");
            }
        } else {
            throw new ParserException(file, line, "There must be 13 arguments for each entry!");
        }
    }

    public boolean contains(PokemonLevelPair plp) {
        boolean contains = false;
        int idx = 0;
        while (!contains && idx < slots.length) {
            if (slots[idx].equals(plp)) {
                contains = true;
            }
            idx++;
        }
        return contains;
    }

    public SingleBattler getBattler(int slot) {
        return new SingleBattler(this, slot);
    }

    public int[] getSlots(SingleBattler battler) {
        List<Integer> slotIDs = new ArrayList<>();
        PokemonLevelPair dummy = new PokemonLevelPair(battler.getPokemon(), battler.level);
        for (int i = 0; i < slots.length; i++) {
            if (dummy.equals(slots[i])) {
                slotIDs.add(i);
            }
        }
        int[] ids = new int[slotIDs.size()];
        for (int i = 0; i < slotIDs.size(); i++) {
            ids[i] = slotIDs.get(i);
        }
        return ids;
    }

    public int[] getSlots(PokemonLevelPair plp) {
        List<Integer> slotIDs = new ArrayList<>();
        for (int i = 0; i < slots.length; i++) {
            if (plp.equals(slots[i])) {
                slotIDs.add(i);
            }
        }
        int[] ids = new int[slotIDs.size()];
        for (int i = 0; i < slotIDs.size(); i++) {
            ids[i] = slotIDs.get(i);
        }
        return ids;
    }

    public int[] getSlots(List<SingleBattler> battlers) {
        List<Integer> slotIDs = new ArrayList<>();
        for (int i = 0; i < battlers.size(); i++) {
            PokemonLevelPair dummy = new PokemonLevelPair(battlers.get(i).getPokemon(), battlers.get(i).level);
            for (int j = 0; j < slots.length; j++) {
                if (dummy.equals(slots[j])) {
                    slotIDs.add(j);
                }
            }
        }
        int[] ids = new int[slotIDs.size()];
        for (int i = 0; i < slotIDs.size(); i++) {
            ids[i] = slotIDs.get(i);
        }
        return ids;
    }

    public List<SingleBattler> getUniqueBattlers() {
        int[] slotIDs = new int[slots.length];
        for (int i = 0; i < slotIDs.length; i++) {
            slotIDs[i] = i;
        }
        return getUniqueBattlers(slotIDs);
    }

    public List<SingleBattler> getUniqueBattlers(int[] slots) {
        List<SingleBattler> uniqueBattlers = new ArrayList<>();
        for (PokemonLevelPair plp : getUniqueSlots(slots)) {
            uniqueBattlers.add(new SingleBattler(plp.pkmn, this, plp.level));
        }
        return uniqueBattlers;
    }

    public List<PokemonLevelPair> getUniqueSlots() {
        int[] slotIDs = new int[slots.length];
        for (int i = 0; i < slotIDs.length; i++) {
            slotIDs[i] = i;
        }
        return getUniqueSlots(slotIDs);
    }

    public List<PokemonLevelPair> getUniqueSlots(int[] slots) {
        List<PokemonLevelPair> uniqueSlots = new ArrayList<>();
        for (int i = 0; i < slots.length; i++) {
            int idx = slots[i];
            if (idx >= 0 && idx <= this.slots.length && !uniqueSlots.contains(this.slots[idx])) {
                uniqueSlots.add(this.slots[idx]);
            }
        }
        return uniqueSlots;
    }

    private PokemonLevelPair parseSlot(String slotString, String file, int line) throws ParserException {
        // "L4 PIKACHU"
        PokemonLevelPair slot;
        if (slotString == null || slotString.equals("")) {
            throw new ParserException(file, line, "A slot cannot be empty!");
        }
        String str = slotString.toUpperCase(Locale.ROOT);
        if (str.substring(0, 1).equals("L")) {
            try {
                str = str.substring(1);
                int space = str.indexOf(' ');
                if (space < 0) {
                    throw new ParserException(file, line, "Please seperate the level from the pokemon name with a space!");
                }
                Pokemon pkmn = rd.getPokemon(str.substring(space + 1));
                int level = Integer.parseInt(str.substring(0, space));
                slot = new PokemonLevelPair(pkmn, level);
                if (slot.pkmn == null) {
                    throw new ParserException(file, line, "Pokemon " + str.substring(space + 1) + " does not exist!");
                }
            } catch (NumberFormatException e) {
                throw new ParserException(file, line, "Could not parse " + slotString);
            }
        } else {
            throw new ParserException(file, line, "Could not parse " + slotString);
        }
        return slot;
    }

    @Override
    public String toString() {
        String str = location.name;
        if (subArea != null && !subArea.equals("")) {
            str += " (" + subArea + ")";
        }
        return str;
    }

    public String getIndexString() {
        return getIndexString(location, subArea);
    }

    public static String getIndexString(Location location, String subArea) {
        String str = location.name;
        if (subArea != null && !subArea.equals("")) {
            str += " (" + subArea + ")";
        }
        return str.toUpperCase(Locale.ROOT);
    }

}
