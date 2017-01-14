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
import be.marcowillems.redrouter.data.Item;
import be.marcowillems.redrouter.data.Move;
import be.marcowillems.redrouter.data.Player;
import be.marcowillems.redrouter.io.PrintSettings;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO: item management in Player
 *
 * @author Marco Willems
 */
public class RouteMenu extends RouteEntry {

    private List<Entry> entries = new ArrayList<>();

    public RouteMenu(Route route, String description) {
        super(route, new RouteEntryInfo("Menu", description), true);
    }

    @Override
    protected Player apply(Player p) {
        Player newPlayer = super.apply(p);
        for (Entry e : entries) {
            e.apply(newPlayer);
        }
        return newPlayer;
    }

    public List<Entry> getEntries() {
        return entries;
    }

    public void addDescription(String description) {
        entries.add(new DescriptionEntry(description));
    }

    public void addSwap(Item item1, int item2Idx) {
        // TODO: pc items => RoutePC?
        entries.add(new SwapEntry(item1, item2Idx));
    }

    public void addSwap(Item item1, Item item2) {
        // TODO: pc items => RoutePC?
        entries.add(new SwapEntry(item1, item2));
    }

    public void addTM(Item tm, int partyIdx) {
        addTM(tm, partyIdx, null);
    }

    public void addTM(Item tm, int partyIdx, Move moveReplaced) {
        entries.add(new TMEntry(tm, partyIdx, moveReplaced));
    }

    public void addTM(Move tm, int partyIdx) {
        addTM(tm, 0, null);
    }

    public void addTM(Move tm, int partyIdx, Move moveReplaced) {
        entries.add(new TMEntry(tm, partyIdx, moveReplaced));
    }

    public void addToss(Item item) {
        addToss(item, 1);
    }

    public void addToss(Item item, int count) {
        entries.add(new TossEntry(item, count));
    }

    public void addUse(Item item) {
        addUse(item, 1);
    }

    public void addUse(Item item, int count) {
        addUse(item, count, 0);
    }

    public void addUse(Item item, int count, int partyIdx) {
        addUse(item, count, partyIdx, null);
    }

    public void addUse(Item item, int count, int partyIdx, Move move) {
        entries.add(new UseEntry(item, count, partyIdx, move));
    }

    @Override
    public String toString() {
        return info.toString();
    }

    @Override
    public String writeToString(int depth, PrintSettings ps) {
        String s = lineToDepth(info.toString(), depth);
        for (Entry e : entries) {
            s += e.writeToString(depth + 1, ps);
        }
        return s;
    }

    public static enum Action {

        TOSS, SWAP, TM, USE
    }

    public static Action getAction(String action) {
        Action a;
        switch (action) { // TODO: dynamic!!
            case "TOSS":
                a = Action.TOSS;
                break;
            case "SWAP":
                a = Action.SWAP;
                break;
            case "TM":
                a = Action.TM;
                break;
            case "USE":
                a = Action.USE;
                break;
            default:
                a = null;
                break;
        }
        return a;
    }

    public abstract class Entry {

        protected final Action action;
        protected final Item item;

        public Entry(Action action, Item item) {
            this.action = action;
            this.item = item;
        }

        public abstract void apply(Player p);

        @Override
        public abstract String toString();

        public String writeToString(int depth, PrintSettings ps) {
            return lineToDepth(action + ": " + item, depth);
        }

    }

    private class DescriptionEntry extends Entry {

        final String description;

        public DescriptionEntry(String description) {
            super(null, null);
            this.description = description;
        }

        @Override
        public void apply(Player p) {
        }

        @Override
        public String toString() {
            return description;
        }

        @Override
        public String writeToString(int depth, PrintSettings ps) {
            return lineToDepth(description, depth);
        }

    }

    private class SwapEntry extends Entry {

        final Item item2;
        final int item2Idx;

        SwapEntry(Item item1, Item item2) {
            this(item1, item2, -1);
        }

        SwapEntry(Item item1, int item2Idx) {
            this(item1, null, item2Idx);
        }

        private SwapEntry(Item item1, Item item2, int item2Idx) {
            super(Action.SWAP, item1);
            this.item2 = item2;
            this.item2Idx = item2Idx;
        }

        @Override
        public void apply(Player p) {
            showMessage(RouterMessage.Type.WARNING, this + ": Swapping items is not implemented yet! (ignoring)");
        }

        @Override
        public String toString() {
            String s = "Swap " + item + " with ";
            s += (item2 == null ? "slot " + item2Idx : item2);
            return s;
        }

