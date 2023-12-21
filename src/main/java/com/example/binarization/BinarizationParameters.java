package com.example.binarization;

import com.example.enums.ColorSpaces;
import com.example.StaticData;
import lombok.Getter;
import lombok.Setter;
import org.opencv.core.Scalar;

public class BinarizationParameters {
    @Getter
    private Scalar minThresholdScalar;//BGR-A

    @Getter
    private Scalar maxThresholdScalar;//BGR-A

    @Getter
    @Setter
    private ColorSpaces currentColorSpace;

    public BinarizationParameters() {
//        hsv is set default
        minThresholdScalar = new Scalar(StaticData.MIN_HUE_SLIDER, StaticData.MIN_SATURATION_SLIDER, StaticData.MIN_VALUE_SLIDER);//BGR-A
        maxThresholdScalar = new Scalar(StaticData.MAX_HUE_SLIDER, StaticData.MAX_SATURATION_SLIDER, StaticData.MAX_VALUE_SLIDER);//BGR-A
        currentColorSpace = ColorSpaces.HSV;
    }

    public void changeMinThresholdScalar(int value1, int value2, int value3) {
        minThresholdScalar = new Scalar(value3, value2, value1);//BGR-A
    }

    public void changeMaxThresholdScalar(int value1, int value2, int value3) {
        maxThresholdScalar = new Scalar(value3, value2, value1);//BGR-A
    }
}
