package com.example.clicker.impl;

import com.example.FingerNames;
import com.example.clicker.IKeyClicker;
import com.example.fingersToKeyConverter.IFingersToKeyConverter;
import org.opencv.core.Point;


import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class KeyClicker implements IKeyClicker {

    private IFingersToKeyConverter fingersToKeyConverter;
    private Robot robot;
    public KeyClicker(IFingersToKeyConverter converter) {
        this.fingersToKeyConverter = converter;
        try {
            robot = new Robot();
        }
        catch (AWTException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void clickKeyBasedOnFingers(Map<Point, FingerNames> fingersMap) {
        List<Integer> keys = fingersToKeyConverter.convertFingersToKey(fingersMap);

        Collection<FingerNames> fingers = fingersMap.values();
        if(keys != null && !keys.isEmpty()) {
            for(Integer key : keys) {
                robot.keyPress(key);
            }
            for(Integer key : keys) {
                robot.keyRelease(key);
            }
            System.out.println("Key(s) pressed: " + keys);
        }
        else {
            System.out.println("No keys pressed, based on fingers: " + fingersMap.values() + "");
        }
    }
}
