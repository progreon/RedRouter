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

import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 *
 * @author Marco Willems
 */
public class Evolution {

    private final Map<Key, Pokemon> options;

    public Evolution(Key key, Pokemon pokemon) {
        options = new TreeMap<>();
        options.put(key, pokemon);
    }

    public Evolution(Map<Key, Pokemon> options) {
        this.options = options;
    }

    public Pokemon get(Key key) {
        if (options != null) {
            Pokemon p = options.get(key);
            if (p == null && key instanceof Level) {
                int level = (int) key.value;
                for (Key k : options.keySet()) {
                    if (p == null && k instanceof Level && level > (int) k.value) {
                        p = options.get(k);
                    }
                }
            }
            return p;
        } else {
            return null;
        }
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
    public static abstract class Key implements Comparable<Key> {

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
        public String toString() {
            return value.toString();
        }

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
        public int compareTo(Key o) {
            if (o instanceof Level) {
                return (int) this.value - (int) o.value;
            } else {
                return -1;
            }
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
        public int compareTo(Key o) {
            if (o instanceof Level) {
                return 1;
            } else {
                return toString().compareTo(o.toString());
            }
        }

    }

}
