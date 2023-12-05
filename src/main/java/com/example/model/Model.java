package com.example.model;

import com.example.StaticData;
import lombok.Getter;
import org.opencv.core.Scalar;

public class Model {
    @Getter
    private Scalar minThresholdScalar;//BGR-A
    @Getter
    private Scalar maxThresholdScalar;//BGR-A

    public Model() {
        minThresholdScalar = new Scalar(StaticData.MIN_BLUE_SLIDER, StaticData.MIN_GREEN_SLIDER, StaticData.MIN_RED_SLIDER);//BGR-A
        maxThresholdScalar = new Scalar(StaticData.MAX_BLUE_SLIDER, StaticData.MAX_GREEN_SLIDER, StaticData.MAX_RED_SLIDER);//BGR-A
    }

    public void changeMinThresholdScalar(int red, int green, int blue) {
        minThresholdScalar = new Scalar(blue, green, red);//BGR-A
    }

    public void changeMaxThresholdScalar(int red, int green, int blue) {
        maxThresholdScalar = new Scalar(blue, green, red);//BGR-A
    }
}
