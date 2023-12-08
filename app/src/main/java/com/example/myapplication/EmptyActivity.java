package com.example.myapplication;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

public class EmptyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.empty_activity_layout);

        if (savedInstanceState == null) {
            initiateFragment();
        }
    }

    private void initiateFragment() {
        Bundle data = getIntent().getExtras();
        FavouriteImageFragment imageFragment = new FavouriteImageFragment();
        if (data != null) {
            imageFragment.setArguments(data);
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.favFragFrame, imageFragment);
        transaction.commit();
    }

    // Keeping onPause as it does not require refactoring
    @Override
    protected void onPause() {
        super.onPause();
    }
}
