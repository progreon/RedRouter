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
package be.marcowillems.redrouter.route;

import java.util.ArrayList;
import java.util.List;
import be.marcowillems.redrouter.data.CombinedBattler;
import be.marcowillems.redrouter.data.Player;
import be.marcowillems.redrouter.data.SingleBattler;
import be.marcowillems.redrouter.io.PrintSettings;

/**
 * TODO split Get and Catch Pokémon? optional pokemon?
 *
 * @author Marco Willems
 */
public class RouteGetPokemon extends RouteEntry {

    private final List<SingleBattler> choices;
    private int preference;

    public RouteGetPokemon(RouteEntryInfo info, SingleBattler choice) {
        this(info, new ArrayList<>());
        choices.add(choice);
        this.preference = 0;
        if (choice.catchLocation != null) {
            setLocation(choice.catchLocation.location);
        }
    }

    public RouteGetPokemon(RouteEntryInfo info, List<SingleBattler> choices) {
        this(info, choices, -1);
    }

    public RouteGetPokemon(RouteEntryInfo info, List<SingleBattler> choices, int preference) {
        super(info, true);
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
            // Temporary hack:
            if (choices.size() > 0 && choices.get(0).catchLocation != null) {
                setLocation(choices.get(0).catchLocation.location);
            }
        }
    }

    @Override
    protected Player apply(Player p) {
        Player newPlayer = super.apply(p);

        SingleBattler pref = getPreference();
        if (pref != null) {
            newPlayer.addBattler(new CombinedBattler(pref));
        }

        return newPlayer;
    }

    public SingleBattler getPreference() {
        if (preference >= 0) {
            return choices.get(preference);
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        String str = "";
        if (super.info != null && (super.info.title != null || super.info.description != null)) {
            if (super.info.title != null) {
                str = super.info.title;
                if (super.info.description != null) {
                    str += "\n\t";
                }
            }
            if (super.info.description != null) {
                str += super.info.description;
            } else if (choices.size() > 0) {
                str += "\n\t";
            }
        }
        if (super.info == null || super.info.description == null) {
            SingleBattler pref = getPreference();
            if (choices.size() > 1) {
                str += "Pick 1 of: ";
                for (SingleBattler b : choices) {
                    str += b + (b == pref ? " (preferece)" : "") + ", ";
                }
                str = str.substring(0, str.length() - 2) + (pref == null ? " (optional)" : "");
            } else if (choices.size() == 1) {
                str += "Get " + choices.get(0) + (pref == null ? " (optional)" : "");
            }
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
