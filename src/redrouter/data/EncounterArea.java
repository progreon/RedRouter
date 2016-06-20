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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 *
 * @author Marco Willems
 */
public class EncounterArea {

    private final Location location;
    private final String subArea;
    // TODO: encounter slots

    public static final Map<String, EncounterArea> areas = new HashMap<>();

    private EncounterArea(Location location, String subArea) {
        this.location = location;
        this.subArea = subArea;
    }

    public static EncounterArea add(Location location, String subArea) {
        if (!areas.containsKey(toString(location, subArea).toUpperCase(Locale.ROOT))) {
            EncounterArea area = new EncounterArea(location, subArea);
            areas.put(area.toString().toUpperCase(Locale.ROOT), area);
            location.encounterAreas.add(area);
            return area;
        } else {
            return null;
        }
    }

    public static EncounterArea get(Location location, String subArea) {
        return areas.get(toString(location, subArea).toUpperCase(Locale.ROOT));
    }

    private static String toString(Location location, String subArea) {
        String str = location.name;
        if (subArea != null && !subArea.equals("")) {
            str += " (" + subArea + ")";
        }
        return str;
    }

    @Override
    public String toString() {
        return toString(this.location, this.subArea);
    }

    private class Slot {

        private final String ERROR_MESSAGE = "Could not parse ";
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
                    this.pkmn = Pokemon.get(str.substring(space + 1));
                    if (this.pkmn == null) {
                        throw new ParserException(file, line, "Pokemon " + str.substring(space + 1) + " does not exist!");
                    }
                } catch (Exception e) {
                    throw new ParserException(file, line, ERROR_MESSAGE + slotString);
                }
            } else {
                throw new ParserException(file, line, ERROR_MESSAGE + slotString);
            }
        }

    }

}
