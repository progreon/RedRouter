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

import be.marcowillems.redrouter.util.DVRange;
import be.marcowillems.redrouter.util.Range;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * TODO: cleanup!
 *
 * @author Marco Willems
 */
public class BattlerImpl extends Battler {

//    private int[] statXP = new int[5]; // hpXP, atkXP, defXP, spdXP, spcXP
    private int hpXP = 0;
    private int atkXP = 0;
    private int defXP = 0;
    private int spdXP = 0;
    private int spcXP = 0;
    // The current value of the stats (updated after leveling or using vitamins)
    private final Range[] currentStats = new Range[5];

    // TODO: DV-range & interacting with DVCalculator?
    public boolean[][] possibleDVs; // [hp, atk, def, spd, spc][0..15] -> true/false
    private static int[] trainerDVs = new int[]{8, 9, 8, 8, 8};

    private static final int DELTA_STATEXP = 2560;
    private static final int MAX_STATEXP = 25600;
    private final int[] multipliers = new int[]{25, 28, 33, 40, 50, 66, 1, 15, 2, 25, 3, 35, 4};
    private final int[] divisors = new int[]{100, 100, 100, 100, 100, 100, 1, 10, 1, 10, 1, 10, 1};

    /**
     * Use this constructor if it's a trainer pokemon.
     *
     * @param rd
     * @param pokemon
     * @param level
     * @param moveset
     */
    public BattlerImpl(RouterData rd, Pokemon pokemon, int level, Move[] moveset) {
        super(rd, pokemon, null, true, level);
        this.moveset = moveset;
        if (this.moveset == null) {
            initDefaultMoveSet(pokemon, level);
        }
        initPossibleDVs();
        updateCurrentStats();
    }

    /**
     * Use this constructor if it's a caught pokemon, or a given one.
     *
     * @param rd
     * @param pokemon
     * @param catchLocation null if pokemon was a given one
     * @param level
     */
    public BattlerImpl(RouterData rd, Pokemon pokemon, EncounterArea catchLocation, int level) {
        super(rd, pokemon, catchLocation, false, level);
        initDefaultMoveSet(pokemon, level);
        initPossibleDVs();
        updateCurrentStats();
    }

    /**
     * Use this constructor if it's a caught pokemon.
     *
     * @param rd
     * @param catchLocation
     * @param slot
     */
    public BattlerImpl(RouterData rd, EncounterArea catchLocation, int slot) {
        super(rd, catchLocation.slots[slot].pkmn, catchLocation, false, catchLocation.slots[slot].level);
        initDefaultMoveSet(pokemon, level);
        initPossibleDVs();
        updateCurrentStats();
    }

    /**
     * Use this constructor if it's a RNG manip'd pokemon
     *
     * @param rd
     * @param catchLocation
     * @param pokemon
     * @param level
     * @param atkDV
     * @param defDV
     * @param spdDV
     * @param spcDV
     */
    public BattlerImpl(RouterData rd, EncounterArea catchLocation, Pokemon pokemon, int level, int atkDV, int defDV, int spdDV, int spcDV) {
        super(rd, pokemon, catchLocation, false, level);
        initDefaultMoveSet(pokemon, level);
        initPossibleDVs(atkDV, defDV, spdDV, spcDV);
        updateCurrentStats();
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
        BattlerImpl newBattler = new BattlerImpl(rd, pokemon, catchLocation, level);

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
        for (int i = 0; i < this.currentStats.length; i++) {
            newBattler.currentStats[i] = new Range(this.currentStats[i]);
        }

        return newBattler;
    }

    @Override
    public Battler defeatBattler(Battler b, int participants) {
        addStatXP(b.pokemon.hp, b.pokemon.atk, b.pokemon.def, b.pokemon.spd, b.pokemon.spc, participants);
        return addXP(b.getExp(participants));
    }

