/*
 * Copyright 2020 Google LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.mlkit.vision;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.annotation.KeepName;
import com.google.mlkit.vision.posedetection.CameraSource;
import com.google.mlkit.vision.posedetection.CameraSourcePreview;
import com.google.mlkit.vision.demo.R;
import com.google.mlkit.vision.posedetection.GraphicOverlay;
import com.google.mlkit.vision.posedetection.posedetector.PoseDetectorProcessor;
import com.google.mlkit.vision.posedetection.PreferenceUtils;
import com.google.mlkit.vision.pose.PoseDetectorOptionsBase;

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

    setContentView(R.layout.activity_vision_live_preview);

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
            PreferenceUtils.getPoseDetectorOptionsForLivePreview(this);
        Log.i(TAG, "Using Pose Detector with options " + poseDetectorOptions);

        boolean shouldShowInFrameLikelihood =
            PreferenceUtils.shouldShowPoseDetectionInFrameLikelihoodLivePreview(this);
        boolean visualizeZ = PreferenceUtils.shouldPoseDetectionVisualizeZ(this);
        boolean rescaleZ = PreferenceUtils.shouldPoseDetectionRescaleZForVisualization(this);
        boolean runClassification = PreferenceUtils.shouldPoseDetectionRunClassification(this);

        cameraSource.setMachineLearningFrameProcessor(
            new PoseDetectorProcessor(
                this,
                poseDetectorOptions,
                shouldShowInFrameLikelihood,
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
