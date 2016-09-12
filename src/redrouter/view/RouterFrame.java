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
package redrouter.view;

import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import redrouter.Settings;
import redrouter.data.DVCalculator;
import redrouter.data.RouterData;
import redrouter.route.RouteFactory;

/**
 *
 * @author Marco Willems
 */
public class RouterFrame extends JFrame {

    public RouterFrame(Settings settings) {
        super(Settings.TITLE + ": " + settings.game);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        RouterData rd = new RouterData(settings);
        DVCalculator calc = new DVCalculator(rd, null);
        RouteFactory rf = new RouteFactory(rd);
        DVCalculatorPanel dvPanel = new DVCalculatorPanel(calc);
        RouteView routeView = new RouteView(rf.getRedExaNidoRoute());
        routeView.setPreferredSize(dvPanel.getPreferredSize());
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("DV Calculator", dvPanel);
        tabbedPane.addTab("Route", routeView);
        this.setContentPane(tabbedPane);
        tabbedPane.setSelectedIndex(1);
        this.pack();
//        this.setSize(new Dimension(Settings.WIDTH, Settings.HEIGHT));
        this.setMinimumSize(new Dimension(this.getPreferredSize().width * 3 / 4, this.getPreferredSize().height * 3 / 4));
        this.setLocationRelativeTo(null);
//        this.setResizable(false);

    }

}
