package com.google.mlkit.vision.posedetection.posedetector;

public class ExerciseClass {
    private final String pose_key;
    private String exercise_mode = "";
    private final int label_id;
    private final int description_id;

    public ExerciseClass(String key, int label, int description) {
        this.pose_key = key;
        this.label_id = label;
        this.description_id = description;
    }

    public void setExercise_mode(String exercise_mode) {
        this.exercise_mode = exercise_mode;
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

    public String getExercise_mode() {
        return exercise_mode;
    }
}



