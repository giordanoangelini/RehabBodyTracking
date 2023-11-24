package com.google.mlkit.vision;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.google.mlkit.vision.R;
import com.google.mlkit.vision.posedetection.utils.PreferenceUtils;

public class SettingsActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_settings);

    SwitchCompat show_info = (SwitchCompat) findViewById(R.id.show_info_pref_id);
    show_info.setChecked(PreferenceUtils.showDetectionInfo(this));
    show_info.setOnCheckedChangeListener(listener);

    SwitchCompat show_body_lines = (SwitchCompat) findViewById(R.id.show_body_lines_pref_id);
    show_body_lines.setChecked(PreferenceUtils.showBodyLines(this));
    show_body_lines.setOnCheckedChangeListener(listener);

    SwitchCompat run_class = (SwitchCompat) findViewById(R.id.show_classification_pref_id);
    run_class.setChecked(PreferenceUtils.showClassificationResults(this));
    run_class.setOnCheckedChangeListener(listener);
  }

  private final CompoundButton.OnCheckedChangeListener listener = (buttonView, isChecked) -> {
    SharedPreferences sharedPreferences = getSharedPreferences(getApplicationContext().getPackageName(), MODE_PRIVATE);
    SharedPreferences.Editor editor = sharedPreferences.edit();
    String prefKey = buttonView.getTag().toString();
    editor.putBoolean(prefKey, isChecked);
    editor.commit();
  };
}
