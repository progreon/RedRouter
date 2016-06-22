/*
 * Copyright (C) 2016 Marco Willems
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
package redrouter.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import redrouter.data.Battler;
import redrouter.data.DVCalculator;
import redrouter.data.Pokemon;

/**
 *
 * @author Marco Willems
 */
public class DVCalculatorPanel extends JPanel {

    private DVCalculator calc;

    private final JPanel gridPanel;
    private final GridLayout gridDVs;
    private final DVButton[][] btnsDVs;
    private final ActionListener dvBtnClick;

    private final JPanel settingsPanel;
    private JComboBox cmbPokemon;
    private JSpinner spnLevel;
    private List<DefeatedPkmn> pkmnDefeated;
    private JList<DefeatedPkmn> lstDefeated;

    private final DVPanel dvPanel;

    public DVCalculatorPanel(DVCalculator calc) {
        super(new BorderLayout());
        this.calc = calc;
        if (this.calc == null) {
            this.calc = new DVCalculator(null);
        }
//        this.add(new JLabel(this.calc.getBattler().getPokemon().name), BorderLayout.NORTH);

        // Grid Panel
        gridDVs = new GridLayout(17, 5, 5, 5);
        gridPanel = new JPanel(gridDVs);
        btnsDVs = new DVButton[5][16];
        dvBtnClick = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                DVButton b = (DVButton) e.getSource();
                if ((e.getModifiers() & ActionEvent.CTRL_MASK) > 0) {
                    DVCalculatorPanel.this.calc.setStatExact(b.stat, b.DV);
                } else {
                    DVCalculatorPanel.this.calc.setStat(b.stat, b.DV);
                }
                System.out.println("Clicked stat:" + b.stat + " DV:" + b.DV);
                enableButtons();
            }
        };
//        initButtons();
        this.add(gridPanel);

        // Settings PAnel
        this.settingsPanel = new JPanel();
        this.settingsPanel.setLayout(new BoxLayout(this.settingsPanel, BoxLayout.Y_AXIS));
        initSettignsPanel();
        this.add(settingsPanel, BorderLayout.EAST);

        JPanel pnlSouth = new JPanel(new FlowLayout(FlowLayout.LEFT));
        this.dvPanel = new DVPanel(this.calc.getStatRanges());
        pnlSouth.add(this.dvPanel, BorderLayout.SOUTH);
        this.add(pnlSouth, BorderLayout.SOUTH);

        initButtons();
    }

