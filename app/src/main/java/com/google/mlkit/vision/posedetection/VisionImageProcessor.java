package com.google.mlkit.vision.posedetection;

import android.graphics.Bitmap;
import com.google.mlkit.common.MlKitException;
import com.google.mlkit.vision.posedetection.camera.FrameMetadata;
import com.google.mlkit.vision.posedetection.graphic.GraphicOverlay;

import java.nio.ByteBuffer;

/** An interface to process the images with different vision detectors and custom image models. */
public interface VisionImageProcessor {

  /** Processes a bitmap image. */
  void processBitmap(Bitmap bitmap, GraphicOverlay graphicOverlay);

  /** Processes ByteBuffer image data, e.g. used for Camera1 live preview case. */
  void processByteBuffer(
          ByteBuffer data, FrameMetadata frameMetadata, GraphicOverlay graphicOverlay)
      throws MlKitException;

  /** Stops the underlying machine learning model and release resources. */
  void stop();
}
