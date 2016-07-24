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

import java.util.Objects;

/**
 * TODO
 *
 * @author Marco Willems
 */
public class Item {

    public enum Type {

        HEALING,
        ETHER, // also elixers
        XITEM,
        TM,
        HM,
        STONE,
        KEYITEM,
        OTHER
    }

    final String name;
    final Type type;
    final int price;
    final boolean usableInBattle;
    final boolean usableOutBattle;

    public Item(String name, Type type, int price, boolean usableInBattle, boolean usableOutBattle) {
        this.name = name;
        this.type = type;
        this.price = price;
        this.usableInBattle = usableInBattle;
        this.usableOutBattle = usableInBattle;
    }

    public boolean isTossable() {
        return type != Type.HM && type != Type.KEYITEM;
    }

    public boolean use(Battle b, int partyIndex) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public boolean use(Player p, int partyIndex) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj instanceof Item && this.hashCode() == ((Item) obj).hashCode();
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
