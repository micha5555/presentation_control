package com.example.fingersToKeyConverter.impl;

import com.example.FingerNames;
import com.example.StaticData;
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
        if(Files.exists(Paths.get(StaticData.FINGERS_TO_KEYS_FILE_PATH))) {
            return getFingersToKeysMapFromFile();
        } else {
            return StaticData.DEFAULT_FINGERS_TO_KEYS_MAP;
        }
    }

    private Map<List<Integer>, List<FingerNames>> getFingersToKeysMapFromFile() {
        Map<List<Integer>, List<FingerNames>> output = new HashMap<>();
        try {
            List<String> lines = Files.readAllLines(Paths.get(StaticData.FINGERS_TO_KEYS_FILE_PATH));
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
                String[] fingersTable = split[1].split("[\\[\\]]");
                String[] fingers = fingersTable[1].split(",");
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
