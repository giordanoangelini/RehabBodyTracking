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
import android.widget.ListView;
import android.widget.TextView;

import com.google.mlkit.vision.posedetection.posedetector.PoseClass;
import com.google.mlkit.vision.posedetection.utils.PreferenceUtils;

/** Demo app chooser which allows you pick from all available testing Activities. */
public final class ChooserActivity extends AppCompatActivity
    implements AdapterView.OnItemClickListener {
  private static final String TAG = "ChooserActivity";

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

    setContentView(R.layout.activity_chooser);

    // Set up ListView and Adapter
    ListView listView = findViewById(R.id.test_activity_list_view);

    MyArrayAdapter adapter = new MyArrayAdapter(this, R.layout.list_layout, PreferenceUtils.EXERCISE_LIST);

    listView.setAdapter(adapter);
    listView.setOnItemClickListener(this);
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    PreferenceUtils.SELECTED_EXERCISE = PreferenceUtils.EXERCISE_LIST[position];
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

      ((TextView) view.findViewById(R.id.big_title)).setText(classes[position].getClass_label());
      ((TextView) view.findViewById(R.id.small_title)).setText(classes[position].getDescription_id());

      return view;
    }
  }
}
