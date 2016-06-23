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

import javax.swing.JFrame;
import redrouter.Settings;
import redrouter.data.DVCalculator;
import redrouter.data.RouterData;

/**
 *
 * @author Marco Willems
 */
public class RouterFrame extends JFrame {

    public RouterFrame(Settings settings) {
        super(Settings.TITLE + ": " + settings.game);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        setSize(new Dimension(400, 300));
        RouterData rd = new RouterData(settings);
        DVCalculator calc = new DVCalculator(rd, null);
        this.setContentPane(new DVCalculatorPanel(calc));
        this.pack();
        // To fit the button text when stat >= 100
        this.setSize(this.getWidth() + 50, this.getHeight());
        this.setLocationRelativeTo(null);
        this.setResizable(false);

    }

}
