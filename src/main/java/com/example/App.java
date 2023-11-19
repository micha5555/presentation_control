package com.example;

import com.example.binarization.IBinarizator;
import com.example.binarization.impl.Binarizator;
import com.example.clicker.IKeyClicker;
import com.example.clicker.impl.KeyClicker;
import com.example.contourizer.IContourizer;
import com.example.contourizer.impl.Contourizer;
import com.example.converters.IConverter;
import com.example.converters.impl.Converter;
import com.example.fingerFinder.IFingerFinder;
import com.example.fingerFinder.impl.FingerFinder;
import com.example.fingersToKeyConverter.impl.FingersToKeyConverter;
import com.example.matProcessor.IMatProcessor;
import com.example.matProcessor.impl.MatProcessor;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import nu.pattern.OpenCV;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class App extends Application
{
    static IBinarizator binarizator;
    static IConverter converter;
    static IContourizer contourizer;
    static IFingerFinder fingerFinder;
    static IMatProcessor matProcessor;
    static IKeyClicker keyClicker;
    // blue glove
    static Scalar minThresholdScalar = new Scalar(90, 0, 0);//BGR-A
    static Scalar maxThresholdScalar = new Scalar(255, 100, 70);//BGR-A
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

    Button saveToFile = new Button("Save to file");
    CheckBox showProportions = new CheckBox("Show proportions");
//    List<Double> proportions = new ArrayList<>();
    Map<FingerNames, List<Double>> mapFingersToProportions = new HashMap<>();
    OpenCVFrameGrabber camera;
    Java2DFrameConverter converterBuffered;
    OpenCVFrameConverter.ToMat opencvConverter;
    public static void main( String[] args ) throws IOException
    {
        binarizator = new Binarizator();
        converter = new Converter();
        contourizer = new Contourizer();
        fingerFinder = new FingerFinder(converter);
        matProcessor = new MatProcessor();
        keyClicker = new KeyClicker(new FingersToKeyConverter());
        OpenCV.loadLocally();
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

        Group root = new Group(originalImage, binarizedImage, contourImage, minThreshold, maxThreshold, showProportions, saveToFile);
        Scene scene = new Scene(root, 2000, 800);
        primaryStage.setScene(scene);
        primaryStage.show();

        saveToFile.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    File myObj = new File("./rectanglearea_to_fingerlength/without_pinky_and_thumb.txt");
                    if (myObj.createNewFile()) {
                        System.out.println("File created: " + myObj.getName());
                        FileWriter myWriter = new FileWriter("./rectanglearea_to_fingerlength/without_pinky_and_thumb.txt");
                        for(FingerNames v : mapFingersToProportions.keySet()) {
                            myWriter.write(v + ": " + mapFingersToProportions.get(v) + "\n");
//                            myWriter.write(v + "\n");
                        }
                        myWriter.close();
                    } else {
                        System.out.println("File already exists.");
                    }
                } catch (IOException e) {
                    System.out.println("An error occurred.");
                    e.printStackTrace();
                }
            }
        });

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
        opencvConverter = new OpenCVFrameConverter.ToMat();
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
                        Mat contourizedImageMat = null;
                        BufferedImage binarizedBufferedImage = null;
                        BufferedImage contouredBufferedImage = null;
                        Map<Point, FingerNames> pointsToFingers = null;
                        if(delay == 0){
                            binarizedImageMat = binarizator.convertBufferedImageToBinarizedMat(originalBufferedImage, minThresholdScalar, maxThresholdScalar );
                            contourizedImageMat = contourizer.findBiggestContour(binarizedImageMat);

                            MatOfPoint convexHull = CommonUtils.findConvexHullPoints(contourizedImageMat);
//                            Point centroid = CommonUtils.findCentroid(converter.convertMatToMatOfPointNonEmptyPoints(contourizedImageMat));
                            Point centroid = CommonUtils.findCentroid(convexHull);

                            binarizedBufferedImage = converter.convertMatToBufferedImage(binarizedImageMat);

                            Point[] rectanglePoints = CommonUtils.findBiggestRectangleOnHand(convexHull);
                            System.out.println("Rectangle points: ");
                            System.out.println(rectanglePoints[0]);
                            System.out.println(rectanglePoints[1]);
                            System.out.println("Centroid: ");
                            System.out.println(centroid.x + " " + centroid.y);
                            int paintedPointsAboveCentroid = CommonUtils.countPaintedPoints(binarizedImageMat, rectanglePoints[0], new Point(rectanglePoints[1].x, centroid.y));
                            System.out.println("Painted points above centroid: " + paintedPointsAboveCentroid);
//                                System.out.println("Painted points above centroid: " + paintedPointsAboveCentroid);
//                                System.out.println("Surface area of rectangle: " + CommonUtils.countRectangleSurfaceArea(rectanglePoints));
                            Point[] rectangleAboveCentroidPoints = new Point[2];
                            rectangleAboveCentroidPoints[0] = new Point(rectanglePoints[0].x, rectanglePoints[0].y);
                            rectangleAboveCentroidPoints[1] = new Point(rectanglePoints[1].x, centroid.y);
                            double rectangleSurfaceArea = CommonUtils.countRectangleSurfaceArea(rectanglePoints);
                            if(paintedPointsAboveCentroid / CommonUtils.countRectangleSurfaceArea(rectangleAboveCentroidPoints) > 0.35 && rectangleSurfaceArea / contourizedImageMat.total() > 0.05) {
                                pointsToFingers = fingerFinder.retrieveFingersFromContour(contourizedImageMat);
                                keyClicker.clickKeyBasedOnFingers(pointsToFingers);
                            } else {
                                pointsToFingers = new HashMap<>();
                            }

//                            Imgproc.rectangle (
//                                    contourizedImageMat,                    //Matrix obj of the image
//                                    rectanglePoints[0],        //p1
//                                    rectanglePoints[1],       //p2
//                                    new Scalar(255, 255, 255),     //Scalar object for color
//                                    5                          //Thickness of the line
//                            );
//                            System.out.println();
//                            if(showProportions.isSelected()) {
//                                int paintedPointsAboveCentroid = CommonUtils.countPaintedPoints(binarizedImageMat, rectanglePoints[0], new Point(rectanglePoints[1].x, centroid.y));
//                                System.out.println("Painted points above centroid: " + paintedPointsAboveCentroid);
//                                System.out.println("Surface area of rectangle: " + CommonUtils.countRectangleSurfaceArea(rectanglePoints));
//                                Point[] rectangleAboveCentroidPoints = new Point[2];
//                                rectangleAboveCentroidPoints[0] = new Point(rectanglePoints[0].x, rectanglePoints[0].y);
//                                rectangleAboveCentroidPoints[1] = new Point(rectanglePoints[1].x, centroid.y);
//                                System.out.println(paintedPointsAboveCentroid / CommonUtils.countRectangleSurfaceArea(rectangleAboveCentroidPoints));
//                                proportions.add(paintedPointsAboveCentroid / CommonUtils.countRectangleSurfaceArea(rectangleAboveCentroidPoints));
//                                System.out.println("Proporcja x/y: " + CommonUtils.countProportionsXtoY(rectanglePoints));
//                                proportions.add(Core.countNonZero(binarizedImageMat) / CommonUtils.countRectangleSurfaceArea(rectanglePoints));
//                                System.out.println(Core.countNonZero(binarizedImageMat) / CommonUtils.countRectangleSurfaceArea(rectanglePoints));
//                                System.out.println(CommonUtils.countPaintedPoints(binarizedImageMat, rectanglePoints[0], rectanglePoints[1]));
//                                System.out.println(CommonUtils.countSurfaceAreaOfContour(contourizedImageMat));
//                                proportions.add(CommonUtils.countSurfaceAreaOfContour(contourizedImageMat) / CommonUtils.countRectangleSurfaceArea(rectanglePoints));
//                            }

                            if(showProportions.isSelected() && pointsToFingers != null) {
////                                System.out.println("Proporcja x/y: " + CommonUtils.countProportionsXtoY(rectanglePoints));
////                                proportions.add(CommonUtils.countProportionsXtoY(rectanglePoints));
//                                proportions.add(CommonUtils.countSurfaceAreaOfContour(contourizedImageMat) / CommonUtils.countRectangleSurfaceArea(rectanglePoints));
                                Set<Point> fingersKeys = pointsToFingers.keySet();
                                for(Point f : fingersKeys) {
                                    if(mapFingersToProportions.containsKey(pointsToFingers.get(f))) {
                                        mapFingersToProportions.get(pointsToFingers.get(f)).add(CommonUtils.countRectangleCircuit(rectanglePoints) / CommonUtils.countLengthOfLine(f, centroid));
                                    } else {
                                        mapFingersToProportions.put(pointsToFingers.get(f), new ArrayList<>());
                                        mapFingersToProportions.get(pointsToFingers.get(f)).add(CommonUtils.countRectangleCircuit(rectanglePoints) / CommonUtils.countLengthOfLine(f, centroid));
                                    }
                                }
                            }
                            contouredBufferedImage = matProcessor.processFinalBufferedImage(converter, contourizedImageMat, pointsToFingers);
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
