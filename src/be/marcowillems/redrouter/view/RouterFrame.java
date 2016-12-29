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
package be.marcowillems.redrouter.view;

import java.awt.Dimension;
import javax.swing.JFrame;
import be.marcowillems.redrouter.Settings;
import be.marcowillems.redrouter.data.RouterData;
import be.marcowillems.redrouter.route.Route;
import be.marcowillems.redrouter.route.RouteFactory;

/**
 *
 * @author Marco Willems
 */
public class RouterFrame extends JFrame {

    public RouterFrame(Settings settings) {
        this(new RouteFactory(new RouterData(settings)).getRedExaNidoRoute());
    }

    public RouterFrame(Route route) {
        super(Settings.TITLE + ": " + route.rd.settings.game);
        System.out.println(route.writeToString(0, null));
        RouteView routeView = new RouteView(route);
        this.setContentPane(routeView);
        this.setJMenuBar(new RouterMenuBar(this, routeView));
        this.pack();
        this.setSize(new Dimension(Settings.WIDTH, Settings.HEIGHT));
        this.setMinimumSize(new Dimension(this.getPreferredSize().width * 3 / 4, this.getPreferredSize().height * 3 / 4));
        this.setLocationRelativeTo(null);
//        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

}
