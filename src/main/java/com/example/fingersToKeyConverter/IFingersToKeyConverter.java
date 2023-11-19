package com.example.fingersToKeyConverter;

import com.example.FingerNames;
import org.opencv.core.Point;

import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Map;

public interface IFingersToKeyConverter {
    Map<Integer, List<FingerNames>> fingersToKeyMap = null;
    Integer convertFingersToKey(Map<Point, FingerNames> fingersMap);
}
