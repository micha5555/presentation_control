package com.example.fingerFinder;

import com.example.FingerNames;
import com.example.converters.IConverter;
import org.opencv.core.Mat;
import org.opencv.core.Point;

import java.util.Map;

public interface IFingerFinder {
    Map<Point, FingerNames> retrieveFingersFromContour(Mat contourMat);
}
