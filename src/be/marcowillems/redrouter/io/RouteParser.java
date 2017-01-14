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
package be.marcowillems.redrouter.io;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import be.marcowillems.redrouter.Settings;
import be.marcowillems.redrouter.data.*;
import be.marcowillems.redrouter.route.*;
import be.marcowillems.redrouter.util.IntPair;
import java.util.Locale;

/**
 * TODO: error messages push system, TODO: handle multiple (white)spaces, TODO:
 * don't generate default titles & descriptions here but in the entry
 * constructors
 *
 * @author Marco Willems
 */
public class RouteParser {

    private final String prefixRegex = "^([^\\s]*?:) .*";

    private final String gamePrefix = "Game:";
    private final String trainerPrefix = "Trainer:";
    private final String moveReplacePrefix = "MoveReplace:";
    private final String routePrefix = "Route:";

    private final String battlePrefix = "B:";
    private final String catchPokemonPrefix = "C:";
    private final String directionsPrefix = "D:";
    private final String encounterPrefix = "E:";
    private final String getPokemonPrefix = "GetP:";
    private final String manipPokemonPrefix = "Manip:"; // TODO: fuse this with catch?
    private final String menuPrefix = "Menu:";
    private final String sectionPrefix = "S:";
    private final String swapPokemonPrefix = "Swap:";

//    private RouterData rd = null;
    private HashMap<String, Trainer> trainers; // alias => trainer
    private String currentFile = null;

    public RouteParser() {
        init();
    }

    private void init() {
        // TODO: set up parser ?
        trainers = new HashMap<>();
    }

    public Route parseFile(File file) throws ParserException {
        currentFile = file.getAbsolutePath();
        Route route = null;
        RouterData rd = null;

        List<String> lines = RouterData.getLinesFromFile(file);
        int lineNo = 0;
        while (lineNo < lines.size() && !isEnd(lines.get(lineNo))) {
            String line = lines.get(lineNo);
            if (isEmptyOrComment(line) || line.matches(prefixRegex)) {
                if (!isEmptyOrComment(line)) {
                    String prefix = line.replaceFirst(prefixRegex, "$1");
                    switch (prefix) {
                        case gamePrefix:
                            rd = getNewRouterData(line.replaceFirst(prefix, "").trim(), lineNo);
                            lineNo++;
                            break;
                        case trainerPrefix:
                            if (rd == null) {
                                throw new ParserException(currentFile, lineNo, "Found a trainer before the game was set", false);
                            }
                            addNewTrainer(rd, line.replaceFirst(prefix, "").trim(), lineNo);
                            lineNo++;
                            break;
                        case moveReplacePrefix:
                            if (rd == null) {
                                throw new ParserException(currentFile, lineNo, "Found a move replacement before the game was set", false);
                            }
                            addNewMoveReplace(rd, line.replaceFirst(prefix, "").trim(), lineNo);
                            lineNo++;
                            break;
                        case routePrefix:
                            if (rd == null) {
                                throw new ParserException(currentFile, lineNo, "Found the route before the game was set", false);
                            }
                            String[] lineBundle = getRelatedLineBundle(lines.toArray(new String[0]), lineNo);
                            route = getNewRoute(rd, lineBundle, lineNo);
                            lineNo += lineBundle.length;
                            break;
                        default:
                            throw new ParserException(currentFile, lineNo, "Did not recognize prefix", false);
                    }
                } else {
                    lineNo++;
                }
            } else {
                throw new ParserException(currentFile, lineNo, "Can't parse line", false);
            }
        }

        currentFile = null;
        return route;
    }

    private RouterData getNewRouterData(String gameLine, int lineNo) throws ParserException {
        RouterData rd = null;
        switch (gameLine) {
            case "Red":
                rd = new RouterData(new Settings(Settings.GAME_RED));
                break;
            case "Blue":
                rd = new RouterData(new Settings(Settings.GAME_BLUE));
                break;
            case "Yellow":
                rd = new RouterData(new Settings(Settings.GAME_YELLOW));
                break;
            default:
                break;
        }
        if (rd == null) {
            throw new ParserException(currentFile, lineNo, "Couldn't find game \"" + gameLine + "\"", false);
        }
        return rd;
    }

