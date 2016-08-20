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
import java.util.Locale;
import java.util.Objects;

/**
 *
 * @author Marco Willems
 */
public class Move {

    public final String name;
    public final Types.Type type;
    public final String effect;
    public final boolean isAttack; // ??
    public final int power;
    public final int accuracy;
    public final int pp;

    public final List<Pokemon> pokemon; // Pokemon that learn this move

    public Move(String name, Types.Type type, boolean isAttack, int power, int accuracy,pp) {
        this(name, type, "NO_ADDITIONAL_EFFECT", isAttack, power, accuracy,pp);
    }

    public Move(String name, Types.Type type, String effect, boolean isAttack, int power, int accuracy,int pp) {
        this.name = name;
        this.type = type;
        this.effect = effect;
        this.isAttack = isAttack;
        this.power = power;
        this.accuracy = accuracy;
        this.pp = pp;
        this.pokemon = new ArrayList<>();
    }

    /**
     * Creates a Move from a parsed line
     * @param pMoveString i.e : POUND,NO_ADDITIONAL_EFFECT,40,NORMAL,100,35
     * @param pFile
     * @param pLine
     * @throws ParserException
     */
    //TODO Move effect
    public Move(String pMoveString, String pFile, int pLine) throws ParserException {
        String[] lStrings = pMoveString.split(",");
        if (lStrings.length != 6) {
            throw new ParserException(pFile, pLine, "Entry must have 6 parameters!");
        } else {
            try {
                this.name = lStrings[0];
                this.power = Integer.parseInt(lStrings[2]);
                this.type = Types.getType(lStrings[3],pFile,pLine);
                this.accuracy = Integer.parseInt(lStrings[4]);
                this.pp = Integer.parseInt(lStrings[5]);
            } catch (NumberFormatException nex) {
                throw new ParserException(pFile, pLine, "Could not parse a move parameter!");
            }

            this.isAttack = this.power>0;
            this.pokemon = new ArrayList<>();
        }
    }
    // TODO : ref. RedHelper for implementation
    public DamageRange getDamageRange(Battler attacker, Battler defender, boolean isCrit) {
        int minDamage = 0;
        int maxDamage = 127;
        int minCritDamage = 0;
        int maxCritDamage = 254;
        int maxRandom = 255;
//        double oneShot = 0.0;

//        if (isAttack) {
//            int attack = Types.isPhysical(type) ? attacker.getAtk(isCrit) : attacker.getSpc(isCrit);
//            int defense = Types.isPhysical(type) ? defender.getDef(isCrit) : defender.getSpc(isCrit);
//            boolean isSTAB = attacker.isType(type);
//            double typeEff = Types.getTypeChart().getFactor(type, defender.getPokemon().type1, defender.getPokemon().type2);
//            int critical = isCrit ? 2 : 1;
//            double oneShot = 0.0; // TODO ??
////            double other = 1; // TODO ??
////            double modifier = STAB * typeEff * critical * other;
//            int damage = (attacker.level * critical) % 256;
//            damage *= attack;
//            damage *= power;
//            damage /= 50;
//            damage /= defense;
//            damage += 2;
//            damage = isSTAB ? damage * 3 / 2 : damage;
//            damage *= typeEff;
//
//            if (damage != 0) {
//                minDamage = Math.max(damage * 217 / 255, 1);
//                maxDamage = Math.max(damage * maxRandom / 255, 1);
//
//                // TODO ??
//                int oneShots = 0;
//                for (int r = 217; r <= 255; r++) {
//                    if ((damage * r / 255) >= defender.getHP()) {
//                        oneShots++;
//                    }
//                }
//                if (oneShots > 0) {
//                    oneShot = (oneShots / 39.0) * 100.0;
//                }
//            }
//        } else {
//            // TODO: RangesPanel:791
//        }
        return new DamageRange(minDamage, maxDamage, minCritDamage, maxCritDamage);
    }

    public static String getIndexString(String name) {
        return name.toUpperCase(Locale.ROOT);
    }

    public String getIndexString() {
        return getIndexString(name);
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public boolean equals(Object obj) {
        // TODO
        return super.equals(obj); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.name);
        hash = 59 * hash + Objects.hashCode(this.type);
        hash = 59 * hash + Objects.hashCode(this.effect);
        hash = 59 * hash + (this.isAttack ? 1 : 0);
        hash = 59 * hash + this.power;
        hash = 59 * hash + this.accuracy;
        return hash;
    }

    public class DamageRange {

        public int min, max, critMin, critMax;

        public DamageRange(int min, int max, int critMin, int critMax) {
            this.min = min;
            this.max = max;
            this.critMin = critMin;
            this.critMax = critMax;
        }

        @Override
        public String toString() {
            return min + "-" + max + " (" + critMin + "-" + critMax + ")";
        }

    }
}
