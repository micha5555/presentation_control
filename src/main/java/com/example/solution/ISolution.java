package com.example.solution;

import com.example.enums.FingerNames;
import com.example.enums.MatTypes;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;

import java.util.Map;

public interface ISolution {
    void execute();
    Map<MatTypes, Mat> getMats();
    void setInitialMats(Mat originalMat, Mat binaryMat);
    String getLastClickedKeys();
    void enableClickingKeys();
    void disableClickingKeys();
    void enableFindingGestures();
    void disableFindingGestures();
    MatOfPoint getConvexHull();
    Point[] getSmallestRectanglePoints();
    Map<Point, FingerNames> getPointsToFingers();
}
