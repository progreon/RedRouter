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
import redrouter.data.CombinedBattler;
import redrouter.data.Player;
import redrouter.data.SingleBattler;
import redrouter.io.PrintSettings;

/**
 * TODO split Get and Catch Pokémon
 *
 * @author Marco Willems
 */
public class RouteGetPokemon extends RouteEntry {

    private final List<SingleBattler> choices;
    private final int preference;

    public RouteGetPokemon(RouteSection parentSection, RouteEntryInfo info, SingleBattler choice) {
        super(parentSection, info);
        choices = new ArrayList<>();
        choices.add(choice);
        this.preference = 0;
    }

    public RouteGetPokemon(RouteSection parentSection, RouteEntryInfo info, List<SingleBattler> choices) {
        this(parentSection, info, choices, -1);
    }

    public RouteGetPokemon(RouteSection parentSection, RouteEntryInfo info, List<SingleBattler> choices, int preference) {
        super(parentSection, info);
        if (choices == null) {
            this.choices = new ArrayList<>();
            this.preference = -1;
        } else {
            this.choices = choices;
            if (preference >= choices.size() && preference < -1) {
                this.preference = -1;
            } else {
                this.preference = preference;
            }
        }
    }

    @Override
    protected Player apply(Player p) {
        Player newPlayer = super.apply(p).getDeepCopy();

        if (this.preference >= 0) {
            newPlayer.addBattler(new CombinedBattler(choices.get(preference)));
        }

        return newPlayer;
    }

    @Override
    public String toString() {
        String str = "";
        if (super.info != null) {
            str = super.info + "\n";
        }
        if (choices.size() > 1) {
            str += "Pick 1 of: ";
            for (SingleBattler b : choices) {
                str += b + ", ";
            }
            str = str.substring(0, str.length() - 2);
        } else if (choices.size() == 1) {
            str += "Get " + choices.get(0);
        }
        return str;
    }

    @Override
    public String writeToString(int depth, PrintSettings ps) {
        if (ps == null) {
            ps = new PrintSettings();
        }
        String str = "GetP:";
        for (int i = 0; i < choices.size(); i++) {
            str += " ";
            if (i == preference) {
                str += "#";
            }
            str += choices.get(i).pokemon.name + ":" + choices.get(i).level;
        }
        // TODO description
        return lineToDepth(str, depth);
    }

}
