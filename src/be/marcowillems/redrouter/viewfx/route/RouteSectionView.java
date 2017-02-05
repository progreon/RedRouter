/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.marcowillems.redrouter.viewfx.route;

import be.marcowillems.redrouter.route.RouteSection;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.text.Font;

/**
 *
 * @author Marco Willems
 */
public class RouteSectionView extends RouteEntryView {

    @FXML
    protected Label title;

    public RouteSectionView(RouteSection routeSection) {
        super(routeSection, "fxml/RouteSectionView.fxml");
        if (routeSection.info != null) {
            title.setText(routeSection.info.title);
        } else {
            title.setText(routeSection.toString());
        }
        Font font = title.getFont();
        double fontSize = font.getSize();
        fontSize += 10;
        while (routeSection.getParentSection() != null) {
            routeSection = routeSection.getParentSection();
            fontSize -= 4;
        }
        title.setFont(new Font(font.getName(), Math.max(fontSize, font.getSize())));
    }

}
