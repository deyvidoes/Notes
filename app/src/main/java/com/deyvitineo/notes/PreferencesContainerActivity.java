package com.deyvitineo.notes;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class PreferencesContainerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preferences_activity);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.preference_fragment_container, new PreferencesFragment())
                .commit();
    }
}
