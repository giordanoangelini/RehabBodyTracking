package com.google.mlkit.vision.posedetection.posedetector.classification;

import com.google.mlkit.vision.ExerciseChooser;
import com.google.mlkit.vision.posedetection.posedetector.PoseClass;
import android.content.Context;
import android.os.Looper;
import android.util.Log;
import androidx.annotation.WorkerThread;
import com.google.common.base.Preconditions;
import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.posedetection.utils.PreferenceUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Accepts a stream of {@link Pose} for classification and Rep counting.
 */
public class PoseClassifierProcessor {
  private static final String TAG = "PoseClassifierProcessor";
  private static final String POSE_SAMPLES_FILE = "pose/fitness_pose_samples.csv";

  // Specify classes for which we want rep counting.
  // These are the labels in the given {@code POSE_SAMPLES_FILE}. You can set your own class labels
  // for your pose samples.

  private final boolean isStreamMode;

  private EMASmoothing emaSmoothing;
  private List<RepetitionCounter> repCounters;
  private List<PoseTimer> poseTimers;
  private PoseClassifier poseClassifier;
  private String lastRepResult;
  private Context context;

  @WorkerThread
  public PoseClassifierProcessor(Context context, boolean isStreamMode) {
    Preconditions.checkState(Looper.myLooper() != Looper.getMainLooper());
    this.isStreamMode = isStreamMode;
    this.context = context;
    if (isStreamMode) {
      emaSmoothing = new EMASmoothing();
      repCounters = new ArrayList<>();
      poseTimers = new ArrayList<>();
      lastRepResult = "";
    }
    loadPoseSamples(context);
  }

  private void loadPoseSamples(Context context) {
    List<PoseSample> poseSamples = new ArrayList<>();
    try {
      BufferedReader reader = new BufferedReader(
          new InputStreamReader(context.getAssets().open(POSE_SAMPLES_FILE)));
      String csvLine = reader.readLine();
      while (csvLine != null) {
        // If line is not a valid {@link PoseSample}, we'll get null and skip adding to the list.
        PoseSample poseSample = PoseSample.getPoseSample(csvLine, ",");
        if (poseSample != null) {
          poseSamples.add(poseSample);
        }
        csvLine = reader.readLine();
      }
    } catch (IOException e) {
      Log.e(TAG, "Error when loading pose samples.\n" + e);
    }
    poseClassifier = new PoseClassifier(poseSamples);
    if (isStreamMode) {
      for (PoseClass exercise : ExerciseChooser.EXERCISE_LIST) {
        if(exercise == ExerciseChooser.SELECTED_EXERCISE && exercise.getExercise_mode() == 0)
          repCounters.add(new RepetitionCounter(exercise.getPose_key()));
        else if(exercise == ExerciseChooser.SELECTED_EXERCISE && exercise.getExercise_mode() == 1)
          poseTimers.add(new PoseTimer(exercise.getPose_key()));
      }
    }
  }

  /**
   * Given a new {@link Pose} input, returns a list of formatted {@link String}s with Pose
   * classification results.
   */
  @WorkerThread
  public List<String> getPoseResult(Pose pose) {

    if(!PreferenceUtils.showClassificationResults(this.context)) return Collections.emptyList();

    Preconditions.checkState(Looper.myLooper() != Looper.getMainLooper());
    List<String> result = new ArrayList<>();
    ClassificationResult classification = poseClassifier.classify(pose);

    // Update {@link RepetitionCounter}s if {@code isStreamMode}.
    if (isStreamMode) {
      // Feed pose to smoothing even if no pose found.
      classification = emaSmoothing.getSmoothedResult(classification);

      // Return early without updating repCounter if no pose found.
      if (pose.getAllPoseLandmarks().isEmpty()) {
        result.add(lastRepResult);
        return result;
      }

      for (RepetitionCounter repCounter : repCounters) {
        int repsBefore = repCounter.getNumRepeats();
        int repsAfter = repCounter.addClassificationResult(classification);
        if (repsAfter > repsBefore) {
          lastRepResult = String.format(
              Locale.US, "%s : %d reps", repCounter.getClassName(), repsAfter);
          break;
        }
      }

      String maxConfidenceClass = classification.getMaxConfidenceClass();
      
      for (PoseTimer poseTimer : poseTimers) {
        poseTimer.setCondition(maxConfidenceClass.equals(poseTimer.getClassName()));
        poseTimer.startTimer();
        lastRepResult = String.format(
                Locale.US, "%s : %d seconds", poseTimer.getClassName(), poseTimer.getTimerValue());
        }

      result.add(lastRepResult);
    }

    // Add maxConfidence class of current frame to result if pose is found.
    if (!pose.getAllPoseLandmarks().isEmpty()) {
      String maxConfidenceClassResult = String.format(
          Locale.US,
          "%s : %.2f confidence",
          ExerciseChooser.SELECTED_EXERCISE.getPose_key(),
          classification.getClassConfidence(ExerciseChooser.SELECTED_EXERCISE.getPose_key())
              / poseClassifier.confidenceRange());
      result.add(maxConfidenceClassResult);
    }

    return result;
  }

}