//    public void setDVCalculator(DVCalculator calc) {
//        this.calc = calc;
//        initButtons();
//        initSettignsPanel();
//    }
    private void setPokemon(Pokemon poke) {
        this.calc.setBattler(new Battler(poke, (int) spnLevel.getValue(), null));
        updateButtons();
    }

    private void initButtons() {
        gridPanel.removeAll();
        gridPanel.add(new JLabel("    HP"));
        gridPanel.add(new JLabel("    Atk"));
        gridPanel.add(new JLabel("    Def"));
        gridPanel.add(new JLabel("    Spd"));
        gridPanel.add(new JLabel("    Spc"));
        for (int DV = 0; DV < 16; DV++) {
            for (int stat = 0; stat < 5; stat++) {
                DVButton b = new DVButton(stat, DV);
                b.addActionListener(dvBtnClick);
                btnsDVs[stat][DV] = b;
                gridPanel.add(b);
//                gridPanel.add(new DVButtonPanel(b));
            }
        }
        enableButtons();
    }

    private void enableButtons() {
        for (int DV = 0; DV < 16; DV++) {
            for (int stat = 0; stat < 5; stat++) {
                btnsDVs[stat][DV].setEnabled(calc.isPossibleDV(stat, DV));
            }
        }
        dvPanel.setRanges(this.calc.getStatRanges());
        dvPanel.revalidate();
    }

    private void updateButtons() {
        for (int DV = 0; DV < 16; DV++) {
            for (int stat = 0; stat < 5; stat++) {
                btnsDVs[stat][DV].update();
            }
        }
        enableButtons();
    }

    private void calculateExperience() {
        calc.resetStatExp();
        for (DefeatedPkmn poke : pkmnDefeated) {
            calc.defeatPokemon(poke.pkmn, poke.isDivided ? 2 : 1);
        }
        calc.setLevel(calc.getBattler().level);
        updateButtons();
    }

    private void defeatPokemon(Pokemon poke, boolean isDivided) {
        pkmnDefeated.add(new DefeatedPkmn(poke, isDivided));
        calc.defeatPokemon(poke, isDivided ? 2 : 1);
        refreshDefeatedPokemon();
    }

    private void undefeatPokemon(int[] indices) {
        for (int i = 0; i < indices.length; i++) {
            pkmnDefeated.remove(indices[indices.length - i - 1]);
        }
        refreshDefeatedPokemon();
        calculateExperience();
    }

    private void refreshDefeatedPokemon() {
        lstDefeated.setListData(pkmnDefeated.toArray(new DefeatedPkmn[0]));
    }

    private void resetDVs() {
        calc.resetSelected();
        enableButtons();
    }

    private void resetAll() {
        resetDVs();
        setLevel(DVCalculator.defaultLevel);
        calc.resetStatExp();
        pkmnDefeated.clear();
        refreshDefeatedPokemon();
    }

    private void setLevel(int level) {
        calc.setLevel(level);
        spnLevel.setValue(level);
        updateButtons();
    }

    private void initSettignsPanel() {
        // Reset
        JButton btnResetDVs = new JButton("Reset DVs");
        btnResetDVs.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                resetDVs();
            }
        });
        btnResetDVs.setAlignmentX(Component.CENTER_ALIGNMENT);
        settingsPanel.add(btnResetDVs);
        JButton btnResetAll = new JButton("Reset all");
        btnResetAll.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                resetAll();
            }
        });
        btnResetAll.setAlignmentX(Component.CENTER_ALIGNMENT);
        settingsPanel.add(btnResetAll);

        settingsPanel.add(Box.createVerticalGlue());
        // Pokemon
        JLabel lblPokemon = new JLabel("Pokemon:");
        lblPokemon.setAlignmentX(Component.CENTER_ALIGNMENT);
        settingsPanel.add(lblPokemon);
        cmbPokemon = new JComboBox(Pokemon.getAll());
        cmbPokemon.setMaximumSize(cmbPokemon.getMinimumSize());
        cmbPokemon.setSelectedItem(calc.getBattler().getPokemon());
        cmbPokemon.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                setPokemon((Pokemon) e.getItem());
            }
        });
        settingsPanel.add(cmbPokemon);

        settingsPanel.add(Box.createVerticalGlue());
        // Level
        spnLevel = new JSpinner(new SpinnerNumberModel(this.calc.getBattler().level, 2, 100, 1));
        spnLevel.setMaximumSize(new Dimension(spnLevel.getMaximumSize().width, spnLevel.getMinimumSize().height));
        spnLevel.addChangeListener((ChangeEvent e) -> {
            setLevel((int) spnLevel.getValue());
        });
        JLabel lblLevel = new JLabel("Level:");
        lblLevel.setAlignmentX(Component.CENTER_ALIGNMENT);
        settingsPanel.add(lblLevel);
        settingsPanel.add(spnLevel);
        settingsPanel.add(Box.createVerticalGlue());

        // TODO Location
        settingsPanel.add(Box.createVerticalGlue());

        // Defeated pokemon
        JLabel lblDefeated = new JLabel("Defeated pokemon:");
        lblDefeated.setAlignmentX(Component.CENTER_ALIGNMENT);
        settingsPanel.add(lblDefeated);
        JLabel lblDefeatedInfo = new JLabel("(before leveling up)");
        lblDefeatedInfo.setAlignmentX(Component.CENTER_ALIGNMENT);
        settingsPanel.add(lblDefeatedInfo);
        JComboBox cmbPokemon2 = new JComboBox(Pokemon.getAll());
        cmbPokemon2.setMaximumSize(cmbPokemon2.getMinimumSize());
        settingsPanel.add(cmbPokemon2);
        JPanel pnlAdd = new JPanel();
        JCheckBox chkDiv = new JCheckBox("shared");
        pnlAdd.add(chkDiv);
        JButton btnAddDefeated = new JButton("Add");
        pnlAdd.add(btnAddDefeated);
        pnlAdd.setAlignmentX(Component.CENTER_ALIGNMENT);
        settingsPanel.add(pnlAdd);
        pkmnDefeated = new ArrayList<>();
        lstDefeated = new JList<>();
        JScrollPane scrDefeatedList = new JScrollPane(lstDefeated);
        scrDefeatedList.setPreferredSize(new Dimension(lblDefeated.getWidth(), 100));
        settingsPanel.add(scrDefeatedList);
        btnAddDefeated.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Pokemon poke = (Pokemon) cmbPokemon2.getSelectedItem();
                defeatPokemon(poke, chkDiv.isSelected());
            }
        });
        JButton btnDeleteDefeated = new JButton("Delete");
        btnDeleteDefeated.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int[] selected = lstDefeated.getSelectedIndices();
                undefeatPokemon(selected);
            }
        });
        btnDeleteDefeated.setAlignmentX(Component.CENTER_ALIGNMENT);
        settingsPanel.add(btnDeleteDefeated);
    }

    private class DVButton extends JButton {

        int stat;
        int DV;

        DVButton(int stat, int DV) {
            this.stat = stat;
            this.DV = DV;
            update();
        }

        final void update() {
            this.setText(DV + ": " + calc.stats[stat][DV]);
//            this.setText(calc.stats[stat][DV] + "");
        }

    }

    // Meh ...
