package com.example.contourizer;

import org.opencv.core.Mat;

public interface IContourizer {
    Mat findBiggestContour(Mat inutMat);
}
