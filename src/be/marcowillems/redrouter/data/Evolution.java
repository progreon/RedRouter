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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author Marco Willems
 */
public class Evolution {

    private final Map<Key, Pokemon> options;

    public Evolution(Key key, Pokemon pokemon) {
        options = new HashMap<>();
        options.put(key, pokemon);
    }

    public Evolution(Map<Key, Pokemon> options) {
        this.options = options;
    }

    public Pokemon get(Key key) {
        return (options == null ? null : options.get(key));
    }

    @Override
    public String toString() {
        String s = "";
        for (Key k : options.keySet()) {
            s += k + " => " + options.get(k) + " ";
        }
        return s;
    }

    // TODO other condition types?
    public static abstract class Key {

        protected final Object value;

        public Key(Object value) {
            this.value = value;
        }

        @Override
        public abstract boolean equals(Object obj);

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 59 * hash + Objects.hashCode(this.value);
            return hash;
        }

        @Override
        public abstract String toString();

    }

    public static class Level extends Key {

        public Level(int value) {
            super(value);
        }

        @Override
        public boolean equals(Object obj) {
            return (obj != null && obj instanceof Level && (int) ((Level) obj).value == (int) value);
        }

        @Override
        public String toString() {
            return (int) value + "";
        }

    }

    public static class Item extends Key {

        public Item(be.marcowillems.redrouter.data.Item value) {
            super(value);
        }

        @Override
        public boolean equals(Object obj) {
            return (obj != null && obj instanceof Item && ((Item) obj).value.equals(value));
        }

        @Override
        public String toString() {
            return (String) value;
        }

    }

}
