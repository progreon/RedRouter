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
package redrouter.route;

import java.util.ArrayList;
import java.util.List;
import redrouter.data.Battler;
import redrouter.data.Protagonist;

/**
 *
 * @author Marco Willems
 */
public class RouteEncounter extends RouteEntry {

    public final List<Battler> choices;
    public final int preference;

    public RouteEncounter(RouteSection parentSection, RouteEntryInfo info, List<Battler> choices) {
        this(parentSection, info, choices, 0);
    }

    public RouteEncounter(RouteSection parentSection, RouteEntryInfo info, List<Battler> choices, int preference) {
        super(parentSection, info);
        if (choices == null) {
            this.choices = new ArrayList<>();
            this.preference = -1;
        } else {
            this.choices = choices;
            if (preference >= choices.size()) {
                this.preference = 0;
            } else {
                this.preference = preference;
            }
        }
    }

    @Override
    public Protagonist apply(Protagonist p) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String toString() {
        String str = info + " Choices: ";

        for (int i = 0; i < choices.size(); i++) {
            str += choices.get(i).toString() + ", ";
        }
        if (choices.size() > 0) {
            str = str.substring(0, str.length() - 2);
        }

        return str;
    }

}
