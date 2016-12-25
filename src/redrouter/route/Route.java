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
import java.io.File;
import redrouter.Settings;
import redrouter.data.Player;
import redrouter.data.RouterData;

/**
 *
 * @author Marco Willems
 */
public class Route extends RouteSection {

    public final RouterData rd;

    public Route(RouterData rd, String title) {
        super(null, title);
        this.rd = rd;
        super.player = new Player("Red", "The playable character", null);
    }

    public final void setPlayer(Player p) {
        super.player = p;
        refreshData(player);
    }

    // TODO
    public void load(File file) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    // TODO
    public void save(File file) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    // TODO
    public void printReadable(File file, PrintSettings printSettings) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String toString() {
        String route = "";
        for (RouteEntry entry : super.children) {
            route += entry + "\n\n";
        }
        return route;
    }

    @Override
    public String writeToString(int depth, PrintSettings ps) {
        if (ps == null) {
            ps = new PrintSettings();
        }
        String str = "Route: ";
        switch (rd.settings.game) {
            case Settings.GAME_BLUE:
                str += "B";
                break;
            case Settings.GAME_RED:
                str += "R";
                break;
            case Settings.GAME_YELLOW:
                str += "Y";
                break;
            default:
                throw new RuntimeException("Error while writing route to file: invalid game \"" + rd.settings.game + "\"");
        }
        str += " :: " + info;

        for (RouteEntry child : children) {
            str += "\n" + child.writeToString(depth + 1, ps);
        }

        return str;
//        String str = lineToDepth("S: " + info, depth);
//        for (RouteEntry child : children) {
//            str += "\n" + child.writeToString(depth + 1, ps);
//        }
//        return str;
    }

}
