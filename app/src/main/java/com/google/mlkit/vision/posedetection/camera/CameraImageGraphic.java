package com.google.mlkit.vision.posedetection.camera;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.google.mlkit.vision.posedetection.graphic.GraphicOverlay;
import com.google.mlkit.vision.posedetection.graphic.GraphicOverlay.Graphic;

/** Draw camera image to background. */
public class CameraImageGraphic extends Graphic {

  private final Bitmap bitmap;

  public CameraImageGraphic(GraphicOverlay overlay, Bitmap bitmap) {
    super(overlay);
    this.bitmap = bitmap;
  }

  @Override
  public void draw(Canvas canvas) {
    canvas.drawBitmap(bitmap, getTransformationMatrix(), null);
  }
}
