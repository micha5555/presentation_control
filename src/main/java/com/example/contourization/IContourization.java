package com.example.contourization;

import org.opencv.core.Mat;

public interface IContourization {
    public Mat findBiggestContour(Mat inutMat);
}
