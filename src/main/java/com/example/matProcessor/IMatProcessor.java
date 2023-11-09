package com.example.matProcessor;

import com.example.FingerNames;
import com.example.converters.IConverter;
import org.opencv.core.Mat;
import org.opencv.core.Point;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;

public interface IMatProcessor {
    BufferedImage processFinalBufferedImage(IConverter converter, Mat biggestContourMat, Map<Point, FingerNames> pointsToFingers) throws IOException;
}
