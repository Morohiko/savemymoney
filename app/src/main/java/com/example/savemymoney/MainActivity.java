package com.example.savemymoney;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

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
                newWithdraw();
            }
        });

        depositBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "depositBtn: onClickListener");
                newDeposit();
            }
        });

        File cacheDir = getCacheDir();
        Log.d(TAG, "onCreate: cacheDir = " + cacheDir);
        Settings.getInstance().installSettings(cacheDir);
        wallet = new Wallet(cacheDir);
    }

    private void newWithdraw() {
        final EditText priceInput = new EditText(this);
        priceInput.setHint("price");
        priceInput.setInputType(InputType.TYPE_CLASS_NUMBER);

        final EditText descInput = new EditText(this);
        descInput.setHint("desc");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(priceInput);
        layout.addView(descInput);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("withdraw:");

        builder.setView(layout);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String priceValue = priceInput.getText().toString();
                String descValue = descInput.getText().toString();

                wallet.withdrawMoney(new Date()/*today*/, Integer.parseInt(priceValue), descValue);

                Toast.makeText(MainActivity.this, "saved:\nprice: " + priceValue + "\ndesc: " + descValue, Toast.LENGTH_LONG).show();
            }
        });

        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void newDeposit() {
        final EditText priceInput = new EditText(this);
        priceInput.setHint("price");
        priceInput.setInputType(InputType.TYPE_CLASS_NUMBER);

        final EditText descInput = new EditText(this);
        descInput.setHint("desc");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(priceInput);
        layout.addView(descInput);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("deposit:");

        builder.setView(layout);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String priceValue = priceInput.getText().toString();
                String descValue = descInput.getText().toString();

                wallet.depositMoney(new Date(), Integer.parseInt(priceValue), descValue);

                Toast.makeText(MainActivity.this, "saved:\nprice: " + priceValue + "\ndesc: " + descValue, Toast.LENGTH_LONG).show();
            }
        });

        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
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