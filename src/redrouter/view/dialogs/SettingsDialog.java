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

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

/**
 * Settings from this dialog are always automatically saved
 *
 * @author Marco Willems
 */
public abstract class SettingsDialog extends JDialog {

    private final JPanel buttonPanel;
    protected boolean changed;

    public SettingsDialog() {
        this.setUndecorated(true);
        this.setModal(true);
        this.setLocationRelativeTo(null);
        this.buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        JButton btnSave = new JButton("Close");
        btnSave.addActionListener((ActionEvent e) -> {
            save();
            SettingsDialog.this.setVisible(false);
        });
        buttonPanel.add(btnSave);
        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                save();
            }

        });
    }

    protected abstract JPanel getContentPanel();

    protected abstract void save();

    public final boolean display(Point mouseLocation) {
        changed = false;
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(getContentPanel());
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        this.setContentPane(mainPanel);
        this.pack();
        this.setLocation(new Point((mouseLocation.x - this.getWidth() / 2), (mouseLocation.y - this.getHeight() / 2)));
        this.setVisible(true);
        return changed;
    }

}
