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
    public final static Battler DUMMY = new Battler(new Pokemon(null, 0, "Dummy Poke", Types.Type.NORMAL, null, 100, 100, 100, 100, 100, 100), null, 5);

    private Pokemon pokemon;
    public List<Move> moveset;
    public EncounterArea catchLocation;

    public int level;
    private int levelXP = 0;
//    public int totalXP = 0;

//    private int[] statXP = new int[5]; // hpXP, atkXP, defXP, spdXP, spcXP
    private int hpXP = 0;
    private int atkXP = 0;
    private int defXP = 0;
    private int spdXP = 0;
    private int spcXP = 0;

    // TODO: DV-range & interacting with DVCalculator?
    public boolean[][] possibleDVs; // [hp, atk, def, spd, spc][0..15] -> true/false

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
        initPossibleDVs(true);
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
        initPossibleDVs(false);
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
        initPossibleDVs(false);
    }

    private void initPossibleDVs(boolean isTrainerPokemon) {
        this.possibleDVs = new boolean[5][16];
        if (isTrainerPokemon) {
            this.possibleDVs[0][8] = true;
            this.possibleDVs[1][9] = true;
            this.possibleDVs[2][8] = true;
            this.possibleDVs[3][8] = true;
            this.possibleDVs[4][8] = true;
        } else {
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 16; j++) {
                    this.possibleDVs[i][j] = true;
                }
            }
        }
    }

    private void initDefaultMoveSet(Pokemon pokemon, int level) {
        moveset = new ArrayList<>();
        // TODO
    }

    // TODO: evolve condition (item, ...)
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

    public DVRange getDVRange(int stat) {
        DVRange range = new DVRange();
        for (int DV = 0; DV < 16; DV++) {
            if (possibleDVs[stat][DV]) {
                range.add(DV);
            }
        }
        return range;
    }

    public DVRange[] getDVRanges() {
        DVRange[] ranges = new DVRange[5];

        for (int s = 0; s < 5; s++) {
            ranges[s] = new DVRange();
            for (int DV = 0; DV < 16; DV++) {
                if (possibleDVs[s][DV]) {
                    ranges[s].add(DV);
                }
            }
        }

        return ranges;
    }

    public StatRange getHP() {
        DVRange dvRange = getDVRange(0);
        double extraStats = 0;
        if (hpXP - 1 >= 0) {
            extraStats = Math.floor(Math.floor((Math.sqrt(hpXP - 1) + 1)) / 4);
        }
        double minStatValue = Math.floor((((pokemon.hp + dvRange.getMin() + 50) * 2 + extraStats) * level / 100) + 10);
        double maxStatValue = Math.floor((((pokemon.hp + dvRange.getMax() + 50) * 2 + extraStats) * level / 100) + 10);
        return new StatRange((int) minStatValue, (int) maxStatValue);
    }

    public StatRange getAtk(int badgeBoosts, int stage) {
        DVRange dvRange = getDVRange(1);
        int min = getStat(pokemon.atk, dvRange.getMin(), atkXP, badgeBoosts, stage);
        int max = getStat(pokemon.atk, dvRange.getMax(), atkXP, badgeBoosts, stage);
        return new StatRange(min, max);
    }

    public StatRange getDef(int badgeBoosts, int stage) {
        DVRange dvRange = getDVRange(2);
        int min = getStat(pokemon.def, dvRange.getMin(), defXP, badgeBoosts, stage);
        int max = getStat(pokemon.def, dvRange.getMax(), defXP, badgeBoosts, stage);
        return new StatRange(min, max);
    }

    public StatRange getSpd(int badgeBoosts, int stage) {
        DVRange dvRange = getDVRange(3);
        int min = getStat(pokemon.spd, dvRange.getMin(), spdXP, badgeBoosts, stage);
        int max = getStat(pokemon.spd, dvRange.getMax(), spdXP, badgeBoosts, stage);
        return new StatRange(min, max);
    }

    public StatRange getSpc(int badgeBoosts, int stage) {
        DVRange dvRange = getDVRange(4);
        int min = getStat(pokemon.spc, dvRange.getMin(), spcXP, badgeBoosts, stage);
        int max = getStat(pokemon.spc, dvRange.getMax(), spcXP, badgeBoosts, stage);
        return new StatRange(min, max);
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
        return getStat(pokemon.atk, DV, atkXP, 0, 0);
    }

    public int getDefStatIfDV(int DV) {
        return getStat(pokemon.def, DV, defXP, 0, 0);
    }

    public int getSpdStatIfDV(int DV) {
        return getStat(pokemon.spd, DV, spdXP, 0, 0);
    }

    public int getSpcStatIfDV(int DV) {
        return getStat(pokemon.spc, DV, spcXP, 0, 0);
    }

    // TODO: calculation with badge boosts & stages
    private int getStat(int base, int DV, int XP, int badgeBoosts, int stage) {
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

    public class DVRange {

        private final List<Integer> dvs = new ArrayList<>();

        public void add(int dv) {
            dvs.add(dv);
        }

        private Integer getMin() {
            int min = 15;
            for (Integer dv : dvs) {
                if (dv < min) {
                    min = dv;
                }
            }
            return min;
        }

        private Integer getMax() {
            int max = 0;
            for (Integer dv : dvs) {
                if (dv > max) {
                    max = dv;
                }
            }
            return max;
        }

        @Override
        public String toString() {
            if (dvs.size() == 1) {
                return dvs.get(0).toString();
            } else if (dvs.size() == 2) {
                return getMin() + "/" + getMax();
            } else {
                return getMin() + "-" + getMax();
            }
        }
    }

    public class StatRange {

        int min, max;

        public StatRange(int min, int max) {
            this.min = min;
            this.max = max;
        }

        @Override
        public String toString() {
            if (min == max) {
                return min + "";
            } else {
                return min + "-" + max;
            }
        }
    }
}
