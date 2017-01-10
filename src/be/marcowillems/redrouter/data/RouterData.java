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
//        initItems();
        initEvolutions();
        initLocations();
        initEncounters();
        initMoves();
        initTms();
        initMovesets();
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

    public Trainer getTrainer(String name) {
        return trainers.get(Trainer.getIndexString(name));
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
                evos.put(key, evo);
            } catch (NumberFormatException nfe) {
                // TODO!!
//            Item item = getItem(args2[1]);
//            key = new Evolution.Item(item);
            }
        }
        Evolution e = new Evolution(evos);
        p.setEvolution(e);

        return e;
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

    // TODO Change this
    private void initTms() {
        tms.put("TM_01", moves.get("MEGA_PUNCH"));
        tms.put("TM_02", moves.get("RAZOR_WIND"));
        tms.put("TM_03", moves.get("SWORDS_DANCE"));
        tms.put("TM_04", moves.get("WHIRLWIND"));
        tms.put("TM_05", moves.get("MEGA_KICK"));
        tms.put("TM_06", moves.get("TOXIC"));
        tms.put("TM_07", moves.get("HORN_DRILL"));
        tms.put("TM_08", moves.get("BODY_SLAM"));
        tms.put("TM_09", moves.get("TAKE_DOWN"));
        tms.put("TM_10", moves.get("DOUBLE_EDGE"));
        tms.put("TM_11", moves.get("BUBBLEBEAM"));
        tms.put("TM_12", moves.get("WATER_GUN"));
        tms.put("TM_13", moves.get("ICE_BEAM"));
        tms.put("TM_14", moves.get("BLIZZARD"));
        tms.put("TM_15", moves.get("HYPER_BEAM"));
        tms.put("TM_16", moves.get("PAY_DAY"));
        tms.put("TM_17", moves.get("SUBMISSION"));
        tms.put("TM_18", moves.get("COUNTER"));
        tms.put("TM_19", moves.get("SEISMIC_TOSS"));
        tms.put("TM_20", moves.get("RAGE"));
        tms.put("TM_21", moves.get("MEGA_DRAIN"));
        tms.put("TM_22", moves.get("SOLARBEAM"));
        tms.put("TM_23", moves.get("DRAGON_RAGE"));
        tms.put("TM_24", moves.get("THUNDERBOLT"));
        tms.put("TM_25", moves.get("THUNDER"));
        tms.put("TM_26", moves.get("EARTHQUAKE"));
        tms.put("TM_27", moves.get("FISSURE"));
        tms.put("TM_28", moves.get("DIG"));
        tms.put("TM_29", moves.get("PSYCHIC_M"));
        tms.put("TM_30", moves.get("TELEPORT"));
        tms.put("TM_31", moves.get("MIMIC"));
        tms.put("TM_32", moves.get("DOUBLE_TEAM"));
        tms.put("TM_33", moves.get("REFLECT"));
        tms.put("TM_34", moves.get("BIDE"));
        tms.put("TM_35", moves.get("METRONOME"));
        tms.put("TM_36", moves.get("SELFDESTRUCT"));
        tms.put("TM_37", moves.get("EGG_BOMB"));
        tms.put("TM_38", moves.get("FIRE_BLAST"));
        tms.put("TM_39", moves.get("SWIFT"));
        tms.put("TM_40", moves.get("SKULL_BASH"));
        tms.put("TM_41", moves.get("SOFTBOILED"));
        tms.put("TM_42", moves.get("DREAM_EATER"));
        tms.put("TM_43", moves.get("SKY_ATTACK"));
        tms.put("TM_44", moves.get("REST"));
        tms.put("TM_45", moves.get("THUNDER_WAVE"));
        tms.put("TM_46", moves.get("PSYWAVE"));
        tms.put("TM_47", moves.get("EXPLOSION"));
        tms.put("TM_48", moves.get("ROCK_SLIDE"));
        tms.put("TM_49", moves.get("TRI_ATTACK"));
        tms.put("TM_50", moves.get("SUBSTITUTE"));
        tms.put("HM_01", moves.get("CUT"));
        tms.put("HM_02", moves.get("FLY"));
        tms.put("HM_03", moves.get("SURF"));
        tms.put("HM_04", moves.get("STRENGTH"));
        tms.put("HM_05", moves.get("FLASH"));
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
