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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import be.marcowillems.redrouter.Settings;
import be.marcowillems.redrouter.data.EncounterArea;
import be.marcowillems.redrouter.data.Location;
import be.marcowillems.redrouter.data.Move;
import be.marcowillems.redrouter.data.Player;
import be.marcowillems.redrouter.data.Pokemon;
import be.marcowillems.redrouter.data.RouterData;
import be.marcowillems.redrouter.data.SingleBattler;
import be.marcowillems.redrouter.data.Trainer;
import be.marcowillems.redrouter.route.Route;
import be.marcowillems.redrouter.route.RouteBattle;
import be.marcowillems.redrouter.route.RouteDirections;
import be.marcowillems.redrouter.route.RouteEncounter;
import be.marcowillems.redrouter.route.RouteEntry;
import be.marcowillems.redrouter.route.RouteEntryInfo;
import be.marcowillems.redrouter.route.RouteGetPokemon;
import be.marcowillems.redrouter.route.RouteImage;
import be.marcowillems.redrouter.route.RouteLearnTmMove;
import be.marcowillems.redrouter.route.RouteSection;
import be.marcowillems.redrouter.route.RouteShopping;
import be.marcowillems.redrouter.route.RouteSwapPokemon;
import be.marcowillems.redrouter.route.RouteUseCandies;
import be.marcowillems.redrouter.util.IntPair;
import java.io.File;
import java.io.FileNotFoundException;

/**
 * TODO error messages push system TODO handle multiple (white)spaces
 *
 * @author Marco Willems
 */
public class RouteParser {

//    private RouterData rd = null;
    private HashMap<String, Trainer> trainers; // alias => trainer

    public RouteParser() {
        init();
    }

    private void init() {
        // TODO: set up parser ?
        trainers = new HashMap<>();
    }

    public Route parseFile(File file) throws RouteParserException {
        Route route = null;

        List<String> lines;
        try {
            lines = RouterData.getLinesFromFile(file);
        } catch (FileNotFoundException ex) {
//            Logger.getLogger(RouteParser.class.getName()).log(Level.SEVERE, null, ex);
            throw new RouteParserException("File not found: " + file.getAbsolutePath(), -1);
        }
        int lineNo = 0;
        boolean ignore = false;
        Stack<RouteSection> sectionStack = new Stack<>();

        // For now: skip until route definition
        while (lineNo < lines.size() && !lines.get(lineNo).startsWith("Route:")) {
            lineNo++;
        }

        // First: initialize route (to set the rd object)
        if (lineNo < lines.size()) {
            String line = lines.get(lineNo);
            route = getNewRoute(line, lineNo);
            route.disableRefresh();
            sectionStack.push(route);
            lineNo++;
        }

        // Then start over to parse all
        lineNo = 0;
        while (lineNo < lines.size() && !lines.get(lineNo).startsWith("Route:")) {
            String line = lines.get(lineNo);
            if (line.startsWith("Trainer:")) {
                addNewTrainer(route, line.substring(8).trim(), lineNo);
            }
            lineNo++;
        }
        lineNo++; // Skip the route line

        while (lineNo < lines.size() && !ignore) {
            String line = lines.get(lineNo);
            lineNo++;
            if (line.startsWith("===")) {
                ignore = true;
                continue;
            }
            int commentIndex = line.indexOf("//");
            if (commentIndex != -1) {
                line = line.substring(0, commentIndex);
            }
            if (!line.trim().isEmpty()) {
                int depth = 0;
                char[] chars = line.toCharArray();
                while (chars[depth] == '\t') {
                    depth++;
                }
                if (depth == 0) {
                    throw new RouteParserException("Wrong indentation, use tabs!!", lineNo);
                }
                while (depth < sectionStack.size()) {
                    sectionStack.pop();
                }

                line = line.trim();
                String[] args = line.split(" ");
                switch (args[0]) {
                    case "Candy:": // TEMPORARY
                        line = line.substring(args[0].length()).trim();
                        sectionStack.peek().addEntry(getNewUseCandies(route, line, lineNo));
                        break;
                    case "TM:": // TEMPORARY
                        line = line.substring(args[0].length()).trim();
                        sectionStack.peek().addEntry(getNewLearnTmMove(route, line, lineNo));
                        break;
                    case "M:":
                        line = line.substring(args[0].length()).trim();
                        sectionStack.peek().addEntry(getNewManipPokemon(route, line, lineNo));
                        break;
                    case "GetP:":
                        line = line.substring(args[0].length()).trim();
                        sectionStack.peek().addEntry(getNewGetPokemon(route, line, lineNo));
                        break;
                    case "E:":
                        line = line.substring(args[0].length()).trim();
                        sectionStack.peek().addEntry(getNewEncounter(route, line, lineNo));
                        break;
                    case "SwapP:":
                        line = line.substring(args[0].length()).trim();
                        sectionStack.peek().addEntry(getNewSwapPokemon(route, line, lineNo));
                        break;
                    case "C:":
                        line = line.substring(args[0].length()).trim();
                        sectionStack.peek().addEntry(getNewCatchPokemon(route, line, lineNo));
                        break;
                    case "B:":
                        line = line.substring(args[0].length()).trim();
                        sectionStack.peek().addEntry(getNewBattle(route, line, lineNo));
                        break;
                    case "S:":
                        line = line.substring(args[0].length()).trim();
                        RouteSection section = getNewSection(route, line, lineNo);
                        sectionStack.peek().addEntry(section);
                        sectionStack.push(section);
                        break;
                    case "D:":
                        line = line.substring(args[0].length()).trim();
                    default:
                        sectionStack.peek().addEntry(getNewDirections(route, line, lineNo));
                        break;
                }
            }
        }

        if (route != null) {
            route.enableRefresh();
        } else {
            throw new RouteParserException("This file ain't contain no route!", -1);
        }
        return route;
    }

