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

    public Move[] moveset; // TODO: private or final?

    public int level;
    private int levelExp = 0;
//    public int totalXP = 0;

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
    private final boolean isTrainerMon; // TODO do this more properly?
    private static int[] trainerDVs = new int[]{8, 9, 8, 8, 8};

    /**
     * Use this constructor if it's a trainer pokemon.
     *
     * @param pokemon
     * @param level
     * @param moveset
     */
    public SingleBattler(RouterData rd, Pokemon pokemon, int level, Move[] moveset) {
        super(rd, pokemon, null);
        this.level = level;
        this.moveset = moveset;
        this.isTrainerMon = true;
        if (this.moveset == null) {
            initDefaultMoveSet(pokemon, level);
        }
        initPossibleDVs();
        updateCurrentStats();
    }

    /**
     * Use this constructor if it's a caught pokemon, or a given one.
     *
     * @param pokemon
     * @param catchLocation null if pokemon was a given one
     * @param level
     */
    public SingleBattler(RouterData rd, Pokemon pokemon, EncounterArea catchLocation, int level) {
        super(rd, pokemon, catchLocation);
        this.level = level;
        this.isTrainerMon = false;
        initDefaultMoveSet(pokemon, level);
        initPossibleDVs();
        updateCurrentStats();
    }

    /**
     * Use this constructor if it's a caught pokemon.
     *
     * @param catchLocation
     * @param slot
     */
    public SingleBattler(RouterData rd, EncounterArea catchLocation, int slot) {
        super(rd, catchLocation.slots[slot].pkmn, catchLocation);
        this.level = catchLocation.slots[slot].level;
        this.isTrainerMon = true;
        initDefaultMoveSet(pokemon, level);
        initPossibleDVs();
        updateCurrentStats();
    }

    /**
     * Use this constructor if it's a RNG manip'd pokemon
     *
     * @param catchLocation
     * @param pokemon
     * @param level
     * @param atkDV
     * @param defDV
     * @param spdDV
     * @param spcDV
     */
    public SingleBattler(RouterData rd, EncounterArea catchLocation, Pokemon pokemon, int level, int atkDV, int defDV, int spdDV, int spcDV) {
        super(rd, pokemon, catchLocation);
        this.level = level;
        this.isTrainerMon = false;
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
        SingleBattler newBattler = new SingleBattler(rd, pokemon, catchLocation, level);

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
    public SingleBattler evolve(Item item) {
        return evolve(new Evolution.Item(item));
    }

    private SingleBattler evolve(Evolution.Key key) {
        if (pokemon.evolution != null && pokemon.evolution.get(key) != null) {
            SingleBattler evo = new SingleBattler(rd, pokemon.evolution.get(key), catchLocation, level);
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
        SingleBattler evolution = evolve(new Evolution.Level(level));
        if (evolution != null) {
            return evolution;
        } else {
            return this;
        }
    }

    @Override
    public boolean learnTmMove(Move newMove, Move oldMove) {
        boolean success = false;
        List<Move> moves = getMoveset();
        if (pokemon.getTmMoves().contains(newMove) && !moves.contains(newMove)) {
            if (oldMove == null || moves.contains(oldMove)) {
                int i = 0;
                while (i < moveset.length && oldMove != moveset[i] && moveset[i] != null) {
                    i++;
                } // only remove the move if no more room!
                if (i < moveset.length) {
                    moveset[i] = newMove;
                    success = true;
                }
            }
        }
        return success;
    }

    @Override
    public Battler useCandy(int count) {
        SingleBattler newBattler = this;
        for (int i = 0; i < count; i++) {
            if (level < 100) {
                newBattler = (SingleBattler) newBattler.addXP(pokemon.expGroup.getDeltaExp(level, level + 1, levelExp));
            }
        }
        return newBattler;
    }

    @Override
    public boolean useHPUp(int count) {
        boolean success = true;
        for (int i = 0; i < count; i++) {
            if (hpXP < 25600) {
                hpXP = Math.min(hpXP + 2560, 25600);
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
            if (atkXP < 25600) {
                atkXP = Math.min(atkXP + 2560, 25600);
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
            if (defXP < 25600) {
                defXP = Math.min(defXP + 2560, 25600);
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
            if (spdXP < 25600) {
                spdXP = Math.min(spdXP + 2560, 25600);
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
            if (spcXP < 25600) {
                spcXP = Math.min(spcXP + 2560, 25600);
            } else {
                success = false;
            }
        }
        return success;
    }

    @Override
    protected boolean checkEvolve() {
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

    @Override
    public Range getHP() {
        return currentStats[0];
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
        int stat = calculateStat(level, pokemon.atk, DV, atkXP);
        return new Range(stat, stat);
    }

    @Override
    public Range getDefStatIfDV(int DV) {
        int stat = calculateStat(level, pokemon.def, DV, defXP);
        return new Range(stat, stat);
    }

    @Override
    public Range getSpdStatIfDV(int DV) {
        int stat = calculateStat(level, pokemon.spd, DV, spdXP);
        return new Range(stat, stat);
    }

    @Override
    public Range getSpcStatIfDV(int DV) {
        int stat = calculateStat(level, pokemon.spc, DV, spcXP);
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
