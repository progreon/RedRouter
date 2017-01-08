/*
 * Copyright (C) 2017 Marco Willems
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

import be.marcowillems.redrouter.data.Item;
import be.marcowillems.redrouter.data.Player;
import be.marcowillems.redrouter.io.PrintSettings;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO for menuing (rename to RouteMenu & RouteMenuEntry?)
 *
 * @author Marco Willems
 */
public class RouteMenu extends RouteEntry {

    private List<Entry> entries = new ArrayList<>();

    public RouteMenu(Route route, RouteEntryInfo info) {
        super(route, info, true);
    }

    @Override
    protected Player apply(Player p) {
//        return super.apply(p); //To change body of generated methods, choose Tools | Templates.
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void addEntry(Entry entry) {
        entries.add(entry);
    }

    @Override
    public String toString() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String writeToString(int depth, PrintSettings ps) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public static enum Action {

        GET, USE, TOSS, SWAP
    }

    public static Action getAction(String action) {
        Action a = null;
        switch (action) {
            case "GET":
                a = Action.GET;
                break;
            case "USE":
                a = Action.USE;
                break;
            case "TOSS":
                a = Action.TOSS;
                break;
            case "SWAP":
                a = Action.SWAP;
        }
        return Action.USE;
    }

    public static class Entry {

        private Action action;
        private Item item1;
        private Item item2 = null;
        private int itemSlot2 = -1;
        private String description;

        public Entry(Action action, Item item1) {
            this(action, item1, null);
        }

        public Entry(Action action, Item item1, String description) {
            this.action = (action == null ? Action.USE : action);
            this.item1 = item1;
            if (description == null) {
                this.description = this.action + ": " + item1;
            }
        }

        public Entry(Action action, Item item1, int itemSlot2, String description) {
            this(action, item1, description);
            if (this.action == Action.SWAP) {
                this.itemSlot2 = itemSlot2;
                if (description == null) {
                    this.description += ", slot " + itemSlot2;
                }
            }
        }

        public Entry(Action action, Item item1, Item item2, String description) {
            this(action, item1, description);
            if (this.action == Action.SWAP) {
                this.item2 = item2;
                if (description == null) {
                    this.description += ", " + item2;
                }
            }
        }

        public void apply(Player p) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }

}
