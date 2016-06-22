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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Marco Willems
 */
public class Pokemon {

    public static class Gender {

        public static final String MALE = "M";
        public static final String FEMALE = "F";
        public static final String NONE = "NA";
        public static final String MMMF = "7/1";
        public static final String MMF = "3/1";
        public static final String MF = "1/1";
        public static final String MFF = "1/3";
        public static final String MFFF = "1/7";

        public static final String[] list = {MALE, FEMALE, NONE, MMMF, MMF, MF, MFF, MFFF};

    }

    // TODO: growth rate
    public final int ID;
    // TODO: Eevee?
    public Pokemon evolution = null;
    public final String name;
    public final Types.Type type1;
    public final Types.Type type2;
    public final String genderRatio;
    public final int expGiven;
    public final int hp;
    public final int atk;
    public final int def;
    public final int spd;
    public final int spc;

    private static final Map<String, Pokemon> pokemonByName = new HashMap<>();
    private static final Map<Integer, Pokemon> pokemonByID = new HashMap<>();

    private final Map<Integer, List<Move>> learnedMoves = new HashMap<>();
    private final List<Move> tmMoves = new ArrayList<>();

    private Pokemon(int ID, String name, Types.Type type1, Types.Type type2, String genderRatio, int expGiven, int hp, int atk, int def, int spd, int spc) {
        this.ID = ID;
        this.name = name;
        this.type1 = type1;
        this.type2 = type2;
        this.genderRatio = (genderRatio == null ? Gender.NONE : genderRatio);
        this.expGiven = expGiven;
        this.hp = hp;
        this.atk = atk;
        this.def = def;
        this.spd = spd;
        this.spc = spc;
    }

    public static Pokemon add(int ID, String name, Types.Type type1, Types.Type type2, String genderRatio, int expGiven, int hp, int atk, int def, int spd, int spc) {
        if (!pokemonByName.containsKey(toString(name).toUpperCase(Locale.ROOT)) && !pokemonByID.containsKey(ID)) {
            Pokemon pkmn = new Pokemon(ID, name, type1, type2, genderRatio, expGiven, hp, atk, def, spd, spc);
            pokemonByName.put(toString(name).toUpperCase(Locale.ROOT), pkmn);
            pokemonByID.put(ID, pkmn);
            return pkmn;
        } else {
            return null;
        }
    }

    public static Pokemon get(String name) {
        return pokemonByName.get(toString(name).toUpperCase(Locale.ROOT));
    }

    public static Pokemon get(int ID) {
        return pokemonByID.get(ID);
    }

    public static Pokemon[] getAll() {
        return pokemonByID.values().toArray(new Pokemon[0]);
    }

    public static String[] getNames() {
        return pokemonByName.keySet().toArray(new String[0]);
    }

    public boolean addLearnedMove(int level, Move move) {
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

    private static String toString(String name) {
        return name;
    }

    @Override
    public String toString() {
//        return species + " [" + name + "]: " + hp + "," + atk + "," + def + "," + spd + "," + spc;
        return toString(this.name);
    }

    public static void initPokemon(String pokemonFile) {
        BufferedReader br = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream(pokemonFile)));
        int lineNr = 0;
        int pokedexEntry = 0;
        try {
            String line;
            line = br.readLine();
            while (line != null) {
                lineNr++;
                if (line.equals("") || line.substring(0, 2).equals("//")) {
                    //nothing to do here
                } else {
                    String[] s = line.split("#");
                    // TODO: Types
                    // TODO: Gender
                    Pokemon poke = Pokemon.add(pokedexEntry, s[0], Types.Type.NORMAL, null, Pokemon.Gender.MF, Integer.parseInt(s[4]), Integer.parseInt(s[5]), Integer.parseInt(s[6]), Integer.parseInt(s[7]), Integer.parseInt(s[8]), Integer.parseInt(s[9]));
                    pokedexEntry++;
                    System.out.println(pokedexEntry + " - " + poke.toString());
                }
                line = br.readLine();
            }
        } catch (IOException ex) {
            Logger.getLogger(RouteFactory.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Syntax error in pokemon.txt on line: " + lineNr);
        } finally {
            try {
                br.close();
            } catch (IOException ex) {
                Logger.getLogger(RouteFactory.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
