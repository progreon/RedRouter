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
import java.util.List;
import java.util.Objects;
import be.marcowillems.redrouter.util.DVRange;
import be.marcowillems.redrouter.util.Range;

/**
 *
 * @author Marco Willems
 */
public class CombinedBattler extends Battler {

    private final List<SingleBattler> possibleBattlers = new ArrayList<>();

    public CombinedBattler(SingleBattler battler) {
        super(battler.rd, battler.pokemon, battler.catchLocation);
        this.possibleBattlers.add(battler);
    }

    @Override
    public Battler getDeepCopy() {
        CombinedBattler newBattler = new CombinedBattler((SingleBattler) possibleBattlers.get(0).getDeepCopy());

        for (int i = 1; i < possibleBattlers.size(); i++) {
            newBattler.possibleBattlers.add((SingleBattler) possibleBattlers.get(i).getDeepCopy());
        }

        return newBattler;
    }

    public boolean combine(SingleBattler battler) {
        boolean equals = false;
        if (!equals) {
            for (SingleBattler sb : possibleBattlers) {
                if (sb.equals(battler)) {
                    equals = true;
                }
            }
        }
        if (!equals && battler.getPokemon() == this.getPokemon() && battler.rd == rd) {
            possibleBattlers.add(battler);
            return true;
        }
        return false || equals;
    }

    public boolean combine(CombinedBattler battler) {
        boolean combined = false;
        for (SingleBattler sb : battler.possibleBattlers) {
            if (this.combine(sb)) {
                combined = true;
            }
        }
        return combined;
    }

    @Override
    public Battler evolve(Item item) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addStatXP(int hp, int atk, int def, int spd, int spc, int nrOfPkmn) {
        for (SingleBattler sb : possibleBattlers) {
            sb.addStatXP(hp, atk, def, spd, spc, nrOfPkmn);
        }
    }

    @Override
    public void resetStatXP() {
        for (SingleBattler sb : possibleBattlers) {
            sb.resetStatXP();
        }
    }

    @Override
    public Battler addXP(int exp) {
        // TODO meh...
        Battler thisOrEvolved = this;
        SingleBattler newB = (SingleBattler) possibleBattlers.get(0).addXP(exp);
        Pokemon p = newB.pokemon;
        CombinedBattler newBattler = new CombinedBattler(newB);
        int combinedCount = 1;
        for (int i = 1; i < possibleBattlers.size(); i++) {
            newB = (SingleBattler) possibleBattlers.get(i).addXP(exp);
            if (p == newB.pokemon) {
                if (newBattler.combine(newB)) {
                    combinedCount++;
                }
            }
        }
        if (super.pokemon != p && combinedCount == possibleBattlers.size()) {
            thisOrEvolved = newBattler;
        }
        return thisOrEvolved;
    }

    @Override
    public boolean learnTmMove(Move newMove, Move oldMove) {
        boolean success = false;
        for (SingleBattler sb : possibleBattlers) {
            success |= sb.learnTmMove(newMove, oldMove);
        }
        return success;
    }

    @Override
    public Battler useCandy(int count) {
        // TODO meh...
        Battler thisOrEvolved = this;
        SingleBattler newB = (SingleBattler) possibleBattlers.get(0).useCandy(count);
        Pokemon p = newB.pokemon;
        CombinedBattler newBattler = new CombinedBattler(newB);
        if (super.pokemon != p) {
            thisOrEvolved = newBattler;
        }
        for (int i = 1; i < possibleBattlers.size(); i++) {
            newB = (SingleBattler) possibleBattlers.get(i).useCandy(count);
            if (p == newB.pokemon) {
                newBattler.combine(newB);
            }
        }
        return thisOrEvolved;
    }

