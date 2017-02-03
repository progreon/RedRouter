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

import be.marcowillems.redrouter.io.ParserException;
import be.marcowillems.redrouter.util.BadgeBoosts;
import be.marcowillems.redrouter.util.Range;
import be.marcowillems.redrouter.util.Stages;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

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

    public DamageRange getDamageRange(Battler attacker, Battler defender, Stages stagesA, Stages stagesB, BadgeBoosts bbA, BadgeBoosts bbB) {
        Range damageRange = getDamageRange(attacker, defender, stagesA, stagesB, bbA, bbB, false);
        Range critRange = getDamageRange(attacker, defender, stagesA, stagesB, bbA, bbB, true);
        return new DamageRange(damageRange.getMin(), damageRange.getMax(), critRange.getMin(), critRange.getMax());
    }

    // TODO: confusion & night shade damage
    // See: http://upcarchive.playker.info/0/upokecenter/content/pokemon-red-version-blue-version-and-yellow-version-damage-calculation-process.html
    private Range getDamageRange(Battler attacker, Battler defender, Stages stagesA, Stages stagesB, BadgeBoosts bbA, BadgeBoosts bbB, boolean isCrit) {
        if (power == 0) {
            return new Range(0, 0); // TODO: special cases?
        }
        // (1), (2), (4)
        Range atkRange = Types.isPhysical(type) ? attacker.getAtk(isCrit ? 0 : bbA.getAtk(), isCrit ? 0 : stagesA.getAtk()) : attacker.getSpc(isCrit ? 0 : bbA.getSpc(), isCrit ? 0 : stagesA.getSpc());
        int minAttack = atkRange.getMin();
        int maxAttack = atkRange.getMax();
        // TODO: (3) attacker is burned
        Range defRange = Types.isPhysical(type) ? defender.getDef(isCrit ? 0 : bbB.getDef(), isCrit ? 0 : stagesB.getDef()) : defender.getSpc(isCrit ? 0 : bbB.getSpc(), isCrit ? 0 : stagesB.getSpc());
        int minDefense = defRange.getMin();
        int maxDefense = defRange.getMax();
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
        minDamage *= Types.getTypeChart().getFactor(type, defender.pokemon.type1, defender.pokemon.type2);
        maxDamage *= Types.getTypeChart().getFactor(type, defender.pokemon.type1, defender.pokemon.type2);
        // (15)
        if (minDamage != 0) {
            minDamage = Math.max(minDamage * 217 / 255, 1);
        }

        return new Range(minDamage, maxDamage);
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
