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

import be.marcowillems.redrouter.data.Player;
import be.marcowillems.redrouter.io.PrintSettings;

/**
 * TODO: include possibility of multiple simultaneous swaps
 *
 * @author Marco Willems
 */
public class RouteSwapPokemon extends RouteEntry {

    public final int index1, index2, box1, box2;

    public RouteSwapPokemon(Route route, RouteEntryInfo info, int index1, int index2) {
        this(route, info, index1, -1, index2, -1);
    }

    public RouteSwapPokemon(Route route, RouteEntryInfo info, int index1, int box1, int index2, int box2) {
        super(route, info, true);
        this.index1 = index1;
        this.index2 = index2;
        this.box1 = box1;
        this.box2 = box2;
        if (info == null || info.description == null) {
            String title = (info == null ? null : info.title);
            String description;
            if (box1 < 0 || box2 < 0) {
                description = "Switch party pokemon with index " + index1 + " and " + index2 + ".";
            } else {
                // TODO
                description = "TODO: switch pokemon from boxes";
            }
            super.info = new RouteEntryInfo(title, description);
        }
    }

    @Override
    protected Player apply(Player p) {
        Player newPlayer = super.apply(p);

        if (index1 >= 0 && index1 < newPlayer.team.size() && index2 >= 0 && index2 < newPlayer.team.size()) {
            newPlayer.swapBattlers(index1, index2);
        } else {
            showMessage(RouterMessage.Type.ERROR, "Invalid party indices! (ignoring swap)");
        }

        return newPlayer;
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
        String str = "Swap: " + index1 + " " + index2;
        // TODO boxes!
        str = lineToDepth(str, depth);

        if (info != null && info.description != null) {
            str += "\n" + lineToDepth(info.description, depth + 1);
        }

        return str;
    }

}