    private void addNewTrainer(Route route, String line, int lineNo) throws RouteParserException {
        String name;
        Location location = null;
        String alias;
        List<SingleBattler> team = new ArrayList<>();

        String[] trArgs = line.split("::");
        if (trArgs.length > 3) {
            throw new RouteParserException("Too many arguments for trainer", lineNo);
        }
        name = trArgs[0].trim();
        if (trArgs.length == 3) {
            location = parseLocation(route, trArgs[1].trim(), lineNo);
            line = trArgs[2].trim();
        } else {
            line = trArgs[1].trim();
        }

        String[] args = line.split(" ");
        if (args.length < 2) {
            throw new RouteParserException("Please give an alias and at least one pokemon", lineNo);
        }
        alias = args[0];
        for (int i = 1; i < args.length; i++) {
            String[] pokeArgs = args[i].split(":");
            if (pokeArgs.length != 2) {
                throw new RouteParserException("Wrong syntax of pokemon argument: \"" + args[i] + "\"", lineNo);
            }
            Pokemon p = route.rd.getPokemon(pokeArgs[0]);
            if (p == null) {
                throw new RouteParserException("Could not find pokemon \"" + pokeArgs[0] + "\"", lineNo);
            }
            int level = -1;
            try {
                level = Integer.parseInt(pokeArgs[1]);
            } catch (NumberFormatException nfe) {
                throw new RouteParserException("Could not parse the pokemon level in \"" + args[i] + "\"", lineNo);
            }
            if (level <= 1) {
                throw new RouteParserException("The pokemon level must be greater than 1", lineNo);
            }
            team.add(new SingleBattler(p, level, null));
        }

        Trainer trainer = new Trainer(location, name, null, team);
        trainers.put(alias, trainer);
    }

    private Route getNewRoute(String line, int lineNo) throws RouteParserException {
        String[] args = line.split(" ");
        // args[0]: "Route:" OK
        // args[1]: "R|B|Y" CHECK
        // args[2]: "::" CHECK
        // args[3...]: "<title>" CHECK

        if (args.length < 4) {
            throw new RouteParserException("Too few arguments!", lineNo);
        }
        if (!args[2].equals("::")) {
            throw new RouteParserException("Unrecognized token: \"" + args[2] + "\"", lineNo);
        }
        RouterData rd;
        switch (args[1]) {
            case "R":
                rd = new RouterData(new Settings(Settings.GAME_RED));
                break;
            case "B":
                rd = new RouterData(new Settings(Settings.GAME_BLUE));
                break;
            case "Y":
                rd = new RouterData(new Settings(Settings.GAME_YELLOW));
                break;
            default:
                throw new RouteParserException("Unrecognized game: \"" + args[1] + "\"", lineNo);
        }
        String routeHeader = "Route: R :: ";
        String title = line.substring(routeHeader.length()).trim();
        return new Route(rd, title);
    }

