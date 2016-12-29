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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import be.marcowillems.redrouter.util.DVRange;
import be.marcowillems.redrouter.util.Range;

/**
 * TODO: cleanup!
 *
 * @author Marco Willems
 */
public class SingleBattler extends Battler {

    public Move[] moveset;

    public int level;
    private int levelExp = 0;
//    public int totalXP = 0;

//    private int[] statXP = new int[5]; // hpXP, atkXP, defXP, spdXP, spcXP
    private int hpXP = 0;
    private int atkXP = 0;
    private int defXP = 0;
    private int spdXP = 0;
    private int spcXP = 0;

    // TODO: DV-range & interacting with DVCalculator?
    public boolean[][] possibleDVs; // [hp, atk, def, spd, spc][0..15] -> true/false
    private final boolean isTrainerMon; // TODO do this more properly?
    private static int[] trainerDVs = new int[]{8, 9, 8, 8, 8};

    /**
     * Use this constructor if it's a trainer pokemon.
     *
     * @param pokemon
     * @param level
     * @param moveset
     */
    public SingleBattler(Pokemon pokemon, int level, Move[] moveset) {
        super(pokemon, null);
        this.level = level;
        this.moveset = moveset;
        this.isTrainerMon = true;
        if (this.moveset == null) {
            initDefaultMoveSet(pokemon, level);
        }
        initPossibleDVs();
    }

    /**
     * Use this constructor if it's a caught pokemon, or a given one.
     *
     * @param pokemon
     * @param catchLocation null if pokemon was a given one
     * @param level
     */
    public SingleBattler(Pokemon pokemon, EncounterArea catchLocation, int level) {
        super(pokemon, catchLocation);
        this.level = level;
        this.isTrainerMon = false;
        initDefaultMoveSet(pokemon, level);
        initPossibleDVs();
    }

    /**
     * Use this constructor if it's a caught pokemon.
     *
     * @param catchLocation
     * @param slot
     */
    public SingleBattler(EncounterArea catchLocation, int slot) {
        super(catchLocation.slots[slot].pkmn, catchLocation);
        this.level = catchLocation.slots[slot].level;
        this.isTrainerMon = true;
        initDefaultMoveSet(pokemon, level);
        initPossibleDVs();
    }

    /**
     * Use this constructor if it's a RNG manip'd pokemon
     *
     * @param pokemon
     * @param level
     * @param atkDV
     * @param defDV
     * @param spdDV
     * @param spcDV
     */
    public SingleBattler(Pokemon pokemon, int level, int atkDV, int defDV, int spdDV, int spcDV) {
        super(pokemon, null);
        this.level = level;
        this.isTrainerMon = false;
        initDefaultMoveSet(pokemon, level);
        initPossibleDVs(atkDV, defDV, spdDV, spcDV);
    }

    private void initPossibleDVs() {
//        if (isTrainerMon) {
//            initPossibleDVs(9, 8, 8, 8);
//        } else {
        this.possibleDVs = new boolean[5][16];
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 16; j++) {
                this.possibleDVs[i][j] = true;
            }
        }
