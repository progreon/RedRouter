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
package redrouter.io;

import java.io.File;
import java.util.HashMap;
import redrouter.data.Trainer;
import redrouter.route.Route;
import redrouter.route.RouteEntry;

/**
 *
 * @author Marco Willems
 */
public class RouteWriter {

    private HashMap<String, Trainer> trainers; // alias => trainer

    public RouteWriter() {
        init();
    }

    private void init() {
        trainers = new HashMap<>();
    }

    public void writeToFile(Route route, File file, PrintSettings printSettings) {
        // TODO
    }

    public String writeToString(Route route, PrintSettings printSettings) {
        if (printSettings != null) {
            // TODO
            return null;
        } else {
            return writeToString(route);
        }
    }

    public String writeToString(Route route) {
        return writeToString(route, 0);
    }

    public String writeToString(RouteEntry entry, int depth) {
        return entry.writeToString(depth, null);
    }

}