        @Override
        public String writeToString(int depth, PrintSettings ps) {
            String s = super.writeToString(depth, ps);
            s += " :: " + (item2 == null ? item2Idx : item2);
            return s;
        }

    }

    private class TMEntry extends Entry {

        Move tmMove;
        int partyIdx;
        Move replacedMove;

        String pokemon = "??";

        public TMEntry(Item tm, int partyIdx, Move replacedMove) {
            super(Action.TM, tm);
            this.tmMove = (tm != null ? getRoute().rd.getTMMove(tm) : null);
            this.partyIdx = partyIdx;
            this.replacedMove = replacedMove;
        }

        public TMEntry(Move tmMove, int partyIdx, Move replacedMove) {
            this(getRoute().rd.getTM(tmMove), partyIdx, replacedMove);
            this.tmMove = tmMove; // assign this again for later reference if item equals null
        }

        @Override
        public void apply(Player p) {
            if (partyIdx < 0 || partyIdx > 5) {
                showMessage(RouterMessage.Type.ERROR, this + ": Party index must be between 0 and 5! (ignoring)");
            } else if (partyIdx >= p.team.size()) {
                showMessage(RouterMessage.Type.ERROR, this + ": The player does not have more than " + p.team.size() + " pokemon in his party! (ignoring)");
            } else {
                Battler b = p.team.get(partyIdx);
                pokemon = b.pokemon.toString();
                if (item == null && tmMove == null) {
                    showMessage(RouterMessage.Type.ERROR, this + ": Something went wrong here, did you pass a null TM item? (ignoring)");
                } else if (item == null) {
                    showMessage(RouterMessage.Type.ERROR, this + ": The move " + tmMove + " is not a TM! (ignoring)");
                } else if (tmMove == null) {
                    showMessage(RouterMessage.Type.ERROR, this + ": The item " + item + " is not a TM? (ignoring)");
                } else if (!b.pokemon.getTmMoves().contains(tmMove)) {
                    showMessage(RouterMessage.Type.ERROR, this + ": " + b.pokemon + " can not learn this TM! (ignoring)");
                } else if (b.getMoveset().size() == 4 && replacedMove == null) { // TODO: movesetsize can be > 4 if combined battler!!
                    showMessage(RouterMessage.Type.ERROR, this + ": " + b.pokemon + " has no room left to learn this TM! (ignoring)");
                } else if (b.getMoveset().size() == 4 && !b.getMoveset().contains(replacedMove)) {
                    showMessage(RouterMessage.Type.ERROR, this + ": " + b.pokemon + " has not learned the move " + replacedMove + " and has no room left to learn this TM! (ignoring)");
                } else {
                    if (replacedMove != null && !b.getMoveset().contains(replacedMove)) {
                        showMessage(RouterMessage.Type.WARNING, this + ": " + b.pokemon + " has not learned the move " + replacedMove + " but there is still room to learn the TM");
                    }
                    if (replacedMove != null && !b.pokemon.getAllMoves().contains(replacedMove)) {
                        showMessage(RouterMessage.Type.HINT, this + ": " + b.pokemon + " can not learn the move " + replacedMove + "!");
                    }
                    if (!b.learnTmMove(tmMove, replacedMove)) {
                        showMessage(RouterMessage.Type.ERROR, this + ": Oops, something went wrong!! " + b + " could not learn the TM " + tmMove);
                    }
                }
            }
        }

        @Override
        public String toString() {
            String s = "Teach " + item + " (" + item.value + ")" + " to " + pokemon;
            if (replacedMove != null) {
                s += " in place of " + replacedMove;
            }
            return s;
        }

        @Override
        public String writeToString(int depth, PrintSettings ps) {
            String s = super.writeToString(depth, ps);
            s += " :: " + partyIdx;
            if (replacedMove != null) {
                s += " :: " + replacedMove;
            }
            return s;
        }

    }

    private class TossEntry extends Entry {

        int count;

        public TossEntry(Item item, int count) {
            super(Action.TOSS, item);
            this.count = count;
        }

        @Override
        public void apply(Player p) {
            if (!item.tossable) {
                showMessage(RouterMessage.Type.ERROR, this + ": It's not possible to toss this item! (ignoring)");
            } else {
                showMessage(RouterMessage.Type.WARNING, this + ": Tossing items is not implemented yet! (ignoring)");
            }
        }

        @Override
        public String toString() {
            return "Toss " + count + " of " + item;
        }

        @Override
        public String writeToString(int depth, PrintSettings ps) {
            String s = super.writeToString(depth, ps);
            if (count > 1) {
                s += " :: " + count;
            }
            return s;
        }

    }