    @Override
    protected boolean checkEvolve() {
        for (SingleBattler sb : possibleBattlers) {
            sb.checkEvolve();
        }
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Move> getMoveset() {
        List<Move> moves = new ArrayList<>();
        for (SingleBattler sb : possibleBattlers) {
            for (Move m : sb.getMoveset()) {
                if (m != null && !moves.contains(m)) {
                    moves.add(m);
                }
            }
        }
        return moves;
    }

    // TODO: range!!
    @Override
    public int getLevel() {
        return possibleBattlers.get(0).getLevel();
    }

    @Override
    public DVRange getDVRange(int stat) {
        DVRange range = new DVRange();
        for (SingleBattler sb : possibleBattlers) {
            range.combine(sb.getDVRange(stat));
        }
        return range;
    }

    @Override
    public Range getHP() {
        Range range = possibleBattlers.get(0).getHP();
        for (SingleBattler sb : possibleBattlers) {
            range.combine(sb.getHP());
        }
        return range;
    }

    @Override
    public Range getAtk(int badgeBoosts, int stage) {
        Range range = possibleBattlers.get(0).getAtk(badgeBoosts, stage);
        for (SingleBattler sb : possibleBattlers) {
            range.combine(sb.getAtk(badgeBoosts, stage));
        }
        return range;
    }

    @Override
    public Range getDef(int badgeBoosts, int stage) {
        Range range = possibleBattlers.get(0).getDef(badgeBoosts, stage);
        for (SingleBattler sb : possibleBattlers) {
            range.combine(sb.getDef(badgeBoosts, stage));
        }
        return range;
    }

    @Override
    public Range getSpd(int badgeBoosts, int stage) {
        Range range = possibleBattlers.get(0).getSpd(badgeBoosts, stage);
        for (SingleBattler sb : possibleBattlers) {
            range.combine(sb.getSpd(badgeBoosts, stage));
        }
        return range;
    }

    @Override
    public Range getSpc(int badgeBoosts, int stage) {
        Range range = possibleBattlers.get(0).getSpc(badgeBoosts, stage);
        for (SingleBattler sb : possibleBattlers) {
            range.combine(sb.getSpc(badgeBoosts, stage));
        }
        return range;
    }

    @Override
    public Range getHPStatIfDV(int DV) {
        Range range = possibleBattlers.get(0).getHPStatIfDV(DV);
        for (SingleBattler sb : possibleBattlers) {
            range.combine(sb.getHPStatIfDV(DV));
        }
        return range;
    }

    @Override
    public Range getAtkStatIfDV(int DV) {
        Range range = possibleBattlers.get(0).getAtkStatIfDV(DV);
        for (SingleBattler sb : possibleBattlers) {
            range.combine(sb.getAtkStatIfDV(DV));
        }
        return range;
    }

    @Override
    public Range getDefStatIfDV(int DV) {
        Range range = possibleBattlers.get(0).getDefStatIfDV(DV);
        for (SingleBattler sb : possibleBattlers) {
            range.combine(sb.getDefStatIfDV(DV));
        }
        return range;
    }

    @Override
    public Range getSpdStatIfDV(int DV) {
        Range range = possibleBattlers.get(0).getSpdStatIfDV(DV);
        for (SingleBattler sb : possibleBattlers) {
            range.combine(sb.getSpdStatIfDV(DV));
        }
        return range;
    }

    @Override
    public Range getSpcStatIfDV(int DV) {
        Range range = possibleBattlers.get(0).getSpcStatIfDV(DV);
        for (SingleBattler sb : possibleBattlers) {
            range.combine(sb.getSpcStatIfDV(DV));
        }
        return range;
    }

    @Override
    public Range getExp(int participants) {
        Range range = possibleBattlers.get(0).getExp(participants);
        for (SingleBattler sb : possibleBattlers) {
            range.combine(sb.getExp(participants));
        }
        return range;
    }

    @Override
    public Range getLevelExp() {
        Range range = possibleBattlers.get(0).getLevelExp();
        for (SingleBattler sb : possibleBattlers) {
            range.combine(sb.getLevelExp());
        }
        return range;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CombinedBattler) {
            int count = 0;
            int antiCount = 0;
            for (SingleBattler sb : ((CombinedBattler) obj).possibleBattlers) {
                if (this.possibleBattlers.contains(sb)) {
                    count++;
                } else {
                    antiCount++;
                }
            }
            return count == this.possibleBattlers.size() && antiCount == 0;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.possibleBattlers);
        return hash;
    }

}
