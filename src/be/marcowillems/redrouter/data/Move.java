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
import java.util.Locale;
import java.util.Objects;
import be.marcowillems.redrouter.io.ParserException;
import be.marcowillems.redrouter.util.Range;

/**
 *
 * @author Marco Willems
 */
public class Move {

    private final RouterData rd;

    public final String name;
    public final Types.Type type;
    public final String effect;
    public final int power;
    public final int accuracy;
    public final int pp;

    private int[] multipliers = new int[]{25, 28, 33, 40, 50, 66, 1, 15, 2, 25, 3, 35, 4};
    private int[] divisors = new int[]{100, 100, 100, 100, 100, 100, 1, 10, 1, 10, 1, 10, 1};

    public final List<Pokemon> pokemon; // Pokemon that learn this move

    public Move(RouterData rd, String name, Types.Type type, boolean isAttack, int power, int accuracy, int pp) {
        this(rd, name, type, "NO_ADDITIONAL_EFFECT", isAttack, power, accuracy, pp);
    }

    public Move(RouterData rd, String name, Types.Type type, String effect, boolean isAttack, int power, int accuracy, int pp) {
        this.rd = rd;
        this.name = name;
        this.type = type;
        this.effect = effect;
        this.power = power;
        this.accuracy = accuracy;
        this.pp = pp;
        this.pokemon = new ArrayList<>();
    }

    /**
     * Creates a Move from a parsed line
     *
     * @param rd
     * @param moveString i.e : POUND,NO_ADDITIONAL_EFFECT#40#NORMAL#100#35
     * @param file
     * @param line
     * @throws ParserException
     */
    // TODO Move effect
    public Move(RouterData rd, String moveString, String file, int line) throws ParserException {
        this.rd = rd;
        String[] s = moveString.split("#");
        if (s.length != 6) {
            throw new ParserException(file, line, "Move entry must have 6 parameters!");
        } else {
            try {
                this.name = s[0];
                this.effect = s[1];
                this.power = Integer.parseInt(s[2]);
                this.type = Types.getType(s[3]);
                this.accuracy = Integer.parseInt(s[4]);
                this.pp = Integer.parseInt(s[5]);
                this.pokemon = new ArrayList<>();
            } catch (NumberFormatException nex) {
                throw new ParserException(file, line, "Could not parse a move parameter");
            } catch (IllegalArgumentException ex) {
                throw new ParserException(file, line, "Move type \"" + s[3] + "\" does not exist");
            }
        }
    }

    // TODO boosts!
    public DamageRange getDamageRange(Player playerA, Player playerB, Battler attacker, Battler defender) {
        Range damageRange = getDamageRange(playerA, playerB, attacker, defender, false);
        Range critRange = getDamageRange(playerA, playerB, attacker, defender, true);
        return new DamageRange(damageRange.getMin(), damageRange.getMax(), critRange.getMin(), critRange.getMax());
    }

    // TODO: confusion & night shade damage
    // TODO: boosts
    // See: http://upcarchive.playker.info/0/upokecenter/content/pokemon-red-version-blue-version-and-yellow-version-damage-calculation-process.html
    private Range getDamageRange(Player playerA, Player playerB, Battler attacker, Battler defender, boolean isCrit) {
        if (power == 0) {
            return new Range(0, 0); // TODO: special cases?
        }
        int atkBadge = 0;
        int defBadge = 0;
        if (playerA != null && Types.isPhysical(type) && playerA.atkBadge) {
            atkBadge = 1;
        }
        if (playerA != null && !Types.isPhysical(type) && playerA.spcBadge) {
            atkBadge = 1;
        }
        if (playerB != null && Types.isPhysical(type) && playerB.defBadge) {
            defBadge = 1;
        }
        if (playerB != null && !Types.isPhysical(type) && playerB.spcBadge) {
            defBadge = 1;
        }
        // (1), (2), (4)
        Range atkRange = Types.isPhysical(type) ? attacker.getAtk() : attacker.getSpc();
        int minAttack = isCrit ? atkRange.getMin() : getStatWithBoosts(atkRange.getMin(), atkBadge, 0);
        int maxAttack = isCrit ? atkRange.getMax() : getStatWithBoosts(atkRange.getMax(), atkBadge, 0);
        // TODO: (3) attacker is burned
        Range defRange = Types.isPhysical(type) ? defender.getDef() : defender.getSpc();
        int minDefense = getStatWithBoosts(defRange.getMin(), defBadge, 0);
        int maxDefense = getStatWithBoosts(defRange.getMax(), defBadge, 0);
        // TODO: (5) Selfdestruct & Explosion
        // (6) ??
        if (minAttack > 255) {
            minAttack = ((((minAttack / 2) % 255) / 2) % 255);
        }
        if (maxAttack > 255) {
            maxAttack = ((((maxAttack / 2) % 255) / 2) % 255);
        }
        if (minDefense > 255) {
            minDefense = ((((minDefense / 2) % 255) / 2) % 255);
        }
        if (maxDefense > 255) {
            maxDefense = ((((maxDefense / 2) % 255) / 2) % 255);
        }
        // TODO: (7) Reflect in effect
        // TODO: (8) Light Screen in effect
        // (9)
        minAttack = minAttack == 0 ? 1 : minAttack;
        maxAttack = maxAttack == 0 ? 1 : maxAttack;
        minDefense = minDefense == 0 ? 1 : minDefense;
        maxDefense = maxDefense == 0 ? 1 : maxDefense;

        // (10)
        int damage = (attacker.getLevel() * (isCrit ? 2 : 1)) % 256;
        damage = (damage * 2 / 5 + 2);
        int minDamage = damage * minAttack * power / maxDefense;
        int maxDamage = damage * maxAttack * power / minDefense;
        minDamage /= 50;
        maxDamage /= 50;
        // (11), (12)
        minDamage = Math.min(minDamage, 997) + 2;
        maxDamage = Math.min(maxDamage, 997) + 2;
        // (13)
        minDamage = attacker.isType(type) ? minDamage * 3 / 2 : minDamage; // STAB
        maxDamage = attacker.isType(type) ? maxDamage * 3 / 2 : maxDamage; // STAB
        // (14)
        minDamage *= Types.getTypeChart().getFactor(type, defender.getPokemon().type1, defender.getPokemon().type2);
        maxDamage *= Types.getTypeChart().getFactor(type, defender.getPokemon().type1, defender.getPokemon().type2);
        // (15)
        if (minDamage != 0) {
            minDamage = Math.max(minDamage * 217 / 255, 1);
        }

        return new Range(minDamage, maxDamage);
    }

    // TODO get this from Battler (and fix its method)
    private int getStatWithBoosts(int stat, int badgeBoostCount, int xItemCount) {
        stat *= multipliers[xItemCount + 6] / divisors[xItemCount + 6];
        for (int bb = 0; bb < badgeBoostCount; bb++) {
            stat = (int) (9 * stat / 8);
        }
        return stat;
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
