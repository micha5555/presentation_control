package com.example.solution;

import com.example.enums.MatTypes;
import org.opencv.core.Mat;

import java.util.Map;

public interface ISolution {
    void execute();
    Map<MatTypes, Mat> getMats();
    void setInitialMats(Mat originalMat, Mat binaryMat, Mat binaryMatWithoutEmptySpacesAndSmallObjects);
    String getLastClickedKeys();
    void enableClickingKeys();
    void disableClickingKeys();
}
