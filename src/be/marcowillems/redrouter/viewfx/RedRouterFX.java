/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.marcowillems.redrouter.viewfx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 *
 * @author Marco Willems
 */
public class RedRouterFX extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        ApplicationController.setStage(stage);
        FXMLLoader rootLoader = new FXMLLoader(getClass().getResource("Application.fxml"));
        Parent root = rootLoader.load();

        // Setting the menu and its controller
        FXMLLoader menuLoader = new FXMLLoader(getClass().getResource("MainMenu.fxml"));
        ((BorderPane) root).setTop(menuLoader.load());
        menuLoader.setController(rootLoader.getController());

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
