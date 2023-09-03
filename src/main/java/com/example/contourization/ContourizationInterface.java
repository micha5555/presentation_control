package com.example.contourization;

import org.opencv.core.Mat;

public interface ContourizationInterface {
    public Mat findBiggestContour(Mat inutMat);
}
