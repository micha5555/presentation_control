package com.example.binarization.impl;

import com.example.CommonUtils;
import com.example.binarization.IBinarizator;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.awt.image.BufferedImage;

public class HSVBasedBinarizator implements IBinarizator {
    @Override
    public Mat convertBufferedImageToBinarizedMat(BufferedImage originalBufferedImage, Scalar minThreshold, Scalar maxThreshold) {
        Mat originalImageMat = CommonUtils.convertBufferedImageToMat(originalBufferedImage);
        Mat hsvImage = convertRGBToHSV(originalImageMat);
        Mat binarized = binarizeHSVImage(hsvImage, minThreshold, maxThreshold);
        Imgproc.cvtColor(binarized, binarized, Imgproc.COLOR_BGR2GRAY);
        return binarized;
    }

    private static Mat convertRGBToHSV(Mat input) {
        if (input.empty()) {
            System.err.println("Error: Could not read the image.");
            return null;
        }

        // Get the number of rows and columns in the image
        int rows = input.rows();
        int cols = input.cols();

        Mat output = input.clone();
        // Iterate through every pixel in the image
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                // Get the RGB values for each channel
                double[] rgbValues = input.get(i, j);

                double hsvValues[] = rgbToHsv(rgbValues[2], rgbValues[1], rgbValues[0]);
                output.put(i, j, new double[]{hsvValues[2], hsvValues[1], hsvValues[0]});

                // Print or use the RGB values as needed
//                System.out.println("Pixel at (" + i + ", " + j + "): R=" + red + ", G=" + green + ", B=" + blue);
            }
        }
        return output;
    }

    public static double[] rgbToHsv(double r, double g, double b)
    {

        // R, G, B values are divided by 255
        // to change the range from 0..255 to 0..1
        r = r / 255.0;
        g = g / 255.0;
        b = b / 255.0;

        // h, s, v = hue, saturation, value
        double cmax = Math.max(r, Math.max(g, b)); // maximum of r, g, b
        double cmin = Math.min(r, Math.min(g, b)); // minimum of r, g, b
        double diff = cmax - cmin; // diff of cmax and cmin.
        double h = -1, s = -1;

        // if cmax and cmax are equal then h = 0
        if (cmax == cmin)
            h = 0;

            // if cmax equal r then compute h
        else if (cmax == r)
            h = (60 * ((g - b) / diff) + 360) % 360;

            // if cmax equal g then compute h
        else if (cmax == g)
            h = (60 * ((b - r) / diff) + 120) % 360;

            // if cmax equal b then compute h
        else if (cmax == b)
            h = (60 * ((r - g) / diff) + 240) % 360;

        // if cmax equal zero
        if (cmax == 0)
            s = 0;
        else
            s = (diff / cmax) * 100;

        // compute v
        double v = cmax * 100;
//
//        System.out.println("Count by first version");
//        System.out.println("Hue: " + h + ", Saturation: " + s + ", Brightness: " + v);
        return new double[]{h, s, v};
    }

    public static double[] rgbToHsv2(double red, double green, double blue) {
        double temp = Math.min(Math.min(red, green), blue);

        // Calculating Value component
        double value = Math.max(Math.max(red, green), blue);

        // TEMP is now the minimum component, and value is the maximum component

        // Calculating Hue component
        double hue = 0;
        if (temp == value) {
            hue = 0;
        } else {
            if (red == value) {
                hue = 0 + ((green - blue) * 60.0 / (value - temp));
            }
            if (green == value) {
                hue = 120 + ((blue - red) * 60.0 / (value - temp));
            }
            if (blue == value) {
                hue = 240 + ((red - green) * 60.0 / (value - temp));
            }
        }

        if (hue < 0) {
            hue += 360;
        }

        // Calculating Saturation component
        double saturation = (value == 0) ? 0 : ((value - temp) * 100.0) / value;

        // Calculating Value component
        double brightness = (value * 100.0) / 255;

        System.out.println("Count by second version");
        System.out.println("Hue: " + hue + ", Saturation: " + saturation + ", Brightness: " + brightness);
        return new double[]{hue, saturation, brightness};
    }

    private static Mat binarizeHSVImage(Mat input, Scalar minHsvThreshold, Scalar maxHsvThreshold) {
        if (input.empty()) {
            System.err.println("Error: Could not read the image.");
            return null;
        }

        // Get the number of rows and columns in the image
        int rows = input.rows();
        int cols = input.cols();

        Mat output = input.clone();
        // Iterate through every pixel in the image
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                // Get the RGB values for each channel
                double[] hsvValues = input.get(i, j);
                if(pixelMetHsvThresholds(hsvValues, minHsvThreshold, maxHsvThreshold)) {
                    output.put(i, j, new double[]{255, 255, 255});
                } else {
                    output.put(i, j, new double[]{0, 0, 0});
                }
            }
        }
        return output;
    }

    private static boolean pixelMetHsvThresholds(double[] pixel, Scalar minHsvThreshold, Scalar maxHsvThreshold) {
        double hue = pixel[0];
        double saturation = pixel[1];
        double value = pixel[2];
        boolean hueMet = hue >= minHsvThreshold.val[0] && hue <= maxHsvThreshold.val[0];
        boolean saturationMet = saturation >= minHsvThreshold.val[1] && saturation <= maxHsvThreshold.val[1];
        boolean valueMet = value >= minHsvThreshold.val[2] && value <= maxHsvThreshold.val[2];
        return hueMet && saturationMet && valueMet;
    }
}
