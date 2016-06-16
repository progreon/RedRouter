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

/**
 *
 * @author Marco Willems
 */
public class Pokemon {

    public enum Pkmn {

        BULBASAUR, IVYSAUR, VENUSAUR, CHARMANDER, CHARMELEON, CHARIZARD, SQUIRTLE, WARTORTLE, BLASTOISE, CATERPIE, METAPOD, BUTTERFREE,
        WEEDLE, KAKUNA, BEEDRILL, PIDGEY, PIDGEOTTO, PIDGEOT, RATTATA, RATICATE, SPEAROW, FEAROW, EKANS, ARBOK, PIKACHU, RAICHU,
        SANDSHREW, SANDSLASH, NIDORANF, NIDORINA, NIDOQUEEN, NIDORANM, NIDORINO, NIDOKING, CLEFAIRY, CLEFABLE, VULPIX, NINETALES,
        JIGGLYPUFF, WIGGLYTUFF, ZUBAT, GOLBAT, ODDISH, GLOOM, VILEPLUME, PARAS, PARASECT, VENONAT, VENOMOTH, DIGLETT, DUGTRIO, MEOWTH,
        PERSIAN, PSYDUCK, GOLDUCK, MANKEY, PRIMEAPE, GROWLITHE, ARCANINE, POLIWAG, POLIWHIRL, POLIWRATH, ABRA, KADABRA, ALAKAZAM, MACHOP,
        MACHOKE, MACHAMP, BELLSPROUT, WEEPINBELL, VICTREEBEL, TENTACOOL, TENTACRUEL, GEODUDE, GRAVELER, GOLEM, PONYTA, RAPIDASH, SLOWPOKE,
        SLOWBRO, MAGNEMITE, MAGNETON, FARFETCHD, DODUO, DODRIO, SEEL, DEWGONG, GRIMER, MUK, SHELLDER, CLOYSTER, GASTLY, HAUNTER, GENGAR,
        ONIX, DROWZEE, HYPNO, KRABBY, KINGLER, VOLTORB, ELECTRODE, EXEGGCUTE, EXEGGUTOR, CUBONE, MAROWAK, HITMONLEE, HITMONCHAN, LICKITUNG,
        KOFFING, WEEZING, RHYHORN, RHYDON, CHANSEY, TANGELA, KANGASKHAN, HORSEA, SEADRA, GOLDEEN, SEAKING, STARYU, STARMIE, MRMIME, SCYTHER,
        JYNX, ELECTABUZZ, MAGMAR, PINSIR, TAUROS, MAGIKARP, GYARADOS, LAPRAS, DITTO, EEVEE, VAPOREON, JOLTEON, FLAREON, PORYGON, OMANYTE,
        OMASTAR, KABUTO, KABUTOPS, AERODACTYL, SNORLAX, ARTICUNO, ZAPDOS, MOLTRES, DRATINI, DRAGONAIR, DRAGONITE, MEWTWO, MEW
    }
    
    public enum Gender {
        BOTH, MALE, FEMALE, NONE
    }

    public final Pkmn species;
    public Pkmn evolution = null;
    public final String name;
    public final Types.Type type1;
    public final Types.Type type2;
    public final Gender possibleGender;
    public final double maleRatio;
    public final int expGiven;
    public final int hp;
    public final int atk;
    public final int def;
    public final int spd;
    public final int spc;

    public Pokemon(Pkmn species, String name, Types.Type type1, Types.Type type2, Gender possibleGender, double maleRatio, int expGiven, int hp, int atk, int def, int spd, int spc) {
        this.species = species;
        if (name == null) {
            name = species.toString().substring(0, 1);
            name += species.toString().substring(1).toLowerCase();
        }
        this.name = name;
        this.type1 = type1;
        this.type2 = type2;
        this.possibleGender = (possibleGender == null?Gender.NONE:possibleGender);
        this.maleRatio = maleRatio;
        this.expGiven = expGiven;
        this.hp = hp;
        this.atk = atk;
        this.def = def;
        this.spd = spd;
        this.spc = spc;
    }

    @Override
    public String toString() {
        return species + " [" + name + "]: " + hp + "," + atk + "," + def + "," + spd + "," + spc;
//        return name;
    }

}