    @Override
    protected Battler evolve(Evolution.Key key) {
        if (pokemon.evolution != null && pokemon.evolution.get(key) != null) {
            BattlerImpl evo = new BattlerImpl(rd, pokemon.evolution.get(key), catchLocation, level);
            // TODO: evolution moves?
            evo.moveset = moveset;
            evo.possibleDVs = possibleDVs;
            evo.hpXP = hpXP;
            evo.atkXP = atkXP;
            evo.defXP = defXP;
            evo.spdXP = spdXP;
            evo.spcXP = spcXP;
            evo.levelExp = levelExp;
            evo.updateCurrentStats();
            return evo;
        } else {
            return null;
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

    @Override
    public Battler addXP(int exp) {
        levelExp += exp;
        int totExp = pokemon.expGroup.getTotalExp(level, levelExp);
        int newLevel = pokemon.expGroup.getLevel(totExp);
        if (level != newLevel) {
            levelExp -= pokemon.expGroup.getDeltaExp(level, newLevel);
            level = newLevel;
            updateCurrentStats(); // Handle it the RBY way
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
                while (i < newMoves.size()) {
                    // TODO check what move to override -> make Battler settings?
                    Move oldMove = rd.getMoveReplaced(pokemon, newMoves.get(i));
                    int oldIdx = 0;
                    boolean found = false;
                    while (!found && oldIdx < moveset.length) {
                        if (oldMove == moveset[oldIdx]) {
                            found = true;
                        } else {
                            oldIdx++;
                        }
                    }
                    if (found) {
                        moveset[oldIdx] = newMoves.get(i);
                    }
                    i++;
                }
            }
        }
        Battler evolution = evolve(new Evolution.Level(level));
        if (evolution != null) {
            return evolution;
        } else {
            return this;
        }
    }

    @Override
    public boolean useHPUp(int count) {
        boolean success = true;
        for (int i = 0; i < count; i++) {
            if (hpXP < MAX_STATEXP) {
                hpXP = Math.min(hpXP + DELTA_STATEXP, MAX_STATEXP);
            } else {
                success = false;
            }
        }
        return success;
    }

    @Override
    public boolean useProtein(int count) {
        boolean success = true;
        for (int i = 0; i < count; i++) {
            if (atkXP < MAX_STATEXP) {
                atkXP = Math.min(atkXP + DELTA_STATEXP, MAX_STATEXP);
            } else {
                success = false;
            }
        }
        return success;
    }

    @Override
    public boolean useIron(int count) {
        boolean success = true;
        for (int i = 0; i < count; i++) {
            if (defXP < MAX_STATEXP) {
                defXP = Math.min(defXP + DELTA_STATEXP, MAX_STATEXP);
            } else {
                success = false;
            }
        }
        return success;
    }

    @Override
    public boolean useCarbos(int count) {
        boolean success = true;
        for (int i = 0; i < count; i++) {
            if (spdXP < MAX_STATEXP) {
                spdXP = Math.min(spdXP + DELTA_STATEXP, MAX_STATEXP);
            } else {
                success = false;
            }
        }
        return success;
    }

    @Override
    public boolean useCalcium(int count) {
        boolean success = true;
        for (int i = 0; i < count; i++) {
            if (spcXP < MAX_STATEXP) {
                spcXP = Math.min(spcXP + DELTA_STATEXP, MAX_STATEXP);
            } else {
                success = false;
            }
        }
        return success;
    }

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

    public DVRange[] getDVRanges() {
        DVRange[] ranges = new DVRange[5];
        for (int s = 0; s < 5; s++) {
            ranges[s] = getDVRange(s);
        }
        return ranges;
    }

    private void updateCurrentStats() {
        currentStats[0] = calculateHP();
        currentStats[1] = calculateAtk();
        currentStats[2] = calculateDef();
        currentStats[3] = calculateSpd();
        currentStats[4] = calculateSpc();
    }

    private Range calculateHP() {
        DVRange dvRange = getDVRange(0);
        double extraStats = 0;
        if (hpXP - 1 >= 0) {
            extraStats = Math.floor(Math.floor((Math.sqrt(hpXP - 1) + 1)) / 4);
        }
        double minStatValue = Math.floor((((pokemon.hp + dvRange.getMin() + 50) * 2 + extraStats) * level / 100) + 10);
        double maxStatValue = Math.floor((((pokemon.hp + dvRange.getMax() + 50) * 2 + extraStats) * level / 100) + 10);
        return new Range((int) minStatValue, (int) maxStatValue);
    }

    private Range calculateAtk() {
        DVRange dvRange = getDVRange(1);
        int min = calculateStat(level, pokemon.atk, dvRange.getMin(), atkXP);
        int max = calculateStat(level, pokemon.atk, dvRange.getMax(), atkXP);
        return new Range(min, max);
    }

    private Range calculateDef() {
        DVRange dvRange = getDVRange(2);
        int min = calculateStat(level, pokemon.def, dvRange.getMin(), defXP);
        int max = calculateStat(level, pokemon.def, dvRange.getMax(), defXP);
        return new Range(min, max);
    }

    private Range calculateSpd() {
        DVRange dvRange = getDVRange(3);
        int min = calculateStat(level, pokemon.spd, dvRange.getMin(), spdXP);
        int max = calculateStat(level, pokemon.spd, dvRange.getMax(), spdXP);
        return new Range(min, max);
    }

    private Range calculateSpc() {
        DVRange dvRange = getDVRange(4);
        int min = calculateStat(level, pokemon.spc, dvRange.getMin(), spcXP);
        int max = calculateStat(level, pokemon.spc, dvRange.getMax(), spcXP);
        return new Range(min, max);
    }

    private int calculateStat(int level, int base, int DV, int XP) {
        double extraStats = 0;
        if (XP - 1 >= 0) {
            extraStats = Math.floor(Math.floor(Math.sqrt(XP - 1) + 1) / 4);
        }
        double statValue = Math.floor((((base + DV) * 2 + extraStats) * level / 100) + 5);
        return (int) statValue;
    }

    @Override
    public Range getHP() {
        return currentStats[0];
    }

    @Override
    public Range getAtk() {
        return currentStats[1];
    }

    @Override
    public Range getDef() {
        return currentStats[2];
    }

    @Override
    public Range getSpd() {
        return currentStats[3];
    }

    @Override
    public Range getSpc() {
        return currentStats[4];
    }

    @Override
    protected Range getBoostedStat(Range statRange, int badgeBoostCount, int xItemCount) {
        Range boostedRange = new Range(statRange);
        boostedRange = boostedRange.multiplyBy(multipliers[xItemCount + 6]).divideBy(divisors[xItemCount + 6]);
        for (int bb = 0; bb < badgeBoostCount; bb++) {
            boostedRange = boostedRange.multiplyBy(9).divideBy(8);
        }
        return boostedRange;
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
        int stat = calculateStat(level, pokemon.atk, DV, atkXP);
        return stat;
    }

    public int getDefStatIfDV(int DV) {
        int stat = calculateStat(level, pokemon.def, DV, defXP);
        return stat;
    }

    public int getSpdStatIfDV(int DV) {
        int stat = calculateStat(level, pokemon.spd, DV, spdXP);
        return stat;
    }

    public int getSpcStatIfDV(int DV) {
        int stat = calculateStat(level, pokemon.spc, DV, spcXP);
        return stat;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BattlerImpl) {
            BattlerImpl b = (BattlerImpl) obj;
            return rd == b.rd
                    && pokemon == b.pokemon
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
