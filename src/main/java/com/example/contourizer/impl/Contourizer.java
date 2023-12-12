package com.example.contourizer.impl;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import com.example.contourizer.IContourizer;

public class Contourizer implements IContourizer {

    @Override
    public Mat findBiggestContourAndFill(Mat inputMat) {
        List<MatOfPoint> contours = findContours(inputMat);
        int maxValIndex = findMaxValIndex(contours);
        Mat canvas = Mat.zeros(inputMat.size(), CvType.CV_8UC1);
        Imgproc.drawContours(canvas, contours, maxValIndex, new Scalar(255, 255, 255), Core.FILLED);
        return canvas;
    }

    @Override
    public Mat findBiggestContourAndNotFill(Mat inputMat) {
        List<MatOfPoint> contours = findContours(inputMat);
        int maxValIndex = findMaxValIndex(contours);
        Mat canvas = Mat.zeros(inputMat.size(), CvType.CV_8UC1);
        Imgproc.drawContours(canvas, contours, maxValIndex, new Scalar(255, 255, 255), 1);
        return canvas;
    }


    private int findMaxValIndex(List<MatOfPoint> contours) {
        double maxVal = 0;
        int maxValIndex = 0;
        for(int contourIndex = 0; contourIndex < contours.size(); contourIndex++) {
            double contourArea = Imgproc.contourArea(contours.get(contourIndex));
            if(contourArea > maxVal) {
                maxVal = contourArea;
                maxValIndex = contourIndex;
            }
        }
        return maxValIndex;
    }

    private List<MatOfPoint> findContours(Mat inputMat) {
        List<MatOfPoint> contours = new ArrayList<>();
        final Mat hierarchy = new Mat();
        Imgproc.findContours(inputMat, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        return contours;
    }
}
