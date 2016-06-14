/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package redrouter.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import redrouter.route.Route;
import redrouter.route.RouteBattle;

/**
 *
 * @author marco
 */
public class RouteFactory {
    
    private static HashMap<Pokemon.Pkmn, Pokemon> pokedexByName;
    private static List<Pokemon> pokedexByID;
    private static List<Trainer> trainers;
    private static Route exaNidoRoute;
    
    public Route getExaNidoRoute() {
        if (exaNidoRoute == null) {
            initPokedex();
            initTrainers();
            initExaNidoRoute();
        }
        return exaNidoRoute;
    }
    
    public static List<Pokemon> getPokedexByID() {
        if (pokedexByID == null) {
            initPokedex();
        }
        return pokedexByID;
    }
    
    public static HashMap<Pokemon.Pkmn, Pokemon> getPokedexByName() {
        if (pokedexByID == null) {
            initPokedex();
        }
        return pokedexByName;
    }
    
    public List<Trainer> getTrainers() {
        if (trainers == null) {
            initTrainers();
        }
        return trainers;
    }
    
    private static void initPokedex() {
        // TODO
        pokedexByID = new ArrayList<>();
        pokedexByName = new HashMap<>();
        for (Pokemon.Pkmn pkmn : Pokemon.Pkmn.values()) {
            // TODO: input file!
            Pokemon p = new Pokemon(pkmn, pkmn.toString(), Types.Type.NORMAL, Types.Type.FIRE, 52, 30, 31, 32, 33, 34);
            pokedexByID.add(p);
            pokedexByName.put(pkmn, p);
        }
    }
    
    private void initTrainers() {
        trainers = new ArrayList<>();
        // TODO: input file!
        List<Move> moveset = makeMoveSet(0);
        List<List<Move>> movesets = new ArrayList<>();
        movesets.add(moveset);
        List<Battler> team = makeTeam(new Pokemon.Pkmn[]{Pokemon.Pkmn.BULBASAUR}, new int[]{5}, movesets);
        Trainer rival1 = new Trainer("Oak's Lab", "Rival 1", null, team);
        trainers.add(rival1);
    }
    
    // TODO: TEMP
    private List<Move> makeMoveSet(int num) {
        List<Move> moveset = new ArrayList<>();
        for (int i=1; i<=4; i++) {
            moveset.add(new Move("Move" + (i + num), Types.Type.NORMAL, true, i*20, 100));
        }
        return moveset;
    }
    
    private List<Battler> makeTeam(Pokemon.Pkmn[] pokemon, int[] levels, List<List<Move>> movesets) {
        if (pokemon.length != levels.length || pokemon.length != movesets.size()) {
            return null;
        } else {
            List<Battler> team = new ArrayList<>();
            for (int i=0; i<pokemon.length; i++) {
                Battler b = new Battler(pokedexByName.get(pokemon[i]), levels[i], movesets.get(i));
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
