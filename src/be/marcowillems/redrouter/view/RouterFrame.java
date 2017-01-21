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
import java.io.File;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import be.marcowillems.redrouter.Settings;
import be.marcowillems.redrouter.io.ParserException;
import be.marcowillems.redrouter.io.PrintSettings;
import be.marcowillems.redrouter.io.RouteParser;
import be.marcowillems.redrouter.route.Route;
import be.marcowillems.redrouter.route.RouterMessage;

/**
 * TODO add stuff for the file-menu actions.
 *
 * @author Marco Willems
 */
public class RouterFrame extends JFrame {

//    private Map<Route, RouteView> openRoutes; // TODO: later, maybe, in tabbed pane?
    private RouteView currentRouteView;

    public RouterFrame(Route route) {
        openRoute(route);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public RouteView getCurrentRouteView() {
        return currentRouteView;
    }

    public final void openRoute(Route route) {
        if (route != null) {
            this.setTitle(Settings.TITLE + ": " + route.info + " (loading ...)");
            this.currentRouteView = new RouteView(route);
            this.setContentPane(currentRouteView);
            this.setJMenuBar(new RouterMenuBar(this, currentRouteView));
            if (!this.isVisible()) { // TODO: fix this
                this.pack();
                this.setSize(new Dimension(Settings.WIDTH, Settings.HEIGHT));
            }
            this.setTitle(Settings.TITLE + ": " + route.info);
            this.setMinimumSize(new Dimension(this.getPreferredSize().width * 3 / 4, this.getPreferredSize().height * 3 / 4));
        } else {
            this.setTitle(Settings.TITLE);
            this.currentRouteView = null;
            this.setContentPane(new JPanel());
            this.setSize(new Dimension(Settings.WIDTH, Settings.HEIGHT));
            this.setJMenuBar(new RouterMenuBar(this, currentRouteView));
        }
        this.revalidate();
    }

    // IO stuff
    // TODO
    public boolean close() {
        openRoute(null);
        return true;
    }

    public boolean load(File file) {
        if (file == null) {
            JFileChooser fc;
            try {
                fc = new JFileChooser(RouterFrame.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
            } catch (URISyntaxException ex) {
                throw new RuntimeException(ex.getMessage());
            }
            int result = fc.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                file = fc.getSelectedFile();
                if (!file.exists()) {
                    file = null;
                }
            }
        }
        if (file != null) {
            try {
                Route route = new RouteParser().parseFile(file);
                for (RouterMessage rm : route.getAllMessages()) {
                    System.out.println(rm);
                }
                if (close()) {
                    openRoute(route);
                    System.out.println(route.writeToString(0, null));
                    return true;
                }
            } catch (ParserException ex) {
                Logger.getLogger(RouterFrame.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Parser error: see console for more details", JOptionPane.ERROR_MESSAGE);
            }
        }
        return false;
    }

    // TODO
    public void save(File file) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    // TODO
    public void printReadable(File file, PrintSettings printSettings) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
