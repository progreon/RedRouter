/*
 * Copyright (C) 2016 marco
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

/**
 *
 * @author marco
 */
public class DVCalculator {

    private final Battler battler;
    private final Location catchLocation;
    public final boolean[][] isPossibleDV; // [hp, atk, def, spd, spc] -> [0..15]
    public final int[][] stats;

    public DVCalculator(Battler battler, Location catchLocation) {
        this.battler = battler;
        this.catchLocation = catchLocation;
        this.isPossibleDV = new boolean[5][16];
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 16; j++) {
                this.isPossibleDV[i][j] = true;
            }
        }
        this.stats = new int[5][16];
        calculateStats();
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

    public void defeatPokemon(Pokemon pkm) {
        battler.addStatXP(pkm.hp, pkm.atk, pkm.def, pkm.spd, pkm.spc);
//        calculateStats();
    }

    public void levelUp() {
        battler.level++;
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
            return true;
        } else {
            return false;
        }
    }

    public Battler getBattler() {
        return battler;
    }

}
