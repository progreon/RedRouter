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
package redrouter.view.dialogs.editroute;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import redrouter.data.EncounterArea;
import redrouter.route.RouteEncounter;
import redrouter.util.PokemonCountPair;
import redrouter.util.PokemonLevelPair;

/**
 *
 * @author Marco Willems
 */
public class RouteEncounterEdit extends EditDialog {

    public final Set<PokemonCountPair> battlerCounts;
    public final EncounterArea encounterArea;

    private JLabel[] labels;
    private JSpinner[] spinners;
    private JCheckBox[] preferredBoxes; // TODO

    public RouteEncounterEdit(RouteEncounter re) {
        super(re);
        this.encounterArea = re.getArea();
        List<PokemonLevelPair> slots = encounterArea.getUniqueSlots();
        battlerCounts = new TreeSet<>();
        for (int i = 0; i < slots.size(); i++) {
            battlerCounts.add(new PokemonCountPair(slots.get(i), 0));
        }
        init();
    }

    public RouteEncounterEdit(RouteEncounter re, Set<PokemonCountPair> battlerCounts) {
        this(re);
        for (PokemonCountPair pcp : battlerCounts) {
            PokemonCountPair pip = getPair(pcp.plp);
            if (pip != null && pip.getCount() == 0) {
                pip.setCount(pcp.getCount());
            }
        }
    }

    private PokemonCountPair getPair(PokemonLevelPair plp) {
        PokemonCountPair bip = null;
        for (PokemonCountPair pcp : battlerCounts) {
            if (pcp.plp.equals(plp) && bip == null) {
                bip = pcp;
            }
        }
        return bip;
    }

    private void init() {
        labels = new JLabel[battlerCounts.size()];
        spinners = new JSpinner[battlerCounts.size()];
        int i = 0;
        for (PokemonCountPair pcp : battlerCounts) {
            labels[i] = new JLabel(pcp.plp.toString());
            spinners[i] = new JSpinner(new SpinnerNumberModel(pcp.getCount(), PokemonCountPair.MIN_COUNT, PokemonCountPair.MAX_COUNT, 1));
            spinners[i].addChangeListener(new ChangeListener() {

                @Override
                public void stateChanged(ChangeEvent e) {
                    changed = true;
                }
            });
            i++;
        }
    }

    private void updateSpinners() {
        int i = 0;
        for (PokemonCountPair pcp : battlerCounts) {
            spinners[i].setValue(pcp.getCount());
            i++;
        }
    }

    @Override
    protected JPanel getSettingsPanel() {
        // TODO move stuff to init & only updatSpinners()?
        JPanel pnlSettings = new JPanel(new GridLayout(0, 2, 5, 5));
        updateSpinners();
        for (int i = 0; i < spinners.length; i++) {
            JPanel pnlSetting = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            pnlSetting.add(labels[i]);
            pnlSetting.add(spinners[i]);
            pnlSettings.add(pnlSetting);
        }
        return pnlSettings;
    }

    @Override
    protected void save() {
        RouteEncounter re = (RouteEncounter) routeEntry;
        int i = 0;
        for (PokemonCountPair pcp : battlerCounts) {
            int value = (Integer) spinners[i].getValue();
            if (value != pcp.getCount()) {
                changed = true;
                pcp.setCount(value);
            }
            i++;
        }
        re.updatePreferences(battlerCounts);
    }

}
