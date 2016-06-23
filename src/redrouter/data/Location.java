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

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Overworld location
 *
 * @author Marco Willems
 */
public class Location {
    
    private final RouterData rd;

    // TODO move to a world class?
    public final String name;
    public final BufferedImage image;
    public final List<EncounterArea> encounterAreas = new ArrayList<>();

    private Location(RouterData rd, String name) {
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

}
