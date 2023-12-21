package com.example.skeletonization.impl;

import org.opencv.core.Mat;

import com.example.skeletonization.ISkeletonization;

public class ZhangSuenISkeletonization implements ISkeletonization {

    @Override
    public Mat skeletonize(Mat inputMat) {
        Mat outputImage = inputMat.clone();
        
        int width = inputMat.cols();
        int height = inputMat.rows();
        
        boolean hasChanged = true;
        
        while (hasChanged) {
            hasChanged = false;
            
            // Iterate over the image pixels
            for (int y = 1; y < height - 1; y++) {
                for (int x = 1; x < width - 1; x++) {
                    double[] pixel = inputMat.get(y, x);
                    
                    if (pixel[0] != 0) {
                        int neighborsCount = getNeighborsCount(inputMat, x, y);
                        int transitions = calculateTransitions(inputMat, x, y);
                        
                        if (neighborsCount >= 2 && neighborsCount <= 6 &&
                            transitions == 1 &&
                            inputMat.get(y - 1, x)[0] * inputMat.get(y, x + 1)[0] * inputMat.get(y + 1, x)[0] == 0 &&
                            inputMat.get(y, x + 1)[0] * inputMat.get(y + 1, x)[0] * inputMat.get(y, x - 1)[0] == 0) {
                            outputImage.put(y, x, 0);
                            hasChanged = true;
                        }
                    }
                }
            }
            
            inputMat = outputImage.clone();
            
            // Repeat the process in the opposite order
            for (int y = 1; y < height - 1; y++) {
                for (int x = 1; x < width - 1; x++) {
                    double[] pixel = outputImage.get(y, x);
                    
                    if (pixel[0] != 0) {
                        int neighborsCount = getNeighborsCount(outputImage, x, y);
                        int transitions = calculateTransitions(outputImage, x, y);
                        
                        if (neighborsCount >= 2 && neighborsCount <= 6 &&
                            transitions == 1 &&
                            outputImage.get(y - 1, x)[0] * outputImage.get(y, x + 1)[0] * outputImage.get(y + 1, x)[0] == 0 &&
                            outputImage.get(y, x + 1)[0] * outputImage.get(y + 1, x)[0] * outputImage.get(y, x - 1)[0] == 0) {
                            inputMat.put(y, x, 0);
                            hasChanged = true;
                        }
                    }
                }
            }
            
            outputImage = inputMat.clone();
        }
        
        return outputImage;
    }

    private int getNeighborsCount(Mat image, int x, int y) {
        int count = 0;
        for (int j = y - 1; j <= y + 1; j++) {
            for (int i = x - 1; i <= x + 1; i++) {
                if (image.get(j, i)[0] != 0) {
                    count++;
                }
            }
        }
        return count - 1; // Exclude the central pixel itself
    }
    
    private int calculateTransitions(Mat image, int x, int y) {
        int transitions = 0;
        double[] pixels = new double[8];
        pixels[0] = image.get(y - 1, x)[0];
        pixels[1] = image.get(y - 1, x + 1)[0];
        pixels[2] = image.get(y, x + 1)[0];
        pixels[3] = image.get(y + 1, x + 1)[0];
        pixels[4] = image.get(y + 1, x)[0];
        pixels[5] = image.get(y + 1, x - 1)[0];
        pixels[6] = image.get(y, x - 1)[0];
        pixels[7] = image.get(y - 1, x - 1)[0];
        
        for (int i = 0; i < 7; i++) {
            if (pixels[i] == 0 && pixels[i + 1] == 255) {
                transitions++;
            }
        }
        
        if (pixels[7] == 0 && pixels[0] == 255) {
            transitions++;
        }
        
        return transitions;
    }
    
}
