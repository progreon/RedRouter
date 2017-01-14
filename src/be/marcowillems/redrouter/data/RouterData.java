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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import be.marcowillems.redrouter.Settings;
import be.marcowillems.redrouter.io.ParserException;
import be.marcowillems.redrouter.util.PokemonLevelPair;

/**
 * The main factory class for the route
 *
 * @author Marco Willems
 */
public class RouterData {

    public final Settings settings;

    private final List<Location> world = new ArrayList<>();
    private final Map<String, Location> locations = new TreeMap<>();
    private Location defaultStartLocation = null;
    private final Map<String, Pokemon> pokemonByName = new TreeMap<>();
    private final Map<Integer, Pokemon> pokemonByID = new TreeMap<>();
    private final Map<String, Move> moves = new TreeMap<>();
    private final Map<String, Item> items = new TreeMap<>();
    private final Map<String, Move> tms = new TreeMap<>();
    private final Map<String, Trainer> trainers = new TreeMap<>();
    /**
     * Pokemon -> New move -> Old move
     */
    private final Map<Pokemon, Map<Move, Move>> movesReplaced = new HashMap<>();

    public RouterData() throws ParserException {
        this(new Settings());
    }

    public RouterData(Settings settings) throws ParserException {
        this.settings = settings;
        // TODO: order?
        initPokemon();
        initLocations();
        initEncounters(); // Needs pokemon and locations
        initMoves(); // Needs pokemon
        initItemsAndTMs(); // Needs moves for TMs
        initEvolutions(); // Needs pokemon (duuh), items for evolution stones
        initMovesets(); // Needs pokemon, moves and tms (cf items)
        // For dummy data
        initTrainers();
    }

    public EncounterArea getEncounterArea(String location) {
        return getEncounterArea(getLocation(location));
    }

    public EncounterArea getEncounterArea(String location, String subArea) {
        return getEncounterArea(getLocation(location), subArea);
    }

    public EncounterArea getEncounterArea(Location location) {
        return getEncounterArea(location, null);
    }

    public EncounterArea getEncounterArea(Location location, String subArea) {
        if (location == null) {
            return null;
        } else {
            if (subArea != null) {
                return location.encounterAreas.get(subArea);
            } else {
                EncounterArea area = null;
                for (EncounterArea ea : location.encounterAreas.values()) {
                    if (area == null) {
                        area = ea;
                    }
                }
                return area;
            }
        }
    }

    public Set<EncounterArea> getEncounterAreas() {
        Set<EncounterArea> allAreas = new TreeSet<>();
        for (Location l : locations.values()) {
            allAreas.addAll(l.getEncounterAreas());
        }
        return allAreas;
    }

    public List<EncounterArea> getEncounterAreas(Pokemon pkmn) {
        List<EncounterArea> l = new ArrayList<>();
        for (EncounterArea ea : getEncounterAreas()) {
            boolean contains = false;
            for (PokemonLevelPair plp : ea.slots) {
                if (plp.pkmn == pkmn) {
                    contains = true;
                }
            }
            if (contains) {
                l.add(ea);
            }
        }
        return l;
    }

    public Item getItem(String item) {
        return items.get(Item.getIndexString(item));
    }

    public Location getLocation(String name) {
        return locations.get(Location.getIndexString(name));
    }

    public Location getDefaultStartLocation() {
        return defaultStartLocation;
    }

    public List<Location> getWorld() {
        return new ArrayList<>(world);
    }

    public Move getMove(String name) {
        return moves.get(Move.getIndexString(name));
    }

    public boolean addMoveReplaced(Pokemon pokemon, Move newMove, Move oldMove) {
        // TODO: check if HM?
        boolean success = false;

        if (!movesReplaced.containsKey(pokemon)) {
            movesReplaced.put(pokemon, new HashMap<>());
        }
        if (!movesReplaced.get(pokemon).containsKey(newMove)) {
            movesReplaced.get(pokemon).put(newMove, oldMove);
            success = true;
        }

        return success;
    }

