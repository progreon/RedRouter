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

import javafx.util.Pair;

/**
 *
 * @author Marco Willems
 */
public class Move {

    public final String NAME;
    public final Types.Type type;
    public final boolean isAttack;
    public final int power;
    public final int accuracy;

    private boolean OITN = false; // OneInThirtyNine

    public Move(String NAME, Types.Type type, boolean isAttack, int power, int accuracy) {
        this.NAME = NAME;
        this.type = type;
        this.isAttack = isAttack;
        this.power = power;
        this.accuracy = accuracy;
    }

    public Pair<Integer, Integer> getDamageRange(Battler attacker, Battler defender, boolean isCrit) {
        int minDamage = 0;
        int maxDamage = 0;
        int maxRandom = (OITN) ? 255 : 254;
//        double oneShot = 0.0;

        if (isAttack) {
            int attack = Types.isPhysical(type) ? attacker.getAtk(isCrit) : attacker.getSpc(isCrit);
            int defense = Types.isPhysical(type) ? defender.getDef(isCrit) : defender.getSpc(isCrit);
            boolean isSTAB = attacker.isType(type);
            double typeEff = Types.getTypeChart().getFactor(type, defender.getPokemon().type1, defender.getPokemon().type2);
            int critical = isCrit ? 2 : 1;
            double oneShot = 0.0; // TODO ??
//            double other = 1; // TODO ??
//            double modifier = STAB * typeEff * critical * other;
            int damage = (attacker.level * critical) % 256;
            damage *= attack;
            damage *= power;
            damage /= 50;
            damage /= defense;
            damage += 2;
            damage = isSTAB ? damage * 3 / 2 : damage;
            damage *= typeEff;

            if (damage != 0) {
                minDamage = Math.max(damage * 217 / 255, 1);
                maxDamage = Math.max(damage * maxRandom / 255, 1);

                // TODO ??
                int oneShots = 0;
                for (int r = 217; r <= 255; r++) {
                    if ((damage * r / 255) >= defender.getHP()) {
                        oneShots++;
                    }
                }
                if (oneShots > 0) {
                    oneShot = (oneShots / 39.0) * 100.0;
                }
            }
        } else {
            // TODO: RangesPanel:791
        }

        return new Pair<>(minDamage, maxDamage);
    }

//    public class DamageRange {
//        
//    }
}
