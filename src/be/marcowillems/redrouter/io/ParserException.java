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
 *
 * @author Marco Willems
 */
public class ParserException extends Exception {

    private final String file;
    private final int line;
    private final boolean fromResouce;

    public ParserException(String file, int lineNo, String message) {
        this(file, lineNo, message, true);
    }

    public ParserException(String file, int lineNo, String message, boolean fromResource) {
        super(message);
        this.file = file;
        this.line = lineNo + 1;
        this.fromResouce = fromResource;
    }

    @Override
    public String getMessage() {
        String str = super.getMessage();
        if (file != null && line > 0) {
            str += " (";
            if (file != null) {
                str += (fromResouce ? "resource file " : "") + file;
            }
            if (line > 0) {
                if (file != null) {
                    str += ":" + line;
                } else {
                    str += "line " + line;
                }
            }
            str += ")";
        }
        return str;
    }

}
