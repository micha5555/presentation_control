package com.example.solution.impl;

import com.example.CommonUtils;
import com.example.clicker.IKeyClicker;
import com.example.contourizer.IContourizer;
import com.example.enums.FingerNames;
import com.example.enums.MatTypes;
import com.example.fingerFinder.IFingerFinder;
import com.example.solution.ISolution;
import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

public class ConvexHullSolution implements ISolution {

    private IContourizer contourizer;

    private IFingerFinder fingerFinder;

    private IKeyClicker keyClicker;

    private boolean findingGesturesEnabled = false;

    private boolean clickingKeysEnabled = false;

    private Map<MatTypes, Mat> mats;

    private String lastClickedKeys = null;

    private MatOfPoint convexHull = null;

    private Point centroid = null;

    private Point[] smallestRectanglePoints = null;

    private Point[] rectangleAboveCentroidPoints = null;

    private Map<Point, FingerNames> pointsToFingers = null;

    public ConvexHullSolution(IContourizer contourizer, IFingerFinder fingerFinder, IKeyClicker keyClicker) {
        this.mats = new HashMap<>();
        this.contourizer = contourizer;
        this.fingerFinder = fingerFinder;
        this.keyClicker = keyClicker;
    }

    @Override
    public void execute() {
//        System.out.println("Thread in ConvexHullSolution: " + Thread.currentThread().getId());
        if(mats.isEmpty()) {
            throw new RuntimeException("Mats not initialized");
        }
        if(!findingGesturesEnabled) {
            return;
        }
        findContours();
        findConvexHull();
        findCentroid();
        findSmallestRectangle();
        findRectangleAboveCentroid();
        findFingers();
        if(clickingKeysEnabled) {
            String clickedKeys = keyClicker.clickKeyBasedOnFingers(pointsToFingers);
            if(clickedKeys != null) {
                lastClickedKeys = clickedKeys;
            }
        }
//        System.out.println("end of execute");
    }

    @Override
    public Map<MatTypes, Mat> getMats() {
        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return mats;
    }

    @Override
    public void setInitialMats(Mat originalMat, Mat binaryMat) {
        mats = new HashMap<>();
        mats.put(MatTypes.ORIGINAL_MAT, originalMat.clone());
        mats.put(MatTypes.BINARIZED_MAT, binaryMat.clone());
        mats.put(MatTypes.BINARIZED_MAT_WITHOUT_EMPTY_SPACES_AND_SMALL_OBJECTS, contourizer.findBiggestContourAndFill(binaryMat.clone()));
    }

    @Override
    public String getLastClickedKeys() {
        return lastClickedKeys;
    }

    @Override
    public void enableClickingKeys() {
        clickingKeysEnabled = true;
    }

    @Override
    public void disableClickingKeys() {
        clickingKeysEnabled = false;
    }

    @Override
    public void enableFindingGestures() {
        findingGesturesEnabled = true;
    }

    @Override
    public void disableFindingGestures() {
        findingGesturesEnabled = false;
    }

    @Override
    public MatOfPoint getConvexHull() {
        return convexHull;
    }

    @Override
    public Point[] getSmallestRectanglePoints() {
        return smallestRectanglePoints;
    }

    @Override
    public Map<Point, FingerNames> getPointsToFingers() {
        return pointsToFingers;
    }

    private void findContours() {
        Mat contourizedImageMat = contourizer.findBiggestContourAndNotFill(mats.get(MatTypes.BINARIZED_MAT));
        mats.put(MatTypes.CONTOURIZED_MAT_NOT_FILLED, contourizedImageMat);
    }

    private void findConvexHull() {
        convexHull = CommonUtils.findConvexHullPoints(mats.get(MatTypes.CONTOURIZED_MAT_NOT_FILLED));
        mats.put(MatTypes.CONVEX_HULL_MAT, convexHull);
    }

    private void findCentroid() {
        centroid = CommonUtils.findCentroid(convexHull);
    }

    private void findSmallestRectangle() {
        smallestRectanglePoints = CommonUtils.findBiggestRectangleOnHand(convexHull);
    }

    private void findRectangleAboveCentroid() {
        rectangleAboveCentroidPoints = new Point[2];
        rectangleAboveCentroidPoints[0] = new Point(smallestRectanglePoints[0].x, smallestRectanglePoints[0].y);
        rectangleAboveCentroidPoints[1] = new Point(smallestRectanglePoints[1].x, centroid.y);
    }

    private void findFingers() {
        pointsToFingers = new HashMap<>();
        Mat binarizedMatWithoutSmallObjects = mats.get(MatTypes.BINARIZED_MAT_WITHOUT_EMPTY_SPACES_AND_SMALL_OBJECTS);
        int paintedPointsAboveCentroid = CommonUtils.countPaintedPoints(binarizedMatWithoutSmallObjects, smallestRectanglePoints[0], new Point(smallestRectanglePoints[1].x, centroid.y));
        double rectangleSurfaceArea = CommonUtils.countRectangleSurfaceArea(smallestRectanglePoints);
        if(paintedPointsAboveCentroid / CommonUtils.countRectangleSurfaceArea(rectangleAboveCentroidPoints) > 0.35 && rectangleSurfaceArea / binarizedMatWithoutSmallObjects.total() > 0.05) {
            pointsToFingers = fingerFinder.retrieveFingersFromContour(convexHull, centroid);
            List<Point> pointsToDraw = new ArrayList<>(pointsToFingers.keySet());
            Mat contourWithFingers = mats.get(MatTypes.CONTOURIZED_MAT_NOT_FILLED).clone();
            for(Point p : pointsToDraw) {
                Imgproc.line(contourWithFingers, centroid, p, new Scalar(255, 0, 0), 3);
//                                            if() {
                Imgproc.putText(contourWithFingers, pointsToFingers.get(p).toString(), p, 1, 2, new Scalar(255, 0, 0));
//                                            }
            }
            mats.put(MatTypes.CONTOURIZED_MAT_NOT_FILLED_WITH_FINGERS, contourWithFingers);
        }
    }
}
