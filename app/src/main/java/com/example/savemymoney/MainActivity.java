package com.example.savemymoney;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.ProgressBar;
import java.io.File;

import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.savemymoney.databinding.ActivityMainBinding;

import java.util.Date;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "SaveMyMoney:MainActivity";

    private AppBarConfiguration mAppBarConfiguration;

    private ProgressBar myProgressBar;

    private Wallet wallet;

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
                R.id.nav_home, R.id.nav_gallery, R.id.nav_settings)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // Find the Button by its ID
        myProgressBar = findViewById(R.id.moneyLeftPb);
        Button withdrawBtn = findViewById(R.id.redBtn);
        Button depositBtn = findViewById(R.id.grnBtn);

        // Set a click listener
        withdrawBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "withdrawBtn: onClicklistener");
                // Generate a random progress value between 0 and 100
                int randomProgress = new Random().nextInt(101);
                // Set the progress bar to this value
                myProgressBar.setProgress(randomProgress);

                // TODO: implement
                wallet.withdrawMoney(new Date()/*today*/, 155, "desc");
            }
        });

        depositBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "depositBtn: onClickListener");

                wallet.depositMoney(new Date(), 11, "desc");
            }
        });

        File cacheDir = getCacheDir();
        Log.d(TAG, "onCreate: cacheDir = " + cacheDir);
        Settings.getInstance().installSettings(cacheDir);
        wallet = new Wallet(cacheDir);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}