package com.google.mlkit.vision.posedetection.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.google.mlkit.vision.R;
import com.google.mlkit.vision.pose.PoseDetectorOptionsBase;
import com.google.mlkit.vision.pose.accurate.AccuratePoseDetectorOptions;
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions;
import com.google.mlkit.vision.posedetection.posedetector.PoseClass;

public class PreferenceUtils {
  public static boolean hideDetectionInfo = true;
  public static boolean preferGPUForPoseDetection = true;
  public static boolean hideBodyLines = false;
  public static boolean showPoseDetectionInFrameLikelihood = true;
  public static boolean poseDetectionVisualizeZ = true;
  public static boolean poseDetectionRescaleZForVisualization = true;
  public static boolean poseDetectionRunClassification = true;
  public static String detectionMode = "Fast";
  public static boolean isCameraLiveViewportEnabled = false;

  public static PoseDetectorOptionsBase getPoseDetectorOptionsForLivePreview() {
    if (detectionMode.equals("Fast")) {
      PoseDetectorOptions.Builder builder =
              new PoseDetectorOptions.Builder().setDetectorMode(PoseDetectorOptions.STREAM_MODE);
      if (preferGPUForPoseDetection) {
        builder.setPreferredHardwareConfigs(PoseDetectorOptions.CPU_GPU);
      }
      return builder.build();
    } else {
      AccuratePoseDetectorOptions.Builder builder =
              new AccuratePoseDetectorOptions.Builder()
                      .setDetectorMode(AccuratePoseDetectorOptions.STREAM_MODE);
      if (preferGPUForPoseDetection) {
        builder.setPreferredHardwareConfigs(AccuratePoseDetectorOptions.CPU_GPU);
      }
      return builder.build();
    }
  }
  public static final PoseClass[] EXERCISE_LIST = new PoseClass[]{
            new PoseClass("squats_down", R.string.pose_detection_squat_description),
            new PoseClass("pushups_down", R.string.pose_detection_pushup_description)
  };

  public static PoseClass SELECTED_EXERCISE;
}
