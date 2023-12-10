package com.example;

import com.example.enums.FingerNames;

import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StaticData {

//  RGB color space for blue glove
    public static final int MIN_RED_SLIDER = 215;
    public static final int MIN_GREEN_SLIDER = 33;
    public static final int MIN_BLUE_SLIDER = 0;
    public static final int MAX_RED_SLIDER = 252;
    public static final int MAX_GREEN_SLIDER = 255;
    public static final int MAX_BLUE_SLIDER = 255;

//  HSV color space for blue glove
    public static final int MIN_HUE_SLIDER = 217;
    public static final int MIN_SATURATION_SLIDER = 44;
    public static final int MIN_VALUE_SLIDER = 0;
    public static final int MAX_HUE_SLIDER = 246;
    public static final int MAX_SATURATION_SLIDER = 100;
    public static final int MAX_VALUE_SLIDER = 100;

    public static final int FRAMES_DELAY = 0;

    public static final int MINIMAL_DISTANCE_BETWEEN_CONVEX_HULL_POINTS = 25;

    public static final int MINIMAL_THUMB_ANGLE = 0;
    public static final int MAXIMAL_THUMB_ANGLE = 45;
    public static final int MINIMAL_INDEX_ANGLE = 46;
    public static final int MAXIMAL_INDEX_ANGLE = 80;
    public static final int MINIMAL_MIDDLE_ANGLE = 81;
    public static final int MAXIMAL_MIDDLE_ANGLE = 100;
    public static final int MINIMAL_RING_ANGLE = 101;
    public static final int MAXIMAL_RING_ANGLE = 120;
    public static final int MINIMAL_PINKY_ANGLE = 121;
    public static final int MAXIMAL_PINKY_ANGLE = 155;
    public static final String FINGERS_TO_KEYS_FILE_PATH = "./fingers_to_keys.txt";
    public static final Map<List<Integer>, List<FingerNames>> DEFAULT_FINGERS_TO_KEYS_MAP = new HashMap<List<Integer>, List<FingerNames>>() {
        {
            put(Arrays.asList(KeyEvent.VK_RIGHT), Arrays.asList(FingerNames.INDEX, FingerNames.MIDDLE, FingerNames.RING));
            put(Arrays.asList(KeyEvent.VK_LEFT), Arrays.asList(FingerNames.INDEX, FingerNames.PINKY));
            put(Arrays.asList(KeyEvent.VK_F5), Arrays.asList(FingerNames.THUMB, FingerNames.INDEX, FingerNames.MIDDLE));
        }
    };
}
