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
package be.marcowillems.redrouter.data;

import java.util.Objects;

/**
 * TODO
 *
 * @author Marco Willems
 */
public class Item {

//    public enum Type {
//
//        HEALING,
//        ETHER, // also elixers
//        XITEM,
//        TM,
//        HM,
//        STONE,
//        KEYITEM,
//        OTHER
//    }
//    
//    final Type type;
    final String name;
    final String type;
    final int price;
    final boolean tossable;
    final boolean usableOutBattle;
    final boolean usableInBattle;

    public Item(String name, boolean tossable, boolean usableOutBattle, boolean usableInBattle, int price) {
        this(name, tossable, usableOutBattle, usableInBattle, price, null);
    }

    public Item(String name, boolean tossable, boolean usableOutBattle, boolean usableInBattle, int price, String value) {
        this.name = name;
        this.type = value;
        this.price = price;
        this.tossable = tossable;
        this.usableOutBattle = usableInBattle;
        this.usableInBattle = usableInBattle;
    }

    public boolean isTossable() {
        return tossable;
    }

    public boolean use(Battle b, int partyIndex) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public boolean use(Player p, int partyIndex) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj instanceof Item && this.name.equals(((Item) obj).name);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + Objects.hashCode(this.name);
        hash = 89 * hash + Objects.hashCode(this.type);
        hash = 89 * hash + this.price;
        return hash;
    }

    @Override
    public String toString() {
        return name;
    }

}
