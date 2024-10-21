package com.example.savemymoney;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import com.example.savemymoney.ui.home.HomeFragment;
import com.example.savemymoney.ui.home.HomeViewModel;
import com.example.savemymoney.ui.settings.SettingsFragment;
import com.example.savemymoney.ui.settings.SettingsViewModel;
import com.google.android.material.navigation.NavigationView;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.savemymoney.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements SettingsFragment.OnFragmentInteractionListener, HomeFragment.FragmentCommandListener {
    private static final String TAG = "SaveMyMoney:MainActivity";

    private AppBarConfiguration mAppBarConfiguration;

    private HomeViewModel homeViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.example.savemymoney.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_settings)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onFragmentInteraction(String data) {
        Log.d(TAG, "onFragmentInteraction: received = " + data);
        if (data.equals("removeCache")) {
            Log.d(TAG, "onFragmentInteraction data = " + data);
            homeViewModel.sendCommand("removeCache");
        } else {
            Log.e(TAG, "onFragmentInteraction: invalid settings command received: " + data);
        }
    }

    @Override
    public void onFragmentCommand(String command) {
        // Find the fragment and call its method
        Log.d(TAG, "onFragmentCommand");
        Log.d(TAG, "all fragments = " + getSupportFragmentManager().getFragments().size());
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
        Log.d(TAG, "child fragments = " + fragment.getChildFragmentManager().getFragments().size());
        if (fragment instanceof NavHostFragment) {
            Log.d(TAG, "fragments: " + fragment.getChildFragmentManager().getFragments().toString());
            Fragment childFragment = fragment.getChildFragmentManager().findFragmentById(R.id.nav_home);
            if (childFragment instanceof HomeFragment) {
                ((HomeFragment) childFragment).removeCache();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }
}