//        }
    }

    private void initPossibleDVs(int atkDV, int defDV, int spdDV, int spcDV) {
        this.possibleDVs = new boolean[5][16];
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 16; j++) {
                this.possibleDVs[i][j] = false;
            }
        }
        int hpDV = (atkDV % 2) * 8 + (defDV % 2) * 4 + (spdDV % 2) * 2 + (spcDV % 2);
        this.possibleDVs[0][hpDV] = true;
        this.possibleDVs[1][atkDV] = true;
        this.possibleDVs[2][defDV] = true;
        this.possibleDVs[3][spdDV] = true;
        this.possibleDVs[4][spcDV] = true;
    }

    private void initDefaultMoveSet(Pokemon pokemon, int level) {
        moveset = pokemon.getDefaultMoveset(level);
    }

    @Override
    public Battler getDeepCopy() {
        SingleBattler newBattler = new SingleBattler(pokemon, catchLocation, level);

        newBattler.moveset = this.moveset.clone();
        newBattler.levelExp = this.levelExp;
//        newBattler.totalXP = this.totalXP;

        newBattler.hpXP = this.hpXP;
        newBattler.atkXP = this.atkXP;
        newBattler.defXP = this.defXP;
        newBattler.spdXP = this.spdXP;
        newBattler.spcXP = this.spcXP;

        newBattler.possibleDVs = new boolean[this.possibleDVs.length][];
        for (int i = 0; i < this.possibleDVs.length; i++) {
            newBattler.possibleDVs[i] = this.possibleDVs[i].clone();
        }

        return newBattler;
    }

    // TODO: evolve condition (item, ...)
    @Override
    public Battler evolve(Item item) {
        if (pokemon.evolution != null) {
            SingleBattler evo = new SingleBattler(pokemon.evolution, catchLocation, level);
            evo.moveset = moveset;
            evo.possibleDVs = possibleDVs;
            evo.hpXP = hpXP;
            evo.atkXP = atkXP;
            evo.defXP = defXP;
            evo.spdXP = spdXP;
            evo.spcXP = spcXP;
            evo.levelExp = levelExp;
            return evo;
        } else {
            return null;
        }
    }

    @Override
    public void addStatXP(int hp, int atk, int def, int spd, int spc, int nrOfPkmn) {
        hpXP += hp / nrOfPkmn;
        atkXP += atk / nrOfPkmn;
        defXP += def / nrOfPkmn;
        spdXP += spd / nrOfPkmn;
        spcXP += spc / nrOfPkmn;
    }

    @Override
    public void resetStatXP() {
        hpXP = 0;
        atkXP = 0;
        defXP = 0;
        spdXP = 0;
        spcXP = 0;
    }

    @Override
    public boolean addXP(int exp) {
        levelExp += exp;
        int totExp = pokemon.expGroup.getTotalExp(level, levelExp);
        int newLevel = pokemon.expGroup.getLevel(totExp);
        if (level != newLevel) {
            levelExp -= pokemon.expGroup.getDeltaExp(level, newLevel);
            level = newLevel;
            List<Move> newMoves = pokemon.getLearnedMoves(level); // Handle it the RBY way
            if (newMoves != null) {
                int numCurMoves = 0;
                while (numCurMoves < moveset.length && moveset[numCurMoves] != null) {
                    numCurMoves++;
                }
                int i = 0;
                while (numCurMoves + i < moveset.length && i < newMoves.size()) {
                    moveset[numCurMoves + i] = newMoves.get(i);
                    i++;
                }
                if (i < newMoves.size()) {
                    // TODO check what move to override -> make Battler settings?
                }
            }
        }
        return false; // TODO: return true if evolving
    }

    @Override
    public boolean checkEvolve() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Move> getMoveset() {
        List<Move> moves = new ArrayList<>();
        for (Move m : moveset) {
            if (m != null) {
                moves.add(m);
            }
        }
        return moves;
    }

    @Override
    public int getLevel() {
        return this.level;
    }

    @Override
    public DVRange getDVRange(int stat) {
        DVRange range = new DVRange();
        if (isTrainerMon) {
            range.add(trainerDVs[stat]);
        } else {
            for (int DV = 0; DV < 16; DV++) {
                if (possibleDVs[stat][DV]) {
                    range.add(DV);
                }
            }
        }
        return range;
    }

    @Override
    public Range getHP() {
        DVRange dvRange = getDVRange(0);
        double extraStats = 0;
        if (hpXP - 1 >= 0) {
            extraStats = Math.floor(Math.floor((Math.sqrt(hpXP - 1) + 1)) / 4);
        }
        double minStatValue = Math.floor((((pokemon.hp + dvRange.getMin() + 50) * 2 + extraStats) * level / 100) + 10);
        double maxStatValue = Math.floor((((pokemon.hp + dvRange.getMax() + 50) * 2 + extraStats) * level / 100) + 10);
        return new Range((int) minStatValue, (int) maxStatValue);
    }

    @Override
    public Range getAtk(int badgeBoosts, int stage) {
        DVRange dvRange = getDVRange(1);
        int min = getStat(level, pokemon.atk, dvRange.getMin(), atkXP, badgeBoosts, stage);
        int max = getStat(level, pokemon.atk, dvRange.getMax(), atkXP, badgeBoosts, stage);
        return new Range(min, max);
    }

    @Override
    public Range getDef(int badgeBoosts, int stage) {
        DVRange dvRange = getDVRange(2);
        int min = getStat(level, pokemon.def, dvRange.getMin(), defXP, badgeBoosts, stage);
        int max = getStat(level, pokemon.def, dvRange.getMax(), defXP, badgeBoosts, stage);
        return new Range(min, max);
    }

    @Override
    public Range getSpd(int badgeBoosts, int stage) {
        DVRange dvRange = getDVRange(3);
        int min = getStat(level, pokemon.spd, dvRange.getMin(), spdXP, badgeBoosts, stage);
        int max = getStat(level, pokemon.spd, dvRange.getMax(), spdXP, badgeBoosts, stage);
        return new Range(min, max);
    }

    @Override
    public Range getSpc(int badgeBoosts, int stage) {
        DVRange dvRange = getDVRange(4);
        int min = getStat(level, pokemon.spc, dvRange.getMin(), spcXP, badgeBoosts, stage);
        int max = getStat(level, pokemon.spc, dvRange.getMax(), spcXP, badgeBoosts, stage);
        return new Range(min, max);
    }

    @Override
    public Range getHPStatIfDV(int DV) {
        double extraStats = 0;
        if (hpXP - 1 >= 0) {
            extraStats = Math.floor(Math.floor((Math.sqrt(hpXP - 1) + 1)) / 4);
        }
        double statValue = Math.floor((((pokemon.hp + DV + 50) * 2 + extraStats) * level / 100) + 10);
        return new Range((int) statValue, (int) statValue);
    }

    @Override
    public Range getAtkStatIfDV(int DV) {
        int stat = getStat(level, pokemon.atk, DV, atkXP, 0, 0);
        return new Range(stat, stat);
    }

    @Override
    public Range getDefStatIfDV(int DV) {
        int stat = getStat(level, pokemon.def, DV, defXP, 0, 0);
        return new Range(stat, stat);
    }

    @Override
    public Range getSpdStatIfDV(int DV) {
        int stat = getStat(level, pokemon.spd, DV, spdXP, 0, 0);
        return new Range(stat, stat);
    }

    @Override
    public Range getSpcStatIfDV(int DV) {
        int stat = getStat(level, pokemon.spc, DV, spcXP, 0, 0);
        return new Range(stat, stat);
    }

    @Override
    public Range getExp(int participants) {
        int exp = pokemon.getExp(level, participants, false, catchLocation == null);
        return new Range(exp, exp);
    }

    @Override
    public Range getLevelExp() {
        return new Range(levelExp, levelExp);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SingleBattler) {
            SingleBattler b = (SingleBattler) obj;
            return pokemon == b.pokemon
                    && Arrays.equals(moveset, b.moveset)
                    && catchLocation == b.catchLocation
                    && level == b.level
                    && levelExp == b.levelExp
                    && hpXP == b.hpXP
                    && atkXP == b.atkXP
                    && defXP == b.defXP
                    && spdXP == b.spdXP
                    && spcXP == b.spcXP
                    && Arrays.deepEquals(possibleDVs, b.possibleDVs);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.pokemon);
        hash = 89 * hash + Arrays.deepHashCode(this.moveset);
        hash = 89 * hash + Objects.hashCode(this.catchLocation);
        hash = 89 * hash + this.level;
        hash = 89 * hash + this.levelExp;
        hash = 89 * hash + this.hpXP;
        hash = 89 * hash + this.atkXP;
        hash = 89 * hash + this.defXP;
        hash = 89 * hash + this.spdXP;
        hash = 89 * hash + this.spcXP;
        hash = 89 * hash + Arrays.deepHashCode(this.possibleDVs);
        return hash;
    }

}
