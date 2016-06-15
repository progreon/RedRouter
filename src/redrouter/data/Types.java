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

import java.util.HashMap;

/**
 *
 * @author Marco Willems
 */
public class Types {

//    private static double[][] typeChart;
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

    public static class TypeChart {

        private final HashMap<Type, HashMap<Type, Double>> chart;

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
            chart.get(Type.NORMAL).put(Type.ROCK, 0.5);
            chart.get(Type.NORMAL).put(Type.GHOST, 0.0);
            chart.get(Type.FIRE).put(Type.FIRE, 0.5);
            chart.get(Type.FIRE).put(Type.WATER, 0.5);
            chart.get(Type.FIRE).put(Type.GRASS, 2.0);
            chart.get(Type.FIRE).put(Type.ICE, 2.0);
            chart.get(Type.FIRE).put(Type.BUG, 2.0);
            chart.get(Type.FIRE).put(Type.ROCK, 0.5);
            chart.get(Type.FIRE).put(Type.DRAGON, 0.5);
            chart.get(Type.WATER).put(Type.FIRE, 2.0);
            // TODO
        }
        
        public double getFactor(Type typeAtk, Type typeDef1, Type typeDef2) {
            double f = 1.0;
            f *= chart.get(typeAtk).get(typeDef1);
            f *= chart.get(typeAtk).get(typeDef2);
            return f;
        }

    }

}
