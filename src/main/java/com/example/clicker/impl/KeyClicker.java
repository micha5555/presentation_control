package com.example.clicker.impl;

import com.example.FingerNames;
import com.example.clicker.IKeyClicker;
import com.example.fingersToKeyConverter.IFingersToKeyConverter;
import com.example.fingersToKeyConverter.impl.FingersToKeyConverter;
import org.opencv.core.Point;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Collection;
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
        Integer key = fingersToKeyConverter.convertFingersToKey(fingersMap);

        Collection<FingerNames> fingers = fingersMap.values();
        if(fingers.size() == 1 && fingers.contains(FingerNames.MIDDLE)) {
            robot.keyPress(KeyEvent.VK_ALT);
            robot.keyPress(KeyEvent.VK_F4);
            robot.keyRelease(KeyEvent.VK_ALT);
            robot.keyRelease(KeyEvent.VK_F4);
            return;
        }
        if(key != null) {
            robot.keyPress(key);
            robot.keyRelease(key);
            System.out.println("Key pressed: " + key);
        }
        else {
            System.out.println("No key pressed, based on finfers: " + fingersMap.values() + "");
        }
    }
}
