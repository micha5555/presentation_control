package com.example;

import java.awt.image.DataBufferByte;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import com.example.converters.IConverter;
import com.example.converters.impl.Converter;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import com.example.contourization.IContourization;
import com.example.contourization.impl.BiggestContourFinder;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

public class CommonUtils {

    private static IConverter converter = new Converter();
    public static ArrayList<File> listFilesForFolder(final File folder) {
        ArrayList<File> files = new ArrayList<File>();
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                continue;
            }
            files.add(fileEntry);
        }
        return files;
    }

//    public static BufferedImage convertBufferedImageToBinarizedBufferedImage(BufferedImage originalBufferedImage, Scalar minThreshold, Scalar maxThreshold) throws IOException{
//        Mat binarizedImageMat = convertBufferedImageToBinarizedMat(originalBufferedImage, minThreshold, maxThreshold);
//
//        return converter.convertMatToBufferedImage(binarizedImageMat);
//    }

    // moving to binarization package
//    public static Mat convertBufferedImageToBinarizedMat(BufferedImage originalBufferedImage, Scalar minThreshold, Scalar maxThreshold) {
//        Mat originalImageMat = CommonUtils.convertBufferedImageToMat(originalBufferedImage);
//        Mat binarizedImageMat = new Mat();
//        Core.inRange(originalImageMat, minThreshold, maxThreshold, binarizedImageMat);
//
//        return binarizedImageMat;
//    }

//    To do rozwalenia na drobniejsze metody
    public static BufferedImage convertMatToContourizedBufferedImage(Mat binarizedImageMat) throws IOException {
        IContourization ci = new BiggestContourFinder();
        Mat biggestContourMat = ci.findBiggestContour(binarizedImageMat);
        List<MatOfPoint> contourMat = new ArrayList<>();
        MatOfPoint convexHull = findConvexHullPoints(biggestContourMat);
        contourMat.add(convexHull);
        Point centroid = findCentroid(convexHull);
        List<Point> convexHullPoints = new ArrayList<>();
        int realPoints = 0;
        for(int i = 0; i < convexHull.toArray().length; i++) {
            Point currentPoint = convexHull.toArray()[i];
            if(currentPoint.y + 30 < centroid.y && !isPointToCloseToAnotherPoint(currentPoint, convexHullPoints)) {
                convexHullPoints.add(currentPoint);
                Imgproc.circle(biggestContourMat, currentPoint, 7, new Scalar(255, 0, 0), Imgproc.FILLED);
                realPoints++;
            }
        }
        Map<Point, FingerNames> pointsToFinger = nameFingerTips(convexHullPoints, centroid);
        List<Point> pointsToDraw = new ArrayList<>(pointsToFinger.keySet());
        for(Point p : pointsToDraw) {
            Imgproc.line(biggestContourMat, centroid, p, new Scalar(255, 0, 0), 3);
            Imgproc.putText(biggestContourMat, pointsToFinger.get(p).toString(), p, 1, 2, new Scalar(255, 0, 0));
        }
//        System.out.println(realPoints);
        Imgproc.circle(biggestContourMat, centroid, 10, new Scalar(255, 0, 0), Imgproc.FILLED);
        Imgproc.drawContours(biggestContourMat, contourMat, 0, new Scalar(255, 0, 0));

        return converter.convertMatToBufferedImage(biggestContourMat);
    }

    private static boolean isPointToCloseToAnotherPoint(Point currentPoint, List<Point> previousPoints) {
//        TODO: przemyśleć
        if(currentPoint == null || previousPoints == null) {
            return true;
        } else if(previousPoints.size() == 0) {
            return false;
        }
        double smallestDistance = 0.0;
        for(int i = 0; i < previousPoints.size(); i++) {
            Point pointFromList = previousPoints.get(i);
            double distanceBetweenPoints = countDistanceBetweenPoints(currentPoint, pointFromList);
            if(i == 0) {
                smallestDistance = distanceBetweenPoints;
                continue;
            }
            if(distanceBetweenPoints < smallestDistance) {
                smallestDistance = distanceBetweenPoints;
            }
        }
        return smallestDistance < StaticData.MINIMAL_DISTANCE_BETWEEN_CONVEX_HULL_POINTS;
    }

    private static double countDistanceBetweenPoints(Point a, Point b) {
        if(a == null || b == null) {
            return 0;
        }
        return Math.sqrt(Math.pow((b.x - a.x), 2) + Math.pow((b.y - a.y), 2));
    }

    private static Map<Point, FingerNames> nameFingerTips(List<Point> points, Point centroid) {
        Map<Point, FingerNames> pointToFinger = new HashMap<>();
        for(Point p : points) {
            // add logic when it is not finger or something
            double angle = countAngleBetweenPointAndLineWithOnlyY(p, centroid);
            if(angle >= 0 && angle <= 45) {
                pointToFinger.put(p, FingerNames.THUMB);
            } else if(angle > 45 && angle <= 80){
                pointToFinger.put(p, FingerNames.INDEX);
            } else if(angle > 80 && angle <= 100){
                pointToFinger.put(p, FingerNames.MIDDLE);
            } else if(angle > 100 && angle <= 130){
                pointToFinger.put(p, FingerNames.RING);
            } else if(angle > 130 && angle <= 170){
                pointToFinger.put(p, FingerNames.PINKY);
            }
            System.out.println(angle);
        }
        System.out.println("");
        return pointToFinger;
    }

    public static BufferedImage processContourizedBufferedImage(BufferedImage contourizedBufferedImage) throws IOException {
        Mat biggestContourMat = convertBufferedImageToMat(contourizedBufferedImage);

        List<MatOfPoint> contourMat = new ArrayList<>();
        MatOfPoint convexHull = CommonUtils.findConvexHullPoints(biggestContourMat);
        contourMat.add(convexHull);
        Point centroid = CommonUtils.findCentroid(convexHull);
        Imgproc.circle(biggestContourMat, centroid, 5, new Scalar(0, 0, 255), Imgproc.FILLED);
        Imgproc.drawContours(biggestContourMat, contourMat, 0, new Scalar(255, 0, 0));
        return converter.convertMatToBufferedImage(biggestContourMat);
    }

    // moving to binarization package