    public Move getMoveReplaced(Pokemon pokemon, Move newMove) {
        Move oldMove = null;

        if (movesReplaced.containsKey(pokemon)) {
            oldMove = movesReplaced.get(pokemon).get(newMove);
        }

        return oldMove;
    }

    public Pokemon getPokemon(String name) {
        return pokemonByName.get(Pokemon.getIndexString(name));
    }

    public Pokemon getPokemon(int ID) {
        return pokemonByID.get(ID);
    }

    public Pokemon[] getAllPokemon() {
        return pokemonByID.values().toArray(new Pokemon[0]);
    }

    public String[] getPokemonNames() {
        return pokemonByName.keySet().toArray(new String[0]);
    }

    public Item getTM(String move) {
        Move m = moves.get(Move.getIndexString(move));
        if (m != null) {
            return getTM(m);
        } else {
            return null;
        }
    }

    public Item getTM(Move move) {
        for (String key : tms.keySet()) {
            Move m = tms.get(key);
            if (m == move) {
                return items.get(key);
            }
        }
        return null;
    }

    public Move getTMMove(Item tmItem) {
        return (tmItem.type == Item.Type.TM ? tms.get(tmItem.name) : null);
    }

    public Trainer getTrainer(String name) {
        return trainers.get(Trainer.getIndexString(name));
    }

    private void initPokemon() throws ParserException {
        List<String> lines = getLinesFromResourceFile(settings.getPokemonFile());
        int ID = 1;
        for (int lno = 0; lno < lines.size(); lno++) {
            String line = lines.get(lno);
            if (!line.equals("") && !line.substring(0, 2).equals("//")) {
                addPokemon(ID, line, settings.getPokemonFile(), lno);
                ID++;
            }
        }
    }

    private Pokemon addPokemon(int ID, String pokemonString, String file, int lineNo) throws ParserException {
        Pokemon pokemon = new Pokemon(this, ID, pokemonString, file, lineNo);
        if (!pokemonByName.containsKey(pokemon.getIndexString()) && !pokemonByID.containsKey(ID)) {
            pokemonByName.put(pokemon.getIndexString(), pokemon);
            pokemonByID.put(ID, pokemon);
            return pokemon;
        } else {
            throw new ParserException(settings.getPokemonFile(), lineNo, "This pokemon already exists!");
        }
    }

    private void initEncounters() throws ParserException {
        List<String> lines = getLinesFromResourceFile(settings.getEncountersFile());
        for (int lno = 0; lno < lines.size(); lno++) {
            String line = lines.get(lno);
            if (!line.equals("") && !line.substring(0, 2).equals("//")) {
                addEncounterArea(line, settings.getEncountersFile(), lno);
            }
        }
    }

    private EncounterArea addEncounterArea(String areaString, String file, int lineNo) throws ParserException {
        EncounterArea area = new EncounterArea(this, areaString, file, lineNo);
        if (!area.location.encounterAreas.containsKey(area.subArea)) {
            area.location.encounterAreas.put(area.subArea, area);
            return area;
        } else {
            throw new ParserException(file, lineNo, "Location \"" + area.location + "\" already contains subarea \"" + area.subArea + "\"");
        }
    }

    private void initEvolutions() throws ParserException {
        List<String> lines = getLinesFromResourceFile(settings.getEvolutionsFile());
        for (int lno = 0; lno < lines.size(); lno++) {
            String line = lines.get(lno);
            if (!line.equals("") && !line.substring(0, 2).equals("//")) {
                addEvolution(line, settings.getEvolutionsFile(), lno);
            }
        }
    }