    private void addNewTrainer(RouterData rd, String line, int lineNo) throws ParserException {
        // Trainer: <name> [:: <location>] :: <alias> <name>:<level> [..]
        String name;
        Location location = null;
        String alias;
        List<SingleBattler> team = new ArrayList<>();

        String[] trArgs = line.split("::");
        if (trArgs.length > 3) {
            throw new ParserException(currentFile, lineNo, "Too many arguments for trainer", false);
        }
        name = trArgs[0].trim();
        if (trArgs.length == 3) {
            location = parseLocation(rd, trArgs[1].trim(), lineNo);
            line = trArgs[2].trim();
        } else {
            line = trArgs[1].trim();
        }

        String[] args = line.split(" ");
        if (args.length < 2) {
            throw new ParserException(currentFile, lineNo, "Please give an alias and at least one pokemon", false);
        }
        alias = args[0];
        for (int i = 1; i < args.length; i++) {
            String[] pokeArgs = args[i].split(":");
            if (pokeArgs.length != 2) {
                throw new ParserException(currentFile, lineNo, "Wrong syntax of pokemon argument: \"" + args[i] + "\"", false);
            }
            Pokemon p = rd.getPokemon(pokeArgs[0]);
            if (p == null) {
                throw new ParserException(currentFile, lineNo, "Could not find pokemon \"" + pokeArgs[0] + "\"", false);
            }
            int level = -1;
            try {
                level = Integer.parseInt(pokeArgs[1]);
            } catch (NumberFormatException nfe) {
                throw new ParserException(currentFile, lineNo, "Could not parse the pokemon level in \"" + args[i] + "\"", false);
            }
            if (level <= 1) {
                throw new ParserException(currentFile, lineNo, "The pokemon level must be greater than 1", false);
            }
            team.add(new SingleBattler(rd, p, level, null));
        }

        Trainer trainer = new Trainer(location, name, null, team);
        trainers.put(alias, trainer);
    }

    private void addNewMoveReplace(RouterData rd, String moveReplaceLine, int lineNo) throws ParserException {
        // MoveReplace: <pokemon> :: <old move> <new move>
        String[] args = moveReplaceLine.split("::");
        if (args.length != 2) {
            throw new ParserException(currentFile, lineNo, "Expected 2 arguments with \"::\" in between", false);
        }
        String strPokemon = args[0].trim();
        Pokemon pokemon = rd.getPokemon(strPokemon);
        if (pokemon == null) {
            throw new ParserException(currentFile, lineNo, "Could not find pokemon \"" + strPokemon + "\"", false);
        }
        String[] moveArgs = args[1].trim().split(" ");
        if (moveArgs.length != 2) {
            throw new ParserException(currentFile, lineNo, "Expected 2 move names after \"::\"", false);
        }
        String strMoveOld = moveArgs[0].trim();
        String strMoveNew = moveArgs[1].trim();
        Move moveOld = rd.getMove(strMoveOld);
        if (moveOld == null) {
            throw new ParserException(currentFile, lineNo, "Could not find the move \"" + strMoveOld + "\"", false);
        }
        Move moveNew = rd.getMove(strMoveNew);
        if (moveNew == null) {
            throw new ParserException(currentFile, lineNo, "Could not find the move \"" + strMoveNew + "\"", false);
        }
        rd.addMoveReplaced(pokemon, moveNew, moveOld);
    }

    private Route getNewRoute(RouterData rd, String[] lines, int lineNo) throws ParserException {
        String routeLine = lines[0].replaceFirst(routePrefix, "").trim();
        Route route = new Route(rd, routeLine);
        route.disableRefresh();

        int subEntryLine = 1;
        while (subEntryLine < lines.length) {
            if (!isEmptyOrComment(lines[subEntryLine])) {
                String[] subEntryLines = getRelatedLineBundle(lines, subEntryLine);
                addNewRouteEntry(route, route, subEntryLines, lineNo + subEntryLine);
                subEntryLine += subEntryLines.length;
            } else {
                subEntryLine++;
            }
        }

        route.enableRefresh();
        return route;
    }

    private RouteEntry addNewRouteEntry(Route route, RouteSection parent, String[] lines, int lineNo) throws ParserException {
        RouteEntry re = null;

        String prefix = lines[0].trim().replaceFirst("(.*?:).*", "$1");
        switch (prefix) {
            case battlePrefix:
                addNewBattle(route, parent, lines, lineNo);
                break;
            case catchPokemonPrefix:
                addNewCatchPokemon(route, parent, lines, lineNo);
                break;
            case encounterPrefix:
                addNewEncounter(route, parent, lines, lineNo);
                break;
            case getPokemonPrefix:
                addNewGetPokemon(route, parent, lines, lineNo);
                break;
            case manipPokemonPrefix:
                addNewManipPokemon(route, parent, lines, lineNo);
                break;
            case menuPrefix:
                addNewMenu(route, parent, lines, lineNo);
                break;
            case sectionPrefix:
                addNewSection(route, parent, lines, lineNo);
                break;
            case swapPokemonPrefix:
                addNewSwapPokemon(route, parent, lines, lineNo);
                break;
            default: // directionsPrefix
                addNewDirections(route, parent, lines, lineNo);
                break;
        }

        return re;
    }

