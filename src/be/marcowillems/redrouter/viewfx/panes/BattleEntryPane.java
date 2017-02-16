/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.marcowillems.redrouter.viewfx.panes;

import be.marcowillems.redrouter.data.Move;
import be.marcowillems.redrouter.util.BadgeBoosts;
import be.marcowillems.redrouter.util.BattleEntry;
import be.marcowillems.redrouter.util.Range;
import be.marcowillems.redrouter.util.Stages;
import be.marcowillems.redrouter.viewfx.util.LabeledSpinner;
import java.io.IOException;
import java.util.Map;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author Marco Willems
 */
public class BattleEntryPane extends GridPane implements LabeledSpinner.SpinnerChangedListener {

    private static final String[] STRINGS = new String[]{"Atk", "Def", "Spd", "Spc"};

    private final BattleEntry battleEntry; // final

    // all final?
    private LabeledSpinner[] spnStagesA;
    private Label[] lblMovesA;
    private Label[] lblMovesB;
    private LabeledSpinner[] spnStagesB;
    private LabeledSpinner[] spnBadgeBoosts;

    @FXML
    protected Button btnBattlerA, btnBattlerB;

    public BattleEntryPane(BattleEntry battleEntry) {
        super();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/BattleEntryPane.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        this.battleEntry = battleEntry;
        init();

        GridPane.setFillWidth(this, Boolean.TRUE);
        super.getColumnConstraints().get(0).setMinWidth(USE_PREF_SIZE);
        super.getColumnConstraints().get(3).setMinWidth(USE_PREF_SIZE);
        super.getColumnConstraints().get(4).setMinWidth(USE_PREF_SIZE);
        super.setGridLinesVisible(true);
    }

