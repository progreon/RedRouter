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
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

/**
 *
 * @author Marco Willems
 */
public abstract class InfoDialog extends JDialog {

    private final JPanel buttonPanel;

    public InfoDialog() {
        this(true);
    }

    public InfoDialog(boolean withHolding) {
        if (withHolding) {
            this.setUndecorated(true);
            this.setModal(false);
            this.setFocusable(false);
        } else {
            this.setModal(true);
        }
        this.setLocationRelativeTo(null);
        if (!withHolding) {
            this.buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
            JButton btnClose = new JButton("Close");
            btnClose.addActionListener((ActionEvent e) -> {
                setVisible(false);
            });
            this.buttonPanel.add(btnClose);
        } else {
            this.buttonPanel = null;
        }
    }

    public final void display(JButton source) {
        this.refreshData();
        if (this.buttonPanel != null) {
            JPanel contentPanel = new JPanel(new BorderLayout());
            contentPanel.add(getMainPanel());
            contentPanel.add(buttonPanel, BorderLayout.SOUTH);
            this.setContentPane(contentPanel);
        } else {
            this.setContentPane(getMainPanel());
        }
        this.pack();
        int btnTop = source.getLocationOnScreen().y;
        int btnCenter = source.getLocationOnScreen().x + (source.getWidth() / 2);
        int dialogX = btnCenter - (this.getWidth() / 2);
        int dialogY = btnTop - this.getHeight();
        if (dialogY < 0) {
            dialogY = btnTop + source.getHeight();
        }
        this.setLocation(dialogX, dialogY);
        this.setVisible(true);
    }

    protected abstract JPanel getMainPanel();

    protected abstract void refreshData();

}