    private Evolution addEvolution(String evolutionString, String file, int lineNo) throws ParserException {
        String[] args = evolutionString.trim().split("#");
        if (args.length < 2) {
            throw new ParserException(file, lineNo, "Expected 2 or more arguments");
        }
        Pokemon p = getPokemon(args[0]);
        if (p == null) {
            throw new ParserException(file, lineNo, "Could not find pokemon \"" + args[0] + "\"");
        }
        Map<Evolution.Key, Pokemon> evos = new HashMap<>();

        for (int i = 1; i < args.length; i++) {
            String args2[] = args[i].split(":");
            if (args2.length != 2) {
                throw new ParserException(file, lineNo, "Expected evolution and condition in between \":\" but found \"" + args[i] + "\"");
            }
            Pokemon evo = getPokemon(args2[0]);
            if (evo == null) {
                throw new ParserException(file, lineNo, "Could not find pokemon \"" + args2[0] + "\"");
            }
            Evolution.Key key;
            try {
                int level = Integer.parseInt(args2[1]);
                key = new Evolution.Level(level);
            } catch (NumberFormatException nfe) {
                String other = args2[1];
                if (other.equals(Evolution.Trade.VALUE)) {
                    key = new Evolution.Trade();
                } else {
                    Item item = getItem(other);
                    if (item == null) {
                        throw new ParserException(file, lineNo, "Could not find item \"" + other + "\"");
                    }
                    key = new Evolution.Item(item);
                }
            }
            evos.put(key, evo);
        }
        Evolution e = new Evolution(evos);
        p.setEvolution(e);

        return e;
    }

    private void initItemsAndTMs() throws ParserException {
        List<String> lines = getLinesFromResourceFile(settings.getItemsFile());
        for (int lno = 0; lno < lines.size(); lno++) {
            String line = lines.get(lno);
            if (!line.equals("") && !line.substring(0, 2).equals("//")) {
                Item item = addItem(line, settings.getItemsFile(), lno);
                // Handle TMs
                if (item.type == Item.Type.TM) {
                    Move move = getMove(item.value);
                    if (move == null) {
                        throw new ParserException(settings.getItemsFile(), lno, "Error while parsing TM: could not find move \"" + item.value + "\"");
                    }
                    tms.put(item.name, move);
                }
            }
        }
    }

    private Item addItem(String itemString, String file, int lineNo) throws ParserException {
        Item item = new Item(this, itemString, file, lineNo);
        if (!items.containsKey(item.getIndexString())) {
            items.put(item.getIndexString(), item);
            return item;
        } else {
            throw new ParserException(settings.getMoveFile(), lineNo, "This item already exists!");
        }
    }

    private void initLocations() throws ParserException {
        List<String> lines = getLinesFromResourceFile(settings.getLocationsFile());
        int idx = 0;
        while (lines.size() > idx && (lines.get(idx).trim().equals("") || lines.get(idx).trim().startsWith("//"))) {
            idx++;
        }
        while (lines.size() > idx) { // Add sublocations
            List<String> subLocationLines = getLocationStringGroup(lines, idx);
            Location location = addLocation(subLocationLines, settings.getLocationsFile(), idx);
            world.add(location);
            idx += subLocationLines.size();
            while (lines.size() > idx && (lines.get(idx).trim().equals("") || lines.get(idx).trim().startsWith("//"))) {
                idx++;
            }
        }
    }

    private Location addLocation(List<String> locationLines, String file, int lineNo) throws ParserException {
        int idx = 0;
        Location location = new Location(this, locationLines.get(idx).trim(), file, lineNo);
        if (!locations.containsKey(location.getIndexString())) {
            locations.put(location.getIndexString(), location);
            if (defaultStartLocation == null) {
                defaultStartLocation = location;
            }
            idx++;
            while (locationLines.size() > idx && (locationLines.get(idx).trim().equals("") || locationLines.get(idx).trim().startsWith("//"))) {
                idx++;
            }
            while (locationLines.size() > idx) { // Add sublocations
                List<String> subLocationLines = getLocationStringGroup(locationLines, idx);
                Location subLocation = addLocation(subLocationLines, file, lineNo + idx);
                location.addSubLocation(subLocation);
                idx += subLocationLines.size();
                while (locationLines.size() > idx && (locationLines.get(idx).trim().equals("") || locationLines.get(idx).trim().startsWith("//"))) {
                    idx++;
                }
            }
            return location;
        } else {
            throw new ParserException(file, lineNo, "The location \"" + location + "\" already exists!");
        }
    }

