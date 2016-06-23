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

import java.util.Locale;

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
        public String toString() {
            return this.pkmn + "(" + this.level + ")";
        }

    }

}
