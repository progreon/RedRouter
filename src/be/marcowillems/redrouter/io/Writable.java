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
package be.marcowillems.redrouter.io;

/**
 * TODO: make abstract class?
 *
 * @author Marco Willems
 */
public abstract class Writable {

    public abstract String writeToString(int depth, PrintSettings ps);

    protected static String lineToDepth(String s, int depth) {
        if (s != null) {
            String newS = "";
            for (int i = 0; i < depth; i++) {
                newS += "\t";
            }
            newS += s;
            return newS;
        } else {
            return null;
        }
    }

}
