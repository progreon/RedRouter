/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.marcowillems.redrouter.viewfx.route;

import be.marcowillems.redrouter.route.RouteEntry;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;

/**
 *
 * @author Marco Willems
 */
public class RouteEntryTreeCell extends TreeCell<RouteEntry> {

    @FXML
    protected BorderPane view;

    private RouteViewManager rvm;

    public RouteEntryTreeCell(RouteViewManager rvm, TreeView treeView) {
        this.rvm = rvm;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/RouteEntryTreeCell.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        this.prefWidthProperty().bind(treeView.widthProperty().subtract(50));
    }

    @Override
    protected void updateItem(RouteEntry item, boolean empty) {
        super.updateItem(item, empty);
        // TODO: handle empty?
        view.setCenter(rvm.getContainer(item, getTreeItem()));
    }

}
