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
import javax.swing.JFrame;
import be.marcowillems.redrouter.Settings;
import be.marcowillems.redrouter.data.RouterData;
import be.marcowillems.redrouter.io.PrintSettings;
import be.marcowillems.redrouter.io.RouteParser;
import be.marcowillems.redrouter.io.RouteParserException;
import be.marcowillems.redrouter.route.Route;
import be.marcowillems.redrouter.route.RouteFactory;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * TODO add stuff for the file-menu actions.
 *
 * @author Marco Willems
 */
public class RouterFrame extends JFrame {

//    private Map<Route, RouteView> openRoutes; // TODO: later, maybe, in tabbed pane?
    private RouteView currentRouteView;

    public RouterFrame(Settings settings) {
        this(new RouteFactory(new RouterData(settings)).getRedExaNidoRoute());
    }

    public RouterFrame(Route route) {
        openRoute(route);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public RouteView getCurrentRouteView() {
        return currentRouteView;
    }

    private void openRoute(Route route) {
        if (route != null) {
            this.setTitle(Settings.TITLE + ": " + route.rd.settings.game);
//            JPanel pnlLoading = new JPanel();
//            pnlLoading.add(new JLabel("Loading route \"" + route.info + "\" ..."));
//            this.setContentPane(pnlLoading);
//            this.revalidate();
            this.currentRouteView = new RouteView(route);
            this.setContentPane(currentRouteView);
            this.setJMenuBar(new RouterMenuBar(this, currentRouteView));
            if (!this.isVisible()) {
                this.pack();
                this.setSize(new Dimension(Settings.WIDTH, Settings.HEIGHT));
            }
            this.setMinimumSize(new Dimension(this.getPreferredSize().width * 3 / 4, this.getPreferredSize().height * 3 / 4));
        } else {
            this.setTitle(Settings.TITLE);
            this.currentRouteView = null;
            this.setContentPane(new JPanel());
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
            JFileChooser fc = new JFileChooser(new File("."));
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
                if (close()) {
                    openRoute(route);
                    return true;
                }
            } catch (RouteParserException ex) {
//                Logger.getLogger(RouterFrame.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Could not parse file", JOptionPane.ERROR_MESSAGE);
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
