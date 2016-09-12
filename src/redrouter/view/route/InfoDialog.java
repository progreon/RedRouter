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
package redrouter.view.route;

import java.awt.Point;
import javax.swing.JDialog;
import javax.swing.JPanel;

/**
 *
 * @author Marco Willems
 */
public abstract class InfoDialog extends JDialog {

    protected JPanel panel;
    private final Point mouseLocation;

    public InfoDialog(Point mouseLocation) {
        this.mouseLocation = mouseLocation;
    }

    protected final void initAndDisplay() {
        initPanel();
        this.setContentPane(panel);
        this.setUndecorated(true);
        this.pack();
        this.setModal(false);
        this.setLocation(new Point(mouseLocation.x, mouseLocation.y - this.getHeight()));
        this.setFocusable(false);
    }

    protected abstract void initPanel();

}
