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

import be.marcowillems.redrouter.data.Battler;
import be.marcowillems.redrouter.data.Player;
import be.marcowillems.redrouter.data.Trainer;
import be.marcowillems.redrouter.io.PrintSettings;
import be.marcowillems.redrouter.util.BadgeBoosts;
import be.marcowillems.redrouter.util.BattleEntry;
import be.marcowillems.redrouter.util.Stages;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 *
 * @author Marco Willems
 */
public class RouteBattle extends RouteEntry {

    public final Trainer opponent;
    private final List<List<RouteBattleEntry>> entries;

    private final List<Player> playerBeforeEvery;
    private final List<Player> playerAfterEvery;

    public RouteBattle(Route route, RouteEntryInfo info, Trainer opponent) {
        this(route, info, opponent, null, null);
    }

    // TODO: only description instead of info => new RouteEntryInfo(opponent.name, description) ??
    public RouteBattle(Route route, RouteEntryInfo info, Trainer opponent, int[][] competingPartyMon, boolean[][] partyMonGetsExp) {
        super(route, info, true);
        if (info == null) {
            super.info = new RouteEntryInfo(opponent.name);
        } else if (info.title == null) {
            super.info = new RouteEntryInfo(opponent.name, info.description);
        }
        this.opponent = opponent;

        this.entries = new ArrayList<>();
        this.playerBeforeEvery = new ArrayList<>();
        this.playerAfterEvery = new ArrayList<>();

        if (opponent.location != null) {
            setLocation(opponent.location);
        }

        // Safety checks
        if (competingPartyMon == null) {
            competingPartyMon = new int[opponent.team.size()][1];
        }
        if (partyMonGetsExp == null) {
            partyMonGetsExp = new boolean[opponent.team.size()][];
            for (int i = 0; i < partyMonGetsExp.length; i++) {
                // TODO: other default?
                partyMonGetsExp[i] = new boolean[competingPartyMon[i].length];
                for (int j = 0; j < partyMonGetsExp[i].length; j++) {
                    partyMonGetsExp[i][j] = true;
                }
            }
        }
        // Add all
        for (int i = 0; i < opponent.team.size(); i++) {
            entries.add(createEntriesForOpponent(i, competingPartyMon[i], partyMonGetsExp[i]));
        }
    }

    private List<RouteBattleEntry> createEntriesForOpponent(int opponentIdx, int[] competingPartyMon, boolean[] partyMonGetsExp) {
        // Safety checks
        if (competingPartyMon == null || competingPartyMon.length == 0) {
            competingPartyMon = new int[]{0};
        }
        if (partyMonGetsExp == null) {
            partyMonGetsExp = new boolean[competingPartyMon.length];
            for (int i = 0; i < partyMonGetsExp.length; i++) {
                partyMonGetsExp[i] = true;
            }
        }
        if (partyMonGetsExp.length < competingPartyMon.length) {
            boolean[] temp = new boolean[competingPartyMon.length];
            System.arraycopy(partyMonGetsExp, 0, temp, 0, partyMonGetsExp.length);
            partyMonGetsExp = temp;
        }

        // Adding
        List<RouteBattleEntry> entries = new ArrayList<>();
        for (int i = 0; i < competingPartyMon.length; i++) {
            RouteBattleEntry be = new RouteBattleEntry(opponentIdx, competingPartyMon[i], partyMonGetsExp[i]);
            entries.add(be);
        }

        return entries;
    }

    @Override
    protected Player apply(Player p) {
        Player newPlayer = super.apply(p);
        playerBeforeEvery.clear();
        playerAfterEvery.clear();

        for (List<RouteBattleEntry> rbes : entries) {
            playerBeforeEvery.add(newPlayer);

            newPlayer = newPlayer.getDeepCopy();
            int n = 0;
            for (RouteBattleEntry be : rbes) {
                if (be.getsExperience) {
                    n++;
                }
            }
            for (RouteBattleEntry be : rbes) {
                if (be.getsExperience) {
                    if (be.partyIdx < 0 || be.partyIdx >= newPlayer.team.size()) {
                        showMessage(RouterMessage.Type.ERROR, "Invalid party index: " + be.partyIdx + " (ignoring)");
                    }
                    Battler updatedBattler = newPlayer.team.get(be.partyIdx).defeatBattler(be.getBattlerOpponent(), n); // TODO: pass isTrainerBattler boolean here instead!!
                    if (be.opponentIdx == entries.size() - 1) { // Only evolve at the end of the battle
                        if (newPlayer.team.set(be.partyIdx, updatedBattler).pokemon != updatedBattler.pokemon) {
                            showMessage(RouterMessage.Type.INFO, "Evolving to " + updatedBattler.pokemon);
                        }
                    }
                }
            }

            playerAfterEvery.add(newPlayer);
        }

        if (opponent.name.equals("Brock")) {
            newPlayer.atkBadge = true;
            showMessage(RouterMessage.Type.INFO, "Attack badge boost aquired!");
        }

        if (opponent.name.equals("Surge")) {
            newPlayer.defBadge = true;
            showMessage(RouterMessage.Type.INFO, "Defense badge boost aquired!");
        }

        if (opponent.name.equals("Koga")) {
            newPlayer.spdBadge = true;
            showMessage(RouterMessage.Type.INFO, "Speed badge boost aquired!");
        }

        if (opponent.name.equals("Blaine")) {
            newPlayer.spcBadge = true;
            showMessage(RouterMessage.Type.INFO, "Special badge boost aquired!");
        }

        setPlayerAfter(newPlayer);
        return newPlayer;
    }

