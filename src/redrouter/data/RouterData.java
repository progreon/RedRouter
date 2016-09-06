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
    private final Map<String, Move> tms = new HashMap<>();
    private final Map<String, Trainer> trainers = new HashMap<>();

    public RouterData() {
        this(new Settings());
    }

    public RouterData(Settings settings) {
        this.settings = settings;
        initPokemon();
        initLocations();
        initEncounters();
        initMoves();
        initTms();
        initMovesets();
        // For dummy data
        initTrainers();

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
        Move[] moveset = makeMoveSet(1);
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
            moveset[i - 1] = moves.get("POUND");
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

    //TODO Exception Handling
    private void initMovesets() {
        List<String> lContent = getLinesFromMovesetFile(settings.getMovesetFile());
        if(lContent == null) {
            Logger.getLogger(RouterData.class.getName()).log(Level.SEVERE, "Error Parsing ",lContent );
        }
        try {
            for(String lString: lContent){
                String[] lData = lString.split("\n");
                if(!addMoveset(lData)){
                    throw new ParserException(settings.getMovesetFile(), -1, "Error with this line : " + lString);
                }
            }
        } catch (ParserException pPE) {
            Logger.getLogger(RouterData.class.getName()).log(Level.SEVERE, null, pPE);
        }
    }

    /**
     *
     * @param lData
     * @throws ParserException
     */
    private boolean addMoveset(String[] lData) throws ParserException{
        // Removes # from name
        String lName = lData[0].substring(1);
        Pokemon lPokemon = pokemonByName.get(lName);
        // Loop so TM Moves and Pokemon name are ignored
        for (int i = 1; i < lData.length-1; i++) {
            String lMove = lData[i];
            String[] lMoveSplit = lMove.split("#");
            // [0] => level [1] => move name
            try{
                lPokemon.addLearnedMove(Integer.parseInt(lMoveSplit[0]),moves.get(lMoveSplit[1]));
            }
            catch (NullPointerException pNpe){
                Logger.getLogger(RouterData.class.getName()).log(Level.SEVERE, null, pNpe);
                return false;
            }
        }
        //Deals with TM Moves
        //Ignores first 3 characters
        String lTmsMove = lData[lData.length-1].substring(3);
        if(!lTmsMove.isEmpty()){
            String[] lTMsSplit = lTmsMove.split(",");
            for (String lTM : lTMsSplit){
                lPokemon.addTmMove(tms.get(lTM));
            }
        }
        return true;
    }

    private List<String> getLinesFromMovesetFile(String pFileName) {
        try {
            StringBuilder lBuffer = new StringBuilder();
            BufferedReader lReader = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream(pFileName), "UTF-8"));
            for (int c = lReader.read(); c != -1; c = lReader.read()){
                lBuffer.append((char)c);
            }
            //Splits file using empty lines as a separator
            String[] lStrings = lBuffer.toString().split("[\\n\\r|\\n|\\r]{2}");
            return Arrays.stream(lStrings).collect(Collectors.toList());

        } catch (IOException pIOE) {
            Logger.getLogger(RouterData.class.getName()).log(Level.SEVERE, null, pIOE);
        }
        return null;
    }
    //TODO Change that
    private void initTms() {
        tms.put("TM_01",moves.get("MEGA_PUNCH"));
        tms.put("TM_02",moves.get("RAZOR_WIND"));
        tms.put("TM_03",moves.get("SWORDS_DANCE"));
        tms.put("TM_04",moves.get("WHIRLWIND"));
        tms.put("TM_05",moves.get("MEGA_KICK"));
        tms.put("TM_06",moves.get("TOXIC"));
        tms.put("TM_07",moves.get("HORN_DRILL"));
        tms.put("TM_08",moves.get("BODY_SLAM"));
        tms.put("TM_09",moves.get("TAKE_DOWN"));
        tms.put("TM_10",moves.get("DOUBLE_EDGE"));
        tms.put("TM_11",moves.get("BUBBLEBEAM"));
        tms.put("TM_12",moves.get("WATER_GUN"));
        tms.put("TM_13",moves.get("ICE_BEAM"));
        tms.put("TM_14",moves.get("BLIZZARD"));
        tms.put("TM_15",moves.get("HYPER_BEAM"));
        tms.put("TM_16",moves.get("PAY_DAY"));
        tms.put("TM_17",moves.get("SUBMISSION"));
        tms.put("TM_18",moves.get("COUNTER"));
        tms.put("TM_19",moves.get("SEISMIC_TOSS"));
        tms.put("TM_20",moves.get("RAGE"));
        tms.put("TM_21",moves.get("MEGA_DRAIN"));
        tms.put("TM_22",moves.get("SOLARBEAM"));
        tms.put("TM_23",moves.get("DRAGON_RAGE"));
        tms.put("TM_24",moves.get("THUNDERBOLT"));
        tms.put("TM_25",moves.get("THUNDER"));
        tms.put("TM_26",moves.get("EARTHQUAKE"));
        tms.put("TM_27",moves.get("FISSURE"));
        tms.put("TM_28",moves.get("DIG"));
        tms.put("TM_29",moves.get("PSYCHIC_M"));
        tms.put("TM_30",moves.get("TELEPORT"));
        tms.put("TM_31",moves.get("MIMIC"));
        tms.put("TM_32",moves.get("DOUBLE_TEAM"));
        tms.put("TM_33",moves.get("REFLECT"));
        tms.put("TM_34",moves.get("BIDE"));
        tms.put("TM_35",moves.get("METRONOME"));
        tms.put("TM_36",moves.get("SELFDESTRUCT"));
        tms.put("TM_37",moves.get("EGG_BOMB"));
        tms.put("TM_38",moves.get("FIRE_BLAST"));
        tms.put("TM_39",moves.get("SWIFT"));
        tms.put("TM_40",moves.get("SKULL_BASH"));
        tms.put("TM_41",moves.get("SOFTBOILED"));
        tms.put("TM_42",moves.get("DREAM_EATER"));
        tms.put("TM_43",moves.get("SKY_ATTACK"));
        tms.put("TM_44",moves.get("REST"));
        tms.put("TM_45",moves.get("THUNDER_WAVE"));
        tms.put("TM_46",moves.get("PSYWAVE"));
        tms.put("TM_47",moves.get("EXPLOSION"));
        tms.put("TM_48",moves.get("ROCK_SLIDE"));
        tms.put("TM_49",moves.get("TRI_ATTACK"));
        tms.put("TM_50",moves.get("SUBSTITUTE"));
        tms.put("HM_01",moves.get("CUT"));
        tms.put("HM_02",moves.get("FLY"));
        tms.put("HM_03",moves.get("SURF"));
        tms.put("HM_04",moves.get("STRENGTH"));
        tms.put("HM_05",moves.get("FLASH"));
    }

    // Dummy space
}
