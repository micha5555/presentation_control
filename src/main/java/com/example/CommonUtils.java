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
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
}