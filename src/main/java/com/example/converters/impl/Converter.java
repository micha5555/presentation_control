package com.example.converters.impl;

import com.example.converters.IConverter;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Converter implements IConverter {

    @Override
    public BufferedImage convertMatToBufferedImage(Mat mat) throws IOException{
        //Encoding the image
        MatOfByte matOfByte = new MatOfByte();
        Imgcodecs.imencode(".jpg", mat, matOfByte);
        //Storing the encoded Mat in a byte array
        byte[] byteArray = matOfByte.toArray();
        //Preparing the Buffered Image
        InputStream in = new ByteArrayInputStream(byteArray);
        BufferedImage bufImage = ImageIO.read(in);
        return bufImage;
    }

    @Override
    public MatOfPoint convertMatToMatOfPointNonEmptyPoints(Mat input) {
        MatOfPoint output = new MatOfPoint();
        List<Point> points = new ArrayList<Point>();
        for (int row = 0; row < input.rows(); row++) {
            for (int col = 0; col < input.cols(); col++) {
                double value = input.get(row, col)[0]; // Get the value at (row, col)
                if (value != 0) {
                    points.add(new Point(col, row));
                }
            }
        }
        output.fromList(points);
        return output;
    }
}
