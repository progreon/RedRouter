/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.marcowillems.redrouter.viewfx.route;

import be.marcowillems.redrouter.viewfx.panes.BattlePane;
import be.marcowillems.redrouter.route.RouteBattle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

/**
 *
 * @author Marco Willems
 */
public class RouteBattleView extends RouteEntryView {

    @FXML
    protected Label title;
    @FXML
    protected Label description;
    @FXML
    protected VBox rangesBox;
    @FXML
    protected ToggleButton toggle;

    private BattlePane battlePane = null;

    public RouteBattleView(RouteBattle routeBattle) {
        super(routeBattle, "fxml/RouteBattleView.fxml");
        title.managedProperty().bind(title.textProperty().isNotNull());
        description.managedProperty().bind(description.textProperty().isNotNull());
        BorderPane.setAlignment(description, Pos.CENTER_LEFT);
        if (routeEntry.info != null) {
            title.setText(routeEntry.info.title);
            description.setText(routeEntry.info.description);
        } else {
            title.setText(routeEntry.toString());
            description.setText(null);
        }
        toggle.selectedProperty().addListener(new ChangeListener<Boolean>() {

            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (!oldValue && newValue && battlePane == null) {
                    battlePane = new BattlePane(routeBattle);
                    battlePane.visibleProperty().bind(toggle.selectedProperty());
                    battlePane.managedProperty().bind(toggle.selectedProperty());
                    rangesBox.getChildren().add(battlePane);
                }
                viewChanged();
            }
        });
    }

}
