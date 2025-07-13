package com.catan;

import java.io.IOException;

import com.catan.controller.MainController;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main application class for the CATAN board game.
 * This class handles the JavaFX application lifecycle and window management.
 */
public class CatanApplication extends Application {
    
    private static final String TITLE = "CATAN - Das Spiel";
    private static final int WINDOW_WIDTH = 950; //1200 davor
    private static final int WINDOW_HEIGHT = 650; // 800 davor
    
    @Override
    public void start(Stage stage) throws IOException {
    	System.out.println("FXML resource: " + CatanApplication.class.getResource("/main-view.fxml"));
        FXMLLoader fxmlLoader = new FXMLLoader(CatanApplication.class.getResource("/main-view.fxml"));

        
        Scene scene = new Scene(fxmlLoader.load(), WINDOW_WIDTH, WINDOW_HEIGHT);

        MainController controller = fxmlLoader.getController();
        controller.setHostServices(getHostServices()); //
        
        stage.setTitle(TITLE);
        stage.setScene(scene);
        stage.setResizable(true);
        stage.setMinWidth(800);
        stage.setMinHeight(620);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
