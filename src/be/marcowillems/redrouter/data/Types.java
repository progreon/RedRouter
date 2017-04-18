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

import java.util.HashMap;

/**
 * TODO: dynamic?
 *
 * @author Marco Willems
 */
public class Types {

    private static TypeChart typeChart;
    private static HashMap<Type, Boolean> isPhysical;

    public enum Type {

        NORMAL, FIRE, WATER, ELECTRIC, GRASS, ICE, FIGHTING, POISON, GROUND,
        FLYING, PSYCHIC, BUG, ROCK, GHOST, DRAGON
    }

    public Types() {
        init();
    }

    private static void init() {
        if (typeChart == null) {
            typeChart = new TypeChart();
            isPhysical = new HashMap<>();
            // TODO: check!
            isPhysical.put(Type.NORMAL, true);
            isPhysical.put(Type.FIRE, false);
            isPhysical.put(Type.WATER, false);
            isPhysical.put(Type.ELECTRIC, false);
            isPhysical.put(Type.GRASS, false);
            isPhysical.put(Type.ICE, false);
            isPhysical.put(Type.FIGHTING, true);
            isPhysical.put(Type.POISON, true);
            isPhysical.put(Type.GROUND, true);
            isPhysical.put(Type.FLYING, true);
            isPhysical.put(Type.PSYCHIC, false);
            isPhysical.put(Type.BUG, true);
            isPhysical.put(Type.ROCK, true);
            isPhysical.put(Type.GHOST, true);
            isPhysical.put(Type.DRAGON, false);
        }
    }

    public static boolean isPhysical(Type type) {
        init();
        return isPhysical.get(type);
    }

    public static TypeChart getTypeChart() {
        init();
        return typeChart;
    }

    /**
     * Get a type from its string
     *
     * @param string
     * @return
     */
    public static Type getType(String string) throws IllegalArgumentException {
        if (string.isEmpty()) {
            return null;
        } else {
            return Type.valueOf(string.toUpperCase());
        }
    }

    public static class TypeChart {

        private final HashMap<Type, HashMap<Type, Double>> chart;

