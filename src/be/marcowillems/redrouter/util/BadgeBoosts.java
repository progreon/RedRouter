/*
 * Copyright (C) 2017 Marco Willems
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
package be.marcowillems.redrouter.util;

import be.marcowillems.redrouter.data.Player;

/**
 *
 * @author Marco Willems
 */
public class BadgeBoosts {

    public static final int MAX = 99;
    public static final int MIN = 0;

    private final int[] values;

    public BadgeBoosts() {
        values = new int[4];
    }

    /**
     * Get default badge boosts from player
     *
     * @param player
     */
    public BadgeBoosts(Player player) {
        values = new int[4];
        if (player != null) {
            values[0] = player.atkBadge ? 1 : 0;
            values[1] = player.defBadge ? 1 : 0;
            values[2] = player.spdBadge ? 1 : 0;
            values[3] = player.spcBadge ? 1 : 0;
        }
    }

    public BadgeBoosts(int atk, int def, int spd, int spc) {
        values = new int[]{atk, def, spd, spc};
        for (int i = 0; i < values.length; i++) {
            if (values[i] < MIN) {
                values[i] = MIN;
            } else if (values[i] > MAX) {
                values[i] = MAX;
            }
        }
    }

    public BadgeBoosts(BadgeBoosts badgeBoosts) {
        values = badgeBoosts.values.clone();
    }

    public int getAtk() {
        return values[0];
    }

    public int getDef() {
        return values[1];
    }

    public int getSpd() {
        return values[2];
    }

    public int getSpc() {
        return values[3];
    }

}
