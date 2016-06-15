/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package redrouter.view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import redrouter.data.Battler;
import redrouter.data.DVCalculator;
import redrouter.data.Pokemon;
import redrouter.data.RouteFactory;

/**
 *
 * @author marco
 */
public class DVCalculatorPanel extends JPanel {

    private DVCalculator calc;
    private final GridLayout gridDVs;
    private final JPanel gridPanel;
    private final DVButton[][] btnsDVs;

    public DVCalculatorPanel(DVCalculator calc) {
        super(new BorderLayout());
        this.calc = calc;
        if (this.calc == null) {
            this.calc = new DVCalculator(new Battler(RouteFactory.getPokemonByName(Pokemon.Pkmn.NIDORANM), 4, null), null);
        }
        this.add(new JLabel(this.calc.getBattler().toString()), BorderLayout.NORTH);
        gridDVs = new GridLayout(17, 5, 5, 5);
        gridPanel = new JPanel(gridDVs);
        btnsDVs = new DVButton[5][16];
        setButtons();
        this.add(gridPanel);
    }

    public void setDVCalculator(DVCalculator calc) {
        this.calc = calc;
        setButtons();
    }

    private void setButtons() {
        gridPanel.removeAll();
        gridPanel.add(new JLabel("HP"));
        gridPanel.add(new JLabel("Atk"));
        gridPanel.add(new JLabel("Def"));
        gridPanel.add(new JLabel("Spd"));
        gridPanel.add(new JLabel("Spc"));
        for (int DV = 0; DV < 16; DV++) {
            for (int stat = 0; stat < 5; stat++) {
                DVButton b = new DVButton(stat, DV);
                b.addActionListener(new DVButtonAction(b));
                btnsDVs[stat][DV] = b;
                gridPanel.add(b);
            }
        }
        enableButtons();
    }

    private void enableButtons() {
        for (int DV = 0; DV < 16; DV++) {
            for (int stat = 0; stat < 5; stat++) {
                btnsDVs[stat][DV].setEnabled(calc.isPossibleDV[stat][DV]);
            }
        }
    }

    private class DVButton extends JButton {

        int stat;
        int DV;

        public DVButton(int stat, int DV) {
            super(DV + ": " + calc.stats[stat][DV]);
            this.stat = stat;
            this.DV = DV;
        }

    }

    private class DVButtonAction extends AbstractAction {

        final DVButton b;

        public DVButtonAction(DVButton b) {
            this.b = b;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if ((e.getModifiers() & ActionEvent.CTRL_MASK) > 0) {
                DVCalculatorPanel.this.calc.setStatExact(b.stat, b.DV);
            } else {
                DVCalculatorPanel.this.calc.setStat(b.stat, b.DV);
            }
            System.out.println("Clicked stat:" + b.stat + " DV:" + b.DV);
            enableButtons();
        }

    }

}