        // TODO Check if chart is right
        public TypeChart() {
            chart = new HashMap<>();
            // First: all x1
            for (Type t1 : Type.values()) {
                chart.put(t1, new HashMap<>());
                for (Type t2 : Type.values()) {
                    chart.get(t1).put(t2, 1.0);
                }
            }

            // Second: specific multipliers
            //NORMAL
            chart.get(Type.NORMAL).put(Type.ROCK, 0.5);
            chart.get(Type.NORMAL).put(Type.GHOST, 0.0);
            //FIRE
            chart.get(Type.FIRE).put(Type.FIRE, 0.5);
            chart.get(Type.FIRE).put(Type.WATER, 0.5);
            chart.get(Type.FIRE).put(Type.GRASS, 2.0);
            chart.get(Type.FIRE).put(Type.ICE, 2.0);
            chart.get(Type.FIRE).put(Type.BUG, 2.0);
            chart.get(Type.FIRE).put(Type.ROCK, 0.5);
            chart.get(Type.FIRE).put(Type.DRAGON, 0.5);
            //WATER
            chart.get(Type.WATER).put(Type.FIRE, 2.0);
            chart.get(Type.WATER).put(Type.WATER, 0.5); // #blameneslon
            chart.get(Type.WATER).put(Type.GRASS, 0.5);
            chart.get(Type.WATER).put(Type.GROUND, 2.0);
            chart.get(Type.WATER).put(Type.ROCK, 2.0);
            chart.get(Type.WATER).put(Type.DRAGON, 0.5);
            //ELECTRIC
            chart.get(Type.ELECTRIC).put(Type.WATER, 2.0);
            chart.get(Type.ELECTRIC).put(Type.ELECTRIC, 0.5);
            chart.get(Type.ELECTRIC).put(Type.GRASS, 0.5);
            chart.get(Type.ELECTRIC).put(Type.GROUND, 0.0);
            chart.get(Type.ELECTRIC).put(Type.FLYING, 2.0);
            chart.get(Type.ELECTRIC).put(Type.DRAGON, 0.5);
            //GRASS
            chart.get(Type.GRASS).put(Type.FIRE, 0.5);
            chart.get(Type.GRASS).put(Type.WATER, 2.0);
            chart.get(Type.GRASS).put(Type.GRASS, 0.5);
            chart.get(Type.GRASS).put(Type.POISON, 0.5);
            chart.get(Type.GRASS).put(Type.GROUND, 2.0);
            chart.get(Type.GRASS).put(Type.FLYING, 0.5);
            chart.get(Type.GRASS).put(Type.BUG, 0.5);
            chart.get(Type.GRASS).put(Type.ROCK, 2.0);
            chart.get(Type.GRASS).put(Type.DRAGON, 0.5);
            //ICE
            chart.get(Type.ICE).put(Type.WATER, 0.5);
            chart.get(Type.ICE).put(Type.GRASS, 2.0);
            chart.get(Type.ICE).put(Type.ICE, 0.5);
            chart.get(Type.ICE).put(Type.GROUND, 2.0);
            chart.get(Type.ICE).put(Type.FLYING, 2.0);
            chart.get(Type.ICE).put(Type.DRAGON, 2.0);
            //FIGHTING
            chart.get(Type.FIGHTING).put(Type.NORMAL, 2.0);
            chart.get(Type.FIGHTING).put(Type.ICE, 2.0);
            chart.get(Type.FIGHTING).put(Type.POISON, 0.5);
            chart.get(Type.FIGHTING).put(Type.FLYING, 0.5);
            chart.get(Type.FIGHTING).put(Type.PSYCHIC, 0.5);
            chart.get(Type.FIGHTING).put(Type.BUG, 0.5);
            chart.get(Type.FIGHTING).put(Type.ROCK, 2.0);
            chart.get(Type.FIGHTING).put(Type.GHOST, 0.0);
            //POISON
            chart.get(Type.POISON).put(Type.GRASS, 2.0);
            chart.get(Type.POISON).put(Type.POISON, 0.5);
            chart.get(Type.POISON).put(Type.GROUND, 0.5);
            chart.get(Type.POISON).put(Type.BUG, 2.0);
            chart.get(Type.POISON).put(Type.ROCK, 0.5);
            chart.get(Type.POISON).put(Type.GHOST, 0.5);
            //GROUND
            chart.get(Type.GROUND).put(Type.FIRE, 2.0);
            chart.get(Type.GROUND).put(Type.ELECTRIC, 2.0);
            chart.get(Type.GROUND).put(Type.GRASS, 0.5);
            chart.get(Type.GROUND).put(Type.POISON, 2.0);
            chart.get(Type.GROUND).put(Type.FLYING, 0.0);
            chart.get(Type.GROUND).put(Type.BUG, 0.5);
            chart.get(Type.GROUND).put(Type.ROCK, 2.0);
            //FLYING
            chart.get(Type.FLYING).put(Type.ELECTRIC, 0.5);
            chart.get(Type.FLYING).put(Type.GRASS, 2.0);
            chart.get(Type.FLYING).put(Type.FIGHTING, 2.0); // #blameneslon
            chart.get(Type.FLYING).put(Type.BUG, 2.0); // #blameneslon
            chart.get(Type.FLYING).put(Type.ROCK, 0.5);
            //PSYCHIC
            chart.get(Type.PSYCHIC).put(Type.FIGHTING, 2.0);
            chart.get(Type.PSYCHIC).put(Type.POISON, 2.0);
            chart.get(Type.PSYCHIC).put(Type.PSYCHIC, 0.5);
            //BUG
            chart.get(Type.BUG).put(Type.FIRE, 0.5);
            chart.get(Type.BUG).put(Type.GRASS, 2.0);
            chart.get(Type.BUG).put(Type.FIGHTING, 0.5); // #blameneslon
            chart.get(Type.BUG).put(Type.POISON, 2.0);
            chart.get(Type.BUG).put(Type.FLYING, 0.5);
            chart.get(Type.BUG).put(Type.PSYCHIC, 2.0);
            chart.get(Type.BUG).put(Type.GHOST, 0.5); // #blameneslon
            //ROCK
            chart.get(Type.ROCK).put(Type.FIRE, 2.0);
            chart.get(Type.ROCK).put(Type.ICE, 2.0);
            chart.get(Type.ROCK).put(Type.FIGHTING, 0.5);
            chart.get(Type.ROCK).put(Type.GROUND, 0.5);
            chart.get(Type.ROCK).put(Type.FLYING, 2.0);
            chart.get(Type.ROCK).put(Type.BUG, 2.0);
            //GHOST
            chart.get(Type.GHOST).put(Type.NORMAL, 0.0);
            chart.get(Type.GHOST).put(Type.PSYCHIC, 0.0);
            chart.get(Type.GHOST).put(Type.GHOST, 2.0);
            //DRAGON
            chart.get(Type.DRAGON).put(Type.DRAGON, 2.0);
        }

        public double getFactor(Type typeAtk, Type typeDef1, Type typeDef2) {
            double f = 1.0;
            f *= chart.get(typeAtk).get(typeDef1);
            f *= typeDef2 == null ? 1.0 : chart.get(typeAtk).get(typeDef2);
            return f;
        }

    }

}
