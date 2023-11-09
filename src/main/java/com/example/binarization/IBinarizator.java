package com.example.binarization;

import org.opencv.core.Mat;
import org.opencv.core.Scalar;

import java.awt.image.BufferedImage;

public interface IBinarizator {
    Mat convertBufferedImageToBinarizedMat(BufferedImage originalBufferedImage, Scalar minThreshold, Scalar maxThreshold);
}
