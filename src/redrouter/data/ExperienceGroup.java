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

/**
 *
 * @author Marco Willems
 */
public class ExperienceGroup {

    private static final String[] groupStrings = new String[]{"Slow", "Medium Slow", "Medium Fast", "Fast"};
    private static final ExperienceGroup[] groups = new ExperienceGroup[]{new ExperienceGroup(Group.SLOW), new ExperienceGroup(Group.MEDIUM_SLOW), new ExperienceGroup(Group.MEDIUM_FAST), new ExperienceGroup(Group.FAST)};

    public enum Group {

        SLOW, MEDIUM_SLOW, MEDIUM_FAST, FAST
    }

    public final Group group;
    private final int[] expCurve = new int[101]; // including lvl 0 for ease

    private ExperienceGroup(Group group) {
        this.group = group;
        initExpCurve();
    }

    // TODO: how is this implemented!?
    private void initExpCurve() {
        if (this.group == Group.FAST) {
            for (int l = 0; l < expCurve.length; l++) {
                expCurve[l] = 4 * l * l * l / 5;
            }
        } else if (this.group == Group.MEDIUM_FAST) {
            for (int l = 0; l < expCurve.length; l++) {
                expCurve[l] = l * l * l;
            }
        } else if (this.group == Group.MEDIUM_SLOW) {
            for (int l = 0; l < expCurve.length; l++) {
                expCurve[l] = 6 * l * l * l / 5 - 15 * l * l + 100 * l - 140;
            }
        } else if (this.group == Group.SLOW) {
            for (int l = 0; l < expCurve.length; l++) {
                expCurve[l] = 5 * l * l * l / 4;
            }
        }
    }

    public static ExperienceGroup getExperienceGroup(String name) {
        int gr = -1;
        for (int i = 0; i < groupStrings.length; i++) {
            if (groupStrings[i].equals(name)) {
                gr = i;
            }
        }
        if (gr >= 0) {
            return groups[gr];
        } else {
            return null; // TODO: throw exception?
//            return new ExperienceGroup(Group.FAST); // DEFAULT
        }
    }

    public int getDeltaExp(int fromLevel, int toLevel) {
        return getDeltaExp(fromLevel, toLevel, 0);
    }

    public int getDeltaExp(int fromLevel, int toLevel, int levelExp) {
        if (toLevel <= fromLevel) {
            return 0;
        } else {
            int exp = getTotalExp(toLevel) - getTotalExp(fromLevel, levelExp);
            return Math.max(exp, 0);
        }
    }

    public int getLevel(int totalExp) {
        int l = 0;
        while (l <= 100 && expCurve[l] <= totalExp) {
            l++;
        }
        return l - 1;
    }

    public int getLevelExp(int totalExp) {
        int level = getLevel(totalExp);
        return totalExp - expCurve[level];
    }

    public int getTotalExp(int level) {
        return getTotalExp(level, 0);
    }

    public int getTotalExp(int level, int levelExp) {
        return expCurve[level] + levelExp;
    }

}
