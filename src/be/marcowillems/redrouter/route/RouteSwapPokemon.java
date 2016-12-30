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

    public RouteSwapPokemon(RouteEntryInfo info, int index1, int index2) {
        this(info, index1, -1, index2, -1);
    }

    public RouteSwapPokemon(RouteEntryInfo info, int index1, int box1, int index2, int box2) {
        super(info, true);
        this.index1 = index1;
        this.index2 = index2;
        this.box1 = box1;
        this.box2 = box2;
    }

    @Override
    protected Player apply(Player p) {
        Player newPlayer = super.apply(p).getDeepCopy();

        newPlayer.swapBattlers(index1, index2);

        return newPlayer;
    }

    @Override
    public String toString() {
        String text = "";

        if (info != null && info.toString() != null) {
            text = info.toString();
        } else {
            if (box1 < 0 || box2 < 0) {
                text = "Switch party pokemon with index " + index1 + " and " + index2 + ".";
            } else {
                // TODO
                text = "TODO: switch pokemon from boxes";
            }
        }

        return text;
    }

    @Override
    public String writeToString(int depth, PrintSettings ps) {
        if (ps == null) {
            ps = new PrintSettings();
        }
        String str = "SwapP: " + index1 + " " + index2;
        // TODO boxes!
        // TODO description
        return lineToDepth(str, depth);
    }

}