//    private class DVButtonPanel extends JPanel {
//
//        DVButton dvButton;
//
//        public DVButtonPanel(DVButton dvButton) {
//            super();
//            this.setLayout(new GridLayout(1, 0, 0, 0));
//            this.dvButton = dvButton;
//            Dimension preferredSize = this.dvButton.getPreferredSize();
//            preferredSize.width += 10;
//            this.dvButton.setPreferredSize(preferredSize);
//            this.add(new JLabel("    " + dvButton.DV));
//            this.add(dvButton);
//        }
//
//    }
    private class DVPanel extends JPanel {

        public DVPanel(DVCalculator.StatRange[] ranges) {
            super(new GridLayout(0, 5));
            init(ranges);
            this.setPreferredSize(new Dimension(300, this.getPreferredSize().height));
        }

        private void init(DVCalculator.StatRange[] ranges) {
            setRanges(ranges);
        }

        public void setRanges(DVCalculator.StatRange[] ranges) {
            this.removeAll();
            String[] statNames = new String[]{"HP", "ATK", "DEF", "SPD", "SPC"};
            for (int stat = 0; stat < 5; stat++) {
                JPanel pnlVert = new JPanel();
                pnlVert.setLayout(new BoxLayout(pnlVert, BoxLayout.Y_AXIS));
                pnlVert.setBackground(Color.white);
                pnlVert.setBorder(new EmptyBorder(10, 10, 10, 10));
//                JLabel lblStatName = new JLabel("<html><b>" + statNames[stat] + "</b></html>"); // Won't work
                JLabel lblStatName = new JLabel(statNames[stat]);
                lblStatName.setAlignmentX(Component.CENTER_ALIGNMENT);
                pnlVert.add(lblStatName);
//                JLabel lblStat = new JLabel("<html><b>" + ranges[stat] + "</b></html>");
                JLabel lblStat = new JLabel(ranges[stat].toString());
                lblStat.setAlignmentX(Component.CENTER_ALIGNMENT);
                pnlVert.add(lblStat);
                this.add(pnlVert);
            }
        }

    }

    private class DefeatedPkmn {

        Pokemon pkmn;
        boolean isDivided;

        public DefeatedPkmn(Pokemon pkmn, boolean isDivided) {
            this.pkmn = pkmn;
            this.isDivided = isDivided;
        }

        @Override
        public String toString() {
            return pkmn.toString() + (isDivided ? "*" : "");
        }

    }

}
