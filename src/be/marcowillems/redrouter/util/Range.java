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

import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author Marco Willems
 */
public class Range {

    private final Map<Integer, Integer> values;
    private int count;
    private int min, max;

    public Range() {
        count = 0;
        values = new TreeMap<>();
        min = 0;
        max = 0;
    }

    public Range(Range range) {
        this.values = new TreeMap<>();
        this.values.putAll(range.values);
        this.count = range.count;
        this.min = range.min;
        this.max = range.max;
    }

    public void combine(Range range) {
        for (int value : range.values.keySet()) {
            addValue(value, range.values.get(value));
        }
    }

    public int getMin() {
        return this.min;
    }

    public int getMax() {
        return this.max;
    }

    public boolean contains(int value) {
        return min <= value && value <= max;
    }

    public boolean containsOneOf(Range range) {
        return (this.min <= range.min && range.min <= this.max)
                || (range.min <= this.min && this.min <= range.max);
    }

    public int getCount() {
        return count;
    }

    public void addValue(int value) {
        values.put(value, values.getOrDefault(value, 0) + 1);
        count++;
        if (count == 1) {
            min = max = value;
        } else {
            if (value < min) {
                min = value;
            } else if (value > max) {
                max = value;
            }
        }
    }

    private void addValue(int value, int count) {
        values.put(value, values.getOrDefault(value, 0) + count);
        this.count += count;
        if (this.count == count) {
            min = max = value;
        } else {
            if (value < min) {
                min = value;
            } else if (value > max) {
                max = value;
            }
        }
    }

    public int[] getValues() {
        int[] vs = new int[count];
        int pos = 0;
        for (int key : values.keySet()) {
            for (int i = 0; i < values.get(key); i++) {
                vs[pos] = key;
                pos++;
            }
        }
        return vs;
    }

    public Range divideBy(int d) {
        Range newRange = new Range();
        for (int key : values.keySet()) {
            newRange.addValue(key / d, values.get(key));
        }
        return newRange;
    }

    public Range multiplyBy(int m) {
        Range newRange = new Range();
        for (int key : values.keySet()) {
            newRange.addValue(key * m, values.get(key));
        }
        return newRange;
    }

    public Range add(int a) {
        Range newRange = new Range();
        for (int key : values.keySet()) {
            newRange.addValue(key + a, values.get(key));
        }
        return newRange;
    }

    public Range add(Range ra) {
        Range newRange = new Range();
        for (int v1 : values.keySet()) {
            for (int v2 : ra.values.keySet()) {
                newRange.addValue(v1 + v2, values.get(v1) * ra.values.get(v2));
            }
        }
        return newRange;
    }

    public Range substract(int s) {
        Range newRange = new Range();
        for (int key : values.keySet()) {
            newRange.addValue(key - s, values.get(key));
        }
        return newRange;
    }

    public Range substract(Range rs) {
        Range newRange = new Range();
        for (int v1 : values.keySet()) {
            for (int v2 : rs.values.keySet()) {
                newRange.addValue(v1 - v2, values.get(v1) * rs.values.get(v2));
            }
        }
        return newRange;
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
