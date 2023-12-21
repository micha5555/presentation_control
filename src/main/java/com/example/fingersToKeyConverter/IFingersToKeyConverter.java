package com.example.fingersToKeyConverter;

import com.example.enums.FingerNames;
import org.opencv.core.Point;

import java.util.List;
import java.util.Map;

public interface IFingersToKeyConverter {
    Map<Integer, List<FingerNames>> fingersToKeyMap = null;
    List<Integer> convertFingersToKey(Map<Point, FingerNames> fingersMap);
}
