package com.example.converters;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;

import java.awt.image.BufferedImage;
import java.io.IOException;

public interface IConverter {
    BufferedImage convertMatToBufferedImage(Mat mat) throws IOException;
    MatOfPoint convertMatToMatOfPointNonEmptyPoints(Mat input);
}