    private void init() {
        // TODO: non-hardcoded "4"
        int statCount = 4;

        spnStagesA = new LabeledSpinner[statCount];
        for (int i = 0; i < statCount; i++) {
            spnStagesA[i] = new LabeledSpinner(STRINGS[i], Stages.MIN, Stages.MAX, battleEntry.getStagesOpponent().getValue(i));
            spnStagesA[i].addListener(this);
            super.add(spnStagesA[i], 0, i + 1);
        }
        btnBattlerA.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                Parent root = new BattlerInfoPane(battleEntry.battlerOpp, false, battleEntry.getStagesOpponent(), new BadgeBoosts());
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle(battleEntry.battlerOpp.toString());
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setResizable(false);
                stage.show();
                Bounds boundsBtn = btnBattlerA.localToScreen(btnBattlerA.getBoundsInLocal());
                stage.setX(boundsBtn.getMinX() + boundsBtn.getWidth() / 2 - stage.getWidth() / 2);
                stage.setY(boundsBtn.getMinY() - stage.getHeight() / 2);
            }
        });
        Map<Move, Move.DamageRange> rangesA = battleEntry.getOpponentRanges();
        lblMovesA = new Label[rangesA.size()];
        int k = 0;
        for (Move m : rangesA.keySet()) {
            Move.DamageRange range = rangesA.get(m);
            if (range.getCritMax() > 0 || range.getMax() > 0) {
                lblMovesA[k] = new Label(m + ": " + range.toString(battleEntry.battlerPl.getHP(), battleEntry.battlerOpp.pokemon.getCritRatio()));
            } else {
                lblMovesA[k] = new Label(m.toString());
            }
            super.add(lblMovesA[k], 1, k + 1);
            k++;
        }
        btnBattlerB.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                Parent root = new BattlerInfoPane(battleEntry.battlerPl, true, battleEntry.getStagesPlayer(), battleEntry.getActualBadgeBoosts());
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle(battleEntry.battlerPl.toString());
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setResizable(false);
                stage.show();
                Bounds boundsBtn = btnBattlerB.localToScreen(btnBattlerB.getBoundsInLocal());
                stage.setX(boundsBtn.getMinX() + boundsBtn.getWidth() / 2 - stage.getWidth() / 2);
                stage.setY(boundsBtn.getMinY() - stage.getHeight() / 2);
            }
        });
        Map<Move, Move.DamageRange> rangesB = battleEntry.getPlayerRanges();
        lblMovesB = new Label[rangesB.size()];
        int l = 0;
        for (Move m : rangesB.keySet()) {
            Move.DamageRange range = rangesB.get(m);
            if (range.getCritMax() > 0 || range.getMax() > 0) {
                lblMovesB[l] = new Label(m + ": " + range.toString(battleEntry.battlerOpp.getHP(), battleEntry.battlerPl.pokemon.getCritRatio()));
            } else {
                lblMovesB[l] = new Label(m.toString());
            }
            super.add(lblMovesB[l], 2, l + 1);
            l++;
        }
        spnStagesB = new LabeledSpinner[statCount];
        for (int i = 0; i < statCount; i++) {
            spnStagesB[i] = new LabeledSpinner(STRINGS[i], Stages.MIN, Stages.MAX, battleEntry.getStagesPlayer().getValue(i));
            spnStagesB[i].addListener(this);
            super.add(spnStagesB[i], 3, i + 1);
        }
        spnBadgeBoosts = new LabeledSpinner[statCount];
        for (int i = 0; i < statCount; i++) {
            spnBadgeBoosts[i] = new LabeledSpinner(STRINGS[i], BadgeBoosts.MIN, BadgeBoosts.MAX, battleEntry.getActualBadgeBoosts().getValue(i));
            spnBadgeBoosts[i].addListener(this);
            super.add(spnBadgeBoosts[i], 4, i + 1);
        }
        updateButtonTexts();
    }

    private void updateButtonTexts() {
        Range deltaSpd = battleEntry.battlerPl.getSpd(battleEntry.getActualBadgeBoosts().getSpd(), battleEntry.getStagesPlayer().getSpd())
                .substract(battleEntry.battlerOpp.getSpd(0, battleEntry.getStagesOpponent().getSpd()));
        String oppSpdStr;
        String plSpdStr;
        if (deltaSpd.contains(0)) {
            oppSpdStr = " (F)";
            plSpdStr = " (F)";
        } else if (deltaSpd.getMin() < 0) { // min or max, doesn't matter
            oppSpdStr = " F";
            plSpdStr = "";
        } else {
            oppSpdStr = "";
            plSpdStr = " F";
        }
        btnBattlerA.setText(battleEntry.battlerOpp + " (" + battleEntry.battlerOpp.getHP() + "hp)" + oppSpdStr);
        btnBattlerB.setText(battleEntry.battlerPl + " (" + battleEntry.battlerPl.getHP() + "hp)" + plSpdStr);
    }

    @Override
    public void changed(LabeledSpinner source, int oldValue, int newValue) {
        battleEntry.setStagesOpponent(spnStagesA[0].getValue(), spnStagesA[1].getValue(), spnStagesA[2].getValue(), spnStagesA[3].getValue());
        battleEntry.setStagesPlayer(spnStagesB[0].getValue(), spnStagesB[1].getValue(), spnStagesB[2].getValue(), spnStagesB[3].getValue());
        battleEntry.setBadgeBoosts(spnBadgeBoosts[0].getValue(), spnBadgeBoosts[1].getValue(), spnBadgeBoosts[2].getValue(), spnBadgeBoosts[3].getValue());

        Map<Move, Move.DamageRange> rangesA = battleEntry.getOpponentRanges();
        int k = 0;
        for (Move m : rangesA.keySet()) {
            Move.DamageRange range = rangesA.get(m);
            if (range.getCritMax() > 0 || range.getMax() > 0) {
                lblMovesA[k].setText(m + ": " + range.toString(battleEntry.battlerPl.getHP(), battleEntry.battlerOpp.pokemon.getCritRatio()));
            } else {
                lblMovesA[k].setText(m.toString());
            }
            k++;
        }
        Map<Move, Move.DamageRange> rangesB = battleEntry.getPlayerRanges();
        int l = 0;
        for (Move m : rangesB.keySet()) {
            Move.DamageRange range = rangesB.get(m);
            if (range.getCritMax() > 0 || range.getMax() > 0) {
                lblMovesB[l].setText(m + ": " + range.toString(battleEntry.battlerOpp.getHP(), battleEntry.battlerPl.pokemon.getCritRatio()));
            } else {
                lblMovesB[l].setText(m.toString());
            }
            l++;
        }
        updateButtonTexts();
    }

}
