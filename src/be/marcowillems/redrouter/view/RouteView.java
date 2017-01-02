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

import be.marcowillems.redrouter.observers.RouteObserver;
import be.marcowillems.redrouter.view.route.RouteTree;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import be.marcowillems.redrouter.route.Route;
import java.awt.Point;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Observable;
import javax.swing.ScrollPaneConstants;

/**
 *
 * @author Marco Willems
 */
public class RouteView extends JPanel implements RouteObserver {

    private static final int SCROLL_SPEED = 30;

    private Route route;
    private RouteTree rt;
    private final JScrollPane scrTree;

    public RouteView(Route route) {
        super(new BorderLayout());
        scrTree = new JScrollPane();
        scrTree.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrTree.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        MouseWheelListener[] mwls = scrTree.getMouseWheelListeners();
        for (MouseWheelListener mwl : mwls) {
            scrTree.removeMouseWheelListener(mwl);
        }
        scrTree.addMouseWheelListener(new MouseWheelListener() {

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                Point p = scrTree.getViewport().getViewPosition();
                int vHeight = scrTree.getViewport().getView().getHeight();
                int vpHeight = scrTree.getViewport().getHeight();
                p.y += e.getPreciseWheelRotation() * SCROLL_SPEED;
                if (p.y < 0) {
                    p.y = 0;
                }
                if (p.y + vpHeight > vHeight) {
                    p.y = vHeight - vpHeight;
                }
                scrTree.getViewport().setViewPosition(p);
            }
        });
        setRoute(route);
        this.add(scrTree);
        scrTree.addComponentListener(new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent e) {
                // TODO: keep the center in the center
                rt.refresh();
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
        route.addObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (this.route.isThisObservable(o)) {
            if (arg instanceof String && ((String) arg).equals(Route.TREE_UPDATED)) {
                this.rt.refresh();
            }
        }
    }

}
