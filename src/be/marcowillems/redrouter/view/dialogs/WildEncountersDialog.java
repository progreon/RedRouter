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
import be.marcowillems.redrouter.util.PokemonCountPair;
import be.marcowillems.redrouter.util.WildEncounters;

/**
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

    private WildEncounters getWildEncounters() {
        return routeEntry.getWildEncounters();
    }

    private void init() {
        WildEncounters we = getWildEncounters();
        if (we != null) {
            int width = 2;
            contentPanel = new JPanel(new GridLayout(0, width, 5, 5));
            contentPanel.add(new JLabel((we.loc == null ? "No location set" : we.loc.name)));
            if (we.encounterCounts != null) {
                contentPanel.add(new JLabel());
                labels = new ArrayList<>();
                spinners = new ArrayList<>();
                for (Set<PokemonCountPair> spcp : we.encounterCounts.values()) {
                    for (PokemonCountPair pcp : spcp) {
                        labels.add(new JLabel(pcp.plp.toString()));
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
                int pos = 0;
                for (EncounterArea ea : we.encounterCounts.keySet()) {
                    if (pos % width != 0) {
                        contentPanel.add(new JLabel());
                        pos++;
                    }
                    if (we.encounterCounts.size() > 1) {
                        contentPanel.add(new JLabel(ea.subArea));
                        contentPanel.add(new JLabel());
                    }
                    pos += 2;
                    Set<PokemonCountPair> spcp = we.encounterCounts.get(ea);
                    for (PokemonCountPair pcp : spcp) {
                        JPanel pnlSetting = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                        pnlSetting.add(labels.get(i));
                        pnlSetting.add(spinners.get(i));
                        contentPanel.add(pnlSetting);
                        i++;
                        pos++;
                    }
                }
            } else {
                contentPanel.add(new JLabel("(no encounters here)"));
            }
        }
    }

    private void updateSpinners() {
        WildEncounters we = getWildEncounters();
        if (we != null && we.encounterCounts != null) {
            int i = 0;
            for (Set<PokemonCountPair> spcp : we.encounterCounts.values()) {
                for (PokemonCountPair pcp : spcp) {
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
//        changed = false;
        WildEncounters we = getWildEncounters();
        if (we != null && we.encounterCounts != null) {
            System.out.println("Saving encounters ...");
            int i = 0;
            for (Set<PokemonCountPair> spcp : we.encounterCounts.values()) {
                for (PokemonCountPair pcp : spcp) {
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
