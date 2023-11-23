package com.google.mlkit.vision.posedetection;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.google.mlkit.vision.R;
import com.google.mlkit.vision.pose.PoseDetectorOptionsBase;
import com.google.mlkit.vision.pose.accurate.AccuratePoseDetectorOptions;
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions;

public class PreferenceUtils {
  public static boolean hideDetectionInfo = false;
  public static boolean preferGPUForPoseDetection = true;
  public static boolean showPoseDetectionInFrameLikelihood = true;
  public static boolean poseDetectionVisualizeZ = true;
  public static boolean poseDetectionRescaleZForVisualization = true;
  public static boolean poseDetectionRunClassification = true;
  public static String detectionMode = "Fast";

  public static PoseDetectorOptionsBase getPoseDetectorOptionsForLivePreview(Context context) {
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

  public static boolean isCameraLiveViewportEnabled(Context context) {
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    String prefKey = context.getString(R.string.pref_key_camera_live_viewport);
    return sharedPreferences.getBoolean(prefKey, false);
  }
}
