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
package be.marcowillems.redrouter.viewfx.panes;

import be.marcowillems.redrouter.data.Battler;
import be.marcowillems.redrouter.util.BadgeBoosts;
import be.marcowillems.redrouter.util.Stages;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

/**
 *
 * @author Marco Willems
 */
public class BattlerInfoPane extends VBox {

    @FXML
    protected GridPane gridInfo, gridDVs, gridStats;

    public BattlerInfoPane(Battler battler, boolean isPlayerBattler, Stages stages, BadgeBoosts boosts) {
        super();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/BattlerInfoPane.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
//        gridInfo.add(new Label("Experience group:"), 0, 0);
//        gridInfo.add(new Label(battler.pokemon.expGroup.group.toString()), 1, 0);
        if (isPlayerBattler) {
            gridInfo.add(new Label("Exp. to next level:"), 0, 1);
            int dExp = battler.pokemon.expGroup.getDeltaExp(battler.getLevel(), battler.getLevel() + 1, battler.getLevelExp());
            gridInfo.add(new Label(dExp + " left (of " + battler.pokemon.expGroup.getDeltaExp(battler.getLevel(), battler.getLevel() + 1) + ")"), 1, 1);
        } else {
            gridInfo.add(new Label("Given experience:"), 0, 1);
            gridInfo.add(new Label(battler.getExp(1) + " xp"), 1, 1);
        }
        gridInfo.add(new Label("Critical hit ratio:"), 0, 2);
        gridInfo.add(new Label((battler.pokemon.getCritRatio() * 100.0) + "%"), 1, 2);
        gridInfo.add(new Label("\tHigh:"), 0, 3);
        gridInfo.add(new Label((battler.pokemon.getHighCritRatio() * 100.0) + "%"), 1, 3);
        if (isPlayerBattler) {
            gridInfo.add(new Label("Redbar:"), 0, 4);
            gridInfo.add(new Label("<" + battler.getHP().multiplyBy(53).divideBy(256).add(1) + " hp"), 1, 4);
        }

        gridDVs.setStyle("-fx-border-color: black");
        gridDVs.add(new Label("DVs"), 0, 0, 5, 1);
        gridDVs.add(new Label("HP"), 0, 1);
        gridDVs.add(new Label("Atk"), 1, 1);
        gridDVs.add(new Label("Def"), 2, 1);
        gridDVs.add(new Label("Spd"), 3, 1);
        gridDVs.add(new Label("Spc"), 4, 1);
        gridDVs.add(new Label(battler.getDVRange(0).toString()), 0, 2);
        gridDVs.add(new Label(battler.getDVRange(1).toString()), 1, 2);
        gridDVs.add(new Label(battler.getDVRange(2).toString()), 2, 2);
        gridDVs.add(new Label(battler.getDVRange(3).toString()), 3, 2);
        gridDVs.add(new Label(battler.getDVRange(4).toString()), 4, 2);

        gridStats.setStyle("-fx-border-color: black");
        gridStats.add(new Label("Stats"), 0, 0, 5, 1);
        gridStats.add(new Label("HP"), 0, 1);
        gridStats.add(new Label("Atk"), 1, 1);
        gridStats.add(new Label("Def"), 2, 1);
        gridStats.add(new Label("Spd"), 3, 1);
        gridStats.add(new Label("Spc"), 4, 1);
        gridStats.add(new Label(battler.getHP().toString()), 0, 2);
        gridStats.add(new Label(battler.getAtk().toString()), 1, 2);
        gridStats.add(new Label(battler.getDef().toString()), 2, 2);
        gridStats.add(new Label(battler.getSpd().toString()), 3, 2);
        gridStats.add(new Label(battler.getSpc().toString()), 4, 2);
        gridStats.add(new Label("With boosts"), 0, 3, 5, 1);
        gridStats.add(new Label(battler.getHP().toString()), 0, 4);
        gridStats.add(new Label(battler.getAtk(boosts.getAtk(), stages.getAtk()).toString()), 1, 4);
        gridStats.add(new Label(battler.getDef(boosts.getDef(), stages.getDef()).toString()), 2, 4);
        gridStats.add(new Label(battler.getSpd(boosts.getSpd(), stages.getSpd()).toString()), 3, 4);
        gridStats.add(new Label(battler.getSpc(boosts.getSpc(), stages.getSpc()).toString()), 4, 4);
    }

}
