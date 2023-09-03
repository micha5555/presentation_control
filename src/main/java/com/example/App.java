package com.example;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import com.example.contourization.ContourizationInterface;
import com.example.contourization.impl.BiggestContourFinder;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import nu.pattern.OpenCV;

/**
 * Hello world!
 *
 */
public class App extends Application
{
    static Scalar min = new Scalar(100, 0, 0);//BGR-A
    static Scalar max= new Scalar(255, 250, 70);//BGR-A
    private static BufferedImage originalBufferedImage = null;
    private static BufferedImage binarizedBufferedImage = null;
    private static BufferedImage skeletonizedBufferedImage = null;
    private static BufferedImage contouredBufferedImage = null;

    ImageView originalImage;
    ImageView binarizedImage;
    ImageView contourImage;

    OpenCVFrameGrabber camera;
    Java2DFrameConverter converterBuffered;
    OpenCVFrameConverter.ToMat converter;
    // to było git dla palm1
    // static Scalar min = new Scalar(150, 0, 0);//BGR-A
    // static Scalar max= new Scalar(255, 250, 110);//BGR-A
    public static void main( String[] args ) throws IOException
    {
        // nu.pattern.OpenCV.loadShared();
        // System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        OpenCV.loadLocally();
        // Imgcodecs imageCodecs = new Imgcodecs();
        // for(File f : CommonUtils.listFilesForFolder(new File("src/main/java/com/example/resources"))) {
        //     String originalImageFile = f.getAbsolutePath();
        //     Mat originalImage = imageCodecs.imread(originalImageFile, Imgcodecs.IMREAD_UNCHANGED);
        //     Mat binarizedImage = new Mat();

        //     Core.inRange(originalImage, min, max, binarizedImage);

        //     String binarizedImageFile = "src/main/java/com/example/outputs/binarized/" + f.getName();

        //     imageCodecs.imwrite(binarizedImageFile, binarizedImage);
        //     System.out.println(binarizedImage.channels());

        //     String contourImageFile = "src/main/java/com/example/outputs/contours/" + f.getName();
        //     ContourizationInterface ci = new BiggestContourFinder();
        //     Mat biggestContour = ci.findBiggestContour(binarizedImage);
        //     System.out.println(biggestContour.channels());
        //     imageCodecs.imwrite(contourImageFile, biggestContour);

        //     originalBufferedImage = CommonUtils.convertMatToBufferedImage(originalImage);
        //     binarizedBufferedImage = CommonUtils.convertMatToBufferedImage(binarizedImage);
        //     contouredBufferedImage = CommonUtils.convertMatToBufferedImage(biggestContour);
        // }
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Hello World!");
        // Button btn = new Button();
        // btn.setText("Say 'Hello World'");
        // btn.setOnAction(new EventHandler<ActionEvent>() {
 
        //     @Override
        //     public void handle(ActionEvent event) {
        //         System.out.println("Hello World!");
        //     }
        // });

        originalImage = JavaFXTools.prepareImageView(originalBufferedImage, 10, 25, 640, 288);
        binarizedImage = JavaFXTools.prepareImageView(binarizedBufferedImage, 660, 25, 640, 288);
        contourImage = JavaFXTools.prepareImageView(contouredBufferedImage, 1310, 25, 640, 288);

        Group root = new Group(originalImage, binarizedImage, contourImage);  
        
        // StackPane root = new StackPane();
        // root.getChildren().add(btn);
        primaryStage.setScene(new Scene(root, 2000, 800));
        primaryStage.show();

        camera = new OpenCVFrameGrabber(0);
        converter = new OpenCVFrameConverter.ToMat();
        converterBuffered = new Java2DFrameConverter();
        // Frame capturedFrame = null;
        org.bytedeco.opencv.opencv_core.Mat matImage = null;
        camera.start();

        captureAndDisplayFrames();
        // System.out.println("aaaaaa");
        // while ((capturedFrame = camera.grab()) != null) {
        // //     System.out.println("bbbb");
        // //     matImage = converter.convertToMat(capturedFrame);
        // //     System.out.println("cccccc");
        // //     BufferedImage bufferedImage = converterBuffered.convert(capturedFrame);
        // //     System.out.println("dddd");
        // //     originalImage.setImage(CommonUtils.bufferedImageToFXImage(bufferedImage));
        // //     System.out.println("eeeee");
        // //     // Graphics g = imageView.getGraphics(); //getting the Graphics Class of the JPanel named as imageView
        // //     // g.drawImage(bufferedImage, 10,10, bufferedImage.getWidth(), bufferedImage.getHeight(),imageView); //this imageView is a JPanel component

        // }
        // int a = 0;
        // while(a < 7000000) {
        //     System.out.println(a);
        //     a++;
        // }
        // camera.stop();
    }

    private void captureAndDisplayFrames() {
        new Thread(() -> {
            while (!Thread.interrupted()) {
                try {
                    Frame frame = camera.grab(); // Capture a frame from the webcam
                    if (frame != null) {
                        BufferedImage originalBufferedImage = converterBuffered.convert(frame);
                        Mat originalImageMat = CommonUtils.convertBufferedImageToMat(originalBufferedImage);
                        Mat binarizedImageMat = new Mat();
                        Core.inRange(originalImageMat, min, max, binarizedImageMat);
                        ContourizationInterface ci = new BiggestContourFinder();
                        Mat biggestContourMat = ci.findBiggestContour(binarizedImageMat);
                        BufferedImage binarizedBufferedImage = CommonUtils.convertMatToBufferedImage(binarizedImageMat);
                        BufferedImage contouredBufferedImage = CommonUtils.convertMatToBufferedImage(CommonUtils.findConvexHull(biggestContourMat));
                        // CommonUtils.findFingerTips(biggestContourMat);
                        // Update the JavaFX ImageView with the captured frame
                        List<MatOfPoint> contourMat = new ArrayList<>();
                        MatOfPoint convexHull = CommonUtils.findConvexHullPoints(biggestContourMat);
                        contourMat.add(convexHull);
                        Point centroid = CommonUtils.findCentroid(convexHull);
                        Imgproc.circle(biggestContourMat, centroid, 5, new Scalar(0, 0, 255), Imgproc.FILLED);
                        Imgproc.drawContours(biggestContourMat, contourMat, 0, new Scalar(255, 0, 0));
                        contouredBufferedImage = CommonUtils.convertMatToBufferedImage(biggestContourMat);
                        originalImage.setImage(CommonUtils.bufferedImageToFXImage(originalBufferedImage));
                        binarizedImage.setImage(CommonUtils.bufferedImageToFXImage(binarizedBufferedImage));
                        contourImage.setImage(CommonUtils.bufferedImageToFXImage(contouredBufferedImage));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
