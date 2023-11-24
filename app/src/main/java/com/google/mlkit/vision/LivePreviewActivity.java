package com.google.mlkit.vision;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.annotation.KeepName;
import com.google.mlkit.vision.posedetection.camera.CameraSource;
import com.google.mlkit.vision.posedetection.camera.CameraSourcePreview;
import com.google.mlkit.vision.posedetection.graphic.GraphicOverlay;
import com.google.mlkit.vision.posedetection.posedetector.PoseDetectorProcessor;
import com.google.mlkit.vision.pose.PoseDetectorOptionsBase;
import com.google.mlkit.vision.posedetection.utils.PreferenceUtils;

import java.io.IOException;

@KeepName
public final class LivePreviewActivity extends AppCompatActivity {

  private static final String TAG = "LivePreviewActivity";

  private CameraSource cameraSource = null;
  private CameraSourcePreview preview;
  private GraphicOverlay graphicOverlay;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_live_preview);

    preview = findViewById(R.id.preview_view);
    if (preview == null) {
      Log.d(TAG, "Preview is null");
    }
    graphicOverlay = findViewById(R.id.graphic_overlay);
    if (graphicOverlay == null) {
      Log.d(TAG, "graphicOverlay is null");
    }

    createCameraSource();
  }

  private void createCameraSource() {
    // If there's no existing cameraSource, create one.
    if (cameraSource == null) {
      cameraSource = new CameraSource(this, graphicOverlay);
      cameraSource.setFacing(CameraSource.CAMERA_FACING_FRONT);
    }

    try {

        PoseDetectorOptionsBase poseDetectorOptions =
            PreferenceUtils.getPoseDetectorOptionsForLivePreview();
        Log.i(TAG, "Using Pose Detector with options " + poseDetectorOptions);

        boolean shouldShowInFrameLikelihood =
            PreferenceUtils.showPoseDetectionInFrameLikelihood;
        boolean hideBodyLines = !PreferenceUtils.showBodyLines(this);
        boolean visualizeZ = PreferenceUtils.poseDetectionVisualizeZ;
        boolean rescaleZ = PreferenceUtils.poseDetectionRescaleZForVisualization;
        boolean runClassification = PreferenceUtils.poseDetectionRunClassification;

        cameraSource.setMachineLearningFrameProcessor(
            new PoseDetectorProcessor(
                this,
                poseDetectorOptions,
                shouldShowInFrameLikelihood,
                hideBodyLines,
                visualizeZ,
                rescaleZ,
                runClassification,
                /* isStreamMode = */ true));

    } catch (RuntimeException e) {
      Toast.makeText(
              getApplicationContext(),
              "Can not create image processor: " + e.getMessage(),
              Toast.LENGTH_LONG)
          .show();
    }
  }

  /**
   * Starts or restarts the camera source, if it exists. If the camera source doesn't exist yet
   * (e.g., because onResume was called before the camera source was created), this will be called
   * again when the camera source is created.
   */
  private void startCameraSource() {
    if (cameraSource != null) {
      try {
        if (preview == null) {
          Log.d(TAG, "resume: Preview is null");
        }
        if (graphicOverlay == null) {
          Log.d(TAG, "resume: graphOverlay is null");
        }
        preview.start(cameraSource, graphicOverlay);
      } catch (IOException e) {
        Log.e(TAG, "Unable to start camera source.", e);
        cameraSource.release();
        cameraSource = null;
      }
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    Log.d(TAG, "onResume");
    createCameraSource();
    startCameraSource();
  }

  /** Stops the camera. */
  @Override
  protected void onPause() {
    super.onPause();
    preview.stop();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (cameraSource != null) {
      cameraSource.release();
    }
  }
}
