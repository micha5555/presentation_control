package com.example.contourizer;

import org.opencv.core.Mat;

public interface IContourizer {
    Mat findBiggestContourAndFill(Mat inutMat);
    Mat findBiggestContourAndNotFill(Mat inutMat);
//    Mat getBiggestContourFilled();
//    Mat getBiggestContourNotFilled();
}
