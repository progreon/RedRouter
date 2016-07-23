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
package redrouter;

/**
 *
 * @author Marco Willems
 */
public class Settings {

    // TODO from settings file?
//    public static final String TITLE = "Gen I DV Calculator";
    public static final String TITLE = "Gen I Router";

    public static final String GAME_RED = "Pokemon Red";
    public static final String GAME_BLUE = "Pokemon Blue";
    public static final String GAME_YELLOW = "Pokemon Yellow";
//    public static final String GAME_YELLOW = "Pokemon Yellow Version: Special Pikachu Edition";
    private final String encountersFileRed = "encounters_red.txt";
    private final String encountersFileBlue = "encounters_blue.txt";
    private final String encountersFileYellow = "encounters_yellow.txt";
    private final String locationsFile = "locations.txt";
    private final String pokemonFile = "pokemon.txt";
    private final String mMoveFile = "moves.txt";

    public final String game;

    public Settings() {
        this.game = GAME_RED;
    }

    public Settings(String game) {
        this.game = game;
    }

    public boolean isRed() {
        return game.equals(GAME_RED);
    }

    public boolean isBlue() {
        return game.equals(GAME_BLUE);
    }

    public boolean isRedBlue() {
        return game.equals(GAME_RED) || game.equals(GAME_BLUE);
    }

    public boolean isYellow() {
        return game.equals(GAME_YELLOW);
    }

    public String getEncountersFile() {
        String encountersFile = encountersFileRed;
        if (isBlue()) {
            encountersFile = encountersFileBlue;
        } else if (isYellow()) {
            encountersFile = encountersFileYellow;
        }
        return encountersFile;
    }

    public String getLocationsFile() {
        return locationsFile;
    }

    public String getPokemonFile() {
        return pokemonFile;
    }

    public String getMoveFile() {
        return mMoveFile;
    }
}
