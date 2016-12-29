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
package redrouter.util;

import java.util.Objects;
import redrouter.data.Pokemon;

/**
 *
 * @author Marco Willems
 */
public class PokemonCountPair implements Comparable<PokemonCountPair> {

    public static final int MIN_COUNT = 0;
    public static final int MAX_COUNT = 99;

    public final PokemonLevelPair plp;
    private int count;

    public PokemonCountPair(PokemonLevelPair plp) {
        this(plp, 0);
    }

    public PokemonCountPair(PokemonLevelPair plp, int count) {
        this.plp = plp;
        if (count < MIN_COUNT) {
            this.count = MIN_COUNT;
        } else if (count > MAX_COUNT) {
            this.count = MAX_COUNT;
        } else {
            this.count = count;
        }
    }

    public PokemonCountPair(Pokemon pkmn, int level) {
        this(new PokemonLevelPair(pkmn, level), 0);
    }

    public PokemonCountPair(Pokemon pkmn, int level, int count) {
        this(new PokemonLevelPair(pkmn, level), count);
    }

    public void inc() {
        setCount(count + 1);
    }

    public void dec() {
        setCount(count - 1);
    }

    public int getCount() {
        return this.count;
    }

    public void setCount(int count) {
        if (count < MIN_COUNT) {
            this.count = MIN_COUNT;
        } else if (count > MAX_COUNT) {
            this.count = MAX_COUNT;
        } else {
            this.count = count;
        }
    }

    @Override
    public String toString() {
        return plp.toString() + ": x" + count;
    }

    @Override
    public int compareTo(PokemonCountPair o) {
        int result = plp.compareTo(o.plp);
        if (result == 0) {
            result = count - o.count;
        }
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PokemonCountPair)) {
            return false;
        } else {
            return ((PokemonCountPair) obj).plp.equals(this.plp) && ((PokemonCountPair) obj).count == this.count;
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.plp);
        hash = 37 * hash + this.count;
        return hash;
    }

}
