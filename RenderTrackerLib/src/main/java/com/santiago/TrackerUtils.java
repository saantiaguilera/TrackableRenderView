package com.santiago;

import android.util.Log;
import android.view.View;

import java.util.Map;

/**
 * Created by santi on 22/07/16.
 */
public class TrackerUtils {

    private static Map<Integer, DataTracker> viewMaps;

    public TrackerUtils() {}

    public static void onMeasure(View view) {
        int hash = view.hashCode();
        if (viewMaps.get(hash) == null) {
            viewMaps.put(hash, new DataTracker());
        }

        viewMaps.get(hash).setOnMeasureTime(System.currentTimeMillis());
        viewMaps.get(hash).setWasMeasuredCalled(true);
    }

    public static void onDraw(View view) {
        int hash = view.hashCode();

        if (viewMaps.get(hash) != null) {
            viewMaps.get(hash).setOnDrawTime(System.currentTimeMillis());
            viewMaps.get(hash).setWasDrawCalled(true);
        }
    }

    public static void printRenderingTimes(View view) {
        DataTracker tracker = viewMaps.get(view.hashCode());

        if (tracker == null) return;

        long now = System.currentTimeMillis();

        if (tracker.wasMeasuredCalled()) {
            Log.w("RenderViewTrackerUtils", "Time from measuring + drawing = " + (now - tracker.getOnMeasureTime()));
            tracker.setWasMeasuredCalled(false);
        }

        if (tracker.wasDrawCalled()) {
            Log.w("RenderViewTrackerUtils", "Time for drawing = " + (now - tracker.getOnDrawTime()));
            tracker.setWasDrawCalled(false);
        }
    }

    public static class DataTracker {
        private long onMeasureTime = 0;
        private long onDrawTime = 0;

        private boolean wasMeasuredCalled = false;
        private boolean wasDrawCalled = false;

        public long getOnDrawTime() {
            return onDrawTime;
        }

        public long getOnMeasureTime() {
            return onMeasureTime;
        }

        public boolean wasDrawCalled() {
            return wasDrawCalled;
        }

        public boolean wasMeasuredCalled() {
            return wasMeasuredCalled;
        }

        public void setOnDrawTime(long onDrawTime) {
            this.onDrawTime = onDrawTime;
        }

        public void setOnMeasureTime(long onMeasureTime) {
            this.onMeasureTime = onMeasureTime;
        }

        public void setWasDrawCalled(boolean wasDrawCalled) {
            this.wasDrawCalled = wasDrawCalled;
        }

        public void setWasMeasuredCalled(boolean wasMeasuredCalled) {
            this.wasMeasuredCalled = wasMeasuredCalled;
        }
    }

}
