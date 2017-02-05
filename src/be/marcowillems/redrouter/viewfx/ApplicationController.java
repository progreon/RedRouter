/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.marcowillems.redrouter.viewfx;

import be.marcowillems.redrouter.io.ParserException;
import be.marcowillems.redrouter.io.RouteParser;
import be.marcowillems.redrouter.route.Route;
import be.marcowillems.redrouter.route.RouteEntry;
import be.marcowillems.redrouter.viewfx.route.RouteViewManager;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 *
 * @author Marco Willems
 */
public class ApplicationController implements Initializable, EventHandler<Event> {

    private static Stage stage = null;

    private final Map<Tab, File> openTabs = new HashMap<>();
    private final Map<File, Route> openFiles = new HashMap<>();

    private int newFileCounter = 0;
    private File lastFolder = null;

    private boolean checkSaveAndContinue(Tab tab) {
        // TODO: check for changes
        return true;
    }

    private void closeTab(Tab tab) {
        if (checkSaveAndContinue(tab)) {
            getTabPaneRoutes().getTabs().remove(tab);
            if (tab != null) {
                openFiles.remove(openTabs.get(tab));
                openTabs.remove(tab);
            }
        }
    }

    private Tab getOpenTab() {
        return getTabPaneRoutes().getSelectionModel().getSelectedItem();
    }

    private TabPane getTabPaneRoutes() {
        return (TabPane) stage.getScene().lookup("#tabPaneRoutes");
    }

    private void openRouteTab(File file, Route route) {
        if (file == null) {
            file = new File("New route" + (newFileCounter == 0 ? "" : " " + newFileCounter++));
            while (openFiles.containsKey(file)) {
                file = new File("New route " + newFileCounter++);
            }
        }
        if (!openFiles.containsKey(file)) {
            TreeItem<RouteEntry> rootItem = new TreeItem<>(route);
            for (RouteEntry child : route.getChildren()) {
                addTreeItem(child, rootItem);
            }
            rootItem.setExpanded(true);

            TreeView treeView = new TreeView(rootItem);
            treeView.setCellFactory(new RouteViewManager());
//            treeView.addEventHandler(TreeItem.graphicChangedEvent(), (TreeItem.TreeModificationEvent<Object> event) -> {
                // TODO: refresh not working?
//                    treeView.refresh();
//                    treeView.scrollTo(treeView.getRow(event.getTreeItem()));
//            });

            Tab newTab = new Tab(file.getName(), treeView);
            newTab.setOnCloseRequest(this);
            getTabPaneRoutes().getTabs().add(newTab);
            getTabPaneRoutes().getSelectionModel().select(newTab);

            openTabs.put(newTab, file);
            openFiles.put(file, route);
        } else {
            Tab tab = null;
            for (Tab t : openTabs.keySet()) {
                if (file.equals(openTabs.get(t))) {
                    tab = t;
                }
            }
            getTabPaneRoutes().getSelectionModel().select(tab);
        }
    }

    private TreeItem<RouteEntry> addTreeItem(RouteEntry routeEntry, TreeItem<RouteEntry> parent) {
        TreeItem<RouteEntry> item = new TreeItem<RouteEntry>(routeEntry);
        item.setExpanded(true);
        parent.getChildren().add(item);
        if (routeEntry.hasChildren()) {
            for (RouteEntry child : routeEntry.getChildren()) {
                addTreeItem(child, item);
            }
        }
        return item;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    private void onClose(ActionEvent event) {
        System.out.println("onClose(ActionEvent)");
        closeTab(getOpenTab());
    }

    @FXML
    private void onCloseAll(ActionEvent event) {
        System.out.println("onCloseAll(ActionEvent)");
        for (Tab tab : getTabPaneRoutes().getTabs().toArray(new Tab[0])) {
            closeTab(tab);
        }
    }

    @FXML
    private void onExit(ActionEvent event) {
        System.out.println("onExit(ActionEvent)");
        // TODO: check for changes
        boolean cont = true;
        for (Tab t : getTabPaneRoutes().getTabs()) {
//            cont &= 
        }
        Platform.exit();
    }

    @FXML
    private void onNew(ActionEvent event) {
        System.out.println("onNew(ActionEvent)");
        // TODO
    }

    @FXML
    private void onOpen(ActionEvent event) {
        System.out.println("onOpen(ActionEvent)");

        if (lastFolder == null) {
            lastFolder = new File(".");
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load a route file");
        fileChooser.setInitialDirectory(lastFolder);
        File f = fileChooser.showOpenDialog(stage);
        if (f != null) {
            lastFolder = f.getParentFile();
            Route route;
            try {
                route = new RouteParser().parseFile(f);
                openRouteTab(f, route);
            } catch (ParserException ex) {
                Logger.getLogger(ApplicationController.class.getName()).log(Level.SEVERE, null, ex);
                Alert parserAlert = new Alert(Alert.AlertType.ERROR);
                parserAlert.setHeaderText(ex.getMessage());
                parserAlert.setContentText("See console for more details");
                parserAlert.showAndWait();
            }
        }
    }

    @FXML
    private void onPrint(ActionEvent event) {
        System.out.println("onPrint(ActionEvent)");
        // TODO
    }

    @FXML
    private void onPrintToHTML(ActionEvent event) {
        System.out.println("onPrintToHTML(ActionEvent)");
        // TODO
    }

    @FXML
    private void onPrintToPDF(ActionEvent event) {
        System.out.println("onPrintToPDF(ActionEvent)");
        // TODO
    }

    @FXML
    private void onSave(ActionEvent event) {
        System.out.println("onSave(ActionEvent)");
        // TODO
    }

    @FXML
    private void onSaveAs(ActionEvent event) {
        System.out.println("onSaveAs(ActionEvent)");
        // TODO
    }

    static void setStage(Stage stage) {
        ApplicationController.stage = stage;
    }

    @Override
    public void handle(Event event) {
        if (event.getEventType().equals(Tab.TAB_CLOSE_REQUEST_EVENT)) {
            closeTab((Tab) event.getSource());
            event.consume();
        }
    }

}
