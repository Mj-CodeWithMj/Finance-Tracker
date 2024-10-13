package com.example.financetracker;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

public class ExtraLayouts extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extra_layouts);
        Log.d("ExtraLayouts", "Activity layout set: " + R.layout.activity_extra_layouts);

        // Check which fragment to load based on intent extra
        if (savedInstanceState == null) {
            // initialize fragment
            Fragment fragment = null;
            // Check the intent extra
            String fragmentType = getIntent().getStringExtra("fragment_type");

            if ("edit_profile".equals(fragmentType)) {
                fragment = new EditProfileFragment();

            } else if ("change_password".equals(fragmentType)){
                fragment = new ChangePasswordFragment();
            }else if ("user_edit_profile".equals(fragmentType)){
                fragment = new UserEditProfileFragment();
            }
            assert fragment != null;
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
    }
}