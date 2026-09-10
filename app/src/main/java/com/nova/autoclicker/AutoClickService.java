package com.nova.autoclicker;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.os.Handler;
import android.os.Looper;
import android.view.accessibility.AccessibilityEvent;

public class AutoClickService extends AccessibilityService {

    private static AutoClickService instance;

    private final Handler handler = new Handler(Looper.getMainLooper());

    private volatile boolean isLooping = false;

    private static final long LOOP_DELAY = 30;

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

    public void startAutoSendLoop() {
        if (isLooping) {
            stopAutoSendLoop();
            return;
        }

        isLooping = true;
        handler.post(loopRunnable);
    }

    public void stopAutoSendLoop() {
        isLooping = false;
        handler.removeCallbacks(loopRunnable);
    }

    private final Runnable loopRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isLooping) return;
            performOneRound();
            handler.postDelayed(this, LOOP_DELAY);
        }
    };

    private void performOneRound() {
        android.util.DisplayMetrics metrics = getResources().getDisplayMetrics();
        int w = metrics.widthPixels;
        int h = metrics.heightPixels;

        longPress(w * 0.38f, h * 0.92f, 400);

        handler.postDelayed(() -> {
            tapAt(w * 0.50f, h * 0.86f);
        }, 500);

        handler.postDelayed(() -> {
            tapAt(w * 0.90f, h * 0.82f);
        }, 800);

        handler.postDelayed(() -> {
            tapAt(w * 0.85f, h * 0.92f);
        }, 1100);
    }

    public void longPress(float x, float y, long duration) {
        Path path = new Path();
        path.moveTo(x, y);

        GestureDescription.StrokeDescription stroke =
                new GestureDescription.StrokeDescription(path, 0, duration);

        GestureDescription gesture = new GestureDescription.Builder()
                .addStroke(stroke)
                .build();

        dispatchGesture(gesture, null, null);
    }

    public void tapAt(float x, float y) {
        Path path = new Path();
        path.moveTo(x, y);

        GestureDescription.StrokeDescription stroke =
                new GestureDescription.StrokeDescription(path, 0, 50);

        GestureDescription gesture = new GestureDescription.Builder()
                .addStroke(stroke)
                .build();

        dispatchGesture(gesture, null, null);
    }

    @Override
    public void onInterrupt() { }

    @Override
    public boolean onUnbind(android.content.Intent intent) {
        instance = null;
        isLooping = false;
        return super.onUnbind(intent);
    }
}
