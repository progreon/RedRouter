/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package redrouter.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import redrouter.route.Route;
import redrouter.route.RouteBattle;

/**
 *
 * @author marco
 */
public class RouteFactory {

    private final static String pokemonFile = "pokemon.txt";

    private static HashMap<Pokemon.Pkmn, Pokemon> pokedexByName;
    private static List<Trainer> trainers;
    private static Route exaNidoRoute;

    public RouteFactory() {
        if (pokedexByName == null) {
            initPokedex();
            initTrainers();
        }
    }

    public Route getExaNidoRoute() {
        if (exaNidoRoute == null) {
            initPokedex();
            initTrainers();
            initExaNidoRoute();
        }
        return exaNidoRoute;
    }

    public static Pokemon getPokemonByID(int id) {
        if (pokedexByName == null) {
            initPokedex();
        }
        return pokedexByName.get(Pokemon.Pkmn.values()[id]);
    }

    public static Pokemon getPokemonByName(Pokemon.Pkmn name) {
        if (pokedexByName == null) {
            initPokedex();
        }
        return pokedexByName.get(name);
    }

    public static HashMap<Pokemon.Pkmn, Pokemon> getPokedex() {
        if (pokedexByName == null) {
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
        pokedexByName = new HashMap<>();
        BufferedReader br = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream(pokemonFile)));
        int lineNr = 0;
        int pokedexEntry = 0;
        try {
            String line;
            line = br.readLine();
            while (line != null) {
                lineNr++;
                if (line.equals("") || line.substring(0, 2).equals("//")) {
                    //nothing to do here
                } else {
                    String[] s = line.split(";");
                    Pokemon poke = new Pokemon(Pokemon.Pkmn.values()[pokedexEntry], s[0], Types.Type.NORMAL, null, Pokemon.Gender.BOTH, 0.5, Integer.parseInt(s[1]), Integer.parseInt(s[2]), Integer.parseInt(s[3]), Integer.parseInt(s[4]), Integer.parseInt(s[5]), Integer.parseInt(s[6]));
                    pokedexByName.put(poke.species, poke);
                    pokedexEntry++;
                    System.out.println(pokedexEntry + " - " + poke.toString());
                }
                line = br.readLine();
            }
        } catch (IOException ex) {
            Logger.getLogger(RouteFactory.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Syntax error in pokemon.txt on line: " + lineNr);
        } finally {
            try {
                br.close();
            } catch (IOException ex) {
                Logger.getLogger(RouteFactory.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void initTrainers() {
        trainers = new ArrayList<>();
        // TODO: input file!
        List<Move> moveset = makeMoveSet(0);
        List<List<Move>> movesets = new ArrayList<>();
        movesets.add(moveset);
        List<Battler> team = makeTeam(new Pokemon[]{pokedexByName.get(Pokemon.Pkmn.BULBASAUR)}, new int[]{5}, movesets);
        Trainer rival1 = new Trainer("Oak's Lab", "Rival 1", null, team);
        trainers.add(rival1);
    }

    // TODO: TEMP
    private List<Move> makeMoveSet(int num) {
        List<Move> moveset = new ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            moveset.add(new Move("Move" + (i + num), Types.Type.NORMAL, true, i * 20, 100));
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
