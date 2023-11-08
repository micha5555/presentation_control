package com.example;

import java.awt.image.BufferedImage;

import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class JavaFXTools {
    
    public static ImageView prepareImageView(BufferedImage bufferedImage, int x, int y, int width, int height) {
        ImageView imageview = new ImageView();
        imageview.setImage(CommonUtils.bufferedImageToFXImage(bufferedImage));
        imageview.setX(x);
        imageview.setY(y);
        imageview.setFitWidth(width);
        imageview.setFitHeight(height);

        return imageview;
    }

    public static VBox prepareSliderWithLabel(int initialValue, String labelText) {
        Slider slider = new Slider(0, 255, initialValue); // Slider with min, max, and initial value
        slider.setShowTickMarks(true);
        slider.setShowTickLabels(true);
        slider.setMinWidth(500);
        Label label = new Label(labelText);

        return new VBox(10, label, slider);
    }

    public static String formatCurrentThresholdFromHBoxes(VBox red, VBox green, VBox blue) {
        Slider redSlider = (Slider)red.getChildren().get(1);
        Slider greenSlider = (Slider)green.getChildren().get(1);
        Slider blueSlider = (Slider)blue.getChildren().get(1);
        return String.format("(%d, %d, %d)", (int)redSlider.getValue(), (int)greenSlider.getValue(), (int)blueSlider.getValue());
    }
}
