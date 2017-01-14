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
package be.marcowillems.redrouter.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * TODO: testing item management, pc pokemon, ...
 *
 * @author Marco Willems
 */
public class Player {

    public final String name;
    public final String info;
    public final List<Battler> team;

    private Location currentLocation = null;
    private int money = 0;
    private final ItemSlot[] bagItems = new ItemSlot[20];
    private final ItemSlot[] pcItems = new ItemSlot[50]; // TODO

    public boolean atkBadge = false;
    public boolean defBadge = false;
    public boolean spdBadge = false;
    public boolean spcBadge = false;

    public Player(String name, String info, List<Battler> team, Location currentLocation) {
        this.name = name;
        this.info = info;
        if (team == null) {
            this.team = new ArrayList<>();
        } else {
            this.team = team;
        }
        this.currentLocation = currentLocation;
    }

    public Player getDeepCopy() {
        Player newPlayer = new Player(this.name, this.info, null, currentLocation);

        for (int i = 0; i < this.team.size(); i++) {
            newPlayer.team.add(this.team.get(i).getDeepCopy());
        }
        newPlayer.money = this.money;
        for (int i = 0; i < 20; i++) {
            if (this.bagItems[i] != null) {
                newPlayer.bagItems[i] = new ItemSlot(this.bagItems[i].item, this.bagItems[i].count);
            }
        }
        for (int i = 0; i < 50; i++) {
            if (this.pcItems[i] != null) {
                newPlayer.pcItems[i] = new ItemSlot(this.pcItems[i].item, this.pcItems[i].count);
            }
        }
        newPlayer.atkBadge = this.atkBadge;
        newPlayer.defBadge = this.defBadge;
        newPlayer.spdBadge = this.spdBadge;
        newPlayer.spcBadge = this.spcBadge;

        return newPlayer;
    }

    public void addBattler(Battler battler) {
        team.add(battler);
    }

    public void swapBattlers(int index1, int index2) {
        if (index1 >= 0 && index1 < team.size() && index2 >= 0 && index2 < team.size()) {
            Battler battler = team.get(index1);
            team.set(index1, team.get(index2));
            team.set(index2, battler);
        }
    }

    public Battler getFrontBattler() {
        return team.isEmpty() ? null : team.get(0);
    }

    public void swapToFront(int index) {
        if (index >= 0 && index < team.size()) {
            Battler battler = team.get(index);
            team.set(index, team.get(0));
            team.set(0, battler);
        }
    }

    public void swapToFront(Battler battler) {
        swapToFront(team.indexOf(battler));
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
        if (index < 0 || index >= items.length || items[index] == null || (!items[index].item.tossable && !fromToPC)) {
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

    public Location getCurrentLocation() {
        return this.currentLocation;
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }

    public void addMoney(int coins) {
        if (coins > 0) {
            this.money += coins;
        }
    }

    public int getMoney() {
        return this.money;
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

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof Player) {
            Player p = (Player) obj;
            if (!(name.equals(p.name) && info.equals(p.info) && money == p.money)) {
                return false;
            }
            for (int i = 0; i < bagItems.length; i++) {
                if (bagItems[i] == null) {
                    if (p.bagItems[i] != null) {
                        return false;
                    }
                } else {
                    if (!bagItems[i].equals(p.bagItems[i])) {
                        return false;
                    }
                }
            }
            for (int i = 0; i < pcItems.length; i++) {
                if (pcItems[i] == null) {
                    if (p.pcItems[i] != null) {
                        return false;
                    }
                } else {
                    if (!pcItems[i].equals(p.pcItems[i])) {
                        return false;
                    }
                }
            }
            if (team.size() != p.team.size()) {
                return false;
            } else {
                for (int i = 0; i < team.size(); i++) {
                    if (!team.get(i).equals(p.team.get(i))) {
                        return false;
                    }
                }
            }
            return atkBadge == p.atkBadge
                    && defBadge == p.defBadge
                    && spdBadge == p.spdBadge
                    && spcBadge == p.spcBadge;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 43 * hash + Objects.hashCode(this.name);
        hash = 43 * hash + Objects.hashCode(this.info);
        hash = 43 * hash + Objects.hashCode(this.team);
        hash = 43 * hash + this.money;
        hash = 43 * hash + Arrays.deepHashCode(this.bagItems);
        hash = 43 * hash + Arrays.deepHashCode(this.pcItems);
        hash = 43 * hash + (this.atkBadge ? 1 : 0);
        hash = 43 * hash + (this.defBadge ? 1 : 0);
        hash = 43 * hash + (this.spdBadge ? 1 : 0);
        hash = 43 * hash + (this.spcBadge ? 1 : 0);
        return hash;
    }

    private class ItemSlot { // TODO to util package

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

        @Override
        public boolean equals(Object obj) {
            if (obj != null && obj instanceof ItemSlot) {
                return this.item == ((ItemSlot) obj).item && this.count == ((ItemSlot) obj).count;
            } else {
                return false;
            }
        }
    }

}