    private RouteBattle getNewBattle(Route route, String line, int lineNo) throws RouteParserException {
        String alias;
        String description = null;
        Trainer opponent;

        int[][] competingPartyMon = null;
        RouteEntry previousEntry = getLastEntry(route);
        route.enableRefresh(); // A forced refresh
        route.disableRefresh();
        Player prevPlayer = previousEntry.getPlayer();

        String[] args = line.split("::");
        if (args.length < 1 || args.length > 3) {
            throw new RouteParserException("Expected 1 to 3 arguments in between \"::\"", lineNo);
        }
        alias = args[0].trim();
        opponent = trainers.get(alias);
        if (opponent == null) {
            throw new RouteParserException("Trainer \"" + alias + "\" could not be found!", lineNo);
        }
        if (args.length == 3) {
            int teamSize = opponent.team.size();
            int[] partyCount = new int[teamSize];
            IntPair[] partyArgs = parseIntPairs(args[1], lineNo);
            for (IntPair partyArg : partyArgs) {
                int opp = partyArg.int1;
                int part = partyArg.int2;
                if (opp < 0 || opp >= teamSize) {
                    throw new RouteParserException("Invalid opponent index in \"" + partyArg + "\"", lineNo);
                }
                if (part < 0 || part >= prevPlayer.team.size()) {
                    throw new RouteParserException("Invalid party index in \"" + partyArg + "\"", lineNo);
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

            description = args[2].trim();
        } else if (args.length == 2) {
            description = args[1].trim();
        }
        return new RouteBattle(new RouteEntryInfo(opponent.name, description), opponent, competingPartyMon);
    }

    private RouteDirections getNewDirections(Route route, String line, int lineNo) throws RouteParserException {
        return new RouteDirections(line);
    }

    private RouteEncounter getNewEncounter(Route route, String line, int lineNo) throws RouteParserException {
        EncounterArea ea; // TODO handle location = area + sub area!!
        RouteEntryInfo info = null;

        String[] locArgs = line.split("::");
        if (locArgs.length < 2 || locArgs.length > 3) {
            throw new RouteParserException("Please provide 2 or 3 arguments with '::' in between!", lineNo);
        }
        String sLocation = locArgs[0].trim();
        ea = parseEncounterArea(route, sLocation, lineNo);
        if (ea == null) {
            throw new RouteParserException("Could not find location \"" + sLocation + "\"", lineNo);
        }
        IntPair[] slotPairs = parseIntPairs(locArgs[1].trim(), lineNo);
        if (locArgs.length == 3) {
            info = new RouteEntryInfo(null, locArgs[2].trim());
        }

        return new RouteEncounter(info, ea, slotPairs);
    }

    private RouteGetPokemon getNewCatchPokemon(Route route, String line, int lineNo) throws RouteParserException {
        EncounterArea ea; // TODO handle location = area + sub area!!
        List<SingleBattler> choices = new ArrayList<>();
        int preference = -1;

        String[] locArgs = line.split("::");
        if (locArgs.length != 2) {
            throw new RouteParserException("Please provide 2 arguments with '::' in between!", lineNo);
        }
        String sLocation = locArgs[0].trim();
        ea = parseEncounterArea(route, sLocation, lineNo);
        String[] slots = locArgs[1].trim().split(" ");
        if (slots.length == 0) {
            throw new RouteParserException("Please list some slots!", lineNo);
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
                throw new RouteParserException("Could not parse slot number: \"" + slots[i] + "\"", lineNo);
            }
            choices.add(ea.getBattler(slot));
        }
        if (preference < 0) {
            preference = 0;
        }

        return new RouteGetPokemon(null, choices, preference);
    }

    private RouteGetPokemon getNewGetPokemon(Route route, String line, int lineNo) throws RouteParserException {
        String[] args = line.split(" ");
        List<SingleBattler> choices = new ArrayList<>();
        int preference = -1;

        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("#")) {
                if (preference < 0) {
                    preference = i;
                }
                args[i] = args[i].substring(1);
            }
            String[] pokeArgs = args[i].split(":");
            if (pokeArgs.length != 2) {
                throw new RouteParserException("Invalid pokemon option \"" + args[i] + "\"", lineNo);
            }
            Pokemon p = route.rd.getPokemon(pokeArgs[0]);
            if (p == null) {
                throw new RouteParserException("Could not find pokemon \"" + pokeArgs[0] + "\"", lineNo);
            }
            int level = -1;
            try {
                level = Integer.parseInt(pokeArgs[1]);
            } catch (NumberFormatException nfe) {
                throw new RouteParserException("Invalid level \"" + pokeArgs[1] + "\", must be an integer!", lineNo);
            }
            if (level < 2 || level > 100) {
                throw new RouteParserException("Invalid level \"" + pokeArgs[1] + "\", must be between 2 and 100 (included)", lineNo);
            }
            choices.add(new SingleBattler(p, level, null));
        }

