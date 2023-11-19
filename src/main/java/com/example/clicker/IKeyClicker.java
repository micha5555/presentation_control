package com.example.clicker;

import com.example.FingerNames;
import org.opencv.core.Point;

import java.util.Map;

public interface IKeyClicker {
    void clickKeyBasedOnFingers(Map<Point, FingerNames> fingersMap);
}
