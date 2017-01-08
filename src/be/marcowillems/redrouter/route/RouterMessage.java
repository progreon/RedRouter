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
package be.marcowillems.redrouter.route;

/**
 * TODO in RouteEntry: void showInformation(type, message)
 *
 * @author Marco Willems
 */
public class RouterMessage {

    public static enum Type {

        ERROR(3), WARNING(2), HINT(1), INFO(0);

        public final int priority;

        private Type(int priority) {
            this.priority = priority;
        }
    }

    public String file = null;
    public int lineNo = -1;
    public RouteEntry entry = null;
    public final Type type;
    public final String message;

    public RouterMessage(String file, int lineNo, RouteEntry entry, Type type, String message) {
        this(file, lineNo, type, message);
        this.entry = entry;
    }

    public RouterMessage(String file, int lineNo, Type type, String message) {
        this(type, message);
        this.file = file;
        this.lineNo = lineNo;
    }

    public RouterMessage(RouteEntry entry, Type type, String message) {
        this(type, message);
        this.entry = entry;
    }

    public RouterMessage(Type type, String message) {
        this.type = (type == null ? Type.INFO : type);
        this.message = (message == null ? "???" : message);
    }

    @Override
    public String toString() {
        String str = type.toString() + "\t";
        if (file != null) {
            str += " in " + file;
            if (lineNo >= 0) {
                str += " on line " + (lineNo + 1);
            }
        }
        str += ": " + message;
        return str;
    }

}
