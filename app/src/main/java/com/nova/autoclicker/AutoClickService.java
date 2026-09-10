package com.nova.autoclicker;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.content.SharedPreferences;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.Arrays;
import java.util.List;

public class AutoClickService extends AccessibilityService {

    private static AutoClickService instance;

    private static final List<String> SEND_TEXTS = Arrays.asList(
            "ارسال", "Send", "Send Message"
    );

    private final Handler handler = new Handler(Looper.getMainLooper());

    private static final long STEP_DELAY = 100;

    public static AutoClickService getInstance() {
        return instance;
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        instance = this;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
    }

    public void performSequence() {
        handler.post(() -> {
            // ۱. متن رو از SharedPreferences بخون
            SharedPreferences prefs = getSharedPreferences(MainActivity.PREFS, MODE_PRIVATE);
            String message = prefs.getString(MainActivity.KEY_MESSAGE, "");
            if (message.isEmpty()) return;

            // ۲. متن رو توی فیلد چت بنویس
            boolean wrote = writeTextToFocusedInput(message);
            sleep(STEP_DELAY);

            // ۳. دکمه ارسال رو بزن
            if (wrote) {
                clickByText(SEND_TEXTS.toArray(new String[0]));
            }
        });
    }

    /**
     * پیدا کردن فیلد متنی که فوکوس داره و نوشتن متن داخلش
     */
    private boolean writeTextToFocusedInput(String text) {
        AccessibilityNodeInfo root = getRootInActiveWindow();
        if (root == null) return false;

        AccessibilityNodeInfo focused = root.findFocus(AccessibilityNodeInfo.FOCUS_INPUT);
        if (focused == null) {
            // اگه فوکوس نداره، دنبال هر EditText بگرد
            focused = findFirstEditable(root);
        }
        if (focused == null) return false;

        Bundle args = new Bundle();
        args.putString(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text);
        return focused.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, args);
    }

    /**
     * پیدا کردن اولین فیلد قابل ویرایش
     */
    private AccessibilityNodeInfo findFirstEditable(AccessibilityNodeInfo node) {
        if (node == null) return null;

        if (node.isEditable()) return node;

        for (int i = 0; i < node.getChildCount(); i++) {
            AccessibilityNodeInfo child = node.getChild(i);
            if (child != null) {
                AccessibilityNodeInfo result = findFirstEditable(child);
                if (result != null) return result;
            }
        }
        return null;
    }

    private boolean clickByText(String... texts) {
        AccessibilityNodeInfo root = getRootInActiveWindow();
        if (root == null) return false;

        for (String text : texts) {
            List<AccessibilityNodeInfo> nodes =
                    root.findAccessibilityNodeInfosByText(text);
            if (nodes == null || nodes.isEmpty()) continue;

            for (AccessibilityNodeInfo node : nodes) {
                if (clickNode(node)) return true;
            }
        }
        return false;
    }

    private boolean clickNode(AccessibilityNodeInfo node) {
        if (node.isClickable()) {
            return node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }
        AccessibilityNodeInfo parent = node.getParent();
        while (parent != null) {
            if (parent.isClickable()) {
                return parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
            AccessibilityNodeInfo next = parent.getParent();
            parent.recycle();
            parent = next;
        }
        return false;
    }

    public void tapAt(float x, float y) {
        Path path = new Path();
        path.moveTo(x, y);

        GestureDescription.StrokeDescription stroke =
                new GestureDescription.StrokeDescription(path, 0, 1);

        GestureDescription gesture = new GestureDescription.Builder()
                .addStroke(stroke)
                .build();

        dispatchGesture(gesture, null, null);
    }

    private void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }

    @Override
    public void onInterrupt() { }

    @Override
    public boolean onUnbind(android.content.Intent intent) {
        instance = null;
        return super.onUnbind(intent);
    }
}
