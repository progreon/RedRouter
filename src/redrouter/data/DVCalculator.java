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
public class DVCalculator {

    public static final String defaultPokemon = "NidoranM";
    public static final int defaultLevel = 3;
    private Battler battler;
    private Location catchLocation;
    public final boolean[][] isPossibleDV; // [hp, atk, def, spd, spc][0..15] -> true/false
    public final int[][] stats;

    public DVCalculator(Battler battler) {
        this(battler, null);
    }

    public DVCalculator(Battler battler, Location catchLocation) {
        this.battler = battler;
        if (this.battler == null) {
            this.battler = getDefaultBattler();
        }
        this.catchLocation = catchLocation;
        this.isPossibleDV = new boolean[5][16];
        this.stats = new int[5][16];
        init();
    }

    private Battler getDefaultBattler() {
        return new Battler(Pokemon.get(defaultPokemon), defaultLevel, null);
    }

    private void init() {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 16; j++) {
                this.isPossibleDV[i][j] = true;
            }
        }
        calculateStats();
    }

    public void defeatPokemon(Pokemon pkm, int nrOfPkmn) {
        battler.addStatXP(pkm.hp, pkm.atk, pkm.def, pkm.spd, pkm.spc, nrOfPkmn);
//        calculateStats();
    }

    public void levelUp() {
        battler.level++;
        calculateStats();
    }

    public void resetStatExp() {
        battler.resetStatXP();
        calculateStats();
    }

    public void resetSelected() {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 16; j++) {
                this.isPossibleDV[i][j] = true;
            }
        }
    }

    public void setLevel(int level) {
        battler.level = level;
        calculateStats();
    }

    public boolean setStat(int stat, int dv) {
        if (isPossibleDV[stat][dv]) {
            int val = stats[stat][dv];
            for (int i = 0; i < isPossibleDV[stat].length; i++) {
                if (stats[stat][i] != val) {
                    isPossibleDV[stat][i] = false;
                }
            }
            handleHPDV(stat);
            return true;
        } else {
            return false;
        }
    }

    public boolean setStatExact(int stat, int dv) {
        if (isPossibleDV[stat][dv]) {
            for (int i = 0; i < isPossibleDV[stat].length; i++) {
                if (i != dv) {
                    isPossibleDV[stat][i] = false;
                }
            }
            handleHPDV(stat);
            return true;
        } else {
            return false;
        }
    }

    public Battler getBattler() {
        return battler;
    }

    public void setBattler(Battler battler) {
        this.battler = battler;
        if (this.battler == null) {
            this.battler = getDefaultBattler();
        }
        init();
    }

    public StatRange[] getStatRanges() {
        StatRange[] ranges = new StatRange[5];

        for (int s = 0; s < 5; s++) {
            ranges[s] = new StatRange();
            for (int DV = 0; DV < 16; DV++) {
                if (isPossibleDV[s][DV]) {
                    ranges[s].add(DV);
                }
            }
        }

        return ranges;
    }

    private void calculateStats() {
        for (int i = 0; i < stats[0].length; i++) {
            stats[0][i] = battler.getHPStatIfDV(i);
            stats[1][i] = battler.getAtkStatIfDV(i);
            stats[2][i] = battler.getDefStatIfDV(i);
            stats[3][i] = battler.getSpdStatIfDV(i);
            stats[4][i] = battler.getSpcStatIfDV(i);
        }
    }

    private void handleHPDV(int statClicked) {
        if (statClicked == 0) { // HP was clicked
            boolean[] statIsEven = new boolean[4];
            boolean[] statIsOdd = new boolean[4];
            for (int dv = 0; dv < 16; dv++) {
                if (isPossibleDV[0][dv]) {
                    if ((dv / 8) % 2 == 1) {
                        statIsOdd[0] = true; // Odd attack possible
                    } else {
                        statIsEven[0] = true; // Even attack possible
                    }
                    if ((dv / 4) % 2 == 1) {
                        statIsOdd[1] = true; // Odd defense possible
                    } else {
                        statIsEven[1] = true; // Even defense possible
                    }
                    if ((dv / 2) % 2 == 1) {
                        statIsOdd[2] = true; // Odd speed possible
                    } else {
                        statIsEven[2] = true; // Even speed possible
                    }
                    if (dv % 2 == 1) {
                        statIsOdd[3] = true; // Odd special possible
                    } else {
                        statIsEven[3] = true; // Even special possible
                    }
                }
            }
            for (int stat = 0; stat < 4; stat++) {
                if (!statIsEven[stat]) {
                    removeOddOrEvenDV(stat + 1, false);
                }
                if (!statIsOdd[stat]) {
                    removeOddOrEvenDV(stat + 1, true);
                }
            }
        } else { // Other stat was clicked (statClicked == 1, 2, 3 or 4)
            boolean isEven = false;
            boolean isOdd = false;
            for (int dv = 0; dv < 16; dv++) {
                if (isPossibleDV[statClicked][dv]) {
                    if (dv % 2 == 0) { // Even is possible
                        isEven = true;
                    } else {
                        isOdd = true;
                    }
                }
            }
            if (!isEven) {
                for (int dv = 0; dv < 16; dv++) {
                    if ((dv >> (4 - statClicked)) % 2 == 0) { // >> is bitwise shift operator ((4 - statClicked) == 3, 2, 1 or 0)
                        isPossibleDV[0][dv] = false;
                    }
                }
                handleHPDV(0);
            }
            if (!isOdd) {
                for (int dv = 0; dv < 16; dv++) {
                    if ((dv >> (4 - statClicked)) % 2 == 1) {
                        isPossibleDV[0][dv] = false;
                    }
                }
                handleHPDV(0);
            }
        }
    }

    private void removeOddOrEvenDV(int stat, boolean odd) {
        for (int dv = odd ? 1 : 0; dv < 16; dv += 2) {
            isPossibleDV[stat][dv] = false;
        }
    }
    
    public class StatRange {
        
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

}
