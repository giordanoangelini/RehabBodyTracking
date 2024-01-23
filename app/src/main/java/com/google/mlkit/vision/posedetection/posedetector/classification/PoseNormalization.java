package com.google.mlkit.vision.posedetection.posedetector.classification;

import static com.google.mlkit.vision.posedetection.posedetector.classification.Utils.average;
import static com.google.mlkit.vision.posedetection.posedetector.classification.Utils.l2Norm2D;
import static com.google.mlkit.vision.posedetection.posedetector.classification.Utils.multiplyAll;
import static com.google.mlkit.vision.posedetection.posedetector.classification.Utils.subtract;
import static com.google.mlkit.vision.posedetection.posedetector.classification.Utils.subtractAll;

import com.google.mlkit.vision.common.PointF3D;
import com.google.mlkit.vision.pose.PoseLandmark;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Generates embedding for given list of Pose landmarks.
 */
public class PoseNormalization {
  // Multiplier to apply to the torso to get minimal body size. Picked this by experimentation.
  private static final float TORSO_MULTIPLIER = 2.5f;

  public static final ArrayList<Integer> filterList = new ArrayList<>(Arrays.asList(
          PoseLandmark.NOSE,
          PoseLandmark.LEFT_EYE_INNER,
          PoseLandmark.LEFT_EYE,
          PoseLandmark.LEFT_EYE_OUTER,
          PoseLandmark.RIGHT_EYE_INNER,
          PoseLandmark.RIGHT_EYE,
          PoseLandmark.RIGHT_EYE_OUTER,
          PoseLandmark.LEFT_EAR,
          PoseLandmark.RIGHT_EAR,
          PoseLandmark.LEFT_MOUTH,
          PoseLandmark.RIGHT_MOUTH,
          PoseLandmark.LEFT_PINKY,
          PoseLandmark.RIGHT_PINKY,
          PoseLandmark.LEFT_INDEX,
          PoseLandmark.RIGHT_INDEX,
          PoseLandmark.LEFT_THUMB,
          PoseLandmark.RIGHT_THUMB
  ));

  public static List<PointF3D> getNormalizedPose(List<PointF3D> landmarks) {
    return filterPoints(normalize(landmarks));
  }

  public static <T> List<T> filterPoints(List<T> landmarks) {
    List<T> new_landmarks = new ArrayList<>(landmarks);
    for (int point: filterList) {
      new_landmarks.set(point, null);
    }
    return new_landmarks.stream()
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
  }

  private static List<PointF3D> normalize(List<PointF3D> landmarks) {
    List<PointF3D> normalizedLandmarks = new ArrayList<>(landmarks);
    // Normalize translation.
    PointF3D center = average(
        landmarks.get(PoseLandmark.LEFT_HIP), landmarks.get(PoseLandmark.RIGHT_HIP));

    subtractAll(center, normalizedLandmarks);

    // Normalize scale.
    multiplyAll(normalizedLandmarks, 1 / getPoseSize(normalizedLandmarks));
    // Multiplication by 100 is not required, but makes it easier to debug.
    multiplyAll(normalizedLandmarks, 100);
    return normalizedLandmarks;
  }

  // Translation normalization should've been done prior to calling this method.
  private static float getPoseSize(List<PointF3D> landmarks) {
    // Note: This approach uses only 2D landmarks to compute pose size as using Z wasn't helpful
    // in our experimentation but you're welcome to tweak.
    PointF3D hipsCenter = average(
        landmarks.get(PoseLandmark.LEFT_HIP), landmarks.get(PoseLandmark.RIGHT_HIP));

    float maxDistance = 0;
    for (PointF3D landmark : landmarks) {
      float distance = l2Norm2D(subtract(hipsCenter, landmark));
      if (distance > maxDistance) {
        maxDistance = distance;
      }
    }
    return maxDistance;
  }

