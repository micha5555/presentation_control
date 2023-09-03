package com.example;

import java.awt.image.DataBufferByte;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfInt4;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

public class CommonUtils {
    
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

    // public static Mat findFingerTips(Mat inputMat) {
    //     MatOfInt hull = new MatOfInt();
    //     List<Point> contourPoints = new ArrayList<>(inputMat.toList());

    //     Imgproc.convexHull(new MatOfPoint(contourPoints.toArray(new Point[0])), hull);
    //     List<Point> hullPoints = new ArrayList<>();
    //     for (int i = 0; i < hull.rows(); i++) {
    //         int index = (int) hull.get(i, 0)[0];
    //         Point point = new MatOfPoint(contourPoints.toArray(new Point[0])).toList().get(index);
    //         hullPoints.add(point);
    //     }
    //     System.out.println(hullPoints);
    //     return inputMat;
    // }
    
    public static BufferedImage convertMatToBufferedImage(Mat mat) throws IOException{
        //Encoding the image
        MatOfByte matOfByte = new MatOfByte();
        Imgcodecs.imencode(".jpg", mat, matOfByte);
        //Storing the encoded Mat in a byte array
        byte[] byteArray = matOfByte.toArray();
        //Preparing the Buffered Image
        InputStream in = new ByteArrayInputStream(byteArray);
        BufferedImage bufImage = ImageIO.read(in);
        return bufImage;
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
        // MatOfPoint matOfPoint = new MatOfPoint();
        // MatOfPoint2f matOfPoint2f = new MatOfPoint2f();
        // mat.convertTo(matOfPoint2f, CvType.CV_32F);
        // matOfPoint2f.convertTo(matOfPoint, CvType.CV_32S);
        // return matOfPoint;
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
}
