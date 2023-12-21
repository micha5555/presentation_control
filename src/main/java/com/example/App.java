package com.example;

import com.example.binarization.impl.HSVBasedBinarizator;
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
//        HSVBasedBinarizator bin = new HSVBasedBinarizator();
//        double r = 56;
//        double g = 120;
//        double b = 255;
//        HSVBasedBinarizator.rgbToHsv(r, g, b);
//        HSVBasedBinarizator.rgbToHsv2(r, g, b);
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            URL fxmlUrl = new File("src/main/java/com/example/view/main_view.fxml").toURI().toURL();
            fxmlLoader.setLocation(fxmlUrl);

            Scene scene = new Scene(fxmlLoader.load(), 1700, 800);
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.setTitle("Presentation Gesture Controller App");
            primaryStage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() throws Exception {
        System.exit(0);
    }
}
