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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.swing.JButton;
import redrouter.view.route.BattlerInfoDialog;

/**
 *
 * @author Marco Willems
 */
public abstract class Battler implements Cloneable {

//    public static final Battler NULL = new SingleBattler(null, 0, null);
    public static final Battler DUMMY = new SingleBattler(new Pokemon(null, 0, "Dummy Poke", Types.Type.NORMAL, null, 100, 100, 100, 100, 100, 100), null, 5);
    protected Pokemon pokemon; // TODO: final!? -> evoluties?
    public final EncounterArea catchLocation;

    public Battler(Pokemon pokemon, EncounterArea catchLocation) {
        this.pokemon = pokemon;
        this.catchLocation = catchLocation;
    }

    @Override
    protected abstract Object clone() throws CloneNotSupportedException;

    // TODO: evolve condition (item, ...)
    public abstract void evolve();

    public abstract void addStatXP(int hp, int atk, int def, int spd, int spc, int nrOfPkmn);

    public abstract void resetStatXP();

    public abstract List<Move> getMoveset();

    // TODO: range!
    public abstract int getLevel();

    public abstract DVRange getDVRange(int stat);

    public DVRange[] getDVRanges() {
        DVRange[] ranges = new DVRange[5];
        for (int s = 0; s < 5; s++) {
            ranges[s] = getDVRange(s);
            //            ranges[s] = new DVRange();
            //            for (int DV = 0; DV < 16; DV++) {
            //                if (possibleDVs[s][DV]) {
            //                    ranges[s].add(DV);
            //                }
            //            }
        }
        return ranges;
    }

    public abstract StatRange getHP();

    public StatRange getAtk() {
        return getAtk(0, 0);
    }

    public StatRange getDef() {
        return getDef(0, 0);
    }

    public StatRange getSpd() {
        return getSpd(0, 0);
    }

    public StatRange getSpc() {
        return getSpc(0, 0);
    }

    public abstract StatRange getAtk(int badgeBoosts, int stage);

    public abstract StatRange getDef(int badgeBoosts, int stage);

    public abstract StatRange getSpd(int badgeBoosts, int stage);

    public abstract StatRange getSpc(int badgeBoosts, int stage);

    public abstract StatRange getHPStatIfDV(int DV);

    public abstract StatRange getAtkStatIfDV(int DV);

    public abstract StatRange getDefStatIfDV(int DV);

    public abstract StatRange getSpdStatIfDV(int DV);

    public abstract StatRange getSpcStatIfDV(int DV);

    // TODO: calculation with badge boosts & stages
    protected int getStat(int level, int base, int DV, int XP, int badgeBoosts, int stage) {
        double extraStats = 0;
        if (XP - 1 >= 0) {
            extraStats = Math.floor(Math.floor(Math.sqrt(XP - 1) + 1) / 4);
        }
        double statValue = Math.floor((((base + DV) * 2 + extraStats) * level / 100) + 5);
        return (int) statValue;
    }

    public Pokemon getPokemon() {
        return this.pokemon;
    }

    public boolean isType(Types.Type type) {
        return type == pokemon.type1 || (pokemon.type2 != null && type == pokemon.type2);
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
//        String battler = (this == NULL ? "-----" : pokemon.name + " Lv." + getLevel());
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

    public JButton makeBattlerInfoButton() {
        JButton btn = new JButton("B");
        btn.addMouseListener(new MouseAdapter() {
            BattlerInfoDialog bif = null;

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    if (bif != null) {
                        bif.dispose();
                    }
                    bif = new BattlerInfoDialog(Battler.this, e.getLocationOnScreen());
                    bif.setVisible(true);
                    //                    tree.requestFocus();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (bif != null) {
                    bif.dispose();
                    bif = null;
                }
            }
        });
        return btn;
    }

    public class DVRange {

        private final List<Integer> dvs = new ArrayList<>();

        public void add(int dv) {
            if (!dvs.contains(dv)) {
                dvs.add(dv);
            }
        }

        public int getMin() {
            int min = 15;
            for (Integer dv : dvs) {
                if (dv < min) {
                    min = dv;
                }
            }
            return min;
        }

        public int getMax() {
            int max = 0;
            for (Integer dv : dvs) {
                if (dv > max) {
                    max = dv;
                }
            }
            return max;
        }

        public void combine(DVRange range) {
            for (int dv : range.dvs) {
                this.add(dv);
            }
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

        public void combine(StatRange statRange) {
            if (statRange.min < this.min) {
                this.min = statRange.min;
            }
            if (statRange.max > this.max) {
                this.max = statRange.max;
            }
        }

        public boolean contains(int stat) {
            return min <= stat && stat <= max;
        }

        public boolean containsOneOf(StatRange statRange) {
            return (this.min <= statRange.min && statRange.min <= this.max) || statRange.containsOneOf(this);
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