    public List<BattleEntry> getBattleEntries() {
        List<BattleEntry> allBattleEntries = new ArrayList<>();

        for (List<BattleEntry> bes : getBattleEntriesPerOpponent()) {
            allBattleEntries.addAll(bes);
        }

        return allBattleEntries;
    }

    public List<List<BattleEntry>> getBattleEntriesPerOpponent() {
        List<List<BattleEntry>> battleEntriesPerOpponent = new ArrayList<>();

        for (List<RouteBattleEntry> rbes : entries) {
            List<BattleEntry> bes = new ArrayList<>();
            for (RouteBattleEntry rbe : rbes) {
                bes.add(new BattleEntry(rbe));
            }
            battleEntriesPerOpponent.add(bes);
        }

        return battleEntriesPerOpponent;
    }

    @Override
    public String toString() {
        return "Battle " + opponent;
    }

    @Override
    public String writeToString(int depth, PrintSettings ps) {
        if (ps == null) {
            ps = new PrintSettings();
        }
        String str = "B: ";
        String alias = opponent.getIndexString().replaceAll(" ", "").toLowerCase(Locale.ROOT); // TODO: TEMP
        str += alias;
        String options = "";
        for (int i = 0; i < entries.size(); i++) {
            if (entries.get(i).size() != 1 || entries.get(i).get(0).partyIdx != 0) {
                for (RouteBattleEntry rbe : entries.get(i)) {
                    options += " " + i + ":" + rbe.partyIdx;
                }
            }
        }
        if (!options.isEmpty()) {
            str += " ::" + options;
        }
        str = lineToDepth(str, depth);

        if (info != null && info.description != null && !info.description.isEmpty()) {
            String description = "";
            if (info.title != null && !info.title.isEmpty()) {
                description = info.title + " :: ";
            }
            description += info.description;
            str += "\n" + lineToDepth(description, depth + 1);
        }

        return str;
    }

    public class RouteBattleEntry {

        private final int opponentIdx;
        private final int partyIdx;
        private boolean getsExperience;

        private Stages stages = new Stages();
        private BadgeBoosts badgeBoosts = new BadgeBoosts(1, 1, 1, 1); // default all 1

        private RouteBattleEntry(int opponentIdx, int partyIdx, boolean getsExperience) {
            this.opponentIdx = opponentIdx;
            this.partyIdx = partyIdx;
            this.getsExperience = getsExperience;
        }
//
//        private void setDefaultBoostsAndStages(boolean[] badges, int[] xItems) {
//            // TODO: non-hardcoded "4"?
//            if (badges.length != xItems.length || badges.length != 4) {
//                throw new IllegalArgumentException("badges and xItems must have a length of " + 4 + "!");
//            }
//            int xCount = 0;
//            int[] xStages = new int[xItems.length];
//            for (int i = 0; i < xItems.length; i++) {
//                if (xItems[i] > 0) {
//                    xStages[i] = Math.min(Stages.MAX, xItems[i]);
//                    xCount += xStages[i];
//                } else {
//                    xStages[i] = 0;
//                }
//            }
//            stages = new Stages(xStages[0], xStages[1], xStages[2], xStages[3]);
//            int[] xBoosts = new int[xStages.length];
//            for (int i = 0; i < xStages.length; i++) {
//                if (badges[i]) {
//                    xBoosts[i] = 1;
//                } else {
//                    xBoosts[i] = 0;
//                }
//                if (xStages[i] == 0 && badges[i]) {
//                    xBoosts[i] += xCount;
//                }
//            }
//            this.badgeBoosts = new BadgeBoosts(xBoosts[0], xBoosts[1], xBoosts[2], xBoosts[3]);
//        }

        public BadgeBoosts getBadgeBoosts() {
            // TODO move to BattleEntry
//            if (opponentIdx < playerBeforeEvery.size()) {
//                // Only apply badge boosts when the player has the badge
//                Player p = playerBeforeEvery.get(opponentIdx);
//                int atk = p.atkBadge ? badgeBoosts.getAtk() : 0;
//                int def = p.defBadge ? badgeBoosts.getDef() : 0;
//                int spd = p.spdBadge ? badgeBoosts.getSpd() : 0;
//                int spc = p.spcBadge ? badgeBoosts.getSpc() : 0;
//                return new BadgeBoosts(atk, def, spd, spc);
//            } else {
            return this.badgeBoosts; // TODO: clone?
//            }
        }

        public void setBadgeBoosts(BadgeBoosts badgeBoosts) {
            this.badgeBoosts = new BadgeBoosts(badgeBoosts);
            notifyDataUpdated();
            notifyRoute();
        }

        public Battler getBattlerOpponent() {
            return opponent.team.get(opponentIdx);
        }

        public Battler getBattlerPlayer() {
            Player p = getPlayer();
            if (p != null && partyIdx < p.team.size()) {
                return p.team.get(partyIdx);
            } else {
                return null;
            }
        }

        public boolean getsExperience() {
            return this.getsExperience;
        }

        public void setGetsExperience(boolean getsExperience) {
            this.getsExperience = getsExperience;
            notifyDataUpdated();
            notifyRoute();
        }

        public Player getPlayer() {
            if (opponentIdx < playerBeforeEvery.size()) {
                return playerBeforeEvery.get(opponentIdx);
            } else {
                return null;
            }
        }

        public Stages getStagesPlayer() {
            return this.stages; // TODO: clone?
        }

        public void setStagesPlayer(Stages stages) {
            this.stages = new Stages(stages);
            notifyDataUpdated();
            notifyRoute();
        }

    }
}
