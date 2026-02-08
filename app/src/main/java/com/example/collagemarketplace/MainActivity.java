package com.example.collagemarketplace;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNav = findViewById(R.id.bottomNav);

        loadFragment(new HomeFragment());

        bottomNav.setOnItemSelectedListener(item -> {

            Fragment fragment = null;

            if (item.getItemId() == R.id.nav_home)
                fragment = new HomeFragment();

            else if (item.getItemId() == R.id.nav_add)
                fragment = new AddItemFragment();

            else if (item.getItemId() == R.id.nav_search)
                fragment = new SearchFragment();

            else if (item.getItemId() == R.id.nav_messages)
                fragment = new MessagesFragment();

            else if (item.getItemId() == R.id.nav_profile)
                fragment = new ProfileFragment();

            return loadFragment(fragment);
        });
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frameLayout, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}
