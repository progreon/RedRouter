/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.marcowillems.redrouter.viewfx.route;

import be.marcowillems.redrouter.data.Move;
import be.marcowillems.redrouter.util.BadgeBoosts;
import be.marcowillems.redrouter.util.BattleEntry;
import be.marcowillems.redrouter.util.Stages;
import be.marcowillems.redrouter.viewfx.util.LabeledSpinner;
import java.io.IOException;
import java.util.Map;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

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

    @Override
    public void changed(LabeledSpinner source, int oldValue, int newValue) {
        // TODO: recalculate all ranges for this battle entry
        battleEntry.setStagesOpponent(spnStagesA[0].getValue(), spnStagesA[1].getValue(), spnStagesA[2].getValue(), spnStagesA[3].getValue());
        battleEntry.setStagesPlayer(spnStagesB[0].getValue(), spnStagesB[1].getValue(), spnStagesB[2].getValue(), spnStagesB[3].getValue());
        battleEntry.setBadgeBoosts(spnBadgeBoosts[0].getValue(), spnBadgeBoosts[1].getValue(), spnBadgeBoosts[2].getValue(), spnBadgeBoosts[3].getValue());

        Map<Move, Move.DamageRange> rangesA = battleEntry.getOpponentRanges();
        int k = 0;
        for (Move m : rangesA.keySet()) {
            Move.DamageRange range = rangesA.get(m);
            if (range.critMax > 0 || range.max > 0) {
                lblMovesA[k].setText(m + ": " + range);
            } else {
                lblMovesA[k].setText(m.toString());
            }
            k++;
        }
        Map<Move, Move.DamageRange> rangesB = battleEntry.getPlayerRanges();
        int l = 0;
        for (Move m : rangesB.keySet()) {
            Move.DamageRange range = rangesB.get(m);
            if (range.critMax > 0 || range.max > 0) {
                lblMovesB[l].setText(m + ": " + range);
            } else {
                lblMovesB[l].setText(m.toString());
            }
            l++;
        }
    }

    private void init() {
        // TODO: non-hardcoded "4"
        // TODO: hp + who's faster?
        int statCount = 4;
        spnStagesA = new LabeledSpinner[statCount];
        for (int i = 0; i < statCount; i++) {
            spnStagesA[i] = new LabeledSpinner(STRINGS[i], Stages.MIN, Stages.MAX, battleEntry.getStagesOpponent().getValue(i));
            spnStagesA[i].addListener(this);
            super.add(spnStagesA[i], 0, i + 1);
        }
        btnBattlerA.setText(battleEntry.battlerOpp.toString());
        Map<Move, Move.DamageRange> rangesA = battleEntry.getOpponentRanges();
        lblMovesA = new Label[rangesA.size()];
        int k = 0;
        for (Move m : rangesA.keySet()) {
            Move.DamageRange range = rangesA.get(m);
            if (range.critMax > 0 || range.max > 0) {
                lblMovesA[k] = new Label(m + ": " + range);
            } else {
                lblMovesA[k] = new Label(m.toString());
            }
            super.add(lblMovesA[k], 1, k + 1);
            k++;
        }
        btnBattlerB.setText(battleEntry.battlerPl.toString());
        Map<Move, Move.DamageRange> rangesB = battleEntry.getPlayerRanges();
        lblMovesB = new Label[rangesB.size()];
        int l = 0;
        for (Move m : rangesB.keySet()) {
            Move.DamageRange range = rangesB.get(m);
            if (range.critMax > 0 || range.max > 0) {
                lblMovesB[l] = new Label(m + ": " + range);
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
    }

}
