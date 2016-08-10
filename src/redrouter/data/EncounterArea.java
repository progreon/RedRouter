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
import java.util.Objects;

/**
 *
 * @author Marco Willems
 */
public class EncounterArea {

    private final RouterData rd;

    public final Location location;
    public final String subArea;
    public final int encounterRate;
    public final Slot[] slots;

    public EncounterArea(RouterData rd, Location location, String subArea, int encounterRate, Slot[] slots) {
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
                this.slots = new Slot[10];
                for (int i = 0; i < 10; i++) {
                    this.slots[i] = new Slot(s[i + 3], file, line);
                }
            } catch (NumberFormatException ex) {
                throw new ParserException(file, line, "Could not parse the encounter rate!");
            }
        } else {
            throw new ParserException(file, line, "There must be 13 arguments for each entry!");
        }
    }

    public SingleBattler getBattler(int slot) {
        return new SingleBattler(this, slot);
    }

    public List<SingleBattler> getBattlers(int[] slots) {
        List<Integer> uniqueSlots = new ArrayList<>();
        for (int i = 0; i < slots.length; i++) {
            boolean contains = false;
            for (int j = 0; j < uniqueSlots.size(); j++) {
                if (this.slots[uniqueSlots.get(j)].equals(this.slots[slots[i]])) {
                    contains = true;
                }
            }
            if (!contains) {
                uniqueSlots.add(slots[i]);
            }
        }
        List<SingleBattler> uniqueBattlers = new ArrayList<>();
        for (int i = 0; i < uniqueSlots.size(); i++) {
            uniqueBattlers.add(new SingleBattler(this, uniqueSlots.get(i)));
        }
        return uniqueBattlers;
    }

    public int[] getSlots(SingleBattler battler) {
        List<Integer> slotIDs = new ArrayList<>();
        Slot dummy = new Slot(battler.level, battler.getPokemon());
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

    public int[] getSlots(List<SingleBattler> battlers) {
        List<Integer> slotIDs = new ArrayList<>();
        for (int i = 0; i < battlers.size(); i++) {
            Slot dummy = new Slot(battlers.get(i).level, battlers.get(i).getPokemon());
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
        return getBattlers(slotIDs);
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

    public class Slot {

        final int level;
        final Pokemon pkmn;

        public Slot(int level, Pokemon pkmn) {
            this.level = level;
            this.pkmn = pkmn;
        }

        public Slot(String slotString, String file, int line) throws ParserException {
            // "L4 PIKACHU"
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
                    this.level = Integer.parseInt(str.substring(0, space));
                    this.pkmn = rd.getPokemon(str.substring(space + 1));
                    if (this.pkmn == null) {
                        throw new ParserException(file, line, "Pokemon " + str.substring(space + 1) + " does not exist!");
                    }
                } catch (NumberFormatException e) {
                    throw new ParserException(file, line, "Could not parse " + slotString);
                }
            } else {
                throw new ParserException(file, line, "Could not parse " + slotString);
            }
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Slot)) {
                return false;
            } else {
                return hashCode() == ((Slot) obj).hashCode();
            }
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 37 * hash + this.level;
            hash = 37 * hash + Objects.hashCode(this.pkmn);
            return hash;
        }

        @Override
        public String toString() {
            return this.pkmn + "(" + this.level + ")";
        }

    }

}
