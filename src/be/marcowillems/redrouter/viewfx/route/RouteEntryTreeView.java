/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.marcowillems.redrouter.viewfx.route;

import be.marcowillems.redrouter.route.RouteBattle;
import be.marcowillems.redrouter.route.RouteEntry;
import be.marcowillems.redrouter.route.RouteMenu;
import be.marcowillems.redrouter.route.RouteSection;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.BorderPane;

/**
 *
 * @author Marco Willems
 */
public class RouteEntryTreeView extends BorderPane implements RouteEntryView.ViewChangedListener {

    @FXML
    protected BorderPane controls;

    private final TreeItem treeItem;

    public RouteEntryTreeView(RouteEntry routeEntry, TreeItem treeItem) {
        this.treeItem = treeItem;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/RouteEntryTreeView.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        setRouteEntryView(routeEntry);
    }

    private void setRouteEntryView(RouteEntry routeEntry) {
        RouteEntryView rev;
        if (routeEntry instanceof RouteBattle) {
            rev = new RouteBattleView((RouteBattle) routeEntry);
        } else if (routeEntry instanceof RouteMenu) {
            rev = new RouteMenuView((RouteMenu) routeEntry);
        } else if (routeEntry instanceof RouteSection) {
            rev = new RouteSectionView((RouteSection) routeEntry);
        } else {
            rev = new RouteDirectionsView(routeEntry);
        }
        controls.visibleProperty().bind(rev.showheaderProperty());
        controls.managedProperty().bind(rev.showheaderProperty());

        rev.setViewChangedListener(this);
        setCenter(rev);
    }

    @Override
    public void viewChanged(RouteEntryView view) {
        // TODO: this doesn't work ...
        fireEvent(new TreeItem.TreeModificationEvent(TreeItem.graphicChangedEvent(), treeItem));
    }

}
