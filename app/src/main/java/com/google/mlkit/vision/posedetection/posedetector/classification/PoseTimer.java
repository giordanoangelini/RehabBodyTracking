package com.google.mlkit.vision.posedetection.posedetector.classification;

import android.util.Log;

public class PoseTimer {

    private int timerValue = 0; // Initial timer value in seconds
    private boolean isRunning = false;
    private boolean condition = false;
    private static final float DEFAULT_THRESHOLD = 6f;
    private final String className;

    public String getClassName() {
        return className;
    }

    public int getTimerValue() {
        return timerValue;
    }

    public PoseTimer(String class_key) {
        this.className = class_key;
    }

    public void startTimer() {
        if (!isRunning) {
            isRunning = true;
            // Use a separate thread or a timer to update the timer value
            new Thread(new TimerRunnable()).start();
        }
    }

    public void setPoseConfidence(ClassificationResult classificationResult) {
        float poseConfidence = classificationResult.getClassConfidence(className);
        this.condition = poseConfidence >= DEFAULT_THRESHOLD;
    }

    private class TimerRunnable implements Runnable {
        @Override
        public void run() {
            while (isRunning) {
                // Increase the timer value every second based on the condition
                if (condition) timerValue++;
                // Sleep for 1000 milliseconds (1 second)
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
