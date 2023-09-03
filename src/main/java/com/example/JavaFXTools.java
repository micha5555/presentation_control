package com.example;

import java.awt.image.BufferedImage;

import javafx.scene.image.ImageView;

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
}
