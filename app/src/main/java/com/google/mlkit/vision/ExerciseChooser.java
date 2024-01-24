package com.google.mlkit.vision;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.multidex.BuildConfig;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import android.Manifest;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

import com.google.mlkit.vision.posedetection.posedetector.ExerciseClass;

/** Demo app chooser which allows you pick from all available testing Activities. */
public final class ExerciseChooser extends AppCompatActivity
        implements AdapterView.OnItemClickListener, PopupMenu.OnMenuItemClickListener {
  private static final String TAG = "ChooserActivity";
  private static final int PERMISSION_REQUESTS = 1;
  private static final String[] REQUIRED_RUNTIME_PERMISSIONS = {
          Manifest.permission.CAMERA
  };

  public static final ExerciseClass[] EXERCISE_LIST = new ExerciseClass[]{
          new ExerciseClass("squats_down", R.string.pose_detection_squats_title, R.string.pose_detection_squats_description),
          new ExerciseClass("pushups_down", R.string.pose_detection_pushups_title, R.string.pose_detection_pushups_description),
          new ExerciseClass("sium_end", R.string.pose_detection_sium_title, R.string.pose_detection_sium_description),

          new ExerciseClass("explorer", R.string.pose_detection_explorer_title, R.string.pose_detection_explorer_description)
  };

  public static ExerciseClass SELECTED_EXERCISE;

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    if (!allRuntimePermissionsGranted()) {
      getRuntimePermissions();
    }

    if (BuildConfig.DEBUG) {
      StrictMode.setThreadPolicy(
          new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build());
      StrictMode.setVmPolicy(
          new StrictMode.VmPolicy.Builder()
              .detectLeakedSqlLiteObjects()
              .detectLeakedClosableObjects()
              .penaltyLog()
              .build());
    }
    super.onCreate(savedInstanceState);
    Log.d(TAG, "onCreate");

    setContentView(R.layout.activity_exercise_chooser);

    // Set up ListView and Adapter
    ListView listView = findViewById(R.id.test_activity_list_view);

    MyArrayAdapter adapter = new MyArrayAdapter(this, R.layout.list_layout, EXERCISE_LIST);

    listView.setAdapter(adapter);
    listView.setOnItemClickListener(this);

    ImageButton settings_button = (ImageButton)findViewById(R.id.settings_button);
    settings_button.setOnClickListener(v -> {
      try {
        startActivity(new Intent(this, SettingsActivity.class));
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    SELECTED_EXERCISE = EXERCISE_LIST[position];

    if(SELECTED_EXERCISE.getPose_key().equals("explorer")) {
      startActivity(new Intent(this, LivePreviewActivity.class));
      return;
    }

    PopupMenu popup = new PopupMenu(this, view, Gravity.END);
    popup.setOnMenuItemClickListener(this);
    popup.inflate(R.menu.exercise_mode_popup_menu);
    popup.show();
  }

  @Override
  public boolean onMenuItemClick(MenuItem item) {
    SELECTED_EXERCISE.setExercise_mode(item.toString());
    startActivity(new Intent(this, LivePreviewActivity.class));
    return true;
  }

  private static class MyArrayAdapter extends ArrayAdapter<ExerciseClass> {

    private final Context context;
    private final ExerciseClass[] classes;

    MyArrayAdapter(Context context, int resource, ExerciseClass[] objects) {
      super(context, resource,objects);
      this.context = context;
      classes = objects;
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
      View view = convertView;

      if (convertView == null) {
        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.list_layout, null);
      }

      ((TextView) view.findViewById(R.id.big_title)).setText(classes[position].getLabel_id());
      ((TextView) view.findViewById(R.id.small_title)).setText(classes[position].getDescription_id());

      return view;
    }
  }

  private boolean allRuntimePermissionsGranted() {
    for (String permission : REQUIRED_RUNTIME_PERMISSIONS) {
      if (isPermissionDenied(this, permission)) return false;
    }
    return true;
  }

  private void getRuntimePermissions() {
    ArrayList<String> permissionsToRequest = new ArrayList<>();
    for (String permission : REQUIRED_RUNTIME_PERMISSIONS) {
      if (isPermissionDenied(this, permission)) {
        permissionsToRequest.add(permission);
      }
    }
    if (!permissionsToRequest.isEmpty()) {
      ActivityCompat.requestPermissions(
              this,
              permissionsToRequest.toArray(new String[0]),
              PERMISSION_REQUESTS
      );
    }
  }

  private boolean isPermissionDenied(Context context, String permission) {
    if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
      Log.i(TAG, "Permission granted: " + permission);
      return false;
    }
    Log.i(TAG, "Permission NOT granted: " + permission);
    return true;
  }
}