    private class UseEntry extends Entry {

        final int count;
        final int partyIdx;
        final Move move;

        String pokemon = "??";

        public UseEntry(Item item, int count, int partyIdx, Move move) {
            super(Action.USE, item);
            this.count = count;
            this.partyIdx = partyIdx;
            this.move = move;
        }

        @Override
        public void apply(Player p) {
            if (!item.usableOutBattle) {
                showMessage(RouterMessage.Type.WARNING, this + ": It's not possble to use this item outside of battle! (ignoring)");
            } else {
                switch (item.type) {
                    case STAT:
                        applyStat(p);
                        break;
                    case STONE:
                        applyStone(p);
                        break;
                    case TM:
                        showMessage(RouterMessage.Type.ERROR, this + ": Wrong menu entry! Use the TM menu entry for this (ignoring)");
                        break;
                    default:
                        showMessage(RouterMessage.Type.WARNING, this + ": Using items is not (fully) implemented yet! (ignoring)");
                        break;
                }
            }
        }

        private void applyStat(Player p) {
            // CALCIUM#TO#9800#STAT:SPC
            if (checkPartyIdx(p)) {
                Battler b = p.team.get(partyIdx);
                switch (item.value) {
                    case "LV":
                        if (b.getLevel() > 100 - count) {
                            showMessage(RouterMessage.Type.HINT, this + ": Not all the candies will be used, level 100 has beeen reached");
                        } else {
                            p.team.set(partyIdx, b.useCandy(count));
                        }
                        break;
                    case "HP":
                        b.useHPUp(count);
                        break;
                    case "ATK":
                        b.useProtein(count);
                        break;
                    case "DEF":
                        b.useIron(count);
                        break;
                    case "SPD":
                        b.useCarbos(count);
                        break;
                    case "SPC":
                        b.useCalcium(count);
                        break;
                    case "PP":
                        showMessage(RouterMessage.Type.WARNING, this + ": PP UP is not implemented yet (ignoring for now)");
                        break;
                    default:
                        showMessage(RouterMessage.Type.ERROR, this + ": Unknown vitamin!");
                        showMessage(RouterMessage.Type.WARNING, this + ": Using items is not (fully) implemented yet! (ignoring)");
                }
            }
        }

        private void applyStone(Player p) {
            // FIRE_STONE#TO#2100#STONE
            if (count < 1 || count > 1) {
                showMessage(RouterMessage.Type.HINT, this + ": This item can only be applied 1 at a time! (ignoring)");
            }
            if (checkPartyIdx(p)) {
                Battler b = p.team.get(partyIdx);
                Battler evo = b.evolve(item);
                if (evo == null) {
                    showMessage(RouterMessage.Type.WARNING, this + ": It is not possible to evolve " + b.pokemon + " with a " + item + "! (ignoring)");
                } else {
                    p.team.set(partyIdx, evo);
                }
            }
        }

        private boolean checkPartyIdx(Player p) {
            boolean isOK = true;
            if (partyIdx < 0 || partyIdx > 5) {
                showMessage(RouterMessage.Type.ERROR, this + ": Invalid party index! (ignoring)");
                isOK = false;
            } else if (partyIdx >= p.team.size()) {
                showMessage(RouterMessage.Type.ERROR, this + ": The player does not have more than " + p.team.size() + " pokemon in his party! (ignoring)");
                isOK = false;
            } else {
                pokemon = p.team.get(partyIdx).pokemon.toString();
            }
            return isOK;
        }

        private boolean checkMove(Battler b) {
            boolean isOK = true;
            if (move == null) {
                showMessage(RouterMessage.Type.ERROR, this + ": Please also enter a move to apply this item to! (ignoring)");
                isOK = false;
            } else if (!b.getMoveset().contains(move)) {
                showMessage(RouterMessage.Type.ERROR, this + ": " + b.pokemon + " does not know this move at the moment! (ignoring)");
                isOK = false;
            }
            return isOK;
        }

        @Override
        public String toString() {
            String s = "Use " + item + (count > 0 ? " " + count + " times" : "");
            if (partyIdx >= 0) {
                s += " on " + pokemon;
                if (move != null) {
                    s += " on " + move;
                }
            }
            return s;
        }

        @Override
        public String writeToString(int depth, PrintSettings ps) {
            String s = super.writeToString(depth, ps);
            if (count >= 0) {
                s += " :: " + count;
                if (partyIdx >= 0) {
                    s += " :: " + partyIdx;
                    if (move != null) {
                        s += " :: " + move;
                    }
                }
            }
            return s;
        }

    }

}
