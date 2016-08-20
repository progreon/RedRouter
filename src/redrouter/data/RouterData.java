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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import redrouter.Settings;

/**
 * The main factory class for the application
 *
 * @author Marco Willems
 */
public class RouterData {

    public final Settings settings;

    private final Map<String, EncounterArea> areas = new HashMap<>();
    private final List<EncounterArea> areasByID = new ArrayList<>();
    private final Map<String, Location> locations = new HashMap<>();
    private final Map<String, Pokemon> pokemonByName = new HashMap<>();
    private final Map<Integer, Pokemon> pokemonByID = new HashMap<>();
    // TODO hoe indexeren?
    private final Map<String, Move> moves = new HashMap<>();
    private final Map<String, Trainer> trainers = new HashMap<>();

    public RouterData() {
        this(new Settings());
    }

    public RouterData(Settings settings) {
        this.settings = settings;
        initPokemon();
        initLocations();
        initEncounters();

        // For dummy data
        initTrainers();
        initMoves();
    }

    public EncounterArea getEncounterArea(Location location, String subArea) {
        return areas.get(EncounterArea.getIndexString(location, subArea));
    }

    public EncounterArea[] getEncounterAreas() {
        return areasByID.toArray(new EncounterArea[0]);
    }

    public List<EncounterArea> getEncounterAreas(Pokemon pkmn) {
        List<EncounterArea> l = new ArrayList<>();
        for (EncounterArea ea : areasByID) {
            boolean contains = false;
            for (EncounterArea.Slot s : ea.slots) {
                if (s.pkmn == pkmn) {
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

    public Move getMove(String name) {
        return moves.get(Move.getIndexString(name));
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

    private void initEncounters() {
        List<String> lines = getLinesFromFile(settings.getEncountersFile());
        try {
            for (int lno = 0; lno < lines.size(); lno++) {
                String line = lines.get(lno);
                if (!line.equals("") && !line.substring(0, 2).equals("//")) {
                    if (addEncounterArea(line, settings.getEncountersFile(), lno) == null) {
                        throw new ParserException(line, lno, "This area already exists!");
                    }
                }
            }
        } catch (ParserException ex) {
            Logger.getLogger(RouterData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private EncounterArea addEncounterArea(String areaString, String file, int line) throws ParserException {
        EncounterArea area = new EncounterArea(this, areaString, file, line);
        if (!areas.containsKey(area.getIndexString())) {
            areas.put(area.getIndexString(), area);
            areasByID.add(area);
            area.location.encounterAreas.add(area);
            return area;
        } else {
            return null;
        }
    }

    private void initLocations() {
        List<String> lines = getLinesFromFile(settings.getLocationsFile());
        try {
            for (int lno = 0; lno < lines.size(); lno++) {
                String line = lines.get(lno);
                if (!line.equals("") && !line.substring(0, 2).equals("//")) {
                    if (addLocation(line, settings.getLocationsFile(), lno) == null) {
                        throw new ParserException(settings.getLocationsFile(), lno, "This location already exists!");
                    }
                }
            }
        } catch (ParserException ex) {
            Logger.getLogger(RouterData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Location addLocation(String locationString, String file, int line) throws ParserException {
        Location location = new Location(this, locationString, file, line);
        if (!locations.containsKey(location.getIndexString())) {
            locations.put(location.getIndexString(), location);
            return location;
        } else {
            return null;
        }
    }


    private void initMoves()  {
        List<String> lMoveList = getLinesFromFile(settings.getMoveFile());
        try {
            for (int i=0;i<lMoveList.size();i++){
                String lMove = lMoveList.get(i);
                if (!lMove.equals("") && !lMove.substring(0, 2).equals("//")) {
                    if (addMove(lMove, settings.getMoveFile(), i) == null) {
                            throw new ParserException(settings.getMoveFile(), i, "This move already exists!");
                    }
                }
            }
        } catch (ParserException pE) {
            Logger.getLogger(RouterData.class.getName()).log(Level.SEVERE, null, pE);
        }
    }

    // TODO TEMP with moveString?
    private Move addMove(String moveString, String file, int line) throws ParserException{
        Move move = new Move(moveString,file,line);
        if (!moves.containsKey(move.getIndexString())) {
            moves.put(move.getIndexString(), move);
            return move;
        } else {
            return null;
        }
    }

    // TODO: TEMP
    private void initTrainers() {
        // TODO: input file!
        Move[] moveset = makeMoveSet(0);
        List<Move[]> movesets = new ArrayList<>();
        movesets.add(moveset);
        List<SingleBattler> team = makeTeam(new Pokemon[]{getPokemon("Bulbasaur")}, new int[]{5}, movesets);
        Trainer rival1 = new Trainer(new Location(this, "Oak's Lab"), "Rival 1", null, team);
        trainers.put(rival1.getIndexString(), rival1);
    }

    // TODO: TEMP
    private Move[] makeMoveSet(int num) {
        Move[] moveset = new Move[4];
        for (int i = 1; i <= 4; i++) {
           // moveset[i - 1] = addMove("Move" + (i + num), Types.Type.NORMAL, true, i * 20, 100);
        }
        return moveset;
    }

    // TODO: TEMP
    private List<SingleBattler> makeTeam(Pokemon[] pokemon, int[] levels, List<Move[]> movesets) {
        if (pokemon.length != levels.length || pokemon.length != movesets.size()) {
            return null;
        } else {
            List<SingleBattler> team = new ArrayList<>();
            for (int i = 0; i < pokemon.length; i++) {
                SingleBattler b = new SingleBattler(pokemon[i], levels[i], movesets.get(i));
                team.add(b);
            }
            return team;
        }
    }

    private void initPokemon() {
        List<String> lines = getLinesFromFile(settings.getPokemonFile());
        try {
            int ID = 1;
            for (int lno = 0; lno < lines.size(); lno++) {
                String line = lines.get(lno);
                if (!line.equals("") && !line.substring(0, 2).equals("//")) {
                    if (addPokemon(ID, line, settings.getPokemonFile(), lno) == null) {
                        throw new ParserException(settings.getPokemonFile(), lno, "This pokemon already exists!");
                    }
                    ID++;
                }
            }
        } catch (ParserException ex) {
            Logger.getLogger(RouterData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Pokemon addPokemon(int ID, String pokemonString, String file, int line) throws ParserException {
        Pokemon pokemon = new Pokemon(this, ID, pokemonString, file, line);
        if (!pokemonByName.containsKey(pokemon.getIndexString()) && !pokemonByID.containsKey(ID)) {
            pokemonByName.put(pokemon.getIndexString(), pokemon);
            pokemonByID.put(ID, pokemon);
            return pokemon;
        } else {
            return null;
        }
    }

    private List<String> getLinesFromFile(String fileName) {
        List<String> lines = new ArrayList<>();
        BufferedReader br = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream(fileName)));
        try {
            String line = br.readLine();
            while (line != null) {
                lines.add(line);
                line = br.readLine();
            }
        } catch (IOException ex) {
            Logger.getLogger(RouterData.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                br.close();
            } catch (IOException ex) {
                Logger.getLogger(RouterData.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return lines;
    }

    private void initMovesets() {
        List<String> lContent = getLinesFromMovesetFile(settings.getMovesetFile());
        if(lContent == null) {
            Logger.getLogger(RouterData.class.getName()).log(Level.SEVERE, null, "Error Parsing ");
        }
        for(String lString: lContent){

        }
    }

    private List<String> getLinesFromMovesetFile(String pFileName) {
        try {
            StringBuilder lBuffer = new StringBuilder();
            BufferedReader lReader = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream(pFileName), "UTF-8"));
            for (int c = lReader.read(); c != -1; c = lReader.read()){
                lBuffer.append((char)c);
            }
            String[] lStrings = lBuffer.toString().split("[\\n\\r|\\n|\\r]{2}");
            return Arrays.stream(lStrings).collect(Collectors.toList());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    // Dummy space
}
