/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package redrouter.data;

/**
 *
 * @author marco
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

    public final Pkmn species;
    public Pkmn evolution = null;
    public final String name;
    public final Types.Type type1;
    public final Types.Type type2;
    public final int expGiven;
    public final int hp;
    public final int atk;
    public final int def;
    public final int spd;
    public final int spc;

    public Pokemon(Pkmn species, String name, Types.Type type1, Types.Type type2, int expGiven, int hp, int atk, int def, int spd, int spc) {
        this.species = species;
        this.name = name;
        this.type1 = type1;
        this.type2 = type2;
        this.expGiven = expGiven;
        this.hp = hp;
        this.atk = atk;
        this.def = def;
        this.spd = spd;
        this.spc = spc;
    }

}
