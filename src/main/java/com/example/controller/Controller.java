package com.example.controller;

import com.example.*;
import com.example.binarization.IBinarizator;
import com.example.binarization.impl.HSVBasedBinarizator;
import com.example.binarization.impl.RGBBasedBinarizator;
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
import com.example.model.Model;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import lombok.Getter;
import lombok.Setter;
import nu.pattern.OpenCV;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.*;

public class Controller implements Initializable {

    @FXML
    private Text actualMaxHeader;

    @FXML
    private Text actualMaxThreshold;

    @FXML
    private Text actualMinHeader;

    @FXML
    private Text actualMinThreshold;

    @FXML
    @Getter
    @Setter
    private ImageView binarizedImageView;

    @FXML
    private CheckBox drawConvexHullCheckbox;

    @FXML
    private CheckBox drawFingersConnectionsCheckbox;

    @FXML
    private CheckBox drawFingersNamesCheckbox;

    @FXML
    private CheckBox drawSmallestRectangleCheckbox;

    @FXML
    private CheckBox enableClickingKeysCheckbox;

    @FXML
    private CheckBox enableFindingFingersCheckBox;

    @FXML
    @Getter
    @Setter
    private ImageView finalImageView;

    @FXML
    @Setter
    private Text lastClickedKeysText;

    @FXML
    private Slider maxFirstSlider;

    @FXML
    private Text maxFirstText;

    @FXML
    private Slider maxSecondSlider;

    @FXML
    private Text maxSecondText;

    @FXML
    private Slider maxThirdSlider;

    @FXML
    private Text maxThirdText;

    @FXML
    private Slider minFirstSlider;

    @FXML
    private Slider minSecondSlider;

    @FXML
    private Slider minThirdSlider;

    @FXML
    private Text minimalFirstText;

    @FXML
    private Text minimalSecondText;

    @FXML
    private Text minimalThirdText;

    @FXML
    @Getter
    @Setter
    private ImageView originalImageView;

    @FXML
    private Button switchBetweenColorSpacesButton;

    private Model model;

    private OpenCVFrameGrabber camera;

    private OpenCVFrameConverter.ToMat opencvConverter;

    private Java2DFrameConverter converterBuffered;

    private IBinarizator binarizator;
    private IConverter converter;
    private IContourizer contourizer;
    private IFingerFinder fingerFinder;
    private IMatProcessor matProcessor;
    private IKeyClicker keyClicker;

//    public Controller(Model model) {
//        this.model = model;
//    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        model = new Model();

        switchBetweenColorSpacesButton.setText("Switch to RGB");
        switchBetweenColorSpacesButton.setOnAction(switchBetweenColorSpaces);

//        initialize model components
        binarizator = new HSVBasedBinarizator();
        converter = new Converter();
        contourizer = new Contourizer();
        fingerFinder = new FingerFinder(converter);
        matProcessor = new MatProcessor();
        keyClicker = new KeyClicker(new FingersToKeyConverter());

//        adding listeners to view components
        addListenerToSlider(minFirstSlider, true);
        addListenerToSlider(minSecondSlider, true);
        addListenerToSlider(minThirdSlider, true);
        addListenerToSlider(maxFirstSlider, false);
        addListenerToSlider(maxSecondSlider, false);
        addListenerToSlider(maxThirdSlider, false);

        Scalar minThresholdScalar = model.getMinThresholdScalar();
        Scalar maxThresholdScalar = model.getMaxThresholdScalar();
        minFirstSlider.setValue(minThresholdScalar.val[0]);
        minSecondSlider.setValue(minThresholdScalar.val[1]);
        minThirdSlider.setValue(minThresholdScalar.val[2]);
        maxFirstSlider.setValue(maxThresholdScalar.val[0]);
        maxSecondSlider.setValue(maxThresholdScalar.val[1]);
        maxThirdSlider.setValue(maxThresholdScalar.val[2]);

        drawConvexHullCheckbox.fire();
        drawFingersConnectionsCheckbox.fire();
        drawFingersNamesCheckbox.fire();

        OpenCV.loadLocally();

