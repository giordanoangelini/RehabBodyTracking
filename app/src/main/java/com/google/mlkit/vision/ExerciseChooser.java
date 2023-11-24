package com.google.mlkit.vision;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import androidx.appcompat.app.AppCompatActivity;
import androidx.multidex.BuildConfig;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.mlkit.vision.posedetection.posedetector.PoseClass;

/** Demo app chooser which allows you pick from all available testing Activities. */
public final class ExerciseChooser extends AppCompatActivity
    implements AdapterView.OnItemClickListener {
  private static final String TAG = "ChooserActivity";

  public static final PoseClass[] EXERCISE_LIST = new PoseClass[]{
          new PoseClass("squats_down", R.string.pose_detection_squats_title, R.string.pose_detection_squats_description),
          new PoseClass("pushups_down", R.string.pose_detection_pushups_title, R.string.pose_detection_pushups_description)
  };

  public static PoseClass SELECTED_EXERCISE;

  @Override
  protected void onCreate(Bundle savedInstanceState) {

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
        Log.e("giordano", "onCreate: ", e);
      }
    });
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    SELECTED_EXERCISE = EXERCISE_LIST[position];
    startActivity(new Intent(this, LivePreviewActivity.class));
  }

  private static class MyArrayAdapter extends ArrayAdapter<PoseClass> {

    private final Context context;
    private final PoseClass[] classes;

    MyArrayAdapter(Context context, int resource, PoseClass[] objects) {
      super(context, resource,objects);
      this.context = context;
      classes = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
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
}
