/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package redrouter.data;

import java.util.List;

/**
 *
 * @author marco
 */
public class Battler {

    private Pokemon pokemon;
    public int level;
    public boolean isMale;
    public List<Move> moveset;
//    public int totalXP = 0;

    public int hpXP = 0;
    public int atkXP = 0;
    public int defXP = 0;
    public int spdXP = 0;
    public int spcXP = 0;

    public int hpDV = 8;
    public int atkDV = 9;
    public int defDV = 8;
    public int spdDV = 8;
    public int spcDV = 8;

    public boolean atkBadge = false;
    public boolean defBadge = false;
    public boolean spdBadge = false;
    public boolean spcBadge = false;

    public Battler(Pokemon pokemon, int level, List<Move> moveset) {
        this.pokemon = pokemon;
        this.level = level;
        this.moveset = moveset;
    }

    public void evolve() {
        if (pokemon.evolution != null) {
            pokemon = RouteFactory.getPokedexByName().get(pokemon.evolution);
        }
    }

    public void addStatXP(int hp, int atk, int def, int spd, int spc) {
        hpXP += hp;
        atkXP += atk;
        defXP += def;
        spdXP += spd;
        spcXP += spc;
    }

    public int getHP() {
        return (int) Math.floor(((pokemon.hp + hpDV) * 2 + Math.floor(Math.ceil(Math.sqrt(hpXP)) / 4)) * level / 100) + level + 10;
    }

    public int getAtk(boolean withBoosts) {
        return getStat(pokemon.atk, atkDV, atkXP, withBoosts);
    }

    public int getDef(boolean withBoosts) {
        return getStat(pokemon.def, defDV, defXP, withBoosts);
    }

    public int getSpd(boolean withBoosts) {
        return getStat(pokemon.spd, spdDV, spdXP, withBoosts);
    }

    public int getSpc(boolean withBoosts) {
        return getStat(pokemon.spc, spcDV, spcXP, withBoosts);
    }

    // TODO: calculation with badge boosts
    private int getStat(int base, int DV, int XP, boolean withBoosts) {
        return (int) Math.floor(((base + DV) * 2 + Math.floor(Math.ceil(Math.sqrt(XP)) / 4)) * level / 100) + 5;
    }

    public Pokemon getPokemon() {
        return this.pokemon;
    }

    public boolean isType(Types.Type type) {
        return (type == pokemon.type1 || (pokemon.type2 != null && type == pokemon.type2));
    }

    public void setDVs(int hp, int atk, int def, int spd, int spc) {
        hpDV += hp;
        atkDV += atk;
        defDV += def;
        spdDV += spd;
        spcDV += spc;
    }

    @Override
    public String toString() {
        String battler = pokemon.name + " Lv." + level;
        String moves = "";
        if (moveset.size() > 0) {
            for (Move m : moveset) {
                moves += "," + m.NAME;
            }
            moves = moves.substring(1);
        }
        battler += " (" + moves + ")";

        return battler;
    }

}
