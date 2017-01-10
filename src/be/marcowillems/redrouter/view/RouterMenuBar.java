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

import be.marcowillems.redrouter.view.dvcalculator.DVCalculatorPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import be.marcowillems.redrouter.data.DVCalculator;
import be.marcowillems.redrouter.data.RouterData;
import be.marcowillems.redrouter.io.ParserException;

/**
 *
 * @author Marco Willems
 */
public class RouterMenuBar extends JMenuBar {

    private final RouteView routeView;
    private final RouterFrame routerFrame;

    public RouterMenuBar(RouterFrame routerFrame, RouteView routeView) {
        this.routerFrame = routerFrame;
        this.routeView = routeView;
        init();
    }

    private void init() {
        // File menu
        JMenu mnFile = new JMenu("File");
        mnFile.setMnemonic('f');
        mnFile.add(new JMenuItem("New Route... [TODO]"));
        mnFile.add(new JSeparator());
        JMenuItem mniLoad = new JMenuItem("Load Route...");
        mniLoad.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                routerFrame.load(null);
            }
        });
        mnFile.add(mniLoad);
        JMenuItem mniClose = new JMenuItem("Close Route");
        mniClose.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                routerFrame.close();
            }
        });
        mnFile.add(mniClose);
        mnFile.add(new JSeparator());
        mnFile.add(new JMenuItem("Save [TODO]"));
        mnFile.add(new JMenuItem("Save As... [TODO]"));
        mnFile.add(new JSeparator());
        mnFile.add(new JMenuItem("Print... [TODO]"));
        mnFile.add(new JMenuItem("Print to HTML... [TODO]"));
        mnFile.add(new JMenuItem("Print Example... [TODO]"));
        mnFile.add(new JSeparator());
        mnFile.add(new JMenuItem("Exit [TODO]"));
        this.add(mnFile);

        // Edit menu
        JMenu mnEdit = new JMenu("Edit");
        mnEdit.setMnemonic('e');
        JCheckBoxMenuItem chkEditing = new JCheckBoxMenuItem("Editing mode");
        if (routeView != null) {
            chkEditing.setState(routeView.isEditMode());
            chkEditing.addItemListener(new ItemListener() {

                @Override
                public void itemStateChanged(ItemEvent e) {
                    if (routeView != null) {
                        if (e.getStateChange() == ItemEvent.DESELECTED && routeView.isEditMode()) {
                            routeView.setEditMode(false);
                        } else if (e.getStateChange() == ItemEvent.SELECTED && !routeView.isEditMode()) {
                            routeView.setEditMode(true);
                        }
                    }
                }
            });
        } else {
            mnEdit.setEnabled(false);
        }
        mnEdit.add(chkEditing);

        this.add(mnEdit);

        // Tools menu
        JMenu mnTools = new JMenu("Tools");
        mnTools.setMnemonic('t');
        JMenuItem mniDVCalc = new JMenuItem("Open DV Calculator...");
        mniDVCalc.setMnemonic('d');
        mniDVCalc.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                GameChooserDialog gcd = new GameChooserDialog(routerFrame);
                gcd.setVisible(true);
                if (gcd.settings != null) {
                    try {
                        RouterData rd = new RouterData(gcd.settings);
                        JDialog DVCalcDialog = new JDialog(routerFrame, "DV Calculator: " + gcd.settings.game);
                        DVCalcDialog.setContentPane(new DVCalculatorPanel(new DVCalculator(rd, null)));
                        DVCalcDialog.pack();
                        DVCalcDialog.setLocationRelativeTo(routerFrame);
                        DVCalcDialog.setVisible(true);
                    } catch (ParserException ex) {
                        Logger.getLogger(RouterMenuBar.class.getName()).log(Level.SEVERE, null, ex);
                        JOptionPane.showMessageDialog(routerFrame, ex.getMessage(), "Parser error: see console for more details", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        mnTools.add(mniDVCalc);

        this.add(mnTools);
    }

}
