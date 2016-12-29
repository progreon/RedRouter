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
public class PokemonLevelPair implements Comparable<PokemonLevelPair> {

    public final Pokemon pkmn;
    public final int level;

    public PokemonLevelPair(Pokemon pkmn, int level) {
        this.pkmn = pkmn;
        this.level = level;
    }

    @Override
    public String toString() {
        return this.pkmn + " (L" + this.level + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PokemonLevelPair)) {
            return false;
        } else {
            return ((PokemonLevelPair) obj).pkmn == this.pkmn && ((PokemonLevelPair) obj).level == this.level;
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.pkmn);
        hash = 53 * hash + this.level;
        return hash;
    }

    @Override
    public int compareTo(PokemonLevelPair o) {
        int result = pkmn.name.compareTo(o.pkmn.name);
        if (result == 0) {
            result = level - o.level;
        }
        return result;
    }
}
