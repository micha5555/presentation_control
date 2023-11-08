package com.example.contourization.impl;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import com.example.contourization.IContourization;

public class BiggestContourFinder implements IContourization {

    @Override
    public Mat findBiggestContour(Mat inputMat) {
        List<MatOfPoint> contours = new ArrayList<>();
        final Mat hierarchy = new Mat();
        Imgproc.findContours(inputMat, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

        double maxVal = 0;
        int maxValIndex = 0;
        for(int contourIndex = 0; contourIndex < contours.size(); contourIndex++) {
            double contourArea = Imgproc.contourArea(contours.get(contourIndex));
            if(contourArea > maxVal) {
                maxVal = contourArea;
                maxValIndex = contourIndex;
            }
        }
        // Mat canvas = Mat.zeros(inputMat.size(), CvType.CV_8UC3);
        Mat canvas = Mat.zeros(inputMat.size(), CvType.CV_32F);
        Imgproc.drawContours(canvas, contours, maxValIndex, new Scalar(255, 255, 255), 1);
        // Mat biggestContour = contours.get(maxValIndex);
        // Mat grayscaledBiggestContour = new Mat();
        // Imgproc.cvtColor(biggestContour, grayscaledBiggestContour, Imgproc.COLOR_BGR2GRAY);

        return canvas;
    }
    
}