//    public static BufferedImage convertMatToBufferedImage(Mat mat) throws IOException{
//        //Encoding the image
//        MatOfByte matOfByte = new MatOfByte();
//        Imgcodecs.imencode(".jpg", mat, matOfByte);
//        //Storing the encoded Mat in a byte array
//        byte[] byteArray = matOfByte.toArray();
//        //Preparing the Buffered Image
//        InputStream in = new ByteArrayInputStream(byteArray);
//        BufferedImage bufImage = ImageIO.read(in);
//        return bufImage;
//    }

    public static Mat convertBufferedImageToMat(BufferedImage bufferedImage) {
        Mat mat = new Mat(bufferedImage.getHeight(), bufferedImage.getWidth(), CvType.CV_8UC3);
        byte[] data = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
        mat.put(0, 0, data);
        return mat;
    }

    public static Image bufferedImageToFXImage(BufferedImage image) {
        WritableImage wr = null;
        if (image != null) {
            wr = new WritableImage(image.getWidth(), image.getHeight());
            PixelWriter pw = wr.getPixelWriter();
            for (int x = 0; x < image.getWidth(); x++) {
                for (int y = 0; y < image.getHeight(); y++) {
                    pw.setArgb(x, y, image.getRGB(x, y));
                }
            }
        }
    
        return new ImageView(wr).getImage();
    }

    public static MatOfPoint matToMatOfPoint(Mat mat) {
        MatOfPoint matOfPoint = new MatOfPoint();
        MatOfPoint2f matOfPoint2f = new MatOfPoint2f();
        mat.convertTo(matOfPoint2f, CvType.CV_32F);
        matOfPoint2f.convertTo(matOfPoint, CvType.CV_32S);
        return matOfPoint;
    }

    public static MatOfPoint convertMatToMatOfPointNonEmptyPoints(Mat input) {
        MatOfPoint output = new MatOfPoint();
        List<Point> points = new ArrayList<Point>();
        for (int row = 0; row < input.rows(); row++) {
            for (int col = 0; col < input.cols(); col++) {
                double value = input.get(row, col)[0]; // Get the value at (row, col)
                if (value != 0) {
                    points.add(new Point(col, row));
                }
            }
        }
        output.fromList(points);
        return output;
    }

    public static Mat findConvexHull(Mat input) {
        List<MatOfPoint> hullList = new ArrayList<>();
        hullList.add(findConvexHullPoints(input));
        Mat drawing = Mat.zeros(input.size(), CvType.CV_8UC3);
        Scalar color = new Scalar(255, 0, 0);
        Imgproc.drawContours(drawing, hullList, 0, color );
        return drawing;
    }

    public static MatOfPoint findConvexHullPoints(Mat input) {
        MatOfPoint contour = convertMatToMatOfPointNonEmptyPoints(input);
        MatOfInt hull = new MatOfInt();
        Imgproc.convexHull(contour, hull);
        Point[] contourArray = contour.toArray();
        Point[] hullPoints = new Point[hull.rows()];
        List<Integer> hullContourIdxList = hull.toList();
        for (int i = 0; i < hullContourIdxList.size(); i++) {
            hullPoints[i] = contourArray[hullContourIdxList.get(i)];
        }

        return new MatOfPoint(hullPoints);
    }

    public static Point findCentroid(MatOfPoint points) {
        if(points == null) {
            return null;
        }
        List<Point> pointsList = points.toList();
        double xSum = 0.0;
        double ySum = 0.0;

        for(Point p : pointsList) {
            xSum += p.x;
            ySum += p.y;
        }

        return new Point(xSum/pointsList.size(), ySum/pointsList.size());
    }

    public static double countAngleBetweenPointAndLineWithOnlyY(Point p, Point centroid) {
        boolean pointOnRightSide = p.x > centroid.x;
        Point firstPoint = p.x > centroid.x ? centroid : p;
        Point secondPoint = p.x > centroid.x ? p : centroid;

        double a = (secondPoint.y - firstPoint.y) / (secondPoint.x - firstPoint.x);

        double angleInRadians = Math.atan(Math.abs(a));
        double angleInDegrees = Math.toDegrees(angleInRadians);
        if(pointOnRightSide) {
            angleInDegrees = 180 - angleInDegrees;
        }
        return angleInDegrees;
    }
}
