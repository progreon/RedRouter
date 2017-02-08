/*
 * Copyright (C) 2017 Marco Willems
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
package be.marcowillems.redrouter.viewfx.route;

import be.marcowillems.redrouter.route.RouteMenu;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

/**
 *
 * @author Marco Willems
 */
public class RouteMenuView extends RouteEntryView {

    @FXML
    protected Label description;

    public RouteMenuView(RouteMenu routeMenu) {
        super(routeMenu, "fxml/RouteMenuView.fxml");
        String text = routeMenu.toString();
        for (RouteMenu.Entry rme : routeMenu.getEntries()) {
            text += "\n\t" + rme;
        }
        BorderPane.setAlignment(description, Pos.CENTER_LEFT);
        description.setText(text);
    }
    
}
