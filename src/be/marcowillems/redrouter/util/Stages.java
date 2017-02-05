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

/**
 * Stages go from -6 to +6.
 *
 * @author Marco Willems
 */
public class Stages {

    public static final int MAX = 6;
    public static final int MIN = -6;

    private final int[] values;

    public Stages() {
        values = new int[4];
    }

    public Stages(int atk, int def, int spd, int spc) {
        this.values = new int[]{atk, def, spd, spc};
        for (int i = 0; i < values.length; i++) {
            if (values[i] < MIN) {
                values[i] = MIN;
            } else if (values[i] > MAX) {
                values[i] = MAX;
            }
        }
    }

    public Stages(Stages stages) {
        values = stages.values.clone();
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
    
    /**
     * Bounds are not checked
     *
     * @param idx
     * @return
     */
    public int getValue(int idx) {
        return values[idx];
    }

}
