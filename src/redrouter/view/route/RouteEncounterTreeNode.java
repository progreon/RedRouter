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
package redrouter.view.route;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import redrouter.data.Battler;
import redrouter.route.RouteEncounter;

/**
 *
 * @author Marco Willems
 */
public class RouteEncounterTreeNode extends RouteEntryTreeNode {

    private JLabel lblInfo;
    private String labelText;

    public RouteEncounterTreeNode(RouteTree tree, RouteEncounter routeEncounter) {
        super(tree, routeEncounter);
    }

    @Override
    protected void initRender(int availableWidth) {
        String text;
        JLabel lbl;
        RouteEncounter re = (RouteEncounter) routeEntry;
        text = re.info + "\n";
        lbl = new JLabel();
        setLabelText(lbl, text, availableWidth);
        view.add(lbl);
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        southPanel.setOpaque(false);
        southPanel.add(new JLabel("Defeated pokemon:"));
        List<Battler> choices = new ArrayList<>();
//        choices.add(Battler.NULL);
        choices.add(null);
        choices.addAll(re.getChoices());
        JComboBox<Battler> cmbChoices = new JComboBox<>(choices.toArray(new Battler[0]));
        cmbChoices.setSelectedIndex(re.getPreference() + 1);
        southPanel.add(cmbChoices);
        view.add(southPanel, BorderLayout.SOUTH);

        labelText = text;
        lblInfo = lbl;
    }

    @Override
    protected void doSizedRender(int availableWidth, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        setLabelText(lblInfo, labelText, availableWidth);
    }

}
