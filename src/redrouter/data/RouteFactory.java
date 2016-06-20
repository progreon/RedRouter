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
import redrouter.route.Route;
import redrouter.route.RouteBattle;

/**
 *
 * @author Marco Willems
 */
public class RouteFactory {

    private static boolean isInit = false;
    private static List<Trainer> trainers;
    private static Route exaNidoRoute;

    public RouteFactory() {
        if (!isInit) {
            initTrainers();
        }
    }

    public Route getExaNidoRoute() {
        if (exaNidoRoute == null) {
            initTrainers();
            initExaNidoRoute();
        }
        return exaNidoRoute;
    }

    public List<Trainer> getTrainers() {
        if (trainers == null) {
            initTrainers();
        }
        return trainers;
    }

    private void initTrainers() {
        trainers = new ArrayList<>();
        // TODO: input file!
        List<Move> moveset = makeMoveSet(0);
        List<List<Move>> movesets = new ArrayList<>();
        movesets.add(moveset);
        List<Battler> team = makeTeam(new Pokemon[]{Pokemon.get("Bulbasaur")}, new int[]{5}, movesets);
        Trainer rival1 = new Trainer("Oak's Lab", "Rival 1", null, team);
        trainers.add(rival1);
        isInit = true;
    }

    // TODO: TEMP
    private List<Move> makeMoveSet(int num) {
        List<Move> moveset = new ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            moveset.add(Move.add("Move" + (i + num), Types.Type.NORMAL, true, i * 20, 100));
        }
        return moveset;
    }

    private List<Battler> makeTeam(Pokemon[] pokemon, int[] levels, List<List<Move>> movesets) {
        if (pokemon.length != levels.length || pokemon.length != movesets.size()) {
            return null;
        } else {
            List<Battler> team = new ArrayList<>();
            for (int i = 0; i < pokemon.length; i++) {
                Battler b = new Battler(pokemon[i], levels[i], movesets.get(i));
                team.add(b);
            }
            return team;
        }
    }

    private void initExaNidoRoute() {
        Route r = new Route();

        r.addDirections("Clear any existing save file by pressing Up + B + Select on the game title screen");
        r.addDirections("New game: text speed fast, animations off, style shift");
        r.addDirections("Start a new game, and name yourself and your rival a one character name.");

        r.addDirections("Exit out of your home, and head north towards Route 1. Prof. Oak will stop you and lead you back to his lab. After he and your rival are done talking, select the middle Pokeball on the table to get Squirtle. Name it a one character name, and go to head out of the lab. Your rival will stop you for a battle.");
        r.addRouteEntry(new RouteBattle(trainers.get(0)));

        exaNidoRoute = r;
    }

}