    private RouteBattle addNewBattle(Route route, RouteSection parent, String[] lines, int lineNo) throws ParserException {
        lines = getNonEmptyLines(lines);
        if (lines.length > 2) {
            throw new ParserException(currentFile, lineNo, "A battle entry can only have 1 extra description line", false);
        }

        // TODO: attacks/pokemon
        // B: <alias> [:: <opponentIndex>:<partyIndex> [..]]
        //      [[<title> ::] [<description>]]
        String battleLine = lines[0].replaceFirst(battlePrefix, "").trim();

        String alias;
        Trainer opponent;
        RouteEntryInfo info;

        // Handle battle line
        int[][] competingPartyMon = null;
        RouteEntry previousEntry = getLastEntry(route);
        route.enableRefresh(); // A forced refresh
        route.disableRefresh();
        Player prevPlayer = previousEntry.getPlayerBefore();

        String[] args = battleLine.split("::");
        if (args.length < 1 || args.length > 2) {
            throw new ParserException(currentFile, lineNo, "Expected 1 or 2 arguments separated by \"::\"", false);
        }
        alias = args[0].trim();
        opponent = trainers.get(alias);
        if (opponent == null) {
            throw new ParserException(currentFile, lineNo, "Trainer \"" + alias + "\" could not be found!", false);
        }
        if (args.length == 2) {
            int teamSize = opponent.team.size();
            int[] partyCount = new int[teamSize];
            IntPair[] partyArgs = parseIntPairs(args[1], lineNo);
            for (IntPair partyArg : partyArgs) {
                int opp = partyArg.int1;
                int part = partyArg.int2;
                if (opp < 0 || opp >= teamSize) {
                    throw new ParserException(currentFile, lineNo, "Invalid opponent index in \"" + partyArg + "\"", false);
                }
                if (part < 0 || part >= prevPlayer.team.size()) {
                    throw new ParserException(currentFile, lineNo, "Invalid party index in \"" + partyArg + "\"", false);
                }
                partyCount[opp]++;
            }

            competingPartyMon = new int[teamSize][];
            for (int i = 0; i < competingPartyMon.length; i++) {
                competingPartyMon[i] = new int[partyCount[i]];
            }
            int[] partyCount2 = new int[partyCount.length]; // TODO ??
            for (IntPair partyArg : partyArgs) {
                competingPartyMon[partyArg.int1][partyCount2[partyArg.int1]] = partyArg.int2;
            }
        }

        // Handle description line
        String title = opponent.name;
        String description = null;
        if (lines.length == 2) {
            String descrLine = lines[1].trim();
            String[] descrArgs = descrLine.split("::");
            if (descrArgs.length > 1) {
                if (!descrArgs[0].trim().isEmpty()) {
                    title = descrArgs[0];
                }
                if (!descrArgs[1].trim().isEmpty()) {
                    description = descrLine.replaceFirst(".*?::", "").trim();
                }
            } else {
                description = descrLine;
            }
        }
        info = new RouteEntryInfo(title, description);

        RouteBattle rb = new RouteBattle(route, info, opponent, competingPartyMon);
        parent.addEntry(rb);
        return rb;
    }

    private RouteDirections addNewDirections(Route route, RouteSection parent, String[] lines, int lineNo) throws ParserException {
        lines = getNonEmptyLines(lines);

        // TODO: location
        // D: [<location> ::] <description>
        //      [...]
        String description = lines[0].trim();
        if (description.startsWith(directionsPrefix)) {
            description = description.replaceFirst(directionsPrefix, "").trim();
        }
        for (int i = 1; i < lines.length; i++) {
            description += "\n\t" + lines[i].trim();
        }
        RouteDirections rd = new RouteDirections(route, description);
        parent.addEntry(rd);
        return rd;
    }

