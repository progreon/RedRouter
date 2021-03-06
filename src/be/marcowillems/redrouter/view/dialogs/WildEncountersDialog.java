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
package be.marcowillems.redrouter.view.dialogs;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import be.marcowillems.redrouter.data.EncounterArea;
import be.marcowillems.redrouter.route.RouteEntry;
import be.marcowillems.redrouter.route.WildEncounters;
import be.marcowillems.redrouter.util.PokemonCountPair;

/**
 * TODO: reset button
 *
 * @author Marco Willems
 */
public class WildEncountersDialog extends SettingsDialog {

    public final RouteEntry routeEntry;
    private List<JLabel> labels;
    private List<JSpinner> spinners;
    private JPanel contentPanel;

    public WildEncountersDialog(RouteEntry routeEntry) {
        this.routeEntry = routeEntry;
        init();
    }

    private void init() { // TODO: speed up?
        WildEncounters we = routeEntry.wildEncounters;
        if (we.getLocation() != null) {
            Set<EncounterArea> areas = we.getLocation().getAllEncounterAreas();
            int height = 1;
            if (areas.size() > 1) {
                height = 2;
            }

            contentPanel = new JPanel(new GridLayout(height, 0, 5, 5));
            if (!areas.isEmpty()) {
                labels = new ArrayList<>();
                spinners = new ArrayList<>();
                for (EncounterArea ea : areas) {
                    for (PokemonCountPair pcp : we.getEncounterCounts(ea)) {
                        JLabel lblP = new JLabel(pcp.plp.toString());
                        lblP.setToolTipText(pcp.plp.pkmn.getExp(pcp.plp.level, 1, false, false) + " xp");
                        labels.add(lblP);
                        JSpinner sp = new JSpinner(new SpinnerNumberModel(pcp.getCount(), PokemonCountPair.MIN_COUNT, PokemonCountPair.MAX_COUNT, 1));
                        sp.addChangeListener(new ChangeListener() {

                            @Override
                            public void stateChanged(ChangeEvent e) {
                                changed = true;
                            }
                        });
                        spinners.add(sp);
                    }
                }
                int i = 0;
                JPanel areaPanel;
                for (EncounterArea ea : areas) {
                    areaPanel = new JPanel(new GridLayout(0, 2, 5, 5));
                    areaPanel.add(new JLabel(ea.toString()));
                    areaPanel.add(new JLabel());
                    for (PokemonCountPair pcp : we.getEncounterCounts(ea)) {
                        JPanel pnlSetting = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                        pnlSetting.add(labels.get(i));
                        pnlSetting.add(spinners.get(i));
                        areaPanel.add(pnlSetting);
                        i++;
                    }
                    contentPanel.add(areaPanel);
                }
            } else {
                contentPanel.add(new JLabel(we.getLocation().toString()));
                contentPanel.add(new JLabel("(no encounters here)"));
            }
        } else {
            contentPanel = new JPanel();
            contentPanel.add(new JLabel("No location set"));
        }
    }

    private void updateSpinners() {
        WildEncounters we = routeEntry.wildEncounters;
        if (we.getLocation() != null) {
            int i = 0;
            for (EncounterArea ea : we.getLocation().getAllEncounterAreas()) {
                for (PokemonCountPair pcp : we.getEncounterCounts(ea)) {
                    spinners.get(i).setValue(pcp.getCount());
                    i++;
                }
            }
        }
    }

    @Override
    protected JPanel getContentPanel() {
        updateSpinners();
        return contentPanel;
    }

    @Override
    protected void save() {
        WildEncounters we = routeEntry.wildEncounters;
        if (we.getLocation() != null) {
            int i = 0;
            for (EncounterArea ea : we.getLocation().getAllEncounterAreas()) {
                for (PokemonCountPair pcp : we.getEncounterCounts(ea)) {
                    int value = (int) spinners.get(i).getValue();
                    if (value != pcp.getCount()) {
                        changed = true;
                        pcp.setCount(value);
                    }
                    i++;
                }
            }
        }
    }
}
