package com.example.converters;

import org.opencv.core.Mat;

import java.awt.image.BufferedImage;
import java.io.IOException;

public interface IConverter {
    BufferedImage convertMatToBufferedImage(Mat mat) throws IOException;
}