    private RouteEncounter addNewEncounter(Route route, RouteSection parent, String[] lines, int lineNo) throws ParserException {
        lines = getNonEmptyLines(lines);
        if (lines.length > 2) {
            throw new ParserException(currentFile, lineNo, "An encounter entry can only have 1 extra description line", false);
        }

        // E: <encounter area> :: <preferred slot>:<count> [..]
        //      [<description>]
        String encounterLine = lines[0].replaceFirst(encounterPrefix, "").trim();

        EncounterArea ea; // TODO handle location = area + sub area!!
        RouteEntryInfo info;

        // Handle encounter line
        String[] locArgs = encounterLine.split("::");
        if (locArgs.length != 2) {
            throw new ParserException(currentFile, lineNo, "Please provide 2 arguments with '::' in between", false);
        }
        String sLocation = locArgs[0].trim();
        ea = parseEncounterArea(route, sLocation, lineNo);
        if (ea == null) {
            throw new ParserException(currentFile, lineNo, "Could not find location \"" + sLocation + "\"", false);
        }
        IntPair[] slotPairs = parseIntPairs(locArgs[1].trim(), lineNo);

        // Handle description line
        String title = ea.toString();
        String description = null;
        if (lines.length == 2) {
            description = lines[1].trim();;
        }
        info = new RouteEntryInfo(title, description);

        RouteEncounter re = new RouteEncounter(route, info, ea, slotPairs);
        parent.addEntry(re);
        return re;
    }

    private RouteGetPokemon addNewCatchPokemon(Route route, RouteSection parent, String[] lines, int lineNo) throws ParserException {
        lines = getNonEmptyLines(lines);
        if (lines.length > 2) {
            throw new ParserException(currentFile, lineNo, "A catch entry can only have 1 extra description line", false);
        }

        // C: <encounter area> :: [#]<slot> [..]
        //      [[<title> ::] [<description>]]
        String catchLine = lines[0].replaceFirst(catchPokemonPrefix, "").trim();

        RouteEntryInfo info = null;
        EncounterArea ea; // TODO handle location = area + sub area!!
        List<SingleBattler> choices = new ArrayList<>();
        int preference = -1;

        // Handle encounter line
        String[] locArgs = catchLine.split("::");
        if (locArgs.length != 2) {
            throw new ParserException(currentFile, lineNo, "Please provide 2 arguments with '::' in between!", false);
        }
        String sLocation = locArgs[0].trim();
        ea = parseEncounterArea(route, sLocation, lineNo);
        String[] slots = locArgs[1].trim().split(" ");
        if (slots.length == 0) {
            throw new ParserException(null, lineNo, "Please list some slots!", false);
        }
        for (int i = 0; i < slots.length; i++) {
            if (slots[i].startsWith("#")) {
                if (preference < 0) {
                    preference = i;
                }
                slots[i] = slots[i].substring(1);
            }
            int slot = 0;
            try {
                slot = Integer.parseInt(slots[i]);
            } catch (NumberFormatException nfe) {
                throw new ParserException(currentFile, lineNo, "Could not parse slot number: \"" + slots[i] + "\"", false);
            }
            choices.add(ea.getBattler(slot));
        }
        if (preference < 0) {
            preference = 0;
        }

        // Handle description line
        String title = null;
        String description = null;
        if (lines.length == 2) {
            String descrLine = lines[1].trim();
            String[] descrArgs = descrLine.split("::");
            if (descrArgs.length > 1) {
                if (!descrArgs[0].trim().isEmpty()) {
                    title = descrArgs[0];
                }
                if (!descrArgs[1].trim().isEmpty()) {
                    description = descrLine.replaceFirst(".*?::", "").trim();
                }
            } else {
                description = descrLine;
            }
        }
        if (title != null || description != null) {
            info = new RouteEntryInfo(title, description);
        }

        RouteGetPokemon rgp = new RouteGetPokemon(route, info, choices, preference);
        parent.addEntry(rgp);
        return rgp;
    }

