/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.marcowillems.redrouter.viewfx.route;

import be.marcowillems.redrouter.route.RouteEntry;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

/**
 * Also used as the default view. TODO: no FXML for performance??
 *
 * @author Marco Willems
 */
public class RouteDirectionsView extends RouteEntryView {

    @FXML
    protected Label title;
//    private Label title = new Label();
    @FXML
    protected Label description;
//    private Label description = new Label();

    public RouteDirectionsView(RouteEntry routeEntry) {
//        super(routeEntry, null);
//        getStyleClass().add("route-description");
//        title.getStyleClass().add("title");
//        description.getStyleClass().add("description");
//        description.setWrapText(true);
//        setTop(title);
//        setCenter(description);
        super(routeEntry, "fxml/RouteDirectionsView.fxml");

        title.managedProperty().bind(title.textProperty().isNotNull());
        description.managedProperty().bind(description.textProperty().isNotNull());
        BorderPane.setAlignment(description, Pos.CENTER_LEFT);
        if (routeEntry.info != null) {
            title.setText(routeEntry.info.title);
            description.setText(routeEntry.info.description);
        } else {
            title.setText(null);
            description.setText(routeEntry.toString());
        }
    }

}
