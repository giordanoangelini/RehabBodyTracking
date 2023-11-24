package com.google.mlkit.vision.posedetection.posedetector;

public class PoseClass {
    private final String pose_key;
    private final int label_id;
    private final int description_id;

    public PoseClass(String key, int label, int description) {
        this.pose_key = key;
        this.label_id = label;
        this.description_id = description;
    }

    public String getPose_key() {
        return pose_key;
    }

    public int getLabel_id() {
        return label_id;
    }

    public int getDescription_id() {
        return description_id;
    }
}



