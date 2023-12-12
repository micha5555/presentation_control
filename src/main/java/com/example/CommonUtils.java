package com.example;

import com.example.converters.IConverter;
import com.example.converters.impl.Converter;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CommonUtils {

    private static IConverter converter = new Converter();

    public static ArrayList<File> listFilesForFolder(final File folder) {
        ArrayList<File> files = new ArrayList<>();
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                continue;
            }
            files.add(fileEntry);
        }
        return files;
    }

    public static MatOfPoint findConvexHullPoints(Mat input) {
        MatOfPoint contour = converter.convertMatToMatOfPointNonEmptyPoints(input);
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

    public static MatOfPoint findConvexHullPointsWithoutDuplicates(Mat input) {
        MatOfPoint contour = converter.convertMatToMatOfPointNonEmptyPoints(input);
        MatOfInt hull = new MatOfInt();
        Imgproc.convexHull(contour, hull);
        Point[] contourArray = contour.toArray();
//        Point[] hullPoints = new Point[hull.rows()];
        List<Point> hullPointsList = new ArrayList<>();
        List<Integer> hullContourIdxList = hull.toList();
        int max = hullContourIdxList.size();
        for (int i = 0; i < max; i++) {
            Point pointFromContour = contourArray[hullContourIdxList.get(i)];
            boolean skip = false;
            for(int j = 0; j < hullPointsList.size(); j++) {
                Point actualPoint = hullPointsList.get(j);
                if(actualPoint.x > pointFromContour.x - 15 && actualPoint.x < pointFromContour.x + 15 && actualPoint.y > pointFromContour.y - 15 && actualPoint.y < pointFromContour.y + 15) {
                    skip = true;
                    break;
                }
            }
            if(skip) {
                continue;
            }
            hullPointsList.add(pointFromContour);
//            hullPoints[i] = contourArray[hullContourIdxList.get(i)];
        }
        Point[] hullPoints = new Point[hullPointsList.size()];
        for(int i = 0; i < hullPointsList.size(); i++) {
            hullPoints[i] = hullPointsList.get(i);
        }
        return new MatOfPoint(hullPoints);
    }

    public static Mat convertBufferedImageToMat(BufferedImage bufferedImage) {
        Mat mat = new Mat(bufferedImage.getHeight(), bufferedImage.getWidth(), CvType.CV_8UC3);
        byte[] data = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
        mat.put(0, 0, data);
        return mat;
    }

//    public static Mat convertBufferedImageToMat(BufferedImage sourceImg) {
//
//        long millis = System.currentTimeMillis();
//
//        DataBuffer dataBuffer = sourceImg.getRaster().getDataBuffer();
//        byte[] imgPixels = null;
//        Mat imgMat = null;
//
//        int width = sourceImg.getWidth();
//        int height = sourceImg.getHeight();
//
//        if(dataBuffer instanceof DataBufferByte) {
//            imgPixels = ((DataBufferByte)dataBuffer).getData();
//        }
//
//        if(dataBuffer instanceof DataBufferInt) {
//
//            int byteSize = width * height;
//            imgPixels = new byte[byteSize*3];
//
//            int[] imgIntegerPixels = ((DataBufferInt)dataBuffer).getData();
//
//            for(int p = 0; p < byteSize; p++) {
//                imgPixels[p*3 + 0] = (byte) ((imgIntegerPixels[p] & 0x00FF0000) >> 16);
//                imgPixels[p*3 + 1] = (byte) ((imgIntegerPixels[p] & 0x0000FF00) >> 8);
//                imgPixels[p*3 + 2] = (byte) (imgIntegerPixels[p] & 0x000000FF);
//            }
//        }
//
//        if(imgPixels != null) {
//            imgMat = new Mat(height, width, CvType.CV_8UC3);
//            imgMat.put(0, 0, imgPixels);
//        }
//
//        System.out.println("matify exec millis: " + (System.currentTimeMillis() - millis));
//
//        return imgMat;
//    }

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

//    public static MatOfPoint matToMatOfPoint(Mat mat) {
//        MatOfPoint matOfPoint = new MatOfPoint();
//        MatOfPoint2f matOfPoint2f = new MatOfPoint2f();
//        mat.convertTo(matOfPoint2f, CvType.CV_32F);
//        matOfPoint2f.convertTo(matOfPoint, CvType.CV_32S);
//        return matOfPoint;
//    }

    public static Point findCentroid(MatOfPoint points) {
        if(points == null) {
            return null;
        }
//        Point[] rectanglePoints = CommonUtils.findBiggestRectangleOnHand(points);
//        System.out.println("Rec p " + rectanglePoints[0].x + " " + rectanglePoints[0].y);
//        System.out.println("Rec p2 " + rectanglePoints[1].x + " " + rectanglePoints[1].y);
//        return new Point(rectanglePoints[0].x + (rectanglePoints[1].x - rectanglePoints[0].x)/2, rectanglePoints[0].y + (rectanglePoints[1].y - rectanglePoints[0].y)*0.75);
//        if(points == null) {
//            return null;
//        }
        List<Point> pointsList = points.toList();
        double xSum = 0.0;
        double ySum = 0.0;
//        double maxY

        for(Point p : pointsList) {
            xSum += p.x;
            ySum += p.y;
        }

        return new Point(xSum/pointsList.size(), ySum/pointsList.size());
    }

    public static Point[] findBiggestRectangleOnHand(MatOfPoint contourizedImageMat) {
//        MatOfPoint convexHull = CommonUtils.findConvexHullPoints(contourizedImageMat);
        List<Point> convexHullPoints = contourizedImageMat.toList();
        Point leftTop = new Point(convexHullPoints.get(0).x, convexHullPoints.get(0).y); // (x, y)
        Point rightBot = new Point(convexHullPoints.get(0).x, convexHullPoints.get(0).y); // (x, y)
        for(Point p : convexHullPoints) {
            if(p.x < leftTop.x) {
                leftTop.x = p.x;
            }
            if(p.x > rightBot.x) {
                rightBot.x = p.x;
            }
            if(p.y < leftTop.y) {
                leftTop.y = p.y;
            }
            if(p.y > rightBot.y) {
                rightBot.y = p.y;
            }
        }

        return new Point[]{leftTop, rightBot};
    }

    public static double countProportionsXtoY(Point[] points) {
        return (points[1].x - points[0].x) / (points[1].y - points[0].y);
    }

    public static double countSurfaceAreaOfContour(Mat contourMat) {
        IConverter converter = new Converter();
        MatOfPoint contour = converter.convertMatToMatOfPointNonEmptyPoints(contourMat);
        double surfaceArea = Imgproc.contourArea(contour);
//        System.out.println("Contour surface area: " + surfaceArea);
        return surfaceArea;
    }

    public static double countRectangleSurfaceArea(Point[] points) {
        double surfaceArea = (points[1].x - points[0].x) * (points[1].y - points[0].y);
//        System.out.println("Rectangle surface area: " + surfaceArea);
        return surfaceArea;
    }

    public static double countRectangleCircuit(Point[] points) {
        double circuit = 2 * (points[1].x - points[0].x) + 2 * (points[1].y - points[0].y);
//        System.out.println("Rectangle circuit: " + circuit);
        return circuit;
    }

    public static int countPaintedPoints(Mat binaryImage, Point topLeft, Point bottomRight) {
        // Create a region of interest (ROI) based on the rectangle
        Rect roi = new Rect(topLeft, bottomRight);

        // Extract the ROI from the binary image
        Mat roiImage = new Mat(binaryImage, roi);

        // Count the non-zero pixels in the ROI
        int paintedPointsCount = (int) roiImage.total() - Core.countNonZero(roiImage);

        // Release the ROI image to avoid memory leaks
        roiImage.release();

        return paintedPointsCount;
    }

    public static double countLengthOfLine(Point p1, Point p2) {
        return Math.sqrt(Math.pow(p2.x - p1.x, 2) + Math.pow(p2.y - p1.y, 2));
    }
}