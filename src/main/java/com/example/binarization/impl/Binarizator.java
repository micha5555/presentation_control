package com.example.binarization.impl;

import com.example.binarization.IBinarizator;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

import java.awt.image.BufferedImage;

public class Binarizator implements IBinarizator {

    @Override
    public Mat convertBufferedImageToBinarizedMat(BufferedImage originalBufferedImage, Scalar minThreshold, Scalar maxThreshold) {
        return null;
    }
}
