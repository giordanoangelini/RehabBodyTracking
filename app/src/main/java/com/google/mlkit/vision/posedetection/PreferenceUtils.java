package com.google.mlkit.vision.posedetection;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.google.mlkit.vision.R;
import com.google.mlkit.vision.pose.PoseDetectorOptionsBase;
import com.google.mlkit.vision.pose.accurate.AccuratePoseDetectorOptions;
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions;

/** Utility class to retrieve shared preferences. */
public class PreferenceUtils {

  private static final String POSE_DETECTOR_PERFORMANCE_MODE_FAST = "Fast";

  public static boolean shouldHideDetectionInfo(Context context) {
    return Boolean.parseBoolean(context.getString(R.string.info_hide));
  }

  public static boolean preferGPUForPoseDetection(Context context) {
    return Boolean.parseBoolean(context.getString(R.string.pose_detector_prefer_gpu));
  }

  public static boolean shouldShowPoseDetectionInFrameLikelihoodLivePreview(Context context) {
    return Boolean.parseBoolean(context.getString(R.string.live_preview_pose_detector_show_in_frame_likelihood));
  }

  public static boolean shouldPoseDetectionVisualizeZ(Context context) {
    return Boolean.parseBoolean(context.getString(R.string.pose_detector_visualize_z));
  }

  public static boolean shouldPoseDetectionRescaleZForVisualization(Context context) {
    return Boolean.parseBoolean(context.getString(R.string.pose_detector_rescale_z));
  }

  public static boolean shouldPoseDetectionRunClassification(Context context) {
    return Boolean.parseBoolean(context.getString(R.string.pose_detector_run_classification));
  }

  public static PoseDetectorOptionsBase getPoseDetectorOptionsForLivePreview(Context context) {
    boolean fastDetection = isFastDetectionEnabled(context);
    boolean preferGPU = preferGPUForPoseDetection(context);
    if (fastDetection) {
      PoseDetectorOptions.Builder builder =
              new PoseDetectorOptions.Builder().setDetectorMode(PoseDetectorOptions.STREAM_MODE);
      if (preferGPU) {
        builder.setPreferredHardwareConfigs(PoseDetectorOptions.CPU_GPU);
      }
      return builder.build();
    } else {
      AccuratePoseDetectorOptions.Builder builder =
              new AccuratePoseDetectorOptions.Builder()
                      .setDetectorMode(AccuratePoseDetectorOptions.STREAM_MODE);
      if (preferGPU) {
        builder.setPreferredHardwareConfigs(AccuratePoseDetectorOptions.CPU_GPU);
      }
      return builder.build();
    }
  }

  /**
   * Mode type preference is backed by {@link android.preference.ListPreference} which only support
   * storing its entry value as string type, so we need to retrieve as string and then convert to
   * integer.
   */
  private static boolean isFastDetectionEnabled(Context context) {
    return Boolean.parseBoolean(context.getString(R.string.live_preview_pose_detection_fast));
  }

  public static boolean isCameraLiveViewportEnabled(Context context) {
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    String prefKey = context.getString(R.string.pref_key_camera_live_viewport);
    return sharedPreferences.getBoolean(prefKey, false);
  }
}
