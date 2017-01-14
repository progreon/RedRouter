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

import be.marcowillems.redrouter.io.ParserException;
import java.util.Locale;
import java.util.Objects;

/**
 * TODO
 *
 * @author Marco Willems
 */
public class Item {

    public enum Type {

        BALL("BALL"),
        HEAL("HEAL"),
        PP("PP"), // ether and elixer
        STAT("STAT"),
        STONE("STONE"),
        TM("TM"), // also HM
        XITEM("X"),
        OTHER("");

        public final String keyword;

        private Type(String keyword) {
            this.keyword = keyword;
        }
    }

    public final Type type;
    public final String name;
    public final String value;
    public final int price;
    public final boolean tossable;
    public final boolean usableOutBattle;
    public final boolean usableInBattle;

    public Item(String name, boolean tossable, boolean usableOutBattle, boolean usableInBattle, int price, Type type) {
        this(name, tossable, usableOutBattle, usableInBattle, price, type, null);
    }

    public Item(String name, boolean tossable, boolean usableOutBattle, boolean usableInBattle, int price, Type type, String value) {
        this.type = type;
        this.name = name;
        this.value = value;
        this.price = price;
        this.tossable = tossable;
        this.usableOutBattle = usableInBattle;
        this.usableInBattle = usableInBattle;
    }

    public Item(RouterData rd, String itemString, String file, int line) throws ParserException {
        // item#[T|O|I]*#price[#type[:value]
        // CALCIUM#TO#9800#STAT:SPC
        String[] args = itemString.trim().split("#");
        if (args.length < 3 || args.length > 4) {
            throw new ParserException(file, line, "An item takes 3 or 4 arguments with a \"#\" in between!");
        }
        this.name = args[0].trim();
        String permissions = args[1].trim();
        // TODO: do permissions
        this.tossable = permissions.contains("T");
        this.usableOutBattle = permissions.contains("O");
        this.usableInBattle = permissions.contains("I");
        try {
            this.price = Integer.parseInt(args[2].trim());
        } catch (NumberFormatException nfe) {
            throw new ParserException(file, line, "Could not parse price \"" + args[2] + "\"");
        }
        if (args.length == 4) {
            String extra = args[3].trim();
            String[] extraArgs = extra.split(":");
            String t = extraArgs[0];
            int typeIdx = 0;
            while (typeIdx < Type.values().length - 1 && !Type.values()[typeIdx].keyword.equals(t)) {
                typeIdx++;
            }
            this.type = Type.values()[typeIdx];
            if (extraArgs.length > 1 && type != Type.OTHER) {
                if (extraArgs.length > 1) {
                    this.value = extra.substring(t.length() + 1);
                } else {
                    this.value = "";
                }
            } else if (type == Type.OTHER) {
                this.value = extra;
            } else {
                this.value = "";
            }
        } else {
            this.type = Type.OTHER;
            this.value = "";
        }
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
        hash = 89 * hash + Objects.hashCode(this.value);
        hash = 89 * hash + this.price;
        return hash;
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
