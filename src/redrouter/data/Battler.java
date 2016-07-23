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
import java.util.List;

/**
 * TODO: cleanup!
 *
 * @author Marco Willems
 */
public class Battler {

    public final static Battler NULL = new Battler(null, 0, null);
    public final static Battler DUMMY = new Battler(new Pokemon(null, 0, "Dummy Poke", Types.Type.NORMAL, null, 100, 100, 100, 100, 100, 100), 5, null);

    private Pokemon pokemon;
    public List<Move> moveset;
//    public int totalXP = 0;
    public EncounterArea catchLocation;

    public int level;
    private int levelXP = 0;

//    private int[] statXP = new int[5]; // hpXP, atkXP, defXP, spdXP, spcXP
    private int hpXP = 0;
    private int atkXP = 0;
    private int defXP = 0;
    private int spdXP = 0;
    private int spcXP = 0;

//    private int[] DVs = new int[5]; // hp, atk, def, spd, spc
    private int hpDV = 8;
    private int atkDV = 9;
    private int defDV = 8;
    private int spdDV = 8;
    private int spcDV = 8;

    private boolean atkBadge = false;
    private boolean defBadge = false;
    private boolean spdBadge = false;
    private boolean spcBadge = false;

    /**
     * Use this constructor if it's a trainer pokemon.
     *
     * @param pokemon
     * @param level
     * @param moveset
     */
    public Battler(Pokemon pokemon, int level, List<Move> moveset) {
        this.pokemon = pokemon;
        this.level = level;
        this.moveset = moveset;
        if (this.moveset == null) {
            initDefaultMoveSet(pokemon, level);
        }
    }

    /**
     * Use this constructor if it's a caught pokemon.
     *
     * @param pokemon
     * @param catchLocation
     * @param level
     */
    public Battler(Pokemon pokemon, EncounterArea catchLocation, int level) {
        this.pokemon = pokemon;
        this.level = level;
        this.catchLocation = catchLocation;
        initDefaultMoveSet(pokemon, level);
        this.hpDV = -1;
        this.atkDV = -1;
        this.defDV = -1;
        this.spdDV = -1;
        this.spcDV = -1;
    }

    /**
     * Use this constructor if it's a caught pokemon.
     *
     * @param catchLocation
     * @param slot
     */
    public Battler(EncounterArea catchLocation, int slot) {
        this.pokemon = catchLocation.slots[slot].pkmn;
        this.level = catchLocation.slots[slot].level;
        this.catchLocation = catchLocation;
        initDefaultMoveSet(pokemon, level);
        this.hpDV = -1;
        this.atkDV = -1;
        this.defDV = -1;
        this.spdDV = -1;
        this.spcDV = -1;
    }

    private void initDefaultMoveSet(Pokemon pokemon, int level) {
        moveset = new ArrayList<>();
        // TODO
    }

    // TODO: Eevee?
    public void evolve() {
        if (this != NULL && pokemon.evolution != null) {
            pokemon = pokemon.evolution;
        }
    }

    public void addStatXP(int hp, int atk, int def, int spd, int spc, int nrOfPkmn) {

        hpXP += hp / nrOfPkmn;
        atkXP += atk / nrOfPkmn;
        defXP += def / nrOfPkmn;
        spdXP += spd / nrOfPkmn;
        spcXP += spc / nrOfPkmn;
    }

    public void resetStatXP() {
        hpXP = 0;
        atkXP = 0;
        defXP = 0;
        spdXP = 0;
        spcXP = 0;
    }

    public int getHP() {
        double extraStats = 0;
        if (hpXP - 1 >= 0) {
            extraStats = Math.floor(Math.floor((Math.sqrt(hpXP - 1) + 1)) / 4);
        }
        double statValue = Math.floor((((pokemon.hp + hpDV + 50) * 2 + extraStats) * level / 100) + 10);
//        double statValue = Math.floor((((pokemon.hp + DVs[0] + 50) * 2 + extraStats) * level / 100) + 10);
        return (int) statValue;
    }

    public int getAtk(boolean withBoosts) {
        return getStat(pokemon.atk, atkDV, atkXP, withBoosts);
    }

    public int getDef(boolean withBoosts) {
        return getStat(pokemon.def, defDV, defXP, withBoosts);
    }

    public int getSpd(boolean withBoosts) {
        return getStat(pokemon.spd, spdDV, spdXP, withBoosts);
    }

    public int getSpc(boolean withBoosts) {
        return getStat(pokemon.spc, spcDV, spcXP, withBoosts);
    }

    public int getHPStatIfDV(int DV) {
        double extraStats = 0;
        if (hpXP - 1 >= 0) {
            extraStats = Math.floor(Math.floor((Math.sqrt(hpXP - 1) + 1)) / 4);
        }
        double statValue = Math.floor((((pokemon.hp + DV + 50) * 2 + extraStats) * level / 100) + 10);
        return (int) statValue;
    }

    public int getAtkStatIfDV(int DV) {
        return getStat(pokemon.atk, DV, atkXP, false);
    }

    public int getDefStatIfDV(int DV) {
        return getStat(pokemon.def, DV, defXP, false);
    }

    public int getSpdStatIfDV(int DV) {
        return getStat(pokemon.spd, DV, spdXP, false);
    }

    public int getSpcStatIfDV(int DV) {
        return getStat(pokemon.spc, DV, spcXP, false);
    }

    // TODO: calculation with badge boosts
    private int getStat(int base, int DV, int XP, boolean withBoosts) {
        double extraStats = 0;
        if (XP - 1 >= 0) {
            extraStats = Math.floor(Math.floor((Math.sqrt(XP - 1) + 1)) / 4);
        }
        double statValue = Math.floor((((base + DV) * 2 + extraStats) * level / 100) + 5);
        return (int) statValue;
    }

    public Pokemon getPokemon() {
        return this.pokemon;
    }

    public boolean isType(Types.Type type) {
        return (type == pokemon.type1 || (pokemon.type2 != null && type == pokemon.type2));
    }

    public void setDVs(int hp, int atk, int def, int spd, int spc) {
        hpDV = hp;
        atkDV = atk;
        defDV = def;
        spdDV = spd;
        spcDV = spc;
    }

    @Override
    public String toString() {
        String battler = (this == NULL ? "-----" : pokemon.name + " Lv." + level);
//        String moves = "";
//        if (moveset.size() > 0) {
//            for (Move m : moveset) {
//                moves += "," + m.NAME;
//            }
//            moves = moves.substring(1);
//        }
//        battler += " (" + moves + ")";

        return battler;
    }

}
