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

import java.util.List;
import java.util.Objects;
import be.marcowillems.redrouter.util.DVRange;
import be.marcowillems.redrouter.util.Range;

/**
 *
 * @author Marco Willems
 */
public abstract class Battler implements Cloneable {

    public final RouterData rd;

    public final Pokemon pokemon;
    public final EncounterArea catchLocation;

    private final int[] multipliers = new int[]{25, 28, 33, 40, 50, 66, 1, 15, 2, 25, 3, 35, 4};
    private final int[] divisors = new int[]{100, 100, 100, 100, 100, 100, 1, 10, 1, 10, 1, 10, 1};

    public Battler(RouterData rd, Pokemon pokemon, EncounterArea catchLocation) {
        this.pokemon = pokemon;
        this.catchLocation = catchLocation;
        this.rd = rd;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return getDeepCopy();
    }

    public abstract Battler getDeepCopy();

    public Battler defeatBattler(Battler b) {
        return defeatBattler(b, 1);
    }

    public Battler defeatBattler(Battler b, int participants) {
        addStatXP(b.pokemon.hp, b.pokemon.atk, b.pokemon.def, b.pokemon.spd, b.pokemon.spc, participants);
        return addXP(b.getExp(participants).getMin());
    }

    /**
     * Tries to evolve the battler with the specified item.
     *
     * @param item The item which triggers the evolution
     * @return Returns the evolved battler or null if it couldn't evolve
     */
    public abstract Battler evolve(Item item);

    public abstract void addStatXP(int hp, int atk, int def, int spd, int spc, int nrOfPkmn);

    public abstract void resetStatXP();

    /**
     *
     * @param exp
     * @return modified Battler (not a deep copy)
     */
    public abstract Battler addXP(int exp);

    /**
     *
     * @param newMove
     * @param oldMove can be null
     * @return
     */
    public abstract boolean learnTmMove(Move newMove, Move oldMove);

    /**
     *
     * @param count
     * @return modified Battler (not a deep copy)
     */
    public abstract Battler useCandy(int count);

    public abstract boolean useHPUp(int count);

    public abstract boolean useProtein(int count);

    public abstract boolean useIron(int count);

    public abstract boolean useCarbos(int count);

    public abstract boolean useCalcium(int count);

    public abstract List<Move> getMoveset();

    // TODO: range!
    public abstract int getLevel();

    public abstract DVRange getDVRange(int stat);

    public DVRange[] getDVRanges() {
        DVRange[] ranges = new DVRange[5];
        for (int s = 0; s < 5; s++) {
            ranges[s] = getDVRange(s);
        }
        return ranges;
    }

    /**
     * Gets the current HP stat value
     *
     * @return
     */
    public abstract Range getHP();

    /**
     * Gets the current Attack stat value
     *
     * @return
     */
    public abstract Range getAtk();

    /**
     * Gets the current Defense stat value
     *
     * @return
     */
    public abstract Range getDef();

    /**
     * Gets the current Speed stat value
     *
     * @return
     */
    public abstract Range getSpd();

    /**
     * Gets the current Special stat value
     *
     * @return
     */
    public abstract Range getSpc();

    /**
     * Gets the current Attack stat value with boosts
     *
     * @param badgeBoosts
     * @param stage
     * @return
     */
    public Range getAtk(int badgeBoosts, int stage) {
        return getBoostedStat(getAtk(), badgeBoosts, stage);
    }

    /**
     * Gets the current Defense stat value with boosts
     *
     * @param badgeBoosts
     * @param stage
     * @return
     */
    public Range getDef(int badgeBoosts, int stage) {
        return getBoostedStat(getDef(), badgeBoosts, stage);
    }

    /**
     * Gets the current Speed stat value with boosts
     *
     * @param badgeBoosts
     * @param stage
     * @return
     */
    public Range getSpd(int badgeBoosts, int stage) {
        return getBoostedStat(getSpd(), badgeBoosts, stage);
    }

    /**
     * Gets the current Special stat value with boosts
     *
     * @param badgeBoosts
     * @param stage
     * @return
     */
    public Range getSpc(int badgeBoosts, int stage) {
        return getBoostedStat(getSpc(), badgeBoosts, stage);
    }

    private Range getBoostedStat(Range statRange, int badgeBoostCount, int xItemCount) {
        Range boostedRange = new Range(statRange);
        boostedRange = boostedRange.multiplyBy(multipliers[xItemCount + 6]).divideBy(divisors[xItemCount + 6]);
        for (int bb = 0; bb < badgeBoostCount; bb++) {
            boostedRange = boostedRange.multiplyBy(9).divideBy(8);
        }
        return boostedRange;
    }

    public abstract Range getHPStatIfDV(int DV);

    public abstract Range getAtkStatIfDV(int DV);

    public abstract Range getDefStatIfDV(int DV);

    public abstract Range getSpdStatIfDV(int DV);

    public abstract Range getSpcStatIfDV(int DV);

    public Pokemon getPokemon() {
        return this.pokemon;
    }

    public boolean isType(Types.Type type) {
        return type == pokemon.type1 || (pokemon.type2 != null && type == pokemon.type2);
    }

    public abstract Range getExp(int participants);

    public abstract Range getLevelExp();

    @Override
    // TODO: NOT with hash codes!!
    public abstract boolean equals(Object obj);

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.pokemon);
        hash = 97 * hash + Objects.hashCode(this.catchLocation);
        return hash;
    }

    @Override
    public String toString() {
        String battler = pokemon.name + " Lv." + getLevel();

        return battler;
    }

}
