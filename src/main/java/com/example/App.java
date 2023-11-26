package com.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class App extends Application
{

    public static void main( String[] args ) throws IOException
    {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL fxmlUrl = new File("src/main/java/com/example/view/main_view.fxml").toURI().toURL();
        fxmlLoader.setLocation(fxmlUrl);

        Scene scene = new Scene(fxmlLoader.load(), 1700, 800);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.setTitle("Presentation Gesture Controller App");
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        System.exit(0);
    }
}
