package com.example;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Point;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import nu.pattern.OpenCV;

public class App extends Application
{
    // blue glove
    static Scalar minThresholdScalar = new Scalar(90, 0, 0);//BGR-A
    static Scalar maxThresholdScalar = new Scalar(255, 100, 70);//BGR-A
    // orange glove
    // static Scalar min = new Scalar(50, 70, 112);//BGR-A
    // static Scalar max= new Scalar(150, 150, 220);//BGR-A
    // yellow glove
    // static Scalar min = new Scalar(50, 85, 85);//BGR-A
    // static Scalar max= new Scalar(90, 255, 255);//BGR-A
    private static BufferedImage originalBufferedImage = null;
    private static BufferedImage binarizedBufferedImage = null;
    private static BufferedImage skeletonizedBufferedImage = null;
    private static BufferedImage contouredBufferedImage = null;

    // JavaFX components
    ImageView originalImage;
    ImageView binarizedImage;
    ImageView contourImage;
    VBox minRed;
    VBox minGreen;
    VBox minBlue;
    VBox maxRed;
    VBox maxGreen;
    VBox maxBlue;
    Text currentMinRGBThreshold = new Text();
    Text currentMaxRGBThreshold = new Text();

    OpenCVFrameGrabber camera;
    Java2DFrameConverter converterBuffered;
    OpenCVFrameConverter.ToMat converter;
    // to było git dla palm1
    // static Scalar min = new Scalar(150, 0, 0);//BGR-A
    // static Scalar max= new Scalar(255, 250, 110);//BGR-A
    public static void main( String[] args ) throws IOException
    {
        OpenCV.loadLocally();
//        System.out.println(CommonUtils.countAngleBetweenPointAndLineWithOnlyY(new Point(0,1), new Point(1,0))); //expected : 45
//        System.out.println(CommonUtils.countAngleBetweenPointAndLineWithOnlyY(new Point(0,1), new Point(0,0))); //expected : 90
//        System.out.println(CommonUtils.countAngleBetweenPointAndLineWithOnlyY(new Point(0,1), new Point(-1,0))); //expected : 135
//        System.out.println(CommonUtils.countAngleBetweenPointAndLineWithOnlyY(new Point(0,1), new Point(0,2))); //expected : 0
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        setupJavaFxStage(primaryStage);
        setupCamera();
    }

