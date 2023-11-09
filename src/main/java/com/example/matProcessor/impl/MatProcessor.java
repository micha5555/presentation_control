package com.example.matProcessor.impl;

import com.example.CommonUtils;
import com.example.FingerNames;
import com.example.converters.IConverter;
import com.example.matProcessor.IMatProcessor;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MatProcessor implements IMatProcessor {
    @Override
    public BufferedImage processFinalBufferedImage(IConverter converter, Mat biggestContourMat, Map<Point, FingerNames> pointsToFingers) throws IOException {
        MatOfPoint convexHull = CommonUtils.findConvexHullPoints(biggestContourMat);
        List<MatOfPoint> contourMat = new ArrayList<>();
        contourMat.add(convexHull);
        List<Point> pointsToDraw = new ArrayList<>(pointsToFingers.keySet());
        Point centroid = CommonUtils.findCentroid(convexHull);
        for(Point p : pointsToDraw) {
            Imgproc.line(biggestContourMat, centroid, p, new Scalar(255, 0, 0), 3);
            Imgproc.putText(biggestContourMat, pointsToFingers.get(p).toString(), p, 1, 2, new Scalar(255, 0, 0));
        }
//        System.out.println(realPoints);
        Imgproc.circle(biggestContourMat, centroid, 10, new Scalar(255, 0, 0), Imgproc.FILLED);
        Imgproc.drawContours(biggestContourMat, contourMat, 0, new Scalar(255, 0, 0));

        return converter.convertMatToBufferedImage(biggestContourMat);
    }
}
