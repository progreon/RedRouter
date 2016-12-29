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
package redrouter.view.dialogs.editroute;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import redrouter.route.RouteEntry;

/**
 *
 * @author Marco Willems
 */
public abstract class EditDialog extends JDialog {

    private final JPanel buttonPanel;
    protected boolean changed;
    public final RouteEntry routeEntry;

    public EditDialog(RouteEntry routeEntry) {
        this.routeEntry = routeEntry;
//        this.setUndecorated(true);
        this.setModal(true);
        this.setLocationRelativeTo(null);
        this.buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener((ActionEvent e) -> {
            EditDialog.this.setVisible(false);
            // TODO: warn for changes?
        });
        buttonPanel.add(btnCancel);
        JButton btnSave = new JButton("Save");
        btnSave.addActionListener((ActionEvent e) -> {
            save();
            EditDialog.this.setVisible(false);
        });
        buttonPanel.add(btnSave);
        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
//                save();
                // TODO: warn for changes?
            }

        });
    }

    protected abstract JPanel getSettingsPanel();

    /**
     * Save edits to the route entry and set changed to true if anything has
     * changed.
     */
    protected abstract void save();

    /**
     * Opens the dialog at the specified location.
     *
     * @param centerLocation
     * @return returns if the route entry has been changed or not, so the tree
     * can be refreshed
     */
    public final boolean display(Point centerLocation) {
        changed = false;
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(getSettingsPanel());
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        this.setContentPane(mainPanel);
        this.pack();
        if (centerLocation != null) {
            this.setLocation(new Point((centerLocation.x + this.getWidth()) / 2, (centerLocation.y + this.getHeight()) / 2));
        } else {
            this.setLocationRelativeTo(null);
        }
        this.setVisible(true);
        return changed;
    }

}