    private List<String> getLocationStringGroup(List<String> parentLocationLines, int lineNo) {
        List<String> lineGroup = new ArrayList<>();

        String line = parentLocationLines.get(lineNo);
        int depth = getTabDepth(line);
        lineGroup.add(line);
        lineNo++;
        while (lineNo < parentLocationLines.size() && (getTabDepth(parentLocationLines.get(lineNo)) > depth || parentLocationLines.get(lineNo).trim().equals("") || parentLocationLines.get(lineNo).trim().startsWith("//"))) {
            line = parentLocationLines.get(lineNo);
            lineGroup.add(line);
            lineNo++;
        }

        return lineGroup;
    }

    private int getTabDepth(String line) {
        int depth = 0;
        char[] chars = line.toCharArray();
        while (depth < chars.length && chars[depth] == '\t') {
            depth++;
        }
        return depth;
    }

    private void initMoves() throws ParserException {
        List<String> lines = getLinesFromResourceFile(settings.getMoveFile());
        for (int lno = 0; lno < lines.size(); lno++) {
            String line = lines.get(lno);
            if (!line.equals("") && !line.substring(0, 2).equals("//")) {
                addMove(line, settings.getMoveFile(), lno);
            }
        }
    }

    private Move addMove(String moveString, String file, int lineNo) throws ParserException {
        Move move = new Move(this, moveString, file, lineNo);
        if (!moves.containsKey(move.getIndexString())) {
            moves.put(move.getIndexString(), move);
            return move;
        } else {
            throw new ParserException(settings.getMoveFile(), lineNo, "This move already exists!");
        }
    }

    private void initMovesets() throws ParserException {
        List<String> lines = getLinesFromResourceFile(settings.getMovesetFile());

        List<String> data = new ArrayList<>();
        int monAtLine = 0;
        for (int lno = 0; lno < lines.size(); lno++) {
            if (!lines.get(lno).isEmpty()) {
                data.add(lines.get(lno));
            } else {
                if (!data.isEmpty()) {
                    String pkmn = data.remove(0).substring(1); // removing the '#'
                    addMoveset(pkmn, data, settings.getMovesetFile(), monAtLine);
                    data = new ArrayList<>();
                    monAtLine = lno + 1;
                }
            }
        }
    }

    private void addMoveset(String pkmn, List<String> data, String file, int lineNo) throws ParserException {
        Pokemon pokemon = pokemonByName.get(pkmn);
        if (pokemon == null) {
            throw new ParserException(file, lineNo, "Could not find the pokemon:" + pkmn);
        }

        // Loop through moves (excluding tm moves)
        for (int i = 0; i < data.size() - 1; i++) {
            String line = data.get(i);
            String[] moveSplit = line.split("#"); // [0] => level [1] => move name
            try {
                pokemon.addLearnedMove(Integer.parseInt(moveSplit[0]), moves.get(moveSplit[1]));
            } catch (NullPointerException ex) {
                throw new ParserException(file, lineNo + i + 1, "Could not parse the line: " + line);
            }
        }

        // Deals with TM Moves
        String tmsMove = data.get(data.size() - 1).substring(3); // Ignores first 3 characters
        if (!tmsMove.isEmpty()) {
            String[] tmsSplit = tmsMove.split(",");
            for (String tm : tmsSplit) {
                Move tmMove = tms.get(tm);
                if (tmMove == null) {
                    throw new ParserException(file, lineNo + data.size(), "Could not find tm: " + tm);
                }
                pokemon.addTmMove(tmMove);
            }
        }
    }

