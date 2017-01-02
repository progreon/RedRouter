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

import be.marcowillems.redrouter.data.Battler;
import be.marcowillems.redrouter.data.Player;
import be.marcowillems.redrouter.data.SingleBattler;
import be.marcowillems.redrouter.data.Trainer;
import be.marcowillems.redrouter.io.PrintSettings;

/**
 * TODO: share exp!
 *
 * @author Marco Willems
 */
public class RouteBattle extends RouteEntry {

    public final Trainer opponent;
    public final RouteBattleEntry[][] entries; // TODO: private & getters?

    private final Player[] playerBeforeEvery;
    private final Player[] playerAfterEvery;

    // TODO: description instead of info => new RouteEntryInfo(opponent.name, description) ??
    public RouteBattle(RouteEntryInfo info, Trainer opponent) {
        super(info, true);
        this.opponent = opponent;

        entries = new RouteBattleEntry[opponent.team.size()][];
        for (int i = 0; i < entries.length; i++) {
            entries[i] = new RouteBattleEntry[1];
            entries[i][0] = new RouteBattleEntry(0);
        }
        playerBeforeEvery = new Player[opponent.team.size()];
        playerAfterEvery = new Player[opponent.team.size()];

        if (opponent.location != null) {
            setLocation(opponent.location);
        }
    }

    public RouteBattle(RouteEntryInfo info, Trainer opponent, int[][] competingPartyMon) {
        this(info, opponent);
        if (competingPartyMon != null && competingPartyMon.length == entries.length) {
            for (int i = 0; i < entries.length; i++) {
                if (competingPartyMon[i].length == 0) {
                    entries[i] = new RouteBattleEntry[1];
                    entries[i][0] = new RouteBattleEntry(0);
                } else {
                    entries[i] = new RouteBattleEntry[competingPartyMon[i].length];
                    for (int j = 0; j < competingPartyMon[i].length; j++) {
                        entries[i][j] = new RouteBattleEntry(competingPartyMon[i][j]);
                    }
                }
            }
        }
    }

    @Override
    protected Player apply(Player p) {
        Player newPlayer = super.apply(p).getDeepCopy();

        for (int i = 0; i < opponent.team.size(); i++) {
            playerBeforeEvery[i] = newPlayer;

            newPlayer = newPlayer.getDeepCopy();
            SingleBattler sb = opponent.team.get(i);
            int n = 0;
            for (int j = 0; j < entries[i].length; j++) {
                if (!entries[i][j].isFainted()) {
                    n++;
                }
            }
            for (int j = 0; j < entries[i].length; j++) {
                if (!entries[i][j].isFainted()) {
                    Battler updatedBattler = newPlayer.team.get(entries[i][j].partyIndex).defeatBattler(sb, n);
                    if (i == opponent.team.size() - 1) { // Only evolve at the end of the battle
                        newPlayer.team.set(entries[i][j].partyIndex, updatedBattler);
                    }
                }
            }

            playerAfterEvery[i] = newPlayer;
        }

        if (opponent.name.equals("Brock")) {
            newPlayer.atkBadge = true;
        }

        if (opponent.name.equals("Surge")) {
            newPlayer.defBadge = true;
        }

        if (opponent.name.equals("Koga")) {
            newPlayer.spdBadge = true;
        }

        if (opponent.name.equals("Blaine")) {
            newPlayer.spcBadge = true;
        }

        return newPlayer;
    }

    public Player[] getPlayersBeforeEvery() {
        return playerBeforeEvery;
    }

    public Player[] getPlayersAfterEvery() {
        return playerAfterEvery;
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

    @Override
    public String writeToString(int depth, PrintSettings ps) {
        if (ps == null) {
            ps = new PrintSettings();
        }
        String str = "B: ";
        String alias = opponent.getIndexString().replaceAll(" ", "");
        str += alias;
        String options = "";
        for (int i = 0; i < entries.length; i++) {
            if (entries[i].length == 1 && entries[i][0].partyIndex == 0) {

            } else {
                for (int j = 0; j < entries[i].length; j++) {
                    options += " " + i + ":" + entries[i][j].partyIndex;
                }
            }
        }
        if (!options.isEmpty()) {
            str += " ::" + options;
        }
        // TODO optional description
        str += " :: " + info.description;

        return lineToDepth(str, depth);
    }

    public class RouteBattleEntry {

        public final int partyIndex;
        private final int[] xItems = new int[5]; // xAtk, xDef, xSpd, xSpc, xAcc

        private int extraBadgeBoosts = 0;
        private boolean fainted = false;

        public RouteBattleEntry(int partyIndex) {
            this.partyIndex = partyIndex;
        }

        public RouteBattleEntry(int partyIndex, int[] xItems) {
            this(partyIndex);
            if (xItems.length == this.xItems.length) {
                for (int i = 0; i < xItems.length; i++) {
                    this.xItems[i] = xItems[i];
                }
            }
        }

        public int[] getXItems() {
            return this.xItems.clone();
        }

        public void setXItemsUsed(int xAtk, int xDef, int xSpd, int xSpc, int xAcc) {
            xItems[0] = xAtk;
            xItems[1] = xDef;
            xItems[2] = xSpd;
            xItems[3] = xSpc;
            xItems[4] = xAcc;
            // TODO: refresh?
        }

        public int getExtraBadgeBoosts() {
            return this.extraBadgeBoosts;
        }

        public void setExtraBadgeBoosts(int extraBadgeBoosts) {
            this.extraBadgeBoosts = extraBadgeBoosts;
        }

        public int getTotalExtraBadgeBoosts() {
            int tot = 0;
            for (int i = 0; i < 5; i++) {
                tot += xItems[i];
            }
            tot += extraBadgeBoosts;
            return tot;
        }

        public boolean isFainted() {
            return this.fainted;
        }

        public void setFainted(boolean isFainted) {
            this.fainted = isFainted;
        }

    }

}
