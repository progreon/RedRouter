/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.marcowillems.redrouter.viewfx.panes;

import be.marcowillems.redrouter.route.RouteBattle;
import be.marcowillems.redrouter.util.BattleEntry;
import java.io.IOException;
import java.util.List;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;

/**
 *
 * @author Marco Willems
 */
public class BattlePane extends VBox {
    
    private RouteBattle routeBattle; // final
    private BattleEntryPane[] battleEntryPanes;

    public BattlePane() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/BattlePane.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
    
    public BattlePane(RouteBattle routeBattle) {
        this();
        this.routeBattle = routeBattle;
        List<BattleEntry> battleEntries = routeBattle.getBattleEntries();
        this.battleEntryPanes = new BattleEntryPane[battleEntries.size()];
        for (int i = 0; i < battleEntries.size(); i++) {
            battleEntryPanes[i] = new BattleEntryPane(battleEntries.get(i));
            super.getChildren().add(battleEntryPanes[i]);
        }
    }
    
}
