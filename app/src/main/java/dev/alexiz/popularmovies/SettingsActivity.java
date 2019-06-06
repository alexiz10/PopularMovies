package dev.alexiz.popularmovies;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class SettingsActivity extends AppCompatActivity {

    private FloatingActionButton settingsBackFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        settingsBackFab = findViewById(R.id.settings_back_fab);
        settingsBackFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateHome();
            }
        });
    }

    private void navigateHome() {
        NavUtils.navigateUpFromSameTask(this);
    }

}