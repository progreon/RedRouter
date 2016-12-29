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

import be.marcowillems.redrouter.view.route.RouteTree;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import be.marcowillems.redrouter.route.Route;

/**
 *
 * @author Marco Willems
 */
public class RouteView extends JPanel {

    private Route route;
    private RouteTree rt;
    private final JScrollPane scrTree;

    public RouteView(Route route) {
        super(new BorderLayout());
        scrTree = new JScrollPane();
        setRoute(route);
        this.add(scrTree);
        scrTree.addComponentListener(new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent e) {
                refreshView();
            }
        });
        this.setPreferredSize(new Dimension(600, 600));
    }

    public boolean isEditMode() {
        return rt.isEditMode();
    }

    public void setEditMode(boolean isEditMode) {
        rt.setEditMode(isEditMode);
    }

    public final Route getRoute() {
        return this.route;
    }

    public final void setRoute(Route route) {
        this.route = route;
        rt = new RouteTree(route);
        scrTree.setViewportView(rt);
    }

    private void refreshView() {
        rt.refresh();
    }

}
