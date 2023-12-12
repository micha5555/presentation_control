package com.example.solution.impl;

import com.example.CommonUtils;
import com.example.clicker.IKeyClicker;
import com.example.contourizer.IContourizer;
import com.example.enums.FingerNames;
import com.example.enums.MatTypes;
import com.example.fingerFinder.IFingerFinder;
import com.example.skeletonization.ISkeletonization;
import com.example.skeletonization.impl.ZhangSuenISkeletonization;
import com.example.solution.ISolution;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Point;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SkeletonBasedSolution implements ISolution {

    private Map<MatTypes, Mat> mats;

    private boolean clickingKeysEnabled = false;

    private boolean findingGesturesEnabled = false;

    private String lastClickedKeys = null;

    private IContourizer contourizer;

    private IFingerFinder fingerFinder;

    private IKeyClicker keyClicker;

    private MatOfPoint convexHull = null;

    private Map<Point, FingerNames> pointsToFingers = null;

    private Point[] smallestRectanglePoints = null;

    private Point[] rectangleAboveCentroidPoints = null;

    private Point centroid = null;

    public SkeletonBasedSolution(IContourizer contourizer, IFingerFinder fingerFinder, IKeyClicker keyClicker) {
        this.mats = new HashMap<>();
        this.contourizer = contourizer;
        this.fingerFinder = fingerFinder;
        this.keyClicker = keyClicker;
//        this.mats.put(MatTypes.ORIGINAL_MAT, originalMat);
//        this.mats.put(MatTypes.BINARIZED_MAT, binaryMat);
//        this.mats.put(MatTypes.BINARIZED_MAT_WITHOUT_EMPTY_SPACES_AND_SMALL_OBJECTS, binaryMatWithoutEmptySpacesAndSmallObjects);
    }

    @Override
    public void execute() {
        if(mats.isEmpty()) {
            throw new RuntimeException("Mats not initialized");
        }
        if(!findingGesturesEnabled) {
            return;
        }
        distanceTransforms();
        findLocalMaxima();
        mergeBinarizedMatWithLocalMaxima();
        extractCriticalPoints();
//        System.out.println("before findConvexHull");
        findConvexHullOnBinarized();
        findCentroid();
        findConvexHullOnSkeleton();

        System.out.println("Convex hull points " + convexHull.toList());
        findSmallestRectangle();
        findRectangleAboveCentroid();
        findFingers();
        if(clickingKeysEnabled) {
            String clickedKeys = keyClicker.clickKeyBasedOnFingers(pointsToFingers);
            if(clickedKeys != null) {
                lastClickedKeys = clickedKeys;
            }
        }
//        System.out.println("after findConvexHull");
    }

    @Override
    public Map<MatTypes, Mat> getMats() {
        return mats;
    }

    @Override
    public void setInitialMats(Mat originalMat, Mat binaryMat) {
        mats.clear();
        mats.put(MatTypes.ORIGINAL_MAT, originalMat);
        mats.put(MatTypes.BINARIZED_MAT, binaryMat);
//        mats.put(MatTypes.BINARIZED_MAT_WITHOUT_EMPTY_SPACES_AND_SMALL_OBJECTS, binaryMatWithoutEmptySpacesAndSmallObjects);
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
        return new Point[0];
    }

    @Override
    public Map<Point, FingerNames> getPointsToFingers() {
        return null;
    }

    private void distanceTransforms() {
//        System.out.println("distanceTransforms " + Math.random());
        Mat distanceTransform = new Mat();
        Imgproc.distanceTransform(mats.get(MatTypes.BINARIZED_MAT_WITHOUT_EMPTY_SPACES_AND_SMALL_OBJECTS), distanceTransform, Imgproc.DIST_L1, Imgproc.DIST_MASK_5);
        mats.put(MatTypes.DISTANCE_TRANSFORMED_MAT, distanceTransform);
    }

    private void findLocalMaxima() {
        Mat localMaxima = new Mat();

        // Perform dilation to find local maxima
        Mat dilated = new Mat();
        Size kernelSize = new Size(4, 4); // Adjust kernel size as needed
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, kernelSize);
        Imgproc.dilate(mats.get(MatTypes.DISTANCE_TRANSFORMED_MAT), dilated, kernel);
        Core.compare(mats.get(MatTypes.DISTANCE_TRANSFORMED_MAT), dilated, localMaxima, Core.CMP_EQ);

        mats.put(MatTypes.LOCAL_MAXIMA_MAT, localMaxima);
    }

    private void mergeBinarizedMatWithLocalMaxima() {
        Mat result = new Mat();
        Core.bitwise_and(mats.get(MatTypes.BINARIZED_MAT_WITHOUT_EMPTY_SPACES_AND_SMALL_OBJECTS), mats.get(MatTypes.LOCAL_MAXIMA_MAT), result);
        mats.put(MatTypes.MERGED_BINARIZED_MAT_WITHOUT_SMALL_OBJECTS_WITH_LOCAL_MAXIMA_MAT, result);
    }

    private void extractCriticalPoints() {
        Mat result = findCriticalPoints(mats.get(MatTypes.DISTANCE_TRANSFORMED_MAT), mats.get(MatTypes.MERGED_BINARIZED_MAT_WITHOUT_SMALL_OBJECTS_WITH_LOCAL_MAXIMA_MAT));
        ISkeletonization skeletonization = new ZhangSuenISkeletonization();
        Mat skeletonized = skeletonization.skeletonize(result);
        mats.put(MatTypes.CRITICAL_POINTS_MAT, skeletonized);
    }

    private void findConvexHullOnSkeleton() {
        convexHull = CommonUtils.findConvexHullPointsWithoutDuplicates(mats.get(MatTypes.CRITICAL_POINTS_MAT));
        mats.put(MatTypes.CONVEX_HULL_MAT_ON_SKELETON, convexHull);
    }

    private void findConvexHullOnBinarized() {
        convexHull = CommonUtils.findConvexHullPoints(mats.get(MatTypes.BINARIZED_MAT_WITHOUT_EMPTY_SPACES_AND_SMALL_OBJECTS));
        mats.put(MatTypes.CONVEX_HULL_MAT, convexHull);
    }

    private void findSmallestRectangle() {
        smallestRectanglePoints = CommonUtils.findBiggestRectangleOnHand(convexHull);
    }

    private void findCentroid() {
        centroid = CommonUtils.findCentroid(convexHull);
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
            Mat contourWithFingers = mats.get(MatTypes.CRITICAL_POINTS_MAT).clone();
            for(Point p : pointsToDraw) {
                Imgproc.line(contourWithFingers, centroid, p, new Scalar(255, 0, 0), 3);
//                                            if() {
                Imgproc.putText(contourWithFingers, pointsToFingers.get(p).toString(), p, 1, 2, new Scalar(255, 0, 0));
//                                            }
            }
            mats.put(MatTypes.CONTOURIZED_MAT_NOT_FILLED_WITH_FINGERS, contourWithFingers);
        }
    }

    private static Mat findCriticalPoints(Mat distanceTransformMat, Mat localMaximaMat) {
        Mat criticalPointsMat = new Mat(distanceTransformMat.size(), CvType.CV_8U, Scalar.all(0));

        // Iterate through the local maxima Mat
        for (int y = 0; y < localMaximaMat.rows(); y++) {
            for (int x = 0; x < localMaximaMat.cols(); x++) {
                double[] localMaximaValue = localMaximaMat.get(y, x);

                // Check if the current pixel is a local maximum
                if (localMaximaValue[0] > 0) {
                    // Check the neighborhood to determine if it's a critical point
                    if (isCriticalPoint(distanceTransformMat, x, y)) {
                        // Mark the critical point in the result Mat
                        criticalPointsMat.put(y, x, 255);
                    }
                }
            }
        }

        return criticalPointsMat;
    }

    private static boolean isCriticalPoint(Mat distanceTransformMat, int x, int y) {
        // You can implement your own logic to determine if a point is a critical point
        // For example, you can check if the pixel value at (x, y) in the distance transformed Mat is greater than a threshold

        double distanceTransformValue = distanceTransformMat.get(y, x)[0];
        double threshold = 5; // Adjust the threshold as needed

        return distanceTransformValue > threshold;
    }
}
