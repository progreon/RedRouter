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
package be.marcowillems.redrouter.util;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Marco Willems
 */
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

    public int[] getValues() {
        int[] values = new int[dvs.size()];
        for (int i = 0; i < values.length; i++) {
            values[i] = dvs.get(i);
        }
        return values;
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