        try {
            setupCamera();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private void addListenerToSlider(Slider slider, boolean isMinThreshold) {
        slider.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<?extends Number> observable, Number oldValue, Number newValue){
                if(isMinThreshold) {
                    int minFirstSliderValue = (int)minFirstSlider.getValue();
                    int minSecondSliderValue = (int)minSecondSlider.getValue();
                    int minThirdSliderValue = (int)minThirdSlider.getValue();
                    model.changeMinThresholdScalar(minFirstSliderValue, minSecondSliderValue, minThirdSliderValue);
//                    minThresholdScalar  = new Scalar((int)minBlueSlider.getValue(), (int)minGreenSlider.getValue(), (int)minRedSlider.getValue());
                    actualMinThreshold.setText(JavaFXTools.formatCurrentThreshold(minFirstSliderValue, minSecondSliderValue, minThirdSliderValue));
                } else {
                    int maxFirstSliderValue = (int)maxFirstSlider.getValue();
                    int maxSecondSliderValue = (int)maxSecondSlider.getValue();
                    int maxThirdSliderValue = (int)maxThirdSlider.getValue();
                    model.changeMaxThresholdScalar(maxFirstSliderValue, maxSecondSliderValue, maxThirdSliderValue);
//                    maxThresholdScalar  = new Scalar((int)maxBlueSlider.getValue(), (int)maxGreenSlider.getValue(), (int)maxRedSlider.getValue());
                    actualMaxThreshold.setText(JavaFXTools.formatCurrentThreshold(maxFirstSliderValue, maxSecondSliderValue, maxThirdSliderValue));
                }
            }
        });
    }

    EventHandler<ActionEvent> switchBetweenColorSpaces = event -> {
        switch(model.getCurrentColorSpace()) {
            case HSV:
                this.binarizator = new RGBBasedBinarizator();
                model.setCurrentColorSpace(ColorSpaces.RGB);
                model.changeMinThresholdScalar(StaticData.MIN_RED_SLIDER, StaticData.MIN_GREEN_SLIDER, StaticData.MIN_BLUE_SLIDER);
                switchBetweenColorSpacesButton.setText("Switch to HSV");
                actualMinHeader.setText("Actual min threshold(R,G,B): ");
                actualMaxHeader.setText("Actual max threshold(R,G,B): ");
                minFirstSlider.setMax(255);
                minSecondSlider.setMax(255);
                minThirdSlider.setMax(255);
                maxFirstSlider.setMax(255);
                maxSecondSlider.setMax(255);
                maxThirdSlider.setMax(255);
                minFirstSlider.setValue(StaticData.MIN_RED_SLIDER);
                minSecondSlider.setValue(StaticData.MIN_GREEN_SLIDER);
                minThirdSlider.setValue(StaticData.MIN_BLUE_SLIDER);
                maxFirstSlider.setValue(StaticData.MAX_RED_SLIDER);
                maxSecondSlider.setValue(StaticData.MAX_GREEN_SLIDER);
                maxThirdSlider.setValue(StaticData.MAX_BLUE_SLIDER);
                minimalFirstText.setText("Minimal red");
                minimalSecondText.setText("Minimal green");
                minimalThirdText.setText("Minimal blue");
                maxFirstText.setText("Maximal red");
                maxSecondText.setText("Maximal green");
                maxThirdText.setText("Maximal blue");
                break;
            case RGB:
                this.binarizator = new HSVBasedBinarizator();
                model.setCurrentColorSpace(ColorSpaces.HSV);
                model.changeMinThresholdScalar(StaticData.MIN_HUE_SLIDER, StaticData.MIN_SATURATION_SLIDER, StaticData.MIN_VALUE_SLIDER);
                switchBetweenColorSpacesButton.setText("Switch to RGB");
                actualMinHeader.setText("Actual min threshold(H,S,V): ");
                actualMaxHeader.setText("Actual max threshold(H,S,V): ");
                minFirstSlider.setMax(360);
                minSecondSlider.setMax(100);
                minThirdSlider.setMax(100);
                maxFirstSlider.setMax(360);
                maxSecondSlider.setMax(100);
                maxThirdSlider.setMax(100);
                minFirstSlider.setValue(StaticData.MIN_HUE_SLIDER);
                minSecondSlider.setValue(StaticData.MIN_SATURATION_SLIDER);
                minThirdSlider.setValue(StaticData.MIN_VALUE_SLIDER);
                maxFirstSlider.setValue(StaticData.MAX_HUE_SLIDER);
                maxSecondSlider.setValue(StaticData.MAX_SATURATION_SLIDER);
                maxThirdSlider.setValue(StaticData.MAX_VALUE_SLIDER);
                minimalFirstText.setText("Minimal hue");
                minimalSecondText.setText("Minimal saturation");
                minimalThirdText.setText("Minimal value");
                maxFirstText.setText("Maximal hue");
                maxSecondText.setText("Maximal saturation");
                maxThirdText.setText("Maximal value");
                break;
        }
    };

    private void setupCamera() throws Exception {
        camera = new OpenCVFrameGrabber(0);
        opencvConverter = new OpenCVFrameConverter.ToMat();
        converterBuffered = new Java2DFrameConverter();
//        org.bytedeco.opencv.opencv_core.Mat matImage = null;
        camera.start();

        captureAndDisplayFrames();
    }

    private void captureAndDisplayFrames() {
        new Thread(() -> {
            int delay = 0;

            while (!Thread.interrupted()) {
                try {
                    Frame frame = camera.grab(); // Capture a frame from the webcam 640x480

                    if (frame != null) {
                        BufferedImage originalBufferedImage = converterBuffered.convert(frame);

                        Mat binarizedImageMat = null;
                        Mat contourizedImageMat = null;
                        BufferedImage binarizedBufferedImage = null;
                        BufferedImage contouredBufferedImage = null;
                        Map<Point, FingerNames> pointsToFingers = null;
                        if(delay == 0){
                            binarizedImageMat = binarizator.convertBufferedImageToBinarizedMat(originalBufferedImage, model.getMinThresholdScalar(), model.getMaxThresholdScalar());
                            binarizedBufferedImage = converter.convertMatToBufferedImage(binarizedImageMat);
                            binarizedImageView.setImage(CommonUtils.bufferedImageToFXImage(binarizedBufferedImage));

                            if(enableFindingFingersCheckBox.isSelected()) {
                                contourizedImageMat = contourizer.findBiggestContour(binarizedImageMat);

                                MatOfPoint convexHull = CommonUtils.findConvexHullPoints(contourizedImageMat);
                                List<MatOfPoint> contourMat = new ArrayList<>();
                                contourMat.add(convexHull);
                                if(drawConvexHullCheckbox.isSelected()) {
                                    Imgproc.drawContours(contourizedImageMat, contourMat, 0, new Scalar(255, 0, 0));
                                }

                                Point centroid = CommonUtils.findCentroid(convexHull);

                                Point[] rectanglePoints = CommonUtils.findBiggestRectangleOnHand(convexHull);

                                int paintedPointsAboveCentroid = CommonUtils.countPaintedPoints(binarizedImageMat, rectanglePoints[0], new Point(rectanglePoints[1].x, centroid.y));
                                Point[] rectangleAboveCentroidPoints = new Point[2];
                                rectangleAboveCentroidPoints[0] = new Point(rectanglePoints[0].x, rectanglePoints[0].y);
                                rectangleAboveCentroidPoints[1] = new Point(rectanglePoints[1].x, centroid.y);
                                double rectangleSurfaceArea = CommonUtils.countRectangleSurfaceArea(rectanglePoints);
                                if(paintedPointsAboveCentroid / CommonUtils.countRectangleSurfaceArea(rectangleAboveCentroidPoints) > 0.35 && rectangleSurfaceArea / contourizedImageMat.total() > 0.05) {
                                    pointsToFingers = fingerFinder.retrieveFingersFromContour(convexHull);
                                    for(Point p : pointsToFingers.keySet()) {
                                        Imgproc.circle(contourizedImageMat, p, 7, new Scalar(255, 0, 0), Imgproc.FILLED);
                                    }
                                    if(drawFingersConnectionsCheckbox.isSelected()) {
                                        List<Point> pointsToDraw = new ArrayList<>(pointsToFingers.keySet());
                                        for(Point p : pointsToDraw) {
                                            Imgproc.line(contourizedImageMat, centroid, p, new Scalar(255, 0, 0), 3);
//                                            if() {
                                                Imgproc.putText(contourizedImageMat, pointsToFingers.get(p).toString(), p, 1, 2, new Scalar(255, 0, 0));
//                                            }
                                        }
                                    }

                                    if(enableClickingKeysCheckbox.isSelected()) {
                                        String clickedKeys = keyClicker.clickKeyBasedOnFingers(pointsToFingers);
                                        if(clickedKeys != null) {
                                            lastClickedKeysText.setText(clickedKeys);
                                        }
                                    }
                                } else {
                                    pointsToFingers = new HashMap<>();
                                }

                                if(drawSmallestRectangleCheckbox.isSelected()) {
                                    Imgproc.rectangle (
                                            contourizedImageMat,                    //Matrix obj of the image
                                            rectanglePoints[0],        //p1
                                            rectanglePoints[1],       //p2
                                            new Scalar(255, 255, 255),     //Scalar object for color
                                            5                          //Thickness of the line
                                    );
                                }

                                contouredBufferedImage = matProcessor.processFinalBufferedImage(converter, contourizedImageMat, pointsToFingers);
                                finalImageView.setImage(CommonUtils.bufferedImageToFXImage(contouredBufferedImage));
                            }

                        }
                        originalImageView.setImage(CommonUtils.bufferedImageToFXImage(originalBufferedImage));
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
