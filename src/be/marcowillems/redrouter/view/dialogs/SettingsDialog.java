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
package be.marcowillems.redrouter.view.dialogs;

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
//        this.setUndecorated(true);
        this.setModal(true);
        this.setLocationRelativeTo(null);
        this.buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        JButton btnClose = new JButton("Close");
        btnClose.addActionListener((ActionEvent e) -> {
            save();
            setVisible(false);
        });
        buttonPanel.add(btnClose);
        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                save();
            }

        });
    }

    protected abstract JPanel getContentPanel();

    protected abstract void save();

    public final boolean display(JButton source) {
        changed = false;
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(getContentPanel());
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        this.setContentPane(mainPanel);
        this.pack();
        int btnBottom = source.getLocationOnScreen().y + source.getHeight();
        int btnLeft = source.getLocationOnScreen().x;
        int dialogX = btnLeft - this.getWidth();
        int dialogY = btnBottom - this.getHeight();
        this.setLocation(dialogX, dialogY);
        this.setVisible(true);
        return changed;
    }

}
