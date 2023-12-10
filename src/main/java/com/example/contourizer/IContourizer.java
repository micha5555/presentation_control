package com.example.contourizer;

import org.opencv.core.Mat;

public interface IContourizer {
    Mat processBiggestContour(Mat inutMat);
    Mat getBiggestContourFilled();
    Mat getBiggestContourNotFilled();
}
