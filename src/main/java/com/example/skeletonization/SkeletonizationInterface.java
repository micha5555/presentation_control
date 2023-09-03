package com.example.skeletonization;

import org.opencv.core.Mat;

public interface SkeletonizationInterface {
    public Mat skeletonize(Mat inputMat);
}
