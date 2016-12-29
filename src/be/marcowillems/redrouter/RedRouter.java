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
package be.marcowillems.redrouter;

import javax.swing.SwingUtilities;
import be.marcowillems.redrouter.view.RouterFrame;

/**
 *
 * @author Marco Willems
 */
public class RedRouter {

    public RedRouter() {
//        GameChooserDialog gcd = new GameChooserDialog(null);
//        gcd.setVisible(true);
//        if (gcd.settings != null) {
//            RouterFrame routerFrame = new RouterFrame(gcd.settings);
//            routerFrame.setVisible(true);
//        }
        RouterFrame rf = new RouterFrame(new Settings(Settings.GAME_RED));
        rf.setVisible(true);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                RedRouter r = new RedRouter();
            }
        });
    }

}