    private void setupJavaFxStage(Stage primaryStage) {
        primaryStage.setTitle("Presentation Gesture Controller");
        originalImage = JavaFXTools.prepareImageView(originalBufferedImage, 10, 25, 640, 288);
        binarizedImage = JavaFXTools.prepareImageView(binarizedBufferedImage, 660, 25, 640, 288);
        contourImage = JavaFXTools.prepareImageView(contouredBufferedImage, 1310, 25, 640, 288);

        minRed = JavaFXTools.prepareSliderWithLabel(StaticData.MIN_RED_SLIDER, "Minimal red threshold");
        addListenerToSlider((Slider)minRed.getChildren().get(1), true);
        minGreen = JavaFXTools.prepareSliderWithLabel(StaticData.MIN_GREEN_SLIDER, "Minimal green threshold");
        addListenerToSlider((Slider)minGreen.getChildren().get(1), true);
        minBlue = JavaFXTools.prepareSliderWithLabel(StaticData.MIN_BLUE_SLIDER, "Minimal blue threshold");
        addListenerToSlider((Slider)minBlue.getChildren().get(1), true);
        currentMinRGBThreshold.setText(JavaFXTools.formatCurrentThresholdFromHBoxes(minRed, minGreen, minBlue));
        VBox minThreshold = new VBox(15, minRed, minGreen, minBlue, currentMinRGBThreshold);

        minThreshold.setLayoutX(10);
        minThreshold.setLayoutY(350);

        maxRed = JavaFXTools.prepareSliderWithLabel(StaticData.MAX_RED_SLIDER, "Max red threshold");
        addListenerToSlider((Slider)maxRed.getChildren().get(1), false);
        maxGreen = JavaFXTools.prepareSliderWithLabel(StaticData.MAX_GREEN_SLIDER, "Max green threshold");
        addListenerToSlider((Slider)maxGreen.getChildren().get(1), false);
        maxBlue = JavaFXTools.prepareSliderWithLabel(StaticData.MAX_BLUE_SLIDER, "Max blue threshold");
        addListenerToSlider((Slider)maxBlue.getChildren().get(1), false);
        currentMaxRGBThreshold.setText(JavaFXTools.formatCurrentThresholdFromHBoxes(maxRed, maxGreen, maxBlue));
        VBox maxThreshold = new VBox(15, maxRed, maxGreen, maxBlue, currentMaxRGBThreshold);
        maxThreshold.setLayoutX(540);
        maxThreshold.setLayoutY(350);

        Group root = new Group(originalImage, binarizedImage, contourImage, minThreshold, maxThreshold);
        Scene scene = new Scene(root, 2000, 800);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void addListenerToSlider(Slider slider, boolean isMinThreshold) {
        slider.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue <?extends Number>observable, Number oldValue, Number newValue){
                if(isMinThreshold) {
                    Slider minRedSlider = (Slider)minRed.getChildren().get(1);
                    Slider minGreenSlider = (Slider)minGreen.getChildren().get(1);;
                    Slider minBlueSlider = (Slider)minBlue.getChildren().get(1);;
                    minThresholdScalar  = new Scalar((int)minBlueSlider.getValue(), (int)minGreenSlider.getValue(), (int)minRedSlider.getValue());
                    currentMinRGBThreshold.setText(JavaFXTools.formatCurrentThresholdFromHBoxes(minRed, minGreen, minBlue));
                } else {
                    Slider maxRedSlider = (Slider)maxRed.getChildren().get(1);
                    Slider maxGreenSlider = (Slider)maxGreen.getChildren().get(1);;
                    Slider maxBlueSlider = (Slider)maxBlue.getChildren().get(1);;
                    maxThresholdScalar  = new Scalar((int)maxBlueSlider.getValue(), (int)maxGreenSlider.getValue(), (int)maxRedSlider.getValue());
                    currentMaxRGBThreshold.setText(JavaFXTools.formatCurrentThresholdFromHBoxes(maxRed, maxGreen, maxBlue));
                }
            }
        });
    }

    private void setupCamera() throws Exception {
        camera = new OpenCVFrameGrabber(0);
        converter = new OpenCVFrameConverter.ToMat();
        converterBuffered = new Java2DFrameConverter();
        org.bytedeco.opencv.opencv_core.Mat matImage = null;
        camera.start();

        captureAndDisplayFrames();
    }

    private void captureAndDisplayFrames() {
        new Thread(() -> {
            int delay = 0;
            while (!Thread.interrupted()) {
                try {
                    Frame frame = camera.grab(); // Capture a frame from the webcam
                    if (frame != null) {
                        BufferedImage originalBufferedImage = converterBuffered.convert(frame);
                        Mat binarizedImageMat = null;
                        BufferedImage binarizedBufferedImage = null;
                        BufferedImage contouredBufferedImage = null;
                        if(delay == 0){
                            binarizedImageMat = CommonUtils.convertBufferedImageToBinarizedMat(originalBufferedImage, minThresholdScalar, maxThresholdScalar );
                            binarizedBufferedImage = CommonUtils.convertBufferedImageToBinarizedBufferedImage(originalBufferedImage, minThresholdScalar, maxThresholdScalar);
                            contouredBufferedImage = CommonUtils.convertMatToContourizedBufferedImage(binarizedImageMat);
                        }
                        originalImage.setImage(CommonUtils.bufferedImageToFXImage(originalBufferedImage));
                        if(delay == 0) {
                            binarizedImage.setImage(CommonUtils.bufferedImageToFXImage(binarizedBufferedImage));
                            contourImage.setImage(CommonUtils.bufferedImageToFXImage(contouredBufferedImage));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(delay == StaticData.FRAMES_DELAY) {
                    delay = 0;
                } else {
                    delay++;
                }
            }
        }).start();
    }
}
