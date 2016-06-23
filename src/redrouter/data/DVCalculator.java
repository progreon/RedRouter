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
import java.util.Map;

/**
 *
 * @author Marco Willems
 */
public class DVCalculator {

    private final RouterData rd;

    // Encounter rate -> Boolean[65536]
    // atk = i >> 12
    // def = (i >> 8) % 16
    // spd = (i >> 4) % 16
    // spc = i % 16
    private Map<Integer, boolean[]> possibleDVCombos = null;

    public final String defaultPokemon = "NidoranM";
    public final int defaultLevel = 3;
    private Battler battler;
    public final int maxEncounterRate = 255;
    private boolean[][] isPossibleDV; // [hp, atk, def, spd, spc][0..15] -> true/false
    public final int[][] stats;

    public DVCalculator(RouterData rd, Battler battler) {
        this.rd = rd;
        this.battler = battler;
        if (this.battler == null) {
            this.battler = getDefaultBattler();
        }
        this.isPossibleDV = new boolean[5][16];
        this.stats = new int[5][16];
        init();
    }

    private Battler getDefaultBattler() {
        return new Battler(rd.getPokemon(defaultPokemon), defaultLevel, null);
    }

    private void init() {
        initPossibleDVCombos();
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
            filterDVs();
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
            filterDVs();
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

    public boolean isPossibleDV(int stat, int dv) {
        return isPossibleDV[stat][dv];
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

    private void filterDVs() {
        boolean[][] newPossibleDVs = new boolean[5][16];
        for (int atk = 0; atk < 16; atk++) {
            if (isPossibleDV[1][atk]) {
                for (int def = 0; def < 16; def++) {
                    if (isPossibleDV[2][def]) {
                        for (int spd = 0; spd < 16; spd++) {
                            if (isPossibleDV[3][spd]) {
                                for (int spc = 0; spc < 16; spc++) {
                                    if (isPossibleDV[4][spc]) {
                                        int hp = 8 * (atk % 2) + 4 * (def % 2) + 2 * (spd % 2) + (spc % 2);
                                        int i = (atk << 12) + (def << 8) + (spd << 4) + spc;
                                        int encounterRate = battler.catchLocation == null ? maxEncounterRate : battler.catchLocation.encounterRate;
                                        if (isPossibleDV[0][hp] && (!possibleDVCombos.containsKey(encounterRate) || possibleDVCombos.get(encounterRate)[i])) {
                                            newPossibleDVs[0][hp] = true;
                                            newPossibleDVs[1][atk] = true;
                                            newPossibleDVs[2][def] = true;
                                            newPossibleDVs[3][spd] = true;
                                            newPossibleDVs[4][spc] = true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        isPossibleDV = newPossibleDVs;
    }

    public void initPossibleDVCombos() {
        if (possibleDVCombos == null) {
            possibleDVCombos = new HashMap<>();
            int[] encounterRates = new int[]{3, 5, 8, 10, 15, 20, 25, 30};
            int rDiv2Max = 255;
            int[] dDiv3s;
            int[] dDiv4s = new int[]{1, 2, 3};
            int[] c2s;
            int c3 = 1;
            int c4 = 1;
            if (rd.settings.isRedBlue()) {
                dDiv3s = new int[]{45, 46, 47};
                c2s = new int[]{0};
            } else {
                dDiv3s = new int[]{47, 48, 49};
                c2s = new int[]{0, 1};
            }
            for (int encRate : encounterRates) {
                possibleDVCombos.put(encRate, new boolean[16 * 16 * 16 * 16]);
                for (int hRandomAdd1 = 0; hRandomAdd1 < encRate; hRandomAdd1++) {
                    for (int rDiv2 = 0; rDiv2 <= rDiv2Max; rDiv2++) {
                        for (int dDiv3 : dDiv3s) {
                            for (int dDiv4 : dDiv4s) {
                                for (int c2 : c2s) {
                                    int hRandomAdd2 = (hRandomAdd1 + rDiv2 + c2) % 256;
                                    int rDiv3 = (rDiv2 + dDiv3) % 256;
                                    int hRandomAdd3 = (hRandomAdd2 + rDiv3 + c3) % 256; // 16*spd + spc
                                    int rDiv4 = (rDiv3 + dDiv4) % 256;
                                    int hRandomAdd4 = (hRandomAdd3 + rDiv4 + c4) % 256; // 16*atk + def
                                    possibleDVCombos.get(encRate)[(hRandomAdd4 << 8) + hRandomAdd3] = true;
                                }
                            }
                        }
                    }
                }
            }
//            printPossibleDVCombos(25);
        }
    }

//    private static void printPossibleDVCombos(int encounterRate) {
//        for (int i = 0; i < possibleDVCombos.get(encounterRate).length; i++) {
//            if (possibleDVCombos.get(encounterRate)[i]) {
//                System.out.println((i >> 12) + "/" + ((i >> 8) % 16) + "/" + ((i >> 4) % 16) + "/" + (i % 16));
//            }
//        }
//    }
    
    public RouterData getRd() {
        return rd;
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
