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

import java.util.ArrayList;
import java.util.List;

/**
 * TODO TODO: testing item management TODO: not extending from Trainer?
 *
 * @author Marco Willems
 */
public class Player {

    public final String name;
    public final String info;
    public final List<CombinedBattler> team;

    private int money = 0;
    private final ItemSlot[] bagItems = new ItemSlot[20];
    private final ItemSlot[] pcItems = new ItemSlot[50]; // TODO

    boolean atkBadge = false;
    boolean defBadge = false;
    boolean spdBadge = false;
    boolean spcBadge = false;

    public Player(Location location, String name, String info, List<CombinedBattler> team) {
        this.name = name;
        this.info = info;
        if (team == null) {
            this.team = new ArrayList<>();
        } else {
            this.team = team;
        }
    }

    public void swapToFront(CombinedBattler battler) {
        int index = team.indexOf(battler);
        if (index != -1) {
            team.set(index, team.get(0));
            team.set(0, battler);
        }
    }

    public CombinedBattler getLead() {
        return team.get(0);
    }

    public boolean addItem(Item item) {
        return addItem(item, 1, false);
    }

    public boolean addItem(Item item, int quantity) {
        return addItem(item, quantity, false);
    }

    // TODO: overflow to next slot if count > ..
    public boolean addItem(Item item, int quantity, boolean pc) {
        ItemSlot[] items;
        if (pc) {
            items = pcItems;
        } else {
            items = bagItems;
        }
        int index = getItemIndex(item, pc);
        if (index > 0 || (index < 0 && items[19] != null)) {
            if (index > 0) {
                items[index].count++;
            } else {
                index = 0;
                while (items[index] != null) {
                    index++;
                }
                items[index] = new ItemSlot(item, 0);
            }
            return true;
        } else {
            return false;
        }
    }

    public boolean buyItem(Item item, int quantity) {
        if (money < item.price * quantity) {
            return false;
        } else {
            boolean success = addItem(item, quantity);
            if (success) {
                money -= item.price;
            }
            return success;
        }
    }

    public boolean swapItems(int index1, Item item2, boolean pc) {
        return swapItems(index1, getItemIndex(item2, pc), pc);
    }

    public boolean swapItems(Item item1, Item item2, boolean pc) {
        return swapItems(getItemIndex(item1, pc), getItemIndex(item2, pc), pc);
    }

    public boolean swapItems(int index1, int index2, boolean pc) {
        ItemSlot[] items;
        if (pc) {
            items = pcItems;
        } else {
            items = bagItems;
        }
        if (index1 < 0 || index1 >= items.length || items[index1] == null
                || index2 < 0 || index2 >= items.length || items[index2] == null
                || index1 == index2) {
            return false;
        } else {
            ItemSlot temp = items[index1];
            items[index1] = items[index2];
            items[index2] = temp;
            return true;
        }
    }

    public boolean toPC(int bagIndex, int quantity) {
        if (bagIndex < 0 || bagIndex >= bagItems.length || bagItems[bagIndex] == null || pcItems[pcItems.length - 1] != null) {
            return false;
        } else {
            boolean success = addItem(bagItems[bagIndex].item, quantity, true);
            if (success) {
                tossItem(bagIndex, quantity, false, true);
            }
            return success;
        }
    }

    public boolean fromPC(int pcIndex, int quantity) {
        if (pcIndex < 0 || pcIndex >= pcItems.length || pcItems[pcIndex] == null || bagItems[bagItems.length - 1] != null) {
            return false;
        } else {
            boolean success = addItem(pcItems[pcIndex].item, quantity, true);
            if (success) {
                tossItem(pcIndex, quantity, true, true);
            }
            return success;
        }
    }

    public boolean tossItem(Item item, int quantity) {
        return tossItem(item, quantity, false, false);
    }

    public boolean tossItem(Item item, int quantity, boolean pc, boolean fromToPC) {
        return tossItem(getItemIndex(item, pc), quantity, pc, fromToPC);
    }

    public boolean tossItem(int index, int quantity) {
        return tossItem(index, quantity, false, false);
    }

    public boolean tossItem(int index, int quantity, boolean pc, boolean fromToPC) {
        ItemSlot[] items;
        if (pc) {
            items = pcItems;
        } else {
            items = bagItems;
        }
        if (index < 0 || index >= items.length || items[index] == null || (!items[index].item.isTossable() && !fromToPC)) {
            return false;
        } else {
            if (quantity == -1) {
                items[index].count = 0;
            } else {
                items[index].count -= quantity;
            }
            if (items[index].count <= 0) {
                while (index + 1 < items.length && items[index + 1] != null) {
                    items[index] = items[index + 1];
                    items[index + 1] = null;
                    index++;
                }
            }
            return true;
        }
    }

    public boolean useItem(Item item) {
        return useItem(item, -1, null);
    }

    public boolean useItem(Item item, int partyIndex) {
        return useItem(item, partyIndex, null);
    }

    public boolean useItem(Item item, Battle battle) {
        return useItem(item, -1, battle);
    }

    public boolean useItem(Item item, int partyIndex, Battle battle) {
        int index = getItemIndex(item, false);
        if (index < 0) {
            return false;
        } else {
            boolean success = false;
            if (battle != null && item.usableInBattle) {
                success = item.use(battle, partyIndex);
            } else if (item.usableOutBattle) {
                success = item.use(this, partyIndex);
            }
            if (success) {
                tossItem(index, 1);
            }
            return success;
        }
    }

    private int getItemIndex(Item item, boolean pc) {
        ItemSlot[] items;
        if (pc) {
            items = pcItems;
        } else {
            items = bagItems;
        }
        int index = 0;
        boolean found = false;

        while (!found && index < items.length) {
            if (items[index].isItem(item)) {
                found = true;
            } else {
                index++;
            }
        }
        if (index == items.length) {
            index = -1;
        }

        return index;
    }

    private class ItemSlot {

        Item item;
        int count;

        public ItemSlot(Item item, int count) {
            this.item = item;
            this.count = count;
        }

        public boolean isItem(Item item) {
            return this.item.equals(item);
        }

        @Override
        public String toString() {
            return item.toString() + " x" + count;
        }
    }

}
