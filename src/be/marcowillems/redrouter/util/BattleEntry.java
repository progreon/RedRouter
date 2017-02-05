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
package be.marcowillems.redrouter.util;

import be.marcowillems.redrouter.data.Battler;
import be.marcowillems.redrouter.data.Move;
import be.marcowillems.redrouter.data.Player;
import be.marcowillems.redrouter.route.RouteBattle;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author Marco Willems
 */
public class BattleEntry {

    public final Battler battlerOpp;
    public final Battler battlerPl;
    public final boolean getsExperience;
    public final boolean isTrainerBattle;

    private Player player = null;
    private Stages stagesOpp = new Stages();
    private Stages stagesPl = new Stages();
    private BadgeBoosts badgeBoosts = new BadgeBoosts();

    public BattleEntry(Battler battlerOpp, Battler battlerPl, boolean getsExperience, boolean isTrainerBattle) {
        this.battlerOpp = battlerOpp;
        this.battlerPl = battlerPl;
        this.getsExperience = getsExperience;
        this.isTrainerBattle = isTrainerBattle;
    }

    public BattleEntry(Battler battlerOpp, Battler battlerPl, boolean getsExperience) {
        this(battlerOpp, battlerPl, getsExperience, true);
    }

    public BattleEntry(Battler battlerOpp, Battler battlerPl) {
        this(battlerOpp, battlerPl, true);
    }

    public BattleEntry(RouteBattle.RouteBattleEntry rbe) {
        this(rbe.getBattlerOpponent(), rbe.getBattlerPlayer(), rbe.getsExperience(), true);
        this.player = rbe.getPlayer();
        this.stagesPl = rbe.getStagesPlayer();
        this.badgeBoosts = rbe.getBadgeBoosts();
    }

    public BadgeBoosts getBadgeBoosts() {
        return badgeBoosts;
    }

    /**
     * Taking the active badges of the player into account.
     *
     * @return
     */
    public BadgeBoosts getActualBadgeBoosts() {
        if (player != null) {
            // Only apply badge boosts when the player has the badge
            int atk = player.atkBadge ? badgeBoosts.getAtk() : 0;
            int def = player.defBadge ? badgeBoosts.getDef() : 0;
            int spd = player.spdBadge ? badgeBoosts.getSpd() : 0;
            int spc = player.spcBadge ? badgeBoosts.getSpc() : 0;
            return new BadgeBoosts(atk, def, spd, spc);
        } else {
            return badgeBoosts;
        }
    }

    public void setBadgeBoosts(int bbAtk, int bbDef, int bbSpd, int bbSpc) {
        badgeBoosts = new BadgeBoosts(bbAtk, bbDef, bbSpd, bbSpc);
    }

    public Stages getStagesOpponent() {
        return stagesOpp;
    }

    public void setStagesOpponent(int sAtk, int sDef, int sSpd, int sSpc) {
        stagesOpp = new Stages(sAtk, sDef, sSpd, sSpc);
    }

    public Stages getStagesPlayer() {
        return stagesPl;
    }

    public void setStagesPlayer(int sAtk, int sDef, int sSpd, int sSpc) {
        stagesPl = new Stages(sAtk, sDef, sSpd, sSpc);
    }

    public Map<Move, Move.DamageRange> getOpponentRanges() {
        Map<Move, Move.DamageRange> ranges = new LinkedHashMap<>();

        for (Move m : battlerOpp.getMoveset()) {
            ranges.put(m, m.getDamageRange(battlerOpp, battlerPl, stagesOpp, stagesPl, new BadgeBoosts(), getActualBadgeBoosts()));
        }

        return ranges;
    }

    public Map<Move, Move.DamageRange> getPlayerRanges() {
        Map<Move, Move.DamageRange> ranges = new LinkedHashMap<>();

        for (Move m : battlerPl.getMoveset()) {
            ranges.put(m, m.getDamageRange(battlerPl, battlerOpp, stagesPl, stagesOpp, getActualBadgeBoosts(), new BadgeBoosts()));
        }

        return ranges;
    }

}
