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

import redrouter.data.Battler;
import redrouter.data.Player;
import redrouter.data.Trainer;

/**
 *
 * @author Marco Willems
 */
public class RouteBattle extends RouteEntry {

    public final Trainer opponent;
//    private final int[][] xItemsPerBattler;
//    private final int[] extraBadgeBoosts;
    private final RouteBattleEntry[] entries;

    public RouteBattle(RouteSection parentSection, RouteEntryInfo info, Trainer opponent) {
        super(parentSection, info);
        this.opponent = opponent;
        
        entries = new RouteBattleEntry[opponent.team.size()];
        for (int i = 0; i < entries.length; i++) {
            entries[i] = new RouteBattleEntry(i);
        }
    }

    @Override
    protected void apply(Player p) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String toString() {
        String s = info.toString() + "\n";
        s += opponent.toString();
//        if (info != null) {
//            s += "\n\n\t" + info;
//        }
        return s;
    }

    public class RouteBattleEntry {

        final int opponentTeamIndex;
        Battler playerBattler = null;

        int[] xItems = new int[5]; // xAtk, xDef, xSpd, xSpc, xAcc
        int extraBadgeBoosts = 0;

        public RouteBattleEntry(int opponentTeamIndex) {
            this.opponentTeamIndex = opponentTeamIndex;
        }

        public void setXItems(int xAtk, int xDef, int xSpd, int xSpc, int xAcc) {
            int i = opponentTeamIndex;
            xItems[0] = xAtk;
            xItems[1] = xDef;
            xItems[2] = xSpd;
            xItems[3] = xSpc;
            xItems[4] = xAcc;
            if (i < entries.length - 1) {
                RouteBattleEntry next = entries[i+1];
                int newAtk = Math.max(next.xItems[0], xItems[0]);
                int newDef = Math.max(next.xItems[1], xItems[1]);
                int newSpd = Math.max(next.xItems[2], xItems[2]);
                int newSpc = Math.max(next.xItems[3], xItems[3]);
                int newAcc = Math.max(next.xItems[4], xItems[4]);
                next.setXItems(newAtk, newDef, newSpd, newSpc, newAcc);
            }
        }

    }

}
