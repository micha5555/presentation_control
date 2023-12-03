package com.example.clicker;

import com.example.FingerNames;
import org.opencv.core.Point;

import java.util.Map;

public interface IKeyClicker {
    String clickKeyBasedOnFingers(Map<Point, FingerNames> fingersMap);
}