        return new RouteGetPokemon(null, choices, preference);
    }

    private RouteGetPokemon getNewManipPokemon(Route route, String line, int lineNo) throws RouteParserException {
        EncounterArea ea; // TODO handle location = area + sub area!!
        SingleBattler sb;
        int atk, def, spd, spc;
        RouteEntryInfo info = null;

        String[] locArgs = line.split("::");
        if (locArgs.length < 3 || locArgs.length > 4) {
            throw new RouteParserException("Please provide 3 or 4 arguments with '::' in between!", lineNo);
        }
        String sLocation = locArgs[0].trim();
        ea = parseEncounterArea(route, sLocation, lineNo);
        String slotString = locArgs[1].trim();

        int slot = 0;
        try {
            slot = Integer.parseInt(slotString);
        } catch (NumberFormatException nfe) {
            throw new RouteParserException("Could not parse slot number: \"" + slotString + "\"", lineNo);
        }
        sb = ea.getBattler(slot);
        String[] dvs = locArgs[2].trim().split(" ");
        if (dvs.length != 4) {
            throw new RouteParserException("Please provide all 4 DV values!", lineNo);
        }
        try {
            atk = Integer.parseInt(dvs[0]);
            def = Integer.parseInt(dvs[1]);
            spd = Integer.parseInt(dvs[2]);
            spc = Integer.parseInt(dvs[3]);
        } catch (NumberFormatException nfe) {
            throw new RouteParserException("Please provide integers for DV values!", lineNo);
        }
        sb = new SingleBattler(ea, sb.pokemon, sb.level, atk, def, spd, spc);

        if (locArgs.length == 4) {
            info = new RouteEntryInfo(null, locArgs[3].trim());
        }

        return new RouteGetPokemon(info, sb);
    }

    // TEMPORARY
    private RouteLearnTmMove getNewLearnTmMove(Route route, String line, int lineNo) throws RouteParserException {
        Move newMove;
        Move oldMove = null;
        RouteEntryInfo info = null;

        String[] params = line.split("::");
        String moveName1 = params[0].trim();
        newMove = route.rd.getMove(moveName1);
        if (newMove == null) {
            throw new RouteParserException("Could not find the move \"" + moveName1 + "\"", lineNo);
        }
        if (params.length > 1) {
            String moveName2 = params[1].trim();
            oldMove = route.rd.getMove(moveName2);
            if (oldMove == null) {
                throw new RouteParserException("Could not find the move \"" + moveName2 + "\"", lineNo);
            }
            info = new RouteEntryInfo(null, "Teach TM " + moveName1 + " over " + moveName2);
        } else {
            info = new RouteEntryInfo(null, "Teach TM " + moveName1);
        }
        return new RouteLearnTmMove(info, newMove, oldMove);
    }

    private RouteImage getNewImage(Route route, String line, int lineNo) throws RouteParserException {
        return null;
    }

    private RouteSection getNewSection(Route route, String line, int lineNo) throws RouteParserException {
        String[] params = line.split("::");
        String title = params[0].trim();
        String description = null;
        if (params.length > 1) {
            description = line.substring(params[0].length() + 2).trim();
        }
        return new RouteSection(title, description);
    }

    private RouteShopping getNewShopping(Route route, String line, int lineNo) throws RouteParserException {
        return null;
    }

    private RouteSwapPokemon getNewSwapPokemon(Route route, String line, int lineNo) throws RouteParserException {
        int index1 = -1;
        int index2 = -1;
        String description = null;

        String[] args = line.split("::");
        if (args.length > 2) {
            throw new RouteParserException("Swap only takes a maximum of 2 parameters separated by \"::\"", lineNo);
        }
        String[] indices = args[0].trim().split(" ");
        if (indices.length != 2) {
            throw new RouteParserException("Swap needs 2 indeces!", lineNo);
        }
        try {
            index1 = Integer.parseInt(indices[0]);
            index2 = Integer.parseInt(indices[1]);
        } catch (NumberFormatException nfe) {
            throw new RouteParserException("Could not parse indices!", lineNo);
        }

        if (args.length > 1) {
            description = args[1].trim();
        }

        return new RouteSwapPokemon(new RouteEntryInfo(null, description), index1, index2);
    }

    // TEMPORARY
    private RouteUseCandies getNewUseCandies(Route route, String line, int lineNo) throws RouteParserException {
        RouteEntryInfo info = null;
        int candyCount = 1;
        String[] params = line.split("::");
        String count = params[0].trim();
        try {
            candyCount = Integer.parseInt(count);
        } catch (NumberFormatException nfe) {
            throw new RouteParserException("Could not parse candy amount", lineNo);
        }
        if (params.length > 1) {
            String description = params[1].trim();
            info = new RouteEntryInfo(null, description);
        }
        if (info == null) {
            info = new RouteEntryInfo(null, "Use " + candyCount + " candies");
        }
        return new RouteUseCandies(info, candyCount);
    }

    private RouteEntry getLastEntry(Route route) {
        RouteEntry last = route;
        while (last.hasChildren()) {
            last = last.getChildren().get(last.getChildren().size() - 1);
        }
        return last;
    }

    private IntPair[] parseIntPairs(String toParse, int lineNo) throws RouteParserException {
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
                    throw new RouteParserException("Invalid integer pair \"" + args[i] + "\"", lineNo);
                }
                try {
                    int1 = Integer.parseInt(args2[0]);
                    int2 = Integer.parseInt(args2[1]);
                } catch (NumberFormatException nfe) {
                    throw new RouteParserException("Invalid integer pair \"" + args[i] + "\"", lineNo);
                }
                pairs[index] = new IntPair(int1, int2);
                index++;
            }
        }

        return pairs;
    }

    private EncounterArea parseEncounterArea(Route route, String toParse, int lineNo) throws RouteParserException {
        EncounterArea ea;
        String[] args = toParse.trim().split(":");
        if (args.length > 2) {
            throw new RouteParserException("Too many arguments for the location", lineNo);
        }
        Location loc = route.rd.getLocation(args[0].trim());
        if (loc == null) {
            throw new RouteParserException("Could not find the location", lineNo);
        }
        if (loc.encounterAreas.size() > 0) {
            ea = loc.encounterAreas.get(0);
        } else {
            throw new RouteParserException("This location doesn't have an encounter area", lineNo);
        }
        if (args.length > 1) {
            ea = route.rd.getEncounterArea(loc, args[1].trim());
            if (ea == null) {
                throw new RouteParserException("Could not find the sublocation", lineNo);
            }
        }
        return ea;
    }

    private Location parseLocation(Route route, String toParse, int lineNo) throws RouteParserException {
        Location loc;
        String[] args = toParse.trim().split(":");
        if (args.length > 2) {
            throw new RouteParserException("Too many arguments for the location", lineNo);
        }
        loc = route.rd.getLocation(args[0].trim());
        if (loc == null) {
            throw new RouteParserException("Could not find the location", lineNo);
        }
        EncounterArea ea = null;
        if (loc.encounterAreas.size() > 0) {
            ea = loc.encounterAreas.get(0);
        }
        if (args.length > 1) {
            ea = route.rd.getEncounterArea(loc, args[1].trim());
            if (ea == null) {
                throw new RouteParserException("Could not find the sublocation", lineNo);
            }
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
