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

import java.util.List;
import redrouter.data.SingleBattler;
import redrouter.data.EncounterArea;
import redrouter.data.Player;

/**
 * TODO: rework this completely!
 *
 * @author Marco Willems
 */
public class RouteEncounter extends RouteEntry {

    private final List<SingleBattler> choices;
    private final int preference;
    // Use this for DSUM later, maybe?
    private final EncounterArea area;
    private final int[] slots;

    public RouteEncounter(RouteSection parentSection, RouteEntryInfo info, EncounterArea area, List<SingleBattler> choices) {
        this(parentSection, info, area, choices, -1);
    }

    public RouteEncounter(RouteSection parentSection, RouteEntryInfo info, EncounterArea area, List<SingleBattler> choices, int preference) {
        super(parentSection, info);
        this.area = area;
        if (choices == null && area != null) {
            this.choices = area.getUniqueBattlers();
            this.slots = new int[area.slots.length];
            for (int i = 0; i < this.slots.length; i++) {
                this.slots[i] = i;
            }
        } else {
            this.choices = choices;
            if (area != null) {
                this.slots = area.getSlots(choices);
            } else {
                this.slots = new int[0];
            }
        }
        if (preference >= this.choices.size() || preference < -1) {
            this.preference = -1;
        } else {
            this.preference = preference;
        }
    }

    public RouteEncounter(RouteSection parentSection, RouteEntryInfo info, EncounterArea area, int[] slots) {
        this(parentSection, info, area, slots, -1);
    }

    public RouteEncounter(RouteSection parentSection, RouteEntryInfo info, EncounterArea area, int[] slots, int preference) {
        super(parentSection, info);
        this.area = area;
        if (slots == null || slots.length > area.slots.length) {
            this.slots = new int[area.slots.length];
            for (int i = 0; i < this.slots.length; i++) {
                this.slots[i] = i;
            }
        } else {
            this.slots = slots;
        }
        this.choices = area.getBattlers(slots);
        if (preference >= this.slots.length || preference < -1) {
            this.preference = -1;
        } else {
            this.preference = preference;
        }
    }

    public EncounterArea getArea() {
        return this.area;
    }

    public List<SingleBattler> getChoices() {
        return this.choices;
    }

    public int getPreference() {
        return this.preference;
    }

    @Override
    protected Player apply(Player p) {
        Player newPlayer = super.apply(p).getDeepCopy();

        if (this.preference >= 0) {
            newPlayer.getFrontBattler().defeatBattler(this.choices.get(this.preference), 1);
        }

        return newPlayer;
    }

    @Override
    public String toString() {
        String str = info + " Choices: ";

        for (int i = 0; i < choices.size(); i++) {
            str += choices.get(i) + ", ";
        }
        if (choices.size() > 0) {
            str = str.substring(0, str.length() - 2);
        }

        return str;
    }

}
