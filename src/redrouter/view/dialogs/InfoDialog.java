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
package redrouter.view.dialogs;

import java.awt.Point;
import javax.swing.JDialog;
import javax.swing.JPanel;

/**
 *
 * @author Marco Willems
 */
public abstract class InfoDialog extends JDialog {

    public InfoDialog() {
        this.setUndecorated(true);
        this.setModal(false);
        this.setFocusable(false);
        this.setLocationRelativeTo(null);
    }

    public final void display(Point mouseLocation) {
        this.refreshData();
        this.setContentPane(getMainPanel());
        this.pack();
        this.setLocation(new Point(mouseLocation.x, mouseLocation.y - this.getHeight()));
        this.setVisible(true);
    }

    protected abstract JPanel getMainPanel();

    protected abstract void refreshData();

}
