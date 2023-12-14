package com.example.fingersToKeyConverter;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

public class TextToKeyAssignData {

    public static Map<String, Integer> textToKeyMap = new HashMap<String, Integer>(){
        {
            put("1", KeyEvent.VK_1);
            put("2", KeyEvent.VK_2);
            put("3", KeyEvent.VK_3);
            put("4", KeyEvent.VK_4);
            put("5", KeyEvent.VK_5);
            put("6", KeyEvent.VK_6);
            put("7", KeyEvent.VK_7);
            put("8", KeyEvent.VK_8);
            put("9", KeyEvent.VK_9);
            put("0", KeyEvent.VK_0);
            put("a", KeyEvent.VK_A);
            put("b", KeyEvent.VK_B);
            put("c", KeyEvent.VK_C);
            put("d", KeyEvent.VK_D);
            put("e", KeyEvent.VK_E);
            put("f", KeyEvent.VK_F);
            put("g", KeyEvent.VK_G);
            put("h", KeyEvent.VK_H);
            put("i", KeyEvent.VK_I);
            put("j", KeyEvent.VK_J);
            put("k", KeyEvent.VK_K);
            put("l", KeyEvent.VK_L);
            put("m", KeyEvent.VK_M);
            put("n", KeyEvent.VK_N);
            put("o", KeyEvent.VK_O);
            put("p", KeyEvent.VK_P);
            put("q", KeyEvent.VK_Q);
            put("r", KeyEvent.VK_R);
            put("s", KeyEvent.VK_S);
            put("t", KeyEvent.VK_T);
            put("u", KeyEvent.VK_U);
            put("v", KeyEvent.VK_V);
            put("w", KeyEvent.VK_W);
            put("x", KeyEvent.VK_X);
            put("y", KeyEvent.VK_Y);
            put("z", KeyEvent.VK_Z);
            // create putting all F keys
            put("f1", KeyEvent.VK_F1);
            put("f2", KeyEvent.VK_F2);
            put("f3", KeyEvent.VK_F3);
            put("f4", KeyEvent.VK_F4);
            put("f5", KeyEvent.VK_F5);
            put("f6", KeyEvent.VK_F6);
            put("f7", KeyEvent.VK_F7);
            put("f8", KeyEvent.VK_F8);
            put("f9", KeyEvent.VK_F9);
            put("f10", KeyEvent.VK_F10);
            put("f11", KeyEvent.VK_F11);
            put("f12", KeyEvent.VK_F12);
            // create putting all number keys
            put("numpad0", KeyEvent.VK_NUMPAD0);
            put("numpad1", KeyEvent.VK_NUMPAD1);
            put("numpad2", KeyEvent.VK_NUMPAD2);
            put("numpad3", KeyEvent.VK_NUMPAD3);
            put("numpad4", KeyEvent.VK_NUMPAD4);
            put("numpad5", KeyEvent.VK_NUMPAD5);
            put("numpad6", KeyEvent.VK_NUMPAD6);
            put("numpad7", KeyEvent.VK_NUMPAD7);
            put("numpad8", KeyEvent.VK_NUMPAD8);
            put("numpad9", KeyEvent.VK_NUMPAD9);
            // create putting all other keys
            put("backspace", KeyEvent.VK_BACK_SPACE);
            put("tab", KeyEvent.VK_TAB);
            put("enter", KeyEvent.VK_ENTER);
            put("shift", KeyEvent.VK_SHIFT);
            put("control", KeyEvent.VK_CONTROL);
            put("alt", KeyEvent.VK_ALT);
            put("pause", KeyEvent.VK_PAUSE);
            put("capslock", KeyEvent.VK_CAPS_LOCK);
            put("escape", KeyEvent.VK_ESCAPE);
            put("space", KeyEvent.VK_SPACE);
            put("pageup", KeyEvent.VK_PAGE_UP);
            put("pagedown", KeyEvent.VK_PAGE_DOWN);
            put("end", KeyEvent.VK_END);
            put("home", KeyEvent.VK_HOME);
            put("left", KeyEvent.VK_LEFT);
            put("up", KeyEvent.VK_UP);
            put("right", KeyEvent.VK_RIGHT);
            put("down", KeyEvent.VK_DOWN);
            put("printscreen", KeyEvent.VK_PRINTSCREEN);
            put("insert", KeyEvent.VK_INSERT);
            put("delete", KeyEvent.VK_DELETE);
            put("windows", KeyEvent.VK_WINDOWS);
        }

    };
}
