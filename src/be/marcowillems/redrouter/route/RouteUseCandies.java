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

import be.marcowillems.redrouter.data.Battler;
import be.marcowillems.redrouter.data.Player;
import be.marcowillems.redrouter.io.PrintSettings;

/**
 * TEMPORARY
 *
 * @author Marco Willems
 */
public class RouteUseCandies extends RouteEntry {

    private final int count;

    public RouteUseCandies(RouteEntryInfo info, int count) {
        super(info, true);
        this.count = count;
    }

    @Override
    protected Player apply(Player p) {
        Player newPlayer = super.apply(p);

        Battler b = newPlayer.getFrontBattler();
        b.useCandy(count);

        showMessage(RouterMessage.Type.WARNING, "This is a temporary entry type!");
        showMessage(RouterMessage.Type.HINT, "There will be a menu entry soon, don't worry :)");

        return newPlayer;
    }

    @Override
    public String toString() {
        return info + "";
    }

    @Override
    public String writeToString(int depth, PrintSettings ps) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
