package com.example.fingersToKeyConverter.impl;

import com.example.FingerNames;
import com.example.fingersToKeyConverter.IFingersToKeyConverter;
import org.opencv.core.Point;

import java.awt.event.KeyEvent;
import java.util.*;

public class FingersToKeyConverter implements IFingersToKeyConverter {
    private Map<List<Integer>, List<FingerNames>> fingersToKeyMap;
    public FingersToKeyConverter() {
        this.fingersToKeyMap = new HashMap<>();
//        this.fingersToKeyMap.put(KeyEvent.VK_Z, Arrays.asList(FingerNames.INDEX));
//        this.fingersToKeyMap.put(KeyEvent.VK_X, Arrays.asList(FingerNames.THUMB));
        this.fingersToKeyMap.put(Arrays.asList(KeyEvent.VK_RIGHT), Arrays.asList(FingerNames.INDEX, FingerNames.MIDDLE, FingerNames.RING));
        this.fingersToKeyMap.put(Arrays.asList(KeyEvent.VK_LEFT), Arrays.asList(FingerNames.INDEX, FingerNames.PINKY));
//        this.fingersToKeyMap.put(KeyEvent.VK_A, Arrays.asList(FingerNames.THUMB, FingerNames.INDEX));
//        this.fingersToKeyMap.put(KeyEvent.VK_V, Arrays.asList(FingerNames.THUMB, FingerNames.INDEX, FingerNames.MIDDLE));
//        this.fingersToKeyMap.put(KeyEvent.VK_C, Arrays.asList(FingerNames.THUMB, FingerNames.INDEX, FingerNames.PINKY));
    }
    @Override
    public List<Integer> convertFingersToKey(Map<Point, FingerNames> fingersMap) {
        Collection<FingerNames> fingers = fingersMap.values();
        for(Map.Entry<List<Integer>, List<FingerNames>> entry : fingersToKeyMap.entrySet()) {
            if(fingers.size() == entry.getValue().size() && fingers.containsAll(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }
}
