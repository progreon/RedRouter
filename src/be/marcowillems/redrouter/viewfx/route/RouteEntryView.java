/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.marcowillems.redrouter.viewfx.route;

import be.marcowillems.redrouter.route.RouteEntry;
import java.io.IOException;
import javafx.beans.DefaultProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;

/**
 * This is, for now, also used as view for RouteDescription
 *
 * @author Marco Willems
 */
@DefaultProperty(value = "center")
public abstract class RouteEntryView extends BorderPane {

    private ViewChangedListener listener = null;

    private final BooleanProperty showheader = new SimpleBooleanProperty(true);

    protected final RouteEntry routeEntry;

    public RouteEntryView(RouteEntry routeEntry, String fxmlFile) {
        super();
        this.routeEntry = routeEntry;
        if (fxmlFile != null) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlFile));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            try {
                fxmlLoader.load();
            } catch (IOException exception) {
                throw new RuntimeException(exception);
            }
        }
        this.getStyleClass().add("route-entry");
    }

    public BooleanProperty showheaderProperty() {
        return this.showheader;
    }

    public void setShowheader(boolean showheader) {
        this.showheader.set(showheader);
    }

    public boolean getShowheader() {
        return this.showheader.get();
    }

    protected void viewChanged() {
        if (listener != null) {
            listener.viewChanged(this);
        }
    }

    public void setViewChangedListener(ViewChangedListener listener) {
        this.listener = listener;
    }

    public static interface ViewChangedListener {

        public void viewChanged(RouteEntryView view);

    }

}
