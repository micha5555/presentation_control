package com.example.solution.impl;

import com.example.enums.MatTypes;
import com.example.solution.ISolution;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.HashMap;
import java.util.Map;

public class SkeletonBasedSolution implements ISolution {

    private Map<MatTypes, Mat> mats;

    private boolean clickingKeysEnabled = false;

    private String lastClickedKeys = null;

    public SkeletonBasedSolution() {
        this.mats = new HashMap<>();
//        this.mats.put(MatTypes.ORIGINAL_MAT, originalMat);
//        this.mats.put(MatTypes.BINARIZED_MAT, binaryMat);
//        this.mats.put(MatTypes.BINARIZED_MAT_WITHOUT_EMPTY_SPACES_AND_SMALL_OBJECTS, binaryMatWithoutEmptySpacesAndSmallObjects);
    }

    @Override
    public void execute() {
        if(mats.isEmpty()) {
            throw new RuntimeException("Mats not initialized");
        }
        distanceTransforms();
        findLocalMaxima();
        mergeBinarizedMatWithLocalMaxima();
    }

    @Override
    public Map<MatTypes, Mat> getMats() {
        return mats;
    }

    @Override
    public void setInitialMats(Mat originalMat, Mat binaryMat, Mat binaryMatWithoutEmptySpacesAndSmallObjects) {
        mats.clear();
        mats.put(MatTypes.ORIGINAL_MAT, originalMat);
        mats.put(MatTypes.BINARIZED_MAT, binaryMat);
        mats.put(MatTypes.BINARIZED_MAT_WITHOUT_EMPTY_SPACES_AND_SMALL_OBJECTS, binaryMatWithoutEmptySpacesAndSmallObjects);
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
}