    // TODO: TEMP
    private void initTrainers() {
        // TODO: input file!
        // rival 1
        List<SingleBattler> teamRival1 = makeTeam(new Pokemon[]{getPokemon("Bulbasaur")}, new int[]{5});
        Trainer trRival1 = new Trainer(getLocation("Pallet Town"), "Rival 1", null, teamRival1);
        trainers.put(trRival1.getIndexString(), trRival1);
        // bug catcher 1 (forest)
        List<SingleBattler> teamBug1 = makeTeam(new Pokemon[]{getPokemon("Weedle")}, new int[]{9});
        Trainer trBug1 = new Trainer(getLocation("Viridian Forest"), "Bug 1", null, teamBug1);
        trainers.put(trBug1.getIndexString(), trBug1);
        // brock
        List<SingleBattler> teamBrock = makeTeam(new Pokemon[]{getPokemon("Geodude"), getPokemon("Onix")}, new int[]{12, 14});
        Trainer trBrock = new Trainer(getLocation("Pewter City"), "Brock", null, teamBrock);
        trainers.put(trBrock.getIndexString(), trBrock);
        // bug catcher 2 (r3)
        List<SingleBattler> teamBug2 = makeTeam(new Pokemon[]{getPokemon("Caterpie"), getPokemon("Weedle"), getPokemon("Caterpie")}, new int[]{10, 10, 10});
        Trainer trBug2 = new Trainer(getLocation("Route 3"), "Bug 2", null, teamBug2);
        trainers.put(trBug2.getIndexString(), trBug2);
        // shorts guy (r3)
        List<SingleBattler> teamShorts = makeTeam(new Pokemon[]{getPokemon("Rattata"), getPokemon("Ekans")}, new int[]{11, 11});
        Trainer trShorts = new Trainer(getLocation("Route 3"), "Shorts guy", null, teamShorts);
        trainers.put(trShorts.getIndexString(), trShorts);
    }

    // TODO: TEMP
    private List<SingleBattler> makeTeam(Pokemon[] pokemon, int[] levels) {
        if (pokemon.length != levels.length) {
            return null;
        } else {
            List<SingleBattler> team = new ArrayList<>();
            for (int i = 0; i < pokemon.length; i++) {
                SingleBattler b = new SingleBattler(this, pokemon[i], levels[i], null);
                team.add(b);
            }
            return team;
        }
    }

    public static List<String> getLinesFromResourceFile(String fileName) throws ParserException {
        return getLinesFromFile(ClassLoader.getSystemResourceAsStream(fileName), fileName, true);
    }

    public static List<String> getLinesFromFile(File file) throws ParserException {
        if (file.exists()) {
            try {
                if (!Files.probeContentType(file.toPath()).equals("text/plain")) {
                    throw new ParserException(null, -1, "This file is not a plain text file: " + file.getAbsolutePath());
                }
            } catch (IOException ex) {
                Logger.getLogger(RouterData.class.getName()).log(Level.SEVERE, null, ex);
                throw new ParserException(file.getAbsolutePath(), -1, "Error while reading the file, see console for more details", false);
            }
        }
        try {
            return getLinesFromFile(new FileInputStream(file), file.getAbsolutePath(), false);
        } catch (FileNotFoundException ex) {
            throw new ParserException(file.getAbsolutePath(), -1, "File not found");
        }
    }

    private static List<String> getLinesFromFile(InputStream inFile, String fileName, boolean fromResource) throws ParserException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inFile))) {
            String line = br.readLine();
            while (line != null) {
                lines.add(line);
                line = br.readLine();
            }
        } catch (IOException ex) {
            Logger.getLogger(RouterData.class.getName()).log(Level.SEVERE, null, ex);
            throw new ParserException(fileName, -1, "Error while reading the file, see console for more details", fromResource);
        }
        return lines;
    }

}