  private static List<PointF3D> getEmbedding(List<PointF3D> lm) {
    List<PointF3D> embedding = new ArrayList<>();

    // We use several pairwise 3D distances to form pose embedding. These were selected
    // based on experimentation for best results with our default pose classes as captued in the
    // pose samples csv. Feel free to play with this and add or remove for your use-cases.

    // We group our distances by number of joints between the pairs.
    // One joint.
    embedding.add(subtract(
        average(lm.get(PoseLandmark.LEFT_HIP), lm.get(PoseLandmark.RIGHT_HIP)),
        average(lm.get(PoseLandmark.LEFT_SHOULDER), lm.get(PoseLandmark.RIGHT_SHOULDER))
    ));

    embedding.add(subtract(
        lm.get(PoseLandmark.LEFT_SHOULDER), lm.get(PoseLandmark.LEFT_ELBOW)));
    embedding.add(subtract(
        lm.get(PoseLandmark.RIGHT_SHOULDER), lm.get(PoseLandmark.RIGHT_ELBOW)));

    embedding.add(subtract(lm.get(PoseLandmark.LEFT_ELBOW), lm.get(PoseLandmark.LEFT_WRIST)));
    embedding.add(subtract(lm.get(PoseLandmark.RIGHT_ELBOW), lm.get(PoseLandmark.RIGHT_WRIST)));

    embedding.add(subtract(lm.get(PoseLandmark.LEFT_HIP), lm.get(PoseLandmark.LEFT_KNEE)));
    embedding.add(subtract(lm.get(PoseLandmark.RIGHT_HIP), lm.get(PoseLandmark.RIGHT_KNEE)));

    embedding.add(subtract(lm.get(PoseLandmark.LEFT_KNEE), lm.get(PoseLandmark.LEFT_ANKLE)));
    embedding.add(subtract(lm.get(PoseLandmark.RIGHT_KNEE), lm.get(PoseLandmark.RIGHT_ANKLE)));

    // Two joints.
    embedding.add(subtract(
        lm.get(PoseLandmark.LEFT_SHOULDER), lm.get(PoseLandmark.LEFT_WRIST)));
    embedding.add(subtract(
        lm.get(PoseLandmark.RIGHT_SHOULDER), lm.get(PoseLandmark.RIGHT_WRIST)));

    embedding.add(subtract(lm.get(PoseLandmark.LEFT_HIP), lm.get(PoseLandmark.LEFT_ANKLE)));
    embedding.add(subtract(lm.get(PoseLandmark.RIGHT_HIP), lm.get(PoseLandmark.RIGHT_ANKLE)));

    // Four joints.
    embedding.add(subtract(lm.get(PoseLandmark.LEFT_HIP), lm.get(PoseLandmark.LEFT_WRIST)));
    embedding.add(subtract(lm.get(PoseLandmark.RIGHT_HIP), lm.get(PoseLandmark.RIGHT_WRIST)));

    // Five joints.
    embedding.add(subtract(
        lm.get(PoseLandmark.LEFT_SHOULDER), lm.get(PoseLandmark.LEFT_ANKLE)));
    embedding.add(subtract(
        lm.get(PoseLandmark.RIGHT_SHOULDER), lm.get(PoseLandmark.RIGHT_ANKLE)));

    embedding.add(subtract(lm.get(PoseLandmark.LEFT_HIP), lm.get(PoseLandmark.LEFT_WRIST)));
    embedding.add(subtract(lm.get(PoseLandmark.RIGHT_HIP), lm.get(PoseLandmark.RIGHT_WRIST)));

    // Cross body.
    embedding.add(subtract(lm.get(PoseLandmark.LEFT_ELBOW), lm.get(PoseLandmark.RIGHT_ELBOW)));
    embedding.add(subtract(lm.get(PoseLandmark.LEFT_KNEE), lm.get(PoseLandmark.RIGHT_KNEE)));

    embedding.add(subtract(lm.get(PoseLandmark.LEFT_WRIST), lm.get(PoseLandmark.RIGHT_WRIST)));
    embedding.add(subtract(lm.get(PoseLandmark.LEFT_ANKLE), lm.get(PoseLandmark.RIGHT_ANKLE)));

    return embedding;
  }

  private PoseNormalization() {}
}
