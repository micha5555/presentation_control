package com.example.fingersToKeyConverter.impl;

import com.example.FingerNames;
import com.example.fingersToKeyConverter.IFingersToKeyConverter;
import com.example.fingersToKeyConverter.TextToKeyAssignData;
import org.opencv.core.Point;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class FingersToKeyConverter implements IFingersToKeyConverter {
    private Map<List<Integer>, List<FingerNames>> fingersToKeyMap;
    public FingersToKeyConverter() {
        this.fingersToKeyMap = getFingersToKeyMap();
        System.out.println(fingersToKeyMap);
//        this.fingersToKeyMap.put(KeyEvent.VK_Z, Arrays.asList(FingerNames.INDEX));
//        this.fingersToKeyMap.put(KeyEvent.VK_X, Arrays.asList(FingerNames.THUMB));
//        this.fingersToKeyMap.put(Arrays.asList(KeyEvent.VK_RIGHT), Arrays.asList(FingerNames.INDEX, FingerNames.MIDDLE, FingerNames.RING));
//        this.fingersToKeyMap.put(Arrays.asList(KeyEvent.VK_LEFT), Arrays.asList(FingerNames.INDEX, FingerNames.PINKY));
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

    private Map<List<Integer>, List<FingerNames>> getFingersToKeyMap() {
        Map<List<Integer>, List<FingerNames>> output = new HashMap<>();
        String fingersToKeysFilePath = "./fingers_to_keys.txt";
        try {
            List<String> lines = Files.readAllLines(Paths.get(fingersToKeysFilePath));
            for(String line : lines) {
                if(line.startsWith("#")) {
                    continue;
                }
                String[] split = line.split(":");
                String[] keys = split[0].substring(1, split[0].length() - 1).split(",");
                List<Integer> keysInt = new ArrayList<>();
                for(String key : keys) {
                    keysInt.add(TextToKeyAssignData.textToKeyMap.get(key.trim()));
                }
//                i have [THUMB,INDEX,MIDDLE], and I want to have THUMB,INDEX,MIDDLE. What is regex to remove [ and ]?
                String[] fingersTable = split[1].split("[\\[\\]]");
//                System.out.println(fingersTable[1]);
                String[] fingers = fingersTable[1].split(",");
//                System.out.println(Arrays.asList(fingers));
                List<FingerNames> fingersNames = new ArrayList<>();
                for(String finger : fingers) {
                    fingersNames.add(FingerNames.valueOf(finger.trim()));
                }
                output.put(keysInt, fingersNames);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return output;
    }
}
