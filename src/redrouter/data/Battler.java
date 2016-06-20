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
 *
 * @author Marco Willems
 */
public class Battler {

    private Pokemon pokemon;
    public int level;
    public Pokemon.Gender gender;
    public List<Move> moveset;
//    public int totalXP = 0;

    public int hpXP = 0;
    public int atkXP = 0;
    public int defXP = 0;
    public int spdXP = 0;
    public int spcXP = 0;

    public int hpDV = 8;
    public int atkDV = 9;
    public int defDV = 8;
    public int spdDV = 8;
    public int spcDV = 8;

    public boolean atkBadge = false;
    public boolean defBadge = false;
    public boolean spdBadge = false;
    public boolean spcBadge = false;

    public Battler(Pokemon pokemon, int level, List<Move> moveset) {
        this.pokemon = pokemon;
        this.level = level;
        this.moveset = moveset;
        if (this.moveset == null) {
            this.moveset = new ArrayList<>();
        }
    }

    public void evolve() {
        if (pokemon.evolution != null) {
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
        hpDV += hp;
        atkDV += atk;
        defDV += def;
        spdDV += spd;
        spcDV += spc;
    }

    @Override
    public String toString() {
        String battler = pokemon.name + " Lv." + level;
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
