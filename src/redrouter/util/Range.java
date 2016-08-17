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
package redrouter.util;

/**
 *
 * @author Marco Willems
 */
public class Range {

    int min, max;

    public Range(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public void combine(Range statRange) {
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

    public boolean containsOneOf(Range statRange) {
        return (this.min <= statRange.min && statRange.min <= this.max) || statRange.containsOneOf(this);
    }

    public Range devideBy(int d) {
        return new Range(min / d, max / d);
    }

    public Range multiplyBy(int m) {
        return new Range(min * m, max * m);
    }

    public Range add(int a) {
        return new Range(min + a, max + a);
    }

    public Range add(Range ra) {
        return new Range(min + ra.min, max + ra.max);
    }

    public Range substract(int s) {
        return new Range(min - s, max - s);
    }

    public Range substract(Range rs) {
        return new Range(min - rs.max, max - rs.min);
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