    private RouteGetPokemon addNewGetPokemon(Route route, RouteSection parent, String[] lines, int lineNo) throws ParserException {
        lines = getNonEmptyLines(lines);
        if (lines.length > 2) {
            throw new ParserException(currentFile, lineNo, "A get pokemon entry can only have 1 extra description line", false);
        }

        // TODO: location
        // GetP: [<location> ::] [#]<<pokemon>:<level>> [..]
        //      [[<title> ::] [<description>]]
        String getpLine = lines[0].replaceFirst(getPokemonPrefix, "").trim();

        RouteEntryInfo info = null;
        List<SingleBattler> choices = new ArrayList<>();
        int preference = -1;

        // Handle get pokemon line
        String[] args = getpLine.split(" ");
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("#")) {
                if (preference < 0) {
                    preference = i;
                }
                args[i] = args[i].substring(1);
            }
            String[] pokeArgs = args[i].split(":");
            if (pokeArgs.length != 2) {
                throw new ParserException(currentFile, lineNo, "Invalid pokemon option \"" + args[i] + "\"", false);
            }
            Pokemon p = route.rd.getPokemon(pokeArgs[0]);
            if (p == null) {
                throw new ParserException(currentFile, lineNo, "Could not find pokemon \"" + pokeArgs[0] + "\"", false);
            }
            int level = -1;
            try {
                level = Integer.parseInt(pokeArgs[1]);
            } catch (NumberFormatException nfe) {
                throw new ParserException(currentFile, lineNo, "Invalid level \"" + pokeArgs[1] + "\", must be an integer!", false);
            }
            if (level < 2 || level > 100) {
                throw new ParserException(currentFile, lineNo, "Invalid level \"" + pokeArgs[1] + "\", must be between 2 and 100 (included)", false);
            }
            choices.add(new SingleBattler(route.rd, p, level, null));
        }

        // Handle description line
        String title = null;
        String description = null;
        if (lines.length == 2) {
            String descrLine = lines[1].trim();
            String[] descrArgs = descrLine.split("::");
            if (descrArgs.length > 1) {
                if (!descrArgs[0].trim().isEmpty()) {
                    title = descrArgs[0];
                }
                if (!descrArgs[1].trim().isEmpty()) {
                    description = descrLine.replaceFirst(".*?::", "").trim();
                }
            } else {
                description = descrLine;
            }
        }
        if (title != null || description != null) {
            info = new RouteEntryInfo(title, description);
        }

        RouteGetPokemon rgp = new RouteGetPokemon(route, info, choices, preference);
        parent.addEntry(rgp);
        return rgp;
    }

    private RouteGetPokemon addNewManipPokemon(Route route, RouteSection parent, String[] lines, int lineNo) throws ParserException {
        lines = getNonEmptyLines(lines);
        if (lines.length > 2) {
            throw new ParserException(currentFile, lineNo, "A manip entry can only have 1 extra description line", false);
        }

        // M: <encounter area> :: <slot> :: <atk> <def> <spd> <spc>
        //      [[<title> ::] [<description>]]
        String manipLine = lines[0].replaceFirst(manipPokemonPrefix, "").trim();

        RouteEntryInfo info = null;
        EncounterArea ea; // TODO handle location = area + sub area!!
        SingleBattler sb;
        int atk, def, spd, spc;

        // Handle manip line
        String[] locArgs = manipLine.split("::");
        if (locArgs.length < 3 || locArgs.length > 4) {
            throw new ParserException(currentFile, lineNo, "Please provide 3 arguments with \"::\" in between!", false);
        }
        String sLocation = locArgs[0].trim();
        ea = parseEncounterArea(route, sLocation, lineNo);
        String slotString = locArgs[1].trim();

        int slot = 0;
        try {
            slot = Integer.parseInt(slotString);
        } catch (NumberFormatException nfe) {
            throw new ParserException(currentFile, lineNo, "Could not parse slot number: \"" + slotString + "\"", false);
        }
        sb = ea.getBattler(slot);
        String[] dvs = locArgs[2].trim().split(" ");
        if (dvs.length != 4) {
            throw new ParserException(currentFile, lineNo, "Please provide all 4 DV values!", false);
        }
        try {
            atk = Integer.parseInt(dvs[0]);
            def = Integer.parseInt(dvs[1]);
            spd = Integer.parseInt(dvs[2]);
            spc = Integer.parseInt(dvs[3]);
        } catch (NumberFormatException nfe) {
            throw new ParserException(currentFile, lineNo, "Please provide integers for DV values!", false);
        }
        sb = new SingleBattler(route.rd, ea, sb.pokemon, sb.level, atk, def, spd, spc);

        // Handle description line
        String title = "Manip " + sb;
        String description = null;
        if (lines.length == 2) {
            String descrLine = lines[1].trim();
            String[] descrArgs = descrLine.split("::");
            if (descrArgs.length > 1) {
                if (!descrArgs[0].trim().isEmpty()) {
                    title = descrArgs[0];
                }
                if (!descrArgs[1].trim().isEmpty()) {
                    description = descrLine.replaceFirst(".*?::", "").trim();
                }
            } else {
                description = descrLine;
            }
        }
        info = new RouteEntryInfo(title, description);

        RouteGetPokemon rgp = new RouteGetPokemon(route, info, sb);
        parent.addEntry(rgp);
        return rgp;
    }

    private RouteMenu addNewMenu(Route route, RouteSection parent, String[] lines, int lineNo) throws ParserException {
        // TODO: extensive testing!
        lines = getNonEmptyLines(lines);
        // Menu: [<description>]
        //    <description>
        //    SWAP: <item1> :: <item2|item2Idx>
        //    TM: <tm|move> :: <partyIdx> [:: <replaced move>]
        //    TOSS: <item> [:: <count>]
        //    USE: <item> [:: <count> [:: <partyIdx> [:: <move>]]]
        String description = lines[0].replaceFirst(menuPrefix, "").trim();
        RouteMenu rm = new RouteMenu(route, (description.isEmpty() ? null : description));

        for (int i = 1; i < lines.length; i++) {
            String entryLine = lines[i].trim();
            String typeString = "";
            int colon = entryLine.indexOf(':');
            if (colon >= 0) {
                typeString = entryLine.substring(0, colon).trim();
            }
            String[] args = entryLine.substring(typeString.length() + 1).split("::");
            for (int j = 0; j < args.length; j++) {
                args[j] = args[j].trim();
            }
            Item item;
            int count;
            int partyIdx;
            switch (typeString) {
                case "SWAP":
                    // SWAP: <item1> :: <item2|item2Idx>
                    if (args.length != 2) {
                        throw new ParserException(currentFile, lineNo, "A swap expects just 2 arguments", false);
                    }
                    Item item1 = route.rd.getItem(args[0]);
                    if (item1 == null) {
                        throw new ParserException(currentFile, lineNo, "Could not find item \"" + args[0] + "\"", false);
                    }
                    try {
                        int item2Idx = Integer.parseInt(args[1]);
                        rm.addSwap(item1, item2Idx);
                    } catch (NumberFormatException nfe) {
                        Item item2 = route.rd.getItem(args[1]);
                        if (item2 == null) {
                            throw new ParserException(currentFile, lineNo, "Could not find item \"" + args[1] + "\"", false);
                        }
                        rm.addSwap(item1, item2);
                    }
                    break;
                case "TM":
                    // TM: <tm|move> :: <partyIdx> [:: <replaced move>]
                    if (args.length < 2 || args.length > 3) {
                        throw new ParserException(currentFile, lineNo, "A TM use expects 2 or 3 arguments", false);
                    }
                    String strTM = args[0];
                    Item tmItem = route.rd.getTM(strTM);
                    Move tmMove = null;
                    if (tmItem == null) {
                        tmMove = route.rd.getMove(strTM);
                        if (tmMove == null) {
                            throw new ParserException(currentFile, lineNo, "Could not find TM \"" + strTM + "\"", false);
                        }
                    }
                    try {
                        partyIdx = Integer.parseInt(args[1]);
                    } catch (NumberFormatException nfe) {
                        throw new ParserException(currentFile, lineNo, "Could not parse party index", false);
                    }
                    if (args.length == 2) {
                        if (tmItem != null) {
                            rm.addTM(tmItem, partyIdx);
                        } else {
                            rm.addTM(tmMove, partyIdx);
                        }
                    } else {
                        Move replMove = route.rd.getMove(args[2]);
                        if (replMove == null) {
                            throw new ParserException(currentFile, lineNo, "Could not find the move \"" + args[2] + "\"", false);
                        }
                        if (tmItem != null) {
                            rm.addTM(tmItem, partyIdx, replMove);
                        } else {
                            rm.addTM(tmMove, partyIdx, replMove);
                        }
                    }
                    break;
                case "TOSS":
                    // TOSS: <item> [:: <count>]
                    if (args.length < 1 || args.length > 2) {
                        throw new ParserException(currentFile, lineNo, "An item toss expects 1 or 2 arguments", false);
                    }
                    item = route.rd.getItem(args[0]);
                    if (item == null) {
                        throw new ParserException(currentFile, lineNo, "Could not find item \"" + args[0] + "\"", false);
                    }
                    if (args.length < 2) {
                        rm.addToss(item);
                    } else {
                        try {
                            count = Integer.parseInt(args[1]);
                            if (count < 1) {
                                throw new NumberFormatException();
                            }
                        } catch (NumberFormatException nfe) {
                            throw new ParserException(currentFile, lineNo, "Could not parse the item count", false);
                        }
                        rm.addToss(item, count);
                    }
                    break;
                case "USE":
                    // USE: <item> [:: <count> [:: <partyIdx> [:: <move>]]]
                    if (args.length < 1 || args.length > 4) {
                        throw new ParserException(currentFile, lineNo, "An item use expects 1 to 4 arguments", false);
                    }
                    item = route.rd.getItem(args[0]);
                    if (item == null) {
                        throw new ParserException(currentFile, lineNo, "Could not find item \"" + args[0] + "\"", false);
                    }
                    if (args.length < 2) {
                        rm.addUse(item);
                    } else {
                        try {
                            count = Integer.parseInt(args[1]);
                            if (count < 1) {
                                throw new NumberFormatException();
                            }
                        } catch (NumberFormatException nfe) {
                            throw new ParserException(currentFile, lineNo, "Could not parse the item count", false);
                        }
                        if (args.length < 3) {
                            rm.addUse(item, count);
                        } else {
                            try {
                                partyIdx = Integer.parseInt(args[2]);
                            } catch (NumberFormatException nfe) {
                                throw new ParserException(currentFile, lineNo, "Could not parse the party index", false);
                            }
                            if (args.length < 4) {
                                rm.addUse(item, count, partyIdx);
                            } else {
                                Move move = route.rd.getMove(args[3]);
                                if (move == null) {
                                    throw new ParserException(currentFile, lineNo, "Could not find the move \"" + args[3] + "\"", false);
                                }
                                rm.addUse(item, count, partyIdx, move);
                            }
                        }
                    }
                    break;
                default:
                    rm.addDescription(entryLine);
                    break;
            }
        }
        parent.addEntry(rm);
        return rm;
    }

    private RouteImage getNewImage(Route route, String line, int lineNo) throws ParserException {
        return null;
    }

    private RouteSection addNewSection(Route route, RouteSection parent, String[] lines, int lineNo) throws ParserException {
        // TODO: location
        // S: [<location> ::] <title>
        //      [<route entry>]
        //      [..]
//        Location location;
        String title;

        String sectionLine = lines[0].replaceFirst(sectionPrefix, "").trim();

        // Handle section line
        title = sectionLine;
        RouteSection rs = new RouteSection(route, title);
        parent.addEntry(rs);

        int subEntryLine = 1;
        while (subEntryLine < lines.length
                && !isEnd(lines[subEntryLine])) {
            if (!isEmptyOrComment(lines[subEntryLine])) {
                String[] subEntryLines = getRelatedLineBundle(lines, subEntryLine);
                addNewRouteEntry(route, rs, subEntryLines, lineNo + subEntryLine);
                subEntryLine += subEntryLines.length;
            } else {
                subEntryLine++;
            }
        }

        return rs;
    }

    private RouteShopping getNewShopping(Route route, String line, int lineNo) throws ParserException {
        return null;
    }

    private RouteSwapPokemon addNewSwapPokemon(Route route, RouteSection parent, String[] lines, int lineNo) throws ParserException {
        lines = getNonEmptyLines(lines);
        if (lines.length > 2) {
            throw new ParserException(currentFile, lineNo, "A swap entry can only have 1 extra description line", false);
        }

        // TODO pc boxes
        // Swap: <party id1> <party id1>
        //      [[<title> ::] [<description>]]
        String swapLine = lines[0].replaceFirst(swapPokemonPrefix, "").trim();

        RouteEntryInfo info = null;
        int index1 = -1;
        int index2 = -1;

        // Handle swap line
        String[] indices = swapLine.split(" ");
        if (indices.length != 2) {
            throw new ParserException(currentFile, lineNo, "Swap needs 2 indeces!", false);
        }
        try {
            index1 = Integer.parseInt(indices[0]);
            index2 = Integer.parseInt(indices[1]);
        } catch (NumberFormatException nfe) {
            throw new ParserException(currentFile, lineNo, "Could not parse indices!", false);
        }

        // Handle description line
        String title = null;
        String description = null;
        if (lines.length == 2) {
            String descrLine = lines[1].trim();
            String[] descrArgs = descrLine.split("::");
            if (descrArgs.length > 1) {
                if (!descrArgs[0].trim().isEmpty()) {
                    title = descrArgs[0];
                }
                if (!descrArgs[1].trim().isEmpty()) {
                    description = descrLine.replaceFirst(".*?::", "").trim();
                }
            } else {
                description = descrLine;
            }
        }
        if (title != null || description != null) {
            info = new RouteEntryInfo(title, description);
        }

        RouteSwapPokemon rsp = new RouteSwapPokemon(route, info, index1, index2);
        parent.addEntry(rsp);
        return rsp;
    }

    private String[] getRelatedLineBundle(String[] lines, int startLine) {
        if (lines.length >= startLine) {
            int l = startLine;
            int depth = getDepth(lines[l]);
            while (l + 1 < lines.length
                    && !isEnd(lines[l + 1])
                    && (isEmptyOrComment(lines[l + 1]) || getDepth(lines[l + 1]) > depth)) {
                l++;
            }
            String[] bundle = new String[l - (startLine - 1)];
            for (int i = 0; i < bundle.length; i++) {
                bundle[i] = lines[startLine + i];
            }
            return bundle;
        } else {
            return new String[0];
        }
    }

    private int getDepth(String line) {
        int depth = 0;
        char[] chars = line.toCharArray();
        while (depth < chars.length && chars[depth] == '\t') {
            depth++;
        }
        return depth;
    }

    private boolean isEmptyOrComment(String line) {
        return line.trim().isEmpty() || line.trim().startsWith("//");
    }

    private boolean isEnd(String line) {
        return line.trim().startsWith("===");
    }

    private String[] getNonEmptyLines(String[] lines) {
        List<String> newLines = new ArrayList<>();
        for (String line : lines) {
            if (!isEmptyOrComment(line)) {
                newLines.add(line.replaceFirst("//.*", ""));
            }
        }
        return newLines.toArray(new String[0]);
    }

    private RouteEntry getLastEntry(Route route) {
        RouteEntry last = route;
        while (last.hasChildren()) {
            last = last.getChildren().get(last.getChildren().size() - 1);
        }
        return last;
    }

    private IntPair[] parseIntPairs(String toParse, int lineNo) throws ParserException {
        String[] args = toParse.trim().split(" ");
        int count = 0;
        for (String s : args) {
            if (!s.isEmpty()) {
                count++;
            }
        }
        IntPair[] pairs = new IntPair[count];
        int index = 0;
        for (int i = 0; i < args.length; i++) {
            if (!args[i].isEmpty()) {
                String[] args2 = args[i].trim().split(":");
                int int1;
                int int2;
                if (args2.length != 2) {
                    throw new ParserException(currentFile, lineNo, "Invalid integer pair \"" + args[i] + "\"", false);
                }
                try {
                    int1 = Integer.parseInt(args2[0]);
                    int2 = Integer.parseInt(args2[1]);
                } catch (NumberFormatException nfe) {
                    throw new ParserException(currentFile, lineNo, "Invalid integer pair \"" + args[i] + "\"", false);
                }
                pairs[index] = new IntPair(int1, int2);
                index++;
            }
        }

        return pairs;
    }

    private EncounterArea parseEncounterArea(Route route, String toParse, int lineNo) throws ParserException {
        EncounterArea ea;
        String[] args = toParse.trim().split(":");
        if (args.length > 2) {
            throw new ParserException(currentFile, lineNo, "Too many arguments for the location", false);
        }
        Location loc = route.rd.getLocation(args[0].trim());
        if (loc == null) {
            throw new ParserException(currentFile, lineNo, "Could not find the location", false);
        }
        if (loc.getEncounterAreas().isEmpty()) {
            throw new ParserException(currentFile, lineNo, "This location doesn't have an encounter area", false);
        }
        ea = route.rd.getEncounterArea(loc, null);
        if (args.length > 1) {
            ea = route.rd.getEncounterArea(loc, args[1].trim());
            if (ea == null) {
                throw new ParserException(currentFile, lineNo, "Could not find the sublocation", false);
            }
        }
        return ea;
    }

    private Location parseLocation(RouterData rd, String toParse, int lineNo) throws ParserException {
        Location loc;
        String[] args = toParse.trim().split(":");
        if (args.length > 2) {
            throw new ParserException(currentFile, lineNo, "Too many arguments for the location", false);
        }
        loc = rd.getLocation(args[0].trim());
        if (loc == null) {
            throw new ParserException(currentFile, lineNo, "Could not find the location", false);
        }
        return loc;
    }

//    public static void main(String[] args) throws RouteParserException {
//        RouteParser parser = new RouteParser();
//        Route route = parser.parseFile("route_example.txt");
//        new RouterFrame(route).setVisible(true);
//        System.out.println("======================================");
//        System.out.println(new RouteWriter().writeToString(route));
//    }
}
