/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.marcowillems.redrouter.viewfx.panes;

import be.marcowillems.redrouter.route.RouteBattle;
import be.marcowillems.redrouter.util.BattleEntry;
import java.util.List;
import javafx.scene.layout.VBox;

/**
 *
 * @author Marco Willems
 */
public class BattlePane extends VBox {

    private final RouteBattle routeBattle;
    private final BattleEntryPane[] battleEntryPanes;

    public BattlePane(RouteBattle routeBattle) {
        super.getStyleClass().add("battle-pane");
        this.routeBattle = routeBattle;
        List<BattleEntry> battleEntries = routeBattle.getBattleEntries();
        this.battleEntryPanes = new BattleEntryPane[battleEntries.size()];
        for (int i = 0; i < battleEntries.size(); i++) {
            battleEntryPanes[i] = new BattleEntryPane(battleEntries.get(i));
            super.getChildren().add(battleEntryPanes[i]);
        }
    }

}
