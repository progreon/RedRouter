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
package redrouter.io;

/**
 * TODO
 *
 * @author Marco Willems
 */
public class PrintSettings {

    private static final int PARSABLE = 0;
    private static final int READABLE = 1;
    private static final int TO_HTML = 2;

    private int option = 0;
    private boolean ommitDirections_ = false;

    public boolean ommitDirections() {
        return ommitDirections_;
    }

    public boolean parsable() {
        return option == PARSABLE;
    }

    public boolean readable() {
        return option == READABLE;
    }

    public boolean toHtml() {
        return option == TO_HTML;
    }

    public void setOmmitDirections(boolean ommitDirections) {
        this.ommitDirections_ = ommitDirections;
    }

    public void setParsable() {
        this.option = PARSABLE;
    }

    public void setReadable() {
        this.option = READABLE;
    }

    public void setToHtml() {
        this.option = TO_HTML;
    }
}
