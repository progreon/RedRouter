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
package be.marcowillems.redrouter.route;

import be.marcowillems.redrouter.data.SingleBattler;
import be.marcowillems.redrouter.data.RouterData;
import java.util.Set;
import java.util.TreeSet;
import be.marcowillems.redrouter.util.PokemonCountPair;

/**
 *
 * @author Marco Willems
 */
public class RouteFactory {

    private final RouterData rd;

    private Route exaNidoRoute;

    public RouteFactory(RouterData rd) {
        this.rd = rd;
    }

    public Route getRedExaNidoRoute() {
        if (exaNidoRoute == null) {
            initRedExaNidoRoute();
        }
        return exaNidoRoute;
    }

    private void initRedExaNidoRoute() {
        Route route = new Route(rd, "Red Any% Glitchless - Exarion Route");

        RouteSection rsStart = new RouteSection(null, "New Game");
        rsStart.addNewDirections("Clear any existing save file by pressing Up + B + Select on the game title screen");
        rsStart.addNewDirections("New game: text speed fast, animations off, style shift");
        // TODO: add image
        rsStart.addNewDirections("Start a new game, and name yourself and your rival a one character name.");
        route.addSection(rsStart);

        RouteSection rsPalletA = new RouteSection(null, "Pallet Town");
        rsPalletA.addNewDirections("Exit out of your home, and head north towards Route 1. Prof. Oak will stop you and lead you back to his lab. After he and your rival are done talking, select the middle Pokeball on the table to get Squirtle. Name it a one character name, and go to head out of the lab. Your rival will stop you for a battle.");
        RouteGetPokemon rgpPalletA = new RouteGetPokemon(new RouteEntryInfo("Get Squirtle"), new SingleBattler(rd.getPokemon("Squirtle"), null, 5));
        rsPalletA.addEntry(rgpPalletA);
        rsPalletA.addNewBattle(new RouteEntryInfo(rd.getTrainer("Rival 1").name, "Tail Whip x1-2, then Tackle until it faints."), rd.getTrainer("Rival 1"));
        rsPalletA.addNewDirections("Head out of the lab, and north to Route 1");
        route.addSection(rsPalletA);

        RouteSection rsParcel = new RouteSection(null, "Getting the Parcel to Oak");

        RouteSection rsRoute1A = new RouteSection(null, "Route 1");
        Set<PokemonCountPair> r1Choices = new TreeSet<>();
        r1Choices.add(new PokemonCountPair(rd.getPokemon("Rattata"), 2, 1));
        r1Choices.add(new PokemonCountPair(rd.getPokemon("Rattata"), 3));
        r1Choices.add(new PokemonCountPair(rd.getPokemon("Pidgey"), 2));
        r1Choices.add(new PokemonCountPair(rd.getPokemon("Pidgey"), 3));
        rsRoute1A.addNewEncounter("You want to defeat an encounter here so that you have enough experience to get Lvl.8 at the Bug Catcher fight later. Only attempt to kill low level pokemon as higher levels take longer to kill.", rd.getEncounterArea(rd.getLocation("Route 1"), null), r1Choices);
        rsRoute1A.addNewDirections("Head north through the route to Viridian City.");
        rsParcel.addSection(rsRoute1A);

        RouteSection rsViridianA = new RouteSection(null, "Viridian City");
        rsViridianA.addNewDirections("Head straight into the Mart and collect Oak's Parcel. Exit, and head south back to Route 1.");
        rsParcel.addSection(rsViridianA);

        RouteSection rsRoute1B = new RouteSection(null, "Route 1");
        rsRoute1B.addNewDirections("Head south, utilizing ledges to avoid as much grass as you can. If you have yet to kill an encounter, take the shorter route through the second-to-last grass patch; otherwise, walk around it.");
        rsParcel.addSection(rsRoute1B);

        RouteSection rsPalletB = new RouteSection(null, "Pallet Town");
        rsPalletB.addNewDirections("Head to the lab, finish the lengthy conversation with Prof. Oak and head back north once more.");
        rsParcel.addSection(rsPalletB);

        route.addSection(rsParcel);

        RouteSection rsNido = new RouteSection(null, "Getting Nidoran!");

        RouteSection rsRoute1C = new RouteSection(null, "Route 1");
        rsRoute1C.addNewDirections("Head north to Viridian City again, remembering to kill an encounter if you haven't already.");
        rsNido.addSection(rsRoute1C);

        RouteSection rsViridianB = new RouteSection(null, "Viridian City");
        rsViridianB.addNewDirections("Head back into the Mart for the first shopping trip.");
        // TODO: Shopping entry!
        rsViridianB.addNewDirections("The quantity of Poké Balls you buy is dependent on your own money management and how often you plan to reset. The recommended number is 8, but 7 is fine too. You only buy 6 if you plan on reseting if you fail to catch a nidoran twice, because you may risk running out of balls to catch HM slaves.");
        rsViridianB.addNewDirections("Head out of the Mart, and west to Route 22.");
        rsNido.addSection(rsViridianB);

        RouteSection rsRoute22A = new RouteSection(null, "Route 22");
        // TODO: Catching entry!
        rsRoute22A.addNewDirections("Time for the Nidoran hunt. You want to catch a Lvl.3-4 Nidoran♂, and give it a one character name.");
        rsRoute22A.addNewDirections("Tackle Lvl.3 Nidorans once to make the catch easier, but just throw PokeBalls at Lvl.4 Nidorans. If you encounter a Lv. 5 Spearow, try to catch it (just throw Poke Balls). If you catch a Lvl.5 Spearow on the first ball, DSum off it by going 5 out, 6 in, 12 out, then the standard DSum (4 in, 2 out, 6 in, 11 out). If you waste at least half your Poke Balls against Spearow, Tackle Lv. 4 Nidorans once or twice to avoid running out.");
        RouteGetPokemon rgpRoute22A = new RouteGetPokemon(new RouteEntryInfo("Catch Nidoran"), new SingleBattler(rd.getPokemon("NidoranM"), rd.getLocation("Route 22").encounterAreas.get(0), 4));
        rsRoute22A.addEntry(rgpRoute22A);
        rsRoute22A.addNewDirections("After you have your Nidoran, head back east to Viridian City.");
        rsNido.addSection(rsRoute22A);

        route.addSection(rsNido);

        RouteSection rsViridianC = new RouteSection(null, "Viridian City");
        rsViridianC.addNewDirections("Head north, and pick up the Potion hidden in the Cut tree on the left.");
        // TODO: add image
        rsViridianC.addNewDirections("Continue north to Route 2, and straight through to Viridian Forest.");
        route.addSection(rsViridianC);

        RouteSection rsViridianF = new RouteSection(null, "Viridian Forest");
        rsViridianF.addNewDirections("Avoid the trainers and walk on the encounterless tiles. Pick up the Antidote on your way up.");
        rsViridianF.addNewBattle(new RouteEntryInfo(rd.getTrainer("Bug 1").name, "Tail Whip x2, then Tackle until it faints."), rd.getTrainer("Bug 1"));
        rsViridianF.addNewDirections("After the battle, exit the grass and open the menu: Squirtle <-> Nidoran, [use potion], [use antidote]");
        RouteSwapPokemon rspiridianF = new RouteSwapPokemon(new RouteEntryInfo("Swap Squirtle with Nidoran"), 0, 1);
        rsViridianF.addEntry(rspiridianF);
        rsViridianF.addNewDirections("Exit Viridian Forest and head north to Pewter City.");
        route.addSection(rsViridianF);

        RouteSection rsPewter = new RouteSection(null, "Pewter City");
        rsPewter.addNewDirections("Head straight into Brock's Gym, go left to avoid the Gym trainer and battle Brock.");
        rsPewter.addNewBattle(new RouteEntryInfo(rd.getTrainer("Brock").name, "Geodude: switch to Squirtle, B x2-3, same for Onix"), rd.getTrainer("Brock"), new int[][]{{0, 1}, {0, 1}});
        rsPewter.addNewDirections("-- Pewter Mart --");
        rsPewter.addNewDirections("Exit the Mart and head east out of the city towards Route 3.");
        rsPewter.addNewDirections("Menu: go to Options and change your Battle Style from Shift to Set.");
        route.addSection(rsPewter);

        RouteSection rsRoute3 = new RouteSection(null, "Route 3");
        rsRoute3.addNewBattle(new RouteEntryInfo(rd.getTrainer("Bug 2").name, "x3: Leer, HA x2"), rd.getTrainer("Bug 2"));
        rsRoute3.addNewDirections("Heal to full or near full for the next fight");
        rsRoute3.addNewBattle(new RouteEntryInfo(rd.getTrainer("Shorts Guy").name, "x2: Leer, HA x2"), rd.getTrainer("Shorts Guy"));
        rsRoute3.addNewDirections("Potion before the next fight ONLY if you're at 1 HP.");
        route.addSection(rsRoute3);

        exaNidoRoute = route;
    }

}
