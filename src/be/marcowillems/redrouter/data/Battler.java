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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * TODO: Change useCandy, useHPUP, ... to useItem?
 *
 * @author Marco Willems
 */
public abstract class Battler implements Cloneable {

    public final RouterData rd;

    public final Pokemon pokemon;
    public final EncounterArea catchLocation;
    public final boolean isTrainerMon; // TODO do this more properly?

    protected Move[] moveset; // TODO: make final

    protected int level;
    protected int levelExp = 0;
//    public int totalXP = 0;

    /**
     * Self explanatory constructor, catchLocation can be null.
     *
     * @param rd
     * @param pokemon
     * @param catchLocation
     * @param isTrainerMon
     * @param level
     */
    public Battler(RouterData rd, Pokemon pokemon, EncounterArea catchLocation, boolean isTrainerMon, int level) {
        this.rd = rd;
        this.pokemon = pokemon;
        this.catchLocation = catchLocation;
        this.isTrainerMon = isTrainerMon;
        this.level = level;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return getDeepCopy();
    }

    public abstract Battler getDeepCopy();

    /**
     * Defeat given battler, this battler is modified but not evolved.
     *
     * @param b Battler to defeat
     * @return Returns the modified/evolved battler (not a deep copy)
     */
    public Battler defeatBattler(Battler b) {
        return defeatBattler(b, 1);
    }

    /**
     * Defeat given battler, this battler is modified but not evolved.
     *
     * @param b Battler to defeat
     * @param participants Amount of participants in the battle
     * @return Returns the modified/evolved battler (not a deep copy)
     */
    public abstract Battler defeatBattler(Battler b, int participants);

    /**
     * Tries to evolve the battler with the specified item.
     *
     * @param item The item which triggers the evolution
     * @return Returns the evolved battler or null if it couldn't evolve
     */
    public Battler evolve(Item item) {
        return evolve(new Evolution.Item(item));
    }

    protected abstract Battler evolve(Evolution.Key key);

    /**
     * Add experience, this battler is modified but not evolved.
     *
     * @param exp
     * @return Returns the modified/evolved Battler (not a deep copy)
     */
    public abstract Battler addXP(int exp);

    /**
     * Try to learn a TM or HM move.
     *
     * @param newMove The TM or HM move
     * @param oldMove can be null
     * @return true if success
     */
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

    /**
     * Use some Rare Candies
     *
     * @param count
     * @return modified Battler (not a deep copy)
     */
    public Battler useCandy(int count) {
        Battler newBattler = this;
        for (int i = 0; i < count; i++) {
            if (level < 100) {
                newBattler = newBattler.addXP(pokemon.expGroup.getDeltaExp(level, level + 1, levelExp));
            }
        }
        return newBattler;
    }

    public abstract boolean useHPUp(int count);

    public abstract boolean useProtein(int count);

    public abstract boolean useIron(int count);

    public abstract boolean useCarbos(int count);

    public abstract boolean useCalcium(int count);
    
    public abstract DVRange getDVRange(int stat);
    
    public abstract DVRange[] getDVRanges();

    public List<Move> getMoveset() {
        List<Move> moves = new ArrayList<>();
        for (Move m : moveset) {
            if (m != null) {
                moves.add(m);
            }
        }
        return moves;
    }

    public int getLevel() {
        return this.level;
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

    protected abstract Range getBoostedStat(Range statRange, int badgeBoostCount, int xItemCount);

//    public Pokemon getPokemon() {
//        return this.pokemon;
//    }

    public boolean isType(Types.Type type) {
        return type == pokemon.type1 || (pokemon.type2 != null && type == pokemon.type2);
    }

    public int getExp(int participants) {
        return pokemon.getExp(level, participants, false, isTrainerMon);
    }

    public int getLevelExp() {
        return levelExp;
    }

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
