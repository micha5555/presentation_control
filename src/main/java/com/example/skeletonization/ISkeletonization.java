package com.example.skeletonization;

import org.opencv.core.Mat;

public interface ISkeletonization {
    public Mat skeletonize(Mat inputMat);
}
