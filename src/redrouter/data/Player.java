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
package redrouter.data;

import java.util.List;

/**
 * Temporary placeholder
 *
 * @author Marco Willems
 */
public class Player extends Trainer {

    private boolean atkBadge = false;
    private boolean defBadge = false;
    private boolean spdBadge = false;
    private boolean spcBadge = false;

    public Player(Location location, String name, String info, List<Battler> team) {
        super(location, name, info, team);
    }

    public void swapToFront(Battler battler) {
        int index = super.team.indexOf(battler);
        if (index != -1) {
            super.team.set(index, super.team.get(0));
            super.team.set(0, battler);
        }
    }

    public Battler getLead() {
        return super.team.get(0);
    }

}