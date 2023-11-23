package com.google.mlkit.vision.posedetection.posedetector;

public class PoseClass {
    private String class_label;
    private int description_id;

    public PoseClass(String label, int id) {
        this.class_label = label;
        this.description_id = id;
    }

    public int getDescription_id() {
        return description_id;
    }

    public String getClass_label() {
        return class_label;
    }
}



