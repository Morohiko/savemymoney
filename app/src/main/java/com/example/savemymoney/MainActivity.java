package com.example.savemymoney;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.savemymoney.databinding.ActivityMainBinding;

import java.io.File;
import java.util.Date;
import java.util.Random;

import com.example.savemymoney.Wallet;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "SaveMyMoney:MainActivity";

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    private ProgressBar myProgressBar;
    private Button withdrawBtn;
    private Button depositBtn;

    private Wallet wallet = new Wallet(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .setAnchorView(R.id.fab).show();
            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // Find the Button by its ID
        myProgressBar = findViewById(R.id.moneyLeftPb);
        withdrawBtn = findViewById(R.id.redBtn);
        depositBtn = findViewById(R.id.grnBtn);

        // Set a click listener
        withdrawBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "withdrawBtn: onClicklistener");
                // Generate a random progress value between 0 and 100
                int randomProgress = new Random().nextInt(101);
                // Set the progress bar to this value
                myProgressBar.setProgress(randomProgress);

                wallet.withdrawMoney(new Date(), 123.456F);
            }
        });

        depositBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "depositBtn: onClickListener");

                wallet.depositMoney(new Date(), 11.22F);
            }
        });
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

    private void updateProgressBar(float amount) {
        
    }
}