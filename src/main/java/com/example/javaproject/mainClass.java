package com.example.javaproject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class mainClass extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/javaproject/AjouterReclamation.fxml"));

        AnchorPane root = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/com/example/Styles/styles.CSS").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Ajouter une Réclamation");
        primaryStage.setWidth(1100);
        primaryStage.setHeight(800);
        primaryStage.show();
    }



    public static void main(String[] args) {
        launch(args);
    }
}
