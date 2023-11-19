package com.example.fingerFinder.impl;

import com.example.CommonUtils;
import com.example.FingerNames;
import com.example.StaticData;
import com.example.converters.IConverter;
import com.example.fingerFinder.IFingerFinder;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.*;

public class FingerFinder implements IFingerFinder {
    private IConverter converter;

    public FingerFinder(IConverter converter) {
        this.converter = converter;
    }

    @Override
    public Map<Point, FingerNames> retrieveFingersFromContour(Mat contourMat) {
//        List<MatOfPoint> contourMatOfPoint = new ArrayList<>();
        MatOfPoint convexHull = CommonUtils.findConvexHullPoints(contourMat);
//        contourMatOfPoint.add(convexHull);
        Point centroid = CommonUtils.findCentroid(convexHull);
//        Point centroid = CommonUtils.findCentroid(converter.convertMatToMatOfPointNonEmptyPoints(contourMat));
        List<Point> convexHullPoints = new ArrayList<>();
        int realPoints = 0;
        for(int i = 0; i < convexHull.toArray().length; i++) {
            Point currentPoint = convexHull.toArray()[i];
            if(currentPoint.y < centroid.y && !isPointToCloseToAnotherPoint(currentPoint, convexHullPoints)) {
                convexHullPoints.add(currentPoint);
                Imgproc.circle(contourMat, currentPoint, 7, new Scalar(255, 0, 0), Imgproc.FILLED);
                realPoints++;
            }
        }
        Map<Point, FingerNames> pointsToFinger = nameFingerTips(convexHullPoints, centroid);
        return pointsToFinger;
    }

    private static Map<Point, FingerNames> nameFingerTips(List<Point> points, Point centroid) {
        Map<Point, FingerNames> pointToFinger = new HashMap<>();
        for(Point p : points) {
            // add logic when it is not finger or something
            Collection<FingerNames> addedFingers = pointToFinger.values();
            double angle = countAngleBetweenPointAndLineWithOnlyY(p, centroid);
            if(!addedFingers.contains(FingerNames.THUMB) && angle >= StaticData.MINIMAL_THUMB_ANGLE && angle <= StaticData.MAXIMAL_THUMB_ANGLE) {
                pointToFinger.put(p, FingerNames.THUMB);
            } else if(!addedFingers.contains(FingerNames.INDEX) && angle > StaticData.MINIMAL_INDEX_ANGLE && angle <= StaticData.MAXIMAL_INDEX_ANGLE){
                pointToFinger.put(p, FingerNames.INDEX);
            } else if(!addedFingers.contains(FingerNames.MIDDLE) && angle > StaticData.MINIMAL_MIDDLE_ANGLE && angle <= StaticData.MAXIMAL_MIDDLE_ANGLE){
                pointToFinger.put(p, FingerNames.MIDDLE);
            } else if(!addedFingers.contains(FingerNames.RING) && angle > StaticData.MINIMAL_RING_ANGLE && angle <= StaticData.MAXIMAL_RING_ANGLE) {
                pointToFinger.put(p, FingerNames.RING);
            } else if(!addedFingers.contains(FingerNames.PINKY) && angle > StaticData.MINIMAL_PINKY_ANGLE && angle <= StaticData.MAXIMAL_PINKY_ANGLE){
                pointToFinger.put(p, FingerNames.PINKY);
            }
//            System.out.println(angle);
        }
//        System.out.println("");
        return pointToFinger;
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
}
