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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 *
 * @author Marco Willems
 */
public class Pokemon {

    private final RouterData rd;

    public final int ID;
    // TODO: Map: EV_Action (level/trade/stone, level) => Evolution
    public Pokemon evolution = null;
    public final String name;
    public final Types.Type type1;
    public final Types.Type type2;
    public final int expGiven;
    public final int hp;
    public final int atk;
    public final int def;
    public final int spd;
    public final int spc;
    // TODO: growth rate

    private final List<Move> defaultMoves = new ArrayList<>();
    private final Map<Integer, List<Move>> learnedMoves = new HashMap<>();
    private final List<Move> tmMoves = new ArrayList<>();

    public Pokemon(RouterData rd, int ID, String name, Types.Type type1, Types.Type type2, int expGiven, int hp, int atk, int def, int spd, int spc) {
        this.rd = rd;
        this.ID = ID;
        this.name = name;
        this.type1 = type1;
        this.type2 = type2;
        this.expGiven = expGiven;
        this.hp = hp;
        this.atk = atk;
        this.def = def;
        this.spd = spd;
        this.spc = spc;
    }

    public Pokemon(RouterData rd, int ID, String pokemonString, String file, int line) throws ParserException {
        this.rd = rd;
        String[] s = pokemonString.split("#");
        if (s.length != 9) {
            throw new ParserException(file, line, "Entry must have 9 parameters!");
        } else {
            try {
                this.ID = ID;
                this.name = s[0];
                this.type1 = Types.Type.NORMAL; // TODO
                this.type2 = null; // TODO
                this.expGiven = Integer.parseInt(s[3]);
                this.hp = Integer.parseInt(s[4]);
                this.atk = Integer.parseInt(s[5]);
                this.def = Integer.parseInt(s[6]);
                this.spd = Integer.parseInt(s[7]);
                this.spc = Integer.parseInt(s[8]);
            } catch (NumberFormatException nex) {
                throw new ParserException(file, line, "Could not parse stat exp parameters!");
            }
        }
    }

    public Move[] getDefaultMoveset(int level) {
        Move[] moveset = new Move[4];

        List<Move> moves = new ArrayList<>();
        for (Move m : this.defaultMoves) {
            if (moves.contains(m)) {
                moves.remove(m);
            }
            moves.add(m);
        }
        for (int i = 0; i <= level; i++) {
            if (this.learnedMoves.containsKey(i)) {
                for (Move m : this.learnedMoves.get(i)) {
                    if (moves.contains(m)) {
                        moves.remove(m);
                    }
                    moves.add(m);
                }
            }
        }
        if (moves.size() >= 4) {
            for (int i = 0; i < 4; i++) {
                moveset[i] = moves.get(moves.size() - 4 + i);
            }
        } else {
            int i = 0;
            while (i < moves.size()) {
                moveset[i] = moves.get(i);
                i++;
            }
            while (i < 4) {
                moveset[i] = null;
            }
        }

        return moveset;
    }

    public boolean addLearnedMove(int level, Move move) {
        if (level == 0) {
            if (!this.defaultMoves.contains(move)) {
                this.defaultMoves.add(move);
            }
        }
        if (!this.learnedMoves.containsKey(level)) {
            this.learnedMoves.put(level, new ArrayList<>());
            move.pokemon.add(this);
        }
        if (!this.learnedMoves.get(level).contains(move)) {
            this.learnedMoves.get(level).add(move);
            return true;
        } else {
            return false;
        }
    }

    public List<Move> getLearnedMoves(int level) {
        return this.learnedMoves.get(level);
    }

    public Map<Integer, List<Move>> getLearnedMoves() {
        return this.learnedMoves;
    }

    public boolean addTmMove(Move move) {
        if (!this.tmMoves.contains(move)) {
            this.tmMoves.add(move);
            move.pokemon.add(this);
            return true;
        } else {
            return false;
        }
    }

    public List<Move> getTmMoves() {
        return this.tmMoves;
    }

    public String getIndexString() {
        return getIndexString(name);
    }

    public static String getIndexString(String name) {
        return name.toUpperCase(Locale.ROOT);
    }

    @Override
    public String toString() {
//        return species + " [" + name + "]: " + hp + "," + atk + "," + def + "," + spd + "," + spc;
        return name;
    }

}
