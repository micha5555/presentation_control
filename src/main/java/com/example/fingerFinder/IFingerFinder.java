package com.example.fingerFinder;

import com.example.enums.FingerNames;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;

import java.util.Map;

public interface IFingerFinder {
    Map<Point, FingerNames> retrieveFingersFromContour(MatOfPoint convexHull, Point centroid);
}
