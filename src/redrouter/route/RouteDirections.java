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

import redrouter.io.PrintSettings;

/**
 *
 * @author Marco Willems
 */
public class RouteDirections extends RouteEntry {

    public RouteDirections(RouteSection parentSection, RouteEntryInfo info) {
        super(parentSection, info);
    }

    public RouteDirections(RouteSection parentSection, String description) {
        this(parentSection, new RouteEntryInfo(null, description));
    }

    @Override
    public String toString() {
        return info.toString();
    }

    @Override
    public String writeToString(int depth, PrintSettings ps) {
        if (ps == null) {
            ps = new PrintSettings();
        }
        if (ps.ommitDirections()) {
            return null;
        } else {
            return lineToDepth("D: " + info.toString(), depth);
        }
    }

}